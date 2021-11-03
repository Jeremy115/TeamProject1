/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;
import java.sql.*;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;

//THIS IS WHERE ALL DATABASE READ CODE WILL GO!!!
/**
 *
 * @author Jerem
 */
public class TASDatabase {

    private Connection conn = null;
    private String query;
    private PreparedStatement pstSelect = null, pstUpdate = null;
        
    private boolean hasresults;
    private int updateCount; 
        
    public TASDatabase(){
        try {
            /* Identify the Server */
                
            String server = ("jdbc:mysql://localhost/tas_fa21_v1");
            String username = "team";
            String password = "CS488";
            System.out.println("Connecting to " + server + "...");
                    
            /* Load the MySQL JDBC Driver */
            //Class.forName("com.mysql.jdbc.Driver");
                    
            /* Open Connection */
            conn = DriverManager.getConnection(server, username, password);
                    
            if(conn.isValid(0)){
            } 
            else {
                throw new SQLException();
            }
        }
        catch(SQLException e){ System.out.println("SQL Connection failed! Invalid database setup?"); }
        //catch(ClassNotFoundException e){ System.out.println("JDBC driver not found, make sure MySQLDriver is added as a library!"); }
        catch (Exception e){}
    }
    
    public void close(){
        try {
            conn.close();
        }
        catch(SQLException e){
        }
        finally{
            if (pstSelect != null) { 
                try { pstSelect.close(); 
                    pstSelect = null; 
                } 
                catch (SQLException e) {
                } 
            }
        }
    }
        
    //ERRORS 
    // becareful here. Code will not compile, but here is where getBadge and get Punch will go. 
    //The Test package should be able to pick these up as we set them as the test package calls them. 
    
    public Badge getBadge(String id){
        Badge outputBadge = null;
            
        try {
            query = "SELECT * from badge where id = \"" + id + "\"";
            pstSelect = conn.prepareStatement(query);
                
            hasresults = pstSelect.execute();
                
            while (hasresults || pstSelect.getUpdateCount() != -1) {
                if (hasresults) {
                    ResultSet resultset = pstSelect.getResultSet();
                        
                    resultset.next();
                    outputBadge = new Badge(resultset.getString("id"), resultset.getString("description"));
                }
            }
        }
        catch(SQLException e){ System.out.println("Error in getBadge()"); 
        }
        return outputBadge;
    }
        
    public Punch getPunch(int id){
        Punch outputPunch = null;
          
        try {
            query = "SELECT * FROM punch WHERE id = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setInt(1, id);
              
            hasresults = pstSelect.execute();
              
            if (hasresults) {
                ResultSet resultset = pstSelect.getResultSet();
                resultset.next();
                    
                int terminalid = resultset.getInt("terminalid");
                String badgeid = resultset.getString("badgeid");
                LocalDateTime originaltimestamp = resultset.getTimestamp("originaltimestamp").toLocalDateTime(); 
                int punchtypeid = resultset.getInt("punchtypeid");

                outputPunch = new Punch(terminalid, getBadge(badgeid), punchtypeid, originaltimestamp);
                }
        }
        catch(SQLException e){ e.printStackTrace();
        }
        return outputPunch;
    }
        
    public Shift getShift(int id){ // method of the database class and provide the shift ID as a parameter.
        Shift outputShift = null;
        
        try{
            // Prepare select query
            query = "SELECT * FROM tas_fa21_v1.shift WHERE id = " + id;
            pstSelect = conn.prepareStatement(query);
               
            // Execute select query
            hasresults = pstSelect.execute();
               
            while(hasresults || pstSelect.getUpdateCount() != -1 ){
                if(hasresults){
                    ResultSet resultset = pstSelect.getResultSet();
                    resultset.next();
                       
                    String description = resultset.getString("description");
                    LocalTime start = LocalTime.parse(resultset.getString("start"));
                    LocalTime stop = LocalTime.parse(resultset.getString("stop")); 
                    //String stop = resultset.getString("stop");
                    int interval = resultset.getInt("interval");
                    int graceperiod = resultset.getInt("graceperiod");
                    int dock = resultset.getInt("dock");
                    LocalTime lunchstart = LocalTime.parse(resultset.getString("lunchstart"));
                    LocalTime lunchstop = LocalTime.parse(resultset.getString("lunchstop"));
                    int lunchdeduct = resultset.getInt("lunchdeduct");
                   
                    
                       
                    outputShift = new Shift(id,description, start, stop, interval, graceperiod, dock, lunchstart, lunchstop, lunchdeduct);
                    System.out.println(outputShift);
                }
            }
        }
        catch(SQLException e){System.out.println(e);}
        return outputShift;
    }
	
    public Shift getShift(Badge badge){
        try{

            query = "SELECT * FROM tas_fa21_v1.employee WHERE badgeid = \""+ badge.getId() +"\"";
            pstSelect = conn.prepareStatement(query);
               
            hasresults = pstSelect.execute();
               
            while(hasresults || pstSelect.getUpdateCount() != -1 ){
                if(hasresults){
                       
                    ResultSet resultset = pstSelect.getResultSet();
                    resultset.next();
                        
                    int shiftid = resultset.getInt("shiftid");
                        
                    return getShift(shiftid); 
                }
            }
        }
        catch(SQLException e){System.out.println(e);}
        return null;
    }
        
    public int insertPunch(Punch p){

        int results = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime originalTime = p.getOriginaltimestamp();
        String otsString = originalTime.format(dtf);
        System.err.println("New Punch Timestamp (from insertPunch(): " + otsString);
        Badge badge = p.getBadge(); 
        int terminalid = p.getTerminalid(); 
        PunchType punchtypeid = p.getPunchtype(); 

        try{
            query = "INSERT INTO tas_fa21_v1.punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)"; 
            pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS); 
             
            pstUpdate.setInt(1, terminalid);
            pstUpdate.setString(2, badge.getId());
            pstUpdate.setString(3, otsString);
            pstUpdate.setInt(4, punchtypeid.ordinal());
             
            updateCount = pstUpdate.executeUpdate();
             
            if(updateCount > 0){
                 
                ResultSet resultset = pstUpdate.getGeneratedKeys(); 
                 
                if (resultset.next()){
                    results = resultset.getInt(1);
                }
            }
        }
        catch(SQLException e){ System.out.println(e);}
        System.err.println("New Punch ID: " + results);
        return results;    
    }
        
    public ArrayList<Punch> getDailyPunchList(Badge badge, LocalDate date){
            
        //Punch variables.
        Punch obj; 
        ArrayList<Punch> output = new ArrayList<>(); 
        String strbadge = badge.getId();
            
        try{
            query = "SELECT * FROM punch WHERE badgeid = ? AND DATE(originaltimestamp) = ?"; 
                
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, strbadge);
            pstSelect.setDate(2, java.sql.Date.valueOf(date));
            //pstSelect.setDate(2, date.);
            System.out.println("HERE IS BADGE: " + strbadge);
            System.out.println("HERE IS DATE: " + date);
                
            hasresults = pstSelect.execute();
                
            if(hasresults){
                ResultSet resultset = pstSelect.getResultSet();

                while(resultset.next()){
                    int punchid = resultset.getInt("id");
                    obj = getPunch(punchid);

                    output.add(obj);
                }
            }
        }
        catch(SQLException e){ System.out.println(e);/*printStackTrace();*/ 
        }
        return output;
        }
    }


