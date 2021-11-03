/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Punch {
//Variables
    
	private int id; 
	private int terminalid;
        private Badge badgeid; 
        private LocalDateTime originaltimestamp; 
        private PunchType punchtypeid;  
        private String adjustmenttype;
        private LocalDateTime adjustedtimestamp;
        

   

//Constructor

    public Punch(int terminalid, Badge badgeid, int punchtypeid) {
          
        this.badgeid = badgeid; //badgeid.getId(); 
        this.terminalid = terminalid;
        this.originaltimestamp = LocalDateTime.now();
        this.punchtypeid = PunchType.values()[punchtypeid];
        this.adjustedtimestamp = LocalDateTime.now(); 
        this.adjustmenttype = null;
    }
      
    public Punch(int terminalid, Badge badgeid, int punchtypeid, LocalDateTime ts) {
          
        this.badgeid = badgeid; //badgeid.getId(); 
        this.terminalid = terminalid;
        this.originaltimestamp = ts;
        this.punchtypeid = PunchType.values()[punchtypeid];
        this.adjustedtimestamp = ts; 
        this.adjustmenttype = null;
    }
     
//Getters


	public int getId(){
            return id; 
	}
       public void setId(int id){
           this.id = id;
       }
        public int getTerminalid(){
            return terminalid;
        }
        
        public Badge getBadge(){
            return badgeid;
        }
        
        public LocalDateTime getOriginaltimestamp() {
            return originaltimestamp;
        }

     
        public PunchType getPunchtype() {
            return punchtypeid;
        }
        
        public String getAdjustmenttype() {
            return adjustmenttype;
        }
        public LocalDateTime getAdjustedtimestamp(){
            return adjustedtimestamp;
        }
       
      
        public void setOriginalTimeStamp(LocalDateTime originaltimestamp) {
            this.originaltimestamp = originaltimestamp;
        }
        
   

    public void setAdjustmenttype(String adjustmenttype) {
        this.adjustmenttype = adjustmenttype;
    }

        
    public void adjust(Shift s){
            

            TemporalField usweekday = WeekFields.of(Locale.US).dayOfWeek();

       

           
        //LocalDateTime variables to compare when employee punches in or out of clock
         

            LocalDateTime shiftstart = s.getStart().atDate(originaltimestamp.toLocalDate());//Shift start
            shiftstart = shiftstart.withSecond(0).withNano(0);
       
            LocalDateTime shiftstop = s.getStop().atDate(originaltimestamp.toLocalDate()); //Shift stop
            shiftstop = shiftstop.withSecond(0).withNano(0);
            
            LocalDateTime lunchstart = s.getLunchStart().atDate(originaltimestamp.toLocalDate()); //Lunch start
            LocalDateTime lunchstop = s.getLunchStop().atDate(originaltimestamp.toLocalDate()); //Lunch stop
           
            LocalDateTime shiftstartgrace = shiftstart.plusMinutes(s.getGracePeriod()); //Shift start grace period
            LocalDateTime shiftstopgrace = shiftstop.minusMinutes(s.getGracePeriod()); //Shift stop grace period
           
            LocalDateTime shiftstartplus = shiftstart.plusMinutes(s.getInterval()); //After shift start time
            LocalDateTime shiftstartminus = shiftstart.minusMinutes(s.getInterval()); //Before shift start time
           
            LocalDateTime shiftstopplus = shiftstop.plusMinutes(s.getInterval()); //Before shift stop time
            LocalDateTime shiftstopminus = shiftstop.minusMinutes(s.getInterval()); //After shift stop time
           
            LocalDateTime shiftstartdockplus = shiftstart.plusMinutes(s.getDock()); //Shift start dock time
            LocalDateTime shiftstopdockminus = shiftstop.minusMinutes(s.getDock()); //Shift stop dock time
            
            int dayofweek = originaltimestamp.get(usweekday);
            int roundint = originaltimestamp.toLocalTime().getMinute() % s.getInterval(); 
            int half = s.getInterval()/2; 
            long roundlong;
            
       
            
        //Punchtypes
        if(punchtypeid == PunchType.CLOCK_IN){
               
            //weekdays 
            if(dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY){
               
                if (originaltimestamp.withSecond(0).withNano(0).isEqual(shiftstart)) {
                    adjustmenttype = "None";
                    adjustedtimestamp = shiftstart;
                }  
                    
                else if (originaltimestamp.withSecond(0).withNano(0).isEqual(lunchstop)) {
                    adjustmenttype = "None";
                    adjustedtimestamp = lunchstop;
                }
                   
                //Early Shift start 
                else if((originaltimestamp.isAfter(shiftstartminus) && originaltimestamp.isEqual(shiftstartminus)) || originaltimestamp.isBefore(shiftstart)) { // equal????
                    adjustmenttype = "Shift Start";
                    adjustedtimestamp = shiftstart; 
                }
                   
                //Late Shift start within Grace period
                else if ((originaltimestamp.isAfter(shiftstart) && originaltimestamp.isEqual(shiftstart)) || originaltimestamp.isBefore(shiftstartgrace)) { 
                    adjustmenttype = "Shift Start";
                    adjustedtimestamp = shiftstart; 
                }
                    
                //Late shift start outside grace period
                else if ((originaltimestamp.isAfter(shiftstartgrace) && originaltimestamp.isEqual(shiftstartgrace)) || originaltimestamp.isBefore(shiftstartplus)) {
                    adjustmenttype = "Shift Dock";
                    adjustedtimestamp = shiftstartdockplus;
                }
                    
                //Early Lunch return.
                else if ((originaltimestamp.isAfter(lunchstart) && originaltimestamp.isEqual(lunchstart)) || originaltimestamp.isBefore(lunchstop)) {
                    adjustmenttype = "Lunch Stop"; 
                    adjustedtimestamp = lunchstop; 
                }
                    
                else {
                    
                    if (roundint != 0) {
                        //round down.
                        if(roundint < half) { 
                            roundlong = new Long(roundint);
                            adjustmenttype = "Interval Round";
                            adjustedtimestamp = originaltimestamp.minusMinutes(roundlong).withSecond(0);
                        } 
                    
                        //round up.
                        else if(roundint >= half){ 
                            roundlong = new Long(s.getInterval() - roundint);
                            adjustmenttype = "Interval Round";
                            adjustedtimestamp = originaltimestamp.plusMinutes(roundlong).withSecond(0);
                        }
                    }
                    else {
                        adjustmenttype = "None";
                        adjustedtimestamp = originaltimestamp.withSecond(0).withNano(0);
                    }
                }
            }
                    
            else{
                // This ELSE for Saturday/CLOCK_IN.
                
                if (roundint != 0) {
                    
                    //round down.
                    if(roundint < half) { 
                        roundlong = new Long(roundint);
                        adjustmenttype = "Interval Round";
                        adjustedtimestamp = originaltimestamp.minusMinutes(roundlong).withSecond(0);
                    }    
                    
                    //round up.
                    else if(roundint >= half){ 
                        roundlong = new Long(s.getInterval() - roundint);
                        adjustmenttype = "Interval Round";
                        adjustedtimestamp = originaltimestamp.plusMinutes(roundlong).withSecond(0);
                    }
                }
                
                else {
                    adjustmenttype = "None";
                    adjustedtimestamp = originaltimestamp.withSecond(0).withNano(0);
                }
            }

        }
         
        else if (punchtypeid == PunchType.CLOCK_OUT){
               
            //weekdays
            if(dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY){
                 
                if (originaltimestamp.withSecond(0).withNano(0).isEqual(shiftstop)) {
                    adjustmenttype = "None";
                    adjustedtimestamp = shiftstop;
                } 
                   
                else if (originaltimestamp.withSecond(0).withNano(0).isEqual(lunchstart)) {
                    adjustmenttype = "None";
                    adjustedtimestamp = lunchstart;
                }
                    
                //Early departure outside grace period.
                else if((originaltimestamp.isAfter(shiftstopminus) || originaltimestamp.isEqual(shiftstopminus))&& 
                        (originaltimestamp.isBefore(shiftstopgrace) || originaltimestamp.isEqual(shiftstopgrace))){
                    adjustmenttype = "Shift Dock";
                    adjustedtimestamp = shiftstopdockminus; 
                }
                    
                //early departure within grace period. 
                else if(originaltimestamp.isAfter(shiftstopgrace) && (originaltimestamp.isBefore(shiftstop) || originaltimestamp.isEqual(shiftstop))){
                    adjustmenttype = "Shift Stop"; 
                    adjustedtimestamp = shiftstop; 
                }
                    
                //Late departure. 
                else if ((originaltimestamp.isAfter(shiftstop) && originaltimestamp.isBefore(shiftstopplus)) || originaltimestamp.isEqual(shiftstopplus)) {
                    adjustmenttype = "Shift Stop"; 
                    adjustedtimestamp = shiftstop;         
                }
                   
                //Late lunch departure
                else if (originaltimestamp.isAfter(lunchstart) && (originaltimestamp.isBefore(lunchstop) || originaltimestamp.isEqual(lunchstop))){
                    adjustmenttype = "Lunch Start";
                    adjustedtimestamp = lunchstart;    
                }
                else {
                    if (roundint != 0) {
                        //round down.
                        if(roundint < half) { 
                            roundlong = new Long(roundint);
                            adjustmenttype = "Interval Round";
                            adjustedtimestamp = originaltimestamp.minusMinutes(roundlong).withSecond(0);
                        } 
                    
                        //round up.
                        else if(roundint >= half){ 
                            roundlong = new Long(s.getInterval() - roundint);
                            adjustmenttype = "Interval Round";
                            adjustedtimestamp = originaltimestamp.plusMinutes(roundlong).withSecond(0);
                        }
                    }
                    
                    else {
                        adjustmenttype = "None";
                        adjustedtimestamp = originaltimestamp.withSecond(0).withNano(0);
                    }
                }
            }
            else{
                // This ELSE for Saturday/CLOCK_OUT.
                
                if (roundint != 0) {
                    //round down.
                    if(roundint < half) { 
                        roundlong = new Long(roundint);
                        adjustmenttype = "Interval Round";
                        adjustedtimestamp = originaltimestamp.minusMinutes(roundlong).withSecond(0);
                    }    

                    //round up.
                    else if(roundint >= half){ 
                        roundlong = new Long(s.getInterval() - roundint);
                        adjustmenttype = "Interval Round";
                        adjustedtimestamp = originaltimestamp.plusMinutes(roundlong).withSecond(0);
                    }
                }
                
                else {
                    adjustmenttype = "None";
                    adjustedtimestamp = originaltimestamp.withSecond(0).withNano(0);
                }
            }
        }
    
        }
       
        public String printAdjusted(){

            //String builder to format the adjusted type. 
            StringBuilder s = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");

            s.append('#').append(badgeid.getId()).append(" ").append(punchtypeid);
            s.append(": ").append(formatter.format(adjustedtimestamp).toUpperCase());
            s.append(" (").append(adjustmenttype).append(")");
            System.out.println(s);
            
            return s.toString();
        
        }
        


    public String printOriginal(){
            
        StringBuilder s = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
            
        s.append('#').append(badgeid.getId()).append(" ").append(punchtypeid);
        s.append(": ").append((formatter.format(originaltimestamp)).toUpperCase());
        System.out.println(s.toString());

        return s.toString();
    } 

}
