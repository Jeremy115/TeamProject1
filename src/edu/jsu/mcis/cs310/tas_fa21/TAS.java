package edu.jsu.mcis.cs310.tas_fa21;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.*;

public class TAS {
     
    public static void main(String[] args) {
        // create new instance of the database
        TASDatabase database = new TASDatabase();
        
        //Deine the punch, badge, shift instances coming in from the database.
        Punch p = database.getPunch(69);
        Badge b = p.getBadge();
        Shift s = database.getShift(b);
        
        //Array to hold the dailypunchlist. 
        ArrayList<Punch> dailypunchlist = database.getDailyPunchList(b, p.getOriginaltimestamp().toLocalDate());
        
        //If the punch type is true then adjust(the method in punch) the punch type. 
        for (Punch punch : dailypunchlist){
            punch.adjust(s);
        }
        calculateTotalMinutes(dailypunchlist, s);
        
    }
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        //int time to add up(required to be int from instructions).
        int time = 0; 
        
        
        try{
            //get the punchlist to compair the time between them. 
            for(int i = 0; i < dailypunchlist.size(); i+= 2){
                
                //Gets time between the adjusted time stamps from punch class. 
                Duration dur = Duration.between(dailypunchlist.get(i).getAdjustedtimestamp(), dailypunchlist.get(i+1).getAdjustedtimestamp());

                
                // accumulates time of minutes between the adjustedtimestamps.
                int minutesTotal = (int)dur.toMinutes();


                //adds up the total time between them.
                time = time + minutesTotal;

                //Prints the total time accumulating.
                //System.out.println(time + "\n");     
            }
            
                //true of false flag to see if they clocked out on time. 
               boolean clockout = false;
               //Get the list of punches to test if we need to deduct time from the shift. 
               for(Punch p : dailypunchlist){ 
                      //If the time equals the lunchstart, then we can skip the check and move on.
                      //If the time is off then the below if will deduct the time of the lunch duration.
                    if (p.getAdjustedtimestamp().toLocalTime().equals(shift.getLunchStart())){

                        clockout = true; 
                        break;
                     }
               }
               //If the clock out remains false, it will mean that we need to deduct the time from the shift. 
               if(!clockout){
                   time = (int) (time - shift.getlunchduration());
               }
               
        }//Exceptions for the 2nd test in the feature 4 test. 
        catch(IndexOutOfBoundsException e){System.out.println("calculateTotalMinutes Error!" + e);} 
        catch(Exception e){e.printStackTrace();}
        
       
        //returns the entire amount of time allocated during the two punch times.
        //System.out.println("Here is the total: " + time);
        return time;//returns the total time accumulated. 
        
    }
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
       /* \"originaltimestamp\":\"TUE 09\\/18\\/2018 11:59:33\",
        \"badgeid\":\"08D01475\",\
        "adjustedtimestamp\":\"TUE 09\\/18\\/2018 12:00:00\",\
        "adjustmenttype\":\"Shift Start\",\
        "terminalid\":\"104\",\
        "id\":\"4943\",\
        "punchtype\":\"CLOCK IN\"
        */
        //Above comes from the feature 5 test class. 
        // Format it the way it is above. 
        
        //Creates jsonData Array. 
        ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();
        //Formats the originaltimestamp and adjustedtimestamp to format to the array when it is compared in the test class. 
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEE" + " LL/dd/uuuu HH:mm:ss" );
        
        //Checks for instances of the punch to the dailypunchlist.
        //iterates through for each instance. 
        for(Punch punch : dailypunchlist){
            
            HashMap<String, String> punchData = new HashMap<>();
            
            //Adds all data required into the punchData HashMap Array.
            //Then we add this into the json String. 
            punchData.put("originaltimestamp", String.valueOf(punch.getOriginaltimestamp().format(format).toUpperCase()));
            punchData.put("badgeid", String.valueOf(punch.getBadge().getId()));
            punchData.put("adjustedtimestamp", String.valueOf(punch.getAdjustedtimestamp().format(format).toUpperCase()));
            punchData.put("adjustmenttype", String.valueOf(punch.getAdjustmenttype()));
            punchData.put("terminalid", String.valueOf(punch.getTerminalid()));
            punchData.put("id",String.valueOf(punch.getId())); 
            punchData.put("punchtype", String.valueOf(punch.getPunchtype()));
            
            //Adds all punchData HashMap array into the json arraylist(HashMap Array).
            jsonData.add(punchData);
        }
        
        String json = JSONValue.toJSONString(jsonData);
        return json;
    }
    
    
     public static double calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s){
        
        //Accpets a list of all the pucnhes are entered by the employee
        //within the entire pay period. 
        
        //Return the total number of scheduled minutes and the total number of
        // minutes accrued by the employee using the given 
        //shift object and Punch collection. 
        
        //Take those totals to compute the employee's absenteeism. 
        //Absenteesim percentage should be returned as a double value. 
        
        
        
        return 0; 
    }
     
}