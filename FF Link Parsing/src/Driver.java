import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//fpros tables
//espn projections
//roto -game logs/links/news, need rank page, hribar?
//cbs trade values
public class Driver {
	
	//function to parse fantasy pros rank table**************************************************************************
	//ex: https://www.fantasypros.com/nfl/rankings/consensus-cheatsheets.php?partner=yahoo_dropdown
	static ArrayList<String> getFPros(Document doc)
	{
		ArrayList<String> data = new ArrayList<String>();
		
		for(Element tr:doc.getElementById("rankings-table-wrapper").getElementsByTag("tr"))
		{
			System.out.println(tr.text());
			data.add(tr.text());
		}
		
		return data;
	}
	//function to get player page links from fantasy pros rank table*******************************************************
	static ArrayList<String> getLinksFPros(Document doc)
	{
		ArrayList<String> data = new ArrayList<String>();
		
		for(Element tr:doc.getElementById("rankings-table-wrapper").getElementsByTag("tr"))
		{	
			if(!tr.getElementsByTag("a").isEmpty())
				System.out.println("https://www.fantasypros.com" + tr.getElementsByTag("a").get(0).attr("href"));
		}
		
		return data;
	}
	//function to get note from fpros player page************************************************************************
	static String getNoteFPros(Document doc)
	{
		String data = doc.getElementsByClass("content").get(0).text();
		return data;
	}
	//function to parse espn projections page****************************************************************************
	//ex: "http://games.espn.com/ffl/tools/projections?display=alt"
	static ArrayList<String> parseESPN(Document doc)
	{
		ArrayList<String> data = new ArrayList<String>();
		
		Elements tables = doc.getElementsByTag("table");
		System.out.println(tables.size());
		
		for(Element tmp:tables)
		{
			Elements rows = tmp.getElementsByTag("tr");
			for(Element tmpRow:rows)
			{
				data.add(tmpRow.text());
				System.out.println(tmpRow.text());
				System.out.println();
				System.out.println("------------------------------------------------");
				System.out.println();
			}
		}
		
		return data;
	}
	//function to get player news from a rototworld page ******************************************************************
	static String[] getRotoNews(String url)
	{
		String[] ret = new String[3];
		try {
			Document doc = Jsoup.connect(url).get();
			Elements playerName = doc.getElementsByClass("playername");
			ret[2] = playerName.get(0).text().split(" \\| ")[1];
			Elements playerNews = doc.getElementsByClass("playernews");
			if(playerNews.size() > 0)
			{
				Elements reports = playerNews.get(0).getElementsByClass("report");
				ret [0] = reports.get(0).html();
				Elements impacts = playerNews.get(0).getElementsByClass("impact");
				ret[1] = impacts.get(0).text();	
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	//function to get the game log from a rotoworld player page ***********************************************************
	static ArrayList<String> getRotoGameLog(Document doc)
	{
		ArrayList<String> data = new ArrayList<String>();
		Elements tables = doc.getElementsByClass("statstable");
		
		for(Element tmpTable:tables)
		{
			if(tmpTable.getElementsByTag("th").get(0).text().equals("Game Log"))
			{
				for(Element tr:tmpTable.getElementsByTag("tr"))
				{
					data.add(tr.text());
					System.out.println(tr.text());
				}
			}
		}
		
		return data;
	}
	//function to parse rotoworld team page data and generate a playerlist ******************************************************
	static ArrayList<Player> getPlayersRoto(Document doc, String team)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		//text left class is a quick shortcut to a set of strings that contains the links i want
				Elements links = doc.getElementsByClass("textLeft");
				
				//filter strings for links and player names
				//html must be referenced to ascertain the pattern to parse
				for(Element tmp: links)
				{	
					if(tmp.getElementsByAttribute("href").size() > 0)
					{
						String href = tmp.child(0).attributes().get("href");
						String html = tmp.child(0).html();
						if(href.length() > 9)
						{
							if(href.substring(0,8).equals("/player/"))
							{
								System.out.println(href);
								System.out.println(html);
								
								Scanner scan = new Scanner(html);
								String tmpFirst = scan.next();
								String tmpLast = scan.next(".*");
								
								Player tmpPlayer = new Player(tmpFirst, tmpLast, team);
								tmpPlayer.setRotoLink(href);
								players.add(tmpPlayer);
								
								scan.close();
							}	
						}
					}
				}
				
		return players;
	}
	
	//Function to parse rotoworld team links from file*********************************************************
	static String[] getRotoTeamLinks(File linkFile)
	{
		String[] teamLinks = new String[32];
		
		Scanner scan;
		try {
			scan = new Scanner(linkFile);
			while(scan.hasNext())
			{
				String link = scan.nextLine();
				String fullLink = "http://www.rotoworld.com"+link;
				System.out.println(fullLink);
				String[] tokens = link.split("/");
				System.out.println(tokens.length);
				tokens[5] = tokens[5].replaceAll("-"," ");
				System.out.println(tokens[4] + " " + tokens[5]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return teamLinks;
	}
	//function to parse cbs trade value page, which is mostly tables *****************************************
	//returned strings go as name, standard value, ppr value - repeat
	static ArrayList<String> parseCBSTradeValues(Document doc)
	{
		ArrayList<String> ret = new ArrayList<String>();
		Elements tables = doc.getElementsByTag("table");
		for(Element table: tables)
		{
			Elements rows = table.getElementsByClass("row1");
			for(Element row:rows)
			{
				Elements tds = row.getElementsByTag("td");
				for(Element td: tds)
				{
					ret.add(td.text());
				}
			}
		}
		return ret;
	}
	//end of functions *********************************************************************************
	public static void main(String[] args) 
	{		
		Document doc = null;
		
		
		//jsoup connect
		try {
			//use saved version of the web page for development testing
			//File input = new File("lions.html");
			//Scanner scan = new Scanner(input);
			//System.out.println(scan.next());
			//connect to site for correct parsing
			//doc = Jsoup.connect("http://www.rotoworld.com/teams/clubhouse/nfl/phi/philadelphia-eagles").get();
			//doc = Jsoup.parse(input, "UTF-8", "http://www.rotoworld.com/teams/clubhouse/nfl/det/detroit-lions");
			doc = Jsoup.connect("https://www.cbssports.com/fantasy/football/news/fantasy-football-week-9-trade-values-chart-and-rest-of-season-rankings/").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create player array
		ArrayList<Player> playerList = new ArrayList<Player>();
		//populate players
		File teamList = new File("rotoTeamUrls.txt");
		Scanner scan;
		
		try {
			//populate player list from roto
			scan = new Scanner(teamList);
			while(scan.hasNext())
			{	
				//get link
				String tmp = scan.next();
				
				//get team name abbrv
				String[] tmpSplit = tmp.split("/");
				String team = tmpSplit[4];
				
				//run player aquisition
				doc = Jsoup.connect("http://www.rotoworld.com" + tmp).get();
				ArrayList<Player> tmpPlayerList = getPlayersRoto(doc, team);
				playerList.addAll(tmpPlayerList);
			}
			System.out.println(playerList.size() + "players");
			//populate rotoblurb
			for(Player tmp:playerList)
			{
				//tmp.setRotoBlurb();
				System.out.println("set " + tmp.firstName + " " + tmp.lastName);
			}
			//populate cbs
			doc = Jsoup.connect("https://www.cbssports.com/fantasy/football/news/fantasy-football-week-9-trade-values-chart-and-rest-of-season-rankings/").get();
			ArrayList<String> valuesArray = parseCBSTradeValues(doc);
			
			for(int i = 0; i < valuesArray.size(); i++)
			{
				if((i+1)%3 == 1)
				{
					String[] tmpNames = valuesArray.get(i).split(",")[0].split(" ");
					for(Player tmpPlayer:playerList)
					{
						if(tmpPlayer.firstName.equals(tmpNames[0]) && tmpPlayer.lastName.equals(tmpNames[1]))
						{
							tmpPlayer.cbsNonValue = Integer.parseInt(valuesArray.get(i+1));
							tmpPlayer.cbsPprValue = Integer.parseInt(valuesArray.get(i+2));
						}
					}
				}
			}
			//populate fpros
			
			//write to file
			File outFile = new File("players.txt");
			outFile.createNewFile();
			FileWriter write = new FileWriter(outFile);
			for(Player tmp:playerList)
			{
				write.write(tmp.toString());
				write.write("\n");
			}
			
			scan.close();
			write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//diagnostic
		
		System.out.println(playerList.size());
		int count = 0;
		for(Player tmp:playerList)
		{
			if(tmp.cbsNonValue > 0)
			{
				System.out.println(tmp.firstName + " " + tmp.lastName + "-" + tmp.cbsNonValue);
				count++;
			}
		}
		String title = doc.title();
		System.out.println(title);
		System.out.println("wowowow");
		System.out.println(playerList.get(3));
		
		
		/*
		
		
		ArrayList<String> valuesArray = parseCBSTradeValues(doc);
		
		for(int i = 0; i < valuesArray.size(); i++)
		{
			if((i+1)%3 == 1)
				System.out.print(valuesArray.get(i).split(",")[0] + " ");
			else	
				System.out.print(valuesArray.get(i) + " ");
			if((i+1)%3 == 0)
				System.out.println();
		}
		

	*/
	
	
	
	}
}
