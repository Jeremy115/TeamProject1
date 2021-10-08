//Commit

package edu.jsu.mcis.cs310.tas_fa21;

public class Badge {
    //Variables

	private final String id; 
	private final String description; 

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
		return description; 
	}

//toString method that puts information into a formated string using StringBuilder

	@Override
	public String toString(){
	//String id and description 
	StringBuilder a = new StringBuilder(); 

        //Formatting the string, the sting is called a from the line above this
        //comment. 
	a.append('#').append(this.id).append(' '); 
	a.append('(').append(this.description).append(')'); 

	return a.toString(); 
	}  
}
