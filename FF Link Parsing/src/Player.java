
public class Player {
	String firstName;
	String lastName;
	String team;
	String rotoLink;
	String position;
	
	public Player(String firstName, String lastName, String team)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.team = team;
	}
	
	public String setRoto(String link)
	{
		rotoLink = "http://www.rotoworld.com" + link;
		return rotoLink;
	}

}
