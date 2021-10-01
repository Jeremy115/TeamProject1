

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

/**
 *
 * @author Jerem
 */
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
