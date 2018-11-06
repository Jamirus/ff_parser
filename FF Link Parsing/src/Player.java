
public class Player {
	String firstName;
	String lastName;
	String team;
	String rotoLink;
	String position;
	int cbsNonValue = 0;
	int cbsPprValue = 0;
	String rotoNews;
	String rotoImpact;
	
	public Player(String firstName, String lastName, String team)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.team = team;
	}
	
	public String setRotoLink(String link)
	{
		rotoLink = "http://www.rotoworld.com" + link;
		return rotoLink;
	}
	
	public String setRotoBlurb()
	{
		String[] pieces = Driver.getRotoNews(rotoLink);
		rotoNews = pieces[0];
		rotoImpact = pieces[1];
		position = pieces[2];
		return rotoNews;
	}
	public String toString()
	{
		return firstName + " " + lastName + " " + team + " " + rotoLink + " " + position + " " + cbsNonValue + " " + cbsPprValue + " " + rotoNews + " " + rotoImpact;
	}

}
