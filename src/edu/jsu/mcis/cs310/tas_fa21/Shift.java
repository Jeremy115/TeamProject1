/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;
//import java.time.LocalDate; 
import java.time.LocalDateTime;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;
//import static java.util.concurrent.TimeUnit.*;

public class Shift {
    
    private int id; //Pulling from database
    private String description;//Pulling from database 
    private int interval; 
    private int dock; 
    private int gracePeriod; //the 5 minute grace period. 
    public LocalTime start; // start the shift. 
    public LocalTime stop; // end the shift.
    public LocalTime lunchstart;//Detects when lunch will start (locally).
    public LocalTime lunchstop; //Detects when lunch will stop (locally).
    public int lunchDeductTime; //Will deduct the time from employee.
    public long lunchduration; //Length of lunch duration.
    public long shiftduration; //Length of the shift.

//Constructor

    public Shift (int id, String description, 
        LocalTime start, LocalTime stop, int interval, 
        int gracePeriod, int dock, LocalTime lunchstart,
        LocalTime lunchstop, int lunchDeductTime){
            
        this.id = id;
        this.description = description;
        this.start = start; // Start time of employee shift
        this.stop = stop; // stop time of employee shift
        this.interval = interval; // # of minutes before start of shift and after end of shift
        this.gracePeriod = gracePeriod; // # of minutes after start of shift and before end of shift used for "forgiveness"
        this.dock = dock; // amount of time adjusted to punish late clock in
        this.lunchstart = lunchstart; // 
        this.lunchstop = lunchstop;
        this.lunchDeductTime = lunchDeductTime;
        this.lunchduration = MINUTES.between(lunchstart, lunchstop);
        this.shiftduration = MINUTES.between(start, stop);

    }

//Getters

    public int getId() {
        return id; 
    }
        
    public String getDescription() {
        return description; 
    }
        
    public LocalTime getStart() {
        return start;
    }

    public LocalTime getStop() {
        return stop;
    }

    public int getInterval() {
        return interval;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public int getDock() {
        return dock;
    }

    public LocalTime getLunchStart() {
        return lunchstart;
    }

    public LocalTime getLunchStop() {
        return lunchstop;
    }
        
    public int getLunchDeductTime() {
        return lunchDeductTime;
    }
        
    public long getlunchduration() {
        return lunchduration; 
    }
    
    public long getshiftduration() {
        return shiftduration; 
    }

//toString method that puts information into a formated string using StringBuilder

    @Override
    public String toString(){
	//String id and description 
	StringBuilder a = new StringBuilder(); 
        
        a.append(description).append(": ");
        a.append(start).append(" - ").append(stop);
        a.append(" (").append(shiftduration).append(" minutes);");
        a.append(" Lunch: ").append(lunchstart).append(" - ").append(lunchstop);
        a.append(" (").append(lunchduration).append(" minutes)");

	return a.toString(); 
    }  
}

