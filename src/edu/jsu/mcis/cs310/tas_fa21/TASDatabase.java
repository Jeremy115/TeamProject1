/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.sql.*;


//THIS IS WHERE ALL DATABASE READ CODE WILL GO!!!
/**
 *
 * @author Jerem
 */
public class TASDatabase {

        private Connection conn = null;
        private String query;
        private PreparedStatement pstSelect = null;
        private ResultSet resultset = null;
        private boolean hasresults;
        
        public TASDatabase(){
		try {
                    /* Identify the Server */
                    
                    String server = ("jdbc:mysql://localhost/tas");
                    String username = "team";
                    String password = "CS488";
                    
                    /* Load the MySQL JDBC Driver */
            
                   // Class.forName("com.mysql.jdbc.Driver").newInstance();
                    
                    /* Open Connection */

                    conn = DriverManager.getConnection(server, username, password);
                    
                    if(!conn.isValid(0)){
                        throw new SQLException();
                    }
                }
               // catch(SQLException e){ System.out.println("SQL Connection failed! Invalid database setup?"); }
                //catch(ClassNotFoundException e){ System.out.println("JDBC driver not found, make sure MySQLDriver is added as a library!"); }
                catch (Exception e){}
	}
        
        
        //ERRORS 
        // becareful here. Code will not compile, but here is where getBadge and get Punch will go. 
        //The Test package should be able to pick these up as we set them as the test package calls them. 
        public Badge(){
              
        }
        public Punch(){
            
        }
        public Shift(){
            
        }
        
        
        
}

