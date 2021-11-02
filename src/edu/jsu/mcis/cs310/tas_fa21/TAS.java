package edu.jsu.mcis.cs310.tas_fa21;

import java.time.Duration;
import java.util.ArrayList;

public class TAS {
     
    public static void main(String[] args) {
        // create new instance of the database
        TASDatabase database = new TASDatabase();
        
        //Deine the punch, badge, shift instances coming in from the database.
        Punch p = database.getPunch(3634);
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
                System.out.println(time + "\n");     
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
        catch(IndexOutOfBoundsException e){System.out.println("Error!");} 
        catch(Exception e){e.printStackTrace();}
       
        //returns the entire amount of time allocated during the two punch times.
        System.out.println("Here is the total: " + time);
        return time;//returns the total time accumulated. 
        
    }
    
}