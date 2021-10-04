package edu.jsu.mcis.cs310.tas_fa21;

public class Badge {
    //Variables

	private String id; 
	private String description; 

//Constructor

	public Badge (String id, String description){
	 this.id = id;
	 this.description = description;
	}

//Getters

	public String getId()
	{
		return id; 
	}
	public String getDescription()
	{
		return id; 
	}

//toString method that puts information into a formated string using StringBuilder

	@Override
	public String toString(){
	//String id and description 
	StringBuilder a = new StringBuilder(); 
	//appends 
	a.append('#').append(id).append(' '); 
	a.append('(').append(description).append(' '); 

	return a.toString(); 
	}  
}
