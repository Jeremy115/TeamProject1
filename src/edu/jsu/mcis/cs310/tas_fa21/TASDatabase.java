/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;
import static com.sun.tools.attach.VirtualMachine.list;
import static java.nio.file.Files.list;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import static java.util.Collections.list;
import java.util.GregorianCalendar;
import java.util.Locale;

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
                
            String server = ("jdbc:mysql://localhost/tas_fa21_v1?useSSL=false");
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
        catch(SQLException e){ System.out.println("SQL Connection failed! Invalid database setup?" + e); }
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
            query = "SELECT * from badge where id = ?"; 
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, id);
                
            hasresults = pstSelect.execute();
                
            if (hasresults) {
                ResultSet resultset = pstSelect.getResultSet();
                        
                resultset.next();
                outputBadge = new Badge(resultset.getString("id"), resultset.getString("description"));
            }
        }
        catch(SQLException e){ System.out.println("Error in getBadge() " + e); 
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
                int punchid = resultset.getInt("id");

                outputPunch = new Punch(terminalid, getBadge(badgeid), punchtypeid, originaltimestamp, punchid);
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
            query = "SELECT * FROM tas_fa21_v1.shift WHERE id = ?" ;
            pstSelect = conn.prepareStatement(query);
            pstSelect.setInt(1, id);
               
            // Execute select query
            hasresults = pstSelect.execute();
               
            //while(hasresults || pstSelect.getUpdateCount() != -1 ){
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
                    //System.out.println(outputShift);
                }
            //}
        }
        catch(SQLException e){System.out.println("getShift: " + e);}
        return outputShift;
    }
	
    public Shift getShift(Badge badge){
        try{

            query = "SELECT * FROM tas_fa21_v1.employee WHERE badgeid = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, badge.getId());
               
            hasresults = pstSelect.execute();
               
            //while(hasresults || pstSelect.getUpdateCount() != -1 ){
                if(hasresults){
                       
                    ResultSet resultset = pstSelect.getResultSet();
                    resultset.next();
                        
                    int shiftid = resultset.getInt("shiftid");
                        
                    return getShift(shiftid); 
                //}
            }
        }
        catch(SQLException e){System.out.println("getShift-badgeid " + e);}
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
        catch(SQLException e){ System.err.println("insertPunch: " + e);}
        //System.err.println("New Punch ID: " + results);
        
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
            //System.out.println("HERE IS BADGE: " + strbadge);
            //System.out.println("HERE IS DATE: " + date);
                
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
        catch(SQLException e){ System.out.println("getDailyPunchList: " + e);/*printStackTrace();*/ 
        }
        return output;
    }
    
    //Gets data from database for Absenteeism. 
    public Absenteeism getAbsenteeism(Badge badge, LocalDate payperiod){
        
        Absenteeism outputAbsen = null; 
        
        try{
            
            //Query for badge and payperiod
            query = "SELECT * FROM absenteeism WHERE badgeid = ? AND payperiod = ?";
            pstSelect = conn.prepareStatement(query);
            pstSelect.setString(1, badge.getId());
            pstSelect.setDate(2, java.sql.Date.valueOf(payperiod));
           
            
            hasresults = pstSelect.execute();
            
            if(hasresults){
                
                ResultSet resultset = pstSelect.getResultSet();
                resultset.next();
                
                //get percentage from database as double. 
                double abpercentage = resultset.getDouble("percentage");
                
                //Add the types into the constructor of the Absenteeism class. 
                outputAbsen = new Absenteeism(badge, payperiod, abpercentage);
                
            }
        }catch (SQLException e){ System.out.println("Error in getAbsenteeism: " + e); }
        
        return outputAbsen; 
    }
    
    
    public ArrayList<Punch> getPayPeriodPunchList(Badge badge, LocalDate payperiod, Shift s){
        
        ArrayList<Punch> punchlist = new ArrayList<>();
       
        //CLASS 2: 
        
        //Accepts a badge object and a timestamp(Expressed as a long integer) value  as arguments
        
        //This should retrieve the list of punches from an entire pay period (SUNDAY - SATURDAY). 
        //May wish to use getDailyPunchList() internally within this new method. 
        
       // LocalDate BeginOfWeek = payperiod.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)); //Begin of payperiod.
        //SUNDAY IS 0 an int type
        //FIRST: Identify the beginning and ending of the payperiod.
        
        TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
        
        //int dayofweek = payperiod.get(fieldUS);
        //use Calendar.X  when compairing. 
        Punch obje;
        
        //Set a localdate (First)
        LocalDate payperiodstart = payperiod.with(fieldUS, Calendar.SUNDAY); //Assumes "date" is a LocalDate
        LocalDate payperiodend = payperiod.with(fieldUS, Calendar.SATURDAY);
        
        
        try{
            //SECOND: Get Punches from the database that fall within this range.
            query = "SELECT * FROM punch WHERE DATE(originaltimestamp) >= ?"
                    + " AND DATE(originaltimestamp) <= ? AND badgeid = ? ORDER BY originaltimestamp";

            // if they are Less than peyperiodstart or greater than payperiodend. Or between. 
            //THIRD: iterate through results and create corresponding punch objects, adding each to an ordered collection. 
            pstSelect = conn.prepareStatement(query);
            pstSelect.setDate(1, java.sql.Date.valueOf(payperiodstart));
            pstSelect.setDate(2, java.sql.Date.valueOf(payperiodend));
            pstSelect.setString(3, badge.getId());

            hasresults = pstSelect.execute();
            
            

            if(hasresults){
                
                ResultSet resultset = pstSelect.getResultSet();

                while(resultset.next()){
                    int punchid = resultset.getInt("id");
                    obje = getPunch(punchid);

                    punchlist.add(obje);
                }
                
            }
            

            //FOURTH: return the collection.
        
        
        }catch(SQLException e){ System.out.println("Error in getPayPeriodPunchList: " + e); }
        
        
        
        
        
        //getPunch method from and retrun the objects.
      //  LocalDate punchDate = payperiodstart;
        
        //for (int i = 0; i < DayOfWeek.SUNDAY.getValue(); i++){
          //  punchlist.addAll(getDailyPunchList(badge, payperiodstart));
            //punchDate = punchDate.plusDays(1);
        //}
        //Specifiying in query begninning of pay period. 
        //See if an absenteesim 
    return punchlist;
    }
    
    public void insertAbsenteeism(Absenteeism absenteeism){
      
        
        //CLASS 3
        
        //Should add a new record if a none exists for 
        //the given badgeid and pay period. 
        
        //If it already exists, it should be updated to 
        //reflect the new absenteeism percentage.
        
        Badge badgeid = absenteeism.getBadgeid();
        LocalDate payperiod = absenteeism.getPayperiod();
        Double percentage = absenteeism.getPercentage();
        
        String badgeids = badgeid.getId();
        
        try{
            query = "INSERT INTO absenteeism (badgeid, payperiod, percentage) VALUES (?, ?, ?)";
            pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            pstUpdate.setString(1, badgeids);
            pstUpdate.setDate(2, java.sql.Date.valueOf(payperiod));
            pstUpdate.setDouble(3, percentage);
            
            updateCount = pstUpdate.executeUpdate();
            
        }catch(SQLException e){ System.out.println("Error in insertAbsenteeism: " + e);}     
     }
}


