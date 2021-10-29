/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author Andy
 */
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
        this.id = 0;
        this.adjustedtimestamp = LocalDateTime.now(); 
        this.adjustmenttype = null;
    }
      
    public Punch(int terminalid, Badge badgeid, int punchtypeid, LocalDateTime ts) {
          
        this.badgeid = badgeid; //badgeid.getId(); 
        this.terminalid = terminalid;
        this.originaltimestamp = ts;
        this.punchtypeid = PunchType.values()[punchtypeid];
        this.id = 0;
        this.adjustedtimestamp = ts; 
        this.adjustmenttype = null;
    }
     

//Getters

	public int getId(){
		return id; 
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
        
        public void setOriginalTimeStamp(LocalDateTime originaltimestamp) {
                this.originaltimestamp = originaltimestamp;
        }
    
        public void setId(int id) {
                this.id = id;
        }

        public void setAdjustmenttype(String adjustmenttype) {
                this.adjustmenttype = adjustmenttype;
        }
        
        public void adjust(Shift s){
            
            TemporalField usweekday = WeekFields.of(Locale.US).dayOfWeek();

               
             /*  Know what type punch…clock in/out, lunch start/stop
                Compare time of punch to time from shift
                Check grace period to be sure time doesn’t need to be docked/reduced
                Adjust/update time of punch to match time from shift if not impacted by grace period */
            
            //Range check 
            
            //Look at value at where it ends and start. 
            
            //If statements with conditions for adjustedtype and adjustedtimestamp.
            //Based on originaltimestamp. Here we get that time stamp and round it. 
            //LATE CLOCK-OUT: SHIFT STOP within 15-min interval (ADJUST LATE CLOCK-OUT
            //BACKWARD TO THE SCHEDULED END OF THE SHIFT)

           
           
           //LocalDateTime variables to compair when employee punches in or out of clock
           
           LocalDateTime shiftstart = s.getStart().atDate(originaltimestamp.toLocalDate());//Shift start
           shiftstart = shiftstart.withSecond(0).withNano(0);
           
           LocalDateTime shiftstartint = shiftstart.minusMinutes(s.getInterval());//Subtracts interval minutes from shiftstart and creates a new local date time object.
           
           LocalDateTime shiftstop = s.getStop().atDate(originaltimestamp.toLocalDate()); //Shift stop
           shiftstop = shiftstop.withSecond(0).withNano(0);
           
           LocalDateTime shiftstopint = shiftstop.plusMinutes(s.getInterval());// Adds interval minutes to shiftstart
                  
           
           LocalDateTime lunchstart = s.getLunchStart().atDate(originaltimestamp.toLocalDate()); //Lunch start
           LocalDateTime lunchstop = s.getLunchStop().atDate(originaltimestamp.toLocalDate()); //Lunch stop
           
           
           LocalDateTime gracestart = shiftstart.plusMinutes(s.getGracePeriod()); // Grace period.
           LocalDateTime gracestop = shiftstart.minusMinutes(s.getGracePeriod());//Grace period stop
                   
           LocalDateTime dockstartshift = shiftstart.minusMinutes(s.getDock());//Docks time shift start
           LocalDateTime dockshiftstop = shiftstop.plusMinutes(s.getDock()); //Docks time shiftstop
           
                   
 
           int dayofweek = originaltimestamp.get(usweekday);
           //Punchtypes
           if(punchtypeid == PunchType.CLOCK_IN){
               
               //weekdays 
               if(dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY){
                   //conditioned if statements.
                   
                   //Early Shift start 
                   if(originaltimestamp.isBefore(shiftstart)){
                       adjustmenttype = "Shift Start";
                       adjustedtimestamp = null; 
                       //Interval for shift start.
                   }
                   
                   //Late Shift start within Grace period
                   if(originaltimestamp.isAfter(shiftstart) && originaltimestamp.isBefore(gracestart)){
                       adjustmenttype = "Shift Start";
                       adjustedtimestamp = null; 
                   }
                    
                   //Late shift start outside grace period
                   if(originaltimestamp.isAfter(shiftstart) && originaltimestamp.isAfter(gracestart)){
                       adjustmenttype = "Shift Start";
                       adjustedtimestamp = null;
                       //we will dock the time above this comment.
                   }
                   //Early Lunch return.
                   if(originaltimestamp.isBefore(lunchstop)){
                       adjustmenttype = "Lunch Stop"; 
                       adjustedtimestamp = null; 
                       //round to lunch stop
                   }
                   
                   
               } 
               else{
                  //interval rule. 
                   
                   //If punch occurs zones on weekend round up or down to nearest increment.  
                       
               }
               
               
           }
           else if (punchtypeid == PunchType.CLOCK_OUT){
               
               //weekdays
               if(dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY){
                   //conditioned if statements. 
               
                   //Late lunch departure
                   if(originaltimestamp.isAfter(lunchstart)){
                       adjustmenttype = "Lunch Start";
                       adjustedtimestamp = null;    
                   }
                   //Early departure outside grace period.
                   if(originaltimestamp.isBefore(shiftstop) && originaltimestamp.isBefore(gracestop)){
                       adjustmenttype = "Shift Stop";
                       adjustedtimestamp = null; 
                   }
                   //early departure within grace period. 
                   if(originaltimestamp.isBefore(shiftstop) && originaltimestamp.isAfter(gracestop)){
                       adjustmenttype = "Shift Stop"; 
                       adjustedtimestamp = null; 
                   }
                   //Late departure. 
                   if(originaltimestamp.isAfter(shiftstop) && originaltimestamp.isAfter(shiftstopint)){
                       adjustmenttype = "Shift Stop"; 
                       adjustedtimestamp = null;         
                   }
               else{
                   //interval rule. 
                   
                   //If punch occurs zones on weekend round up or down to nearest increment. 
                   
               }
               
                   
           }
           
           

            
            //************************PUNCH OUT**************************
            //Weekdays
            if ( !"SAT".equals(strDay) || !"SUN".equals(strDay)){
             
                //Late Clock out
                if(originaltimestamp.toLocalTime().isAfter(s.getStop()) && (Math.abs(s.getStop().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay()) <= s.getInterval() * 60)){

                    
  //VARIABLES       LocalDateTime       LocalDateTime   CONV LOCALTIME  CONV TO INT                        LocalDateTime   CONV TO LOCALDATE. CONV TO INT HERE.     LOCALTIME      CONVERTS TO INT HERE      
                    adjustedtimestamp = (originaltimestamp.toLocalTime().toSecondOfDay() - 1000 * Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getLunchStart().atDate(originaltimestamp).toSecondOfDay()));
                   
                                                                                       //Math.abs for absolute value of int type.
                    adjustedtimestamp = originaltimestamp  ;
                    LocalDateTime lunchtest = s.getLunchStart().atDate(originaltimestamp.toLocalDate());
                    if(originaltimestamp.isBefore(lunchtest))
                    adjustmenttype = "Shift Start";        
                }
                //Late Lunch Start.
                else if(originaltimestamp.isAfter(s.getLunchStart().atDate(originaltimestamp.toLocalDate())) && originaltimestamp.isBefore(s.getLunchStop().atDate(originaltimestamp.toLocalDate()))){
                    adjustmenttype = "Lunch Start";  
                    adjustedtimestamp = originaltimestamp - 1000 * Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getLunchStart().toSecondOfDay()); 
                }
                //Early Clock Out.
                else if(originaltimestamp.toLocalTime().isAfter(s.getLunchStop()) && originaltimestamp.toLocalTime().isBefore(s.getStop()) && (Math.abs(s.getStop().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay())) <= s.getGracePeriod() * 60){
                    adjustmenttype = "Shift Stop";
                    adjustedtimestamp = originaltimestamp + 1000 Math.abs(s.getStop().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay());
                }
                //Dock 15 minutes. 
                else if(originaltimestamp.toLocalTime().isAfter(s.getLunchStop()) && originaltimestamp.toLocalTime().isBefore(s.getStop()) && (Math.abs(s.getStop().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay())) > s.getGracePeriod() * 60 && (Math.abs(s.getStop().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay()) <= s.getDock() * 60)){

                    adjustmenttype = "Shift Dock"; 
                    adjustedtimestamp = (originaltimestamp + (Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getStop().toSecondOfDay())) * 1000) - (s.getDock() * 60 * 1000); 
                }
                //Correct time(none).
                else if((originaltimestamp.toLocalTime().getMinute() % s.getInterval()) == 0){
                    adjustmenttype = "None"; 
                    adjustedtimestamp = originaltimestamp - (originaltimestamp.getSecond() * 1000);
                }
                else{

                    //Default when clock out. 
                    //Interval Round
                    long round = originaltimestamp.toLocalTime().getMinute() % s.getInterval()/2; 
                    int half = s.getInterval()/2; 
                    if(round != 0){
                            if(round < half){ //round down. 
                                adjustedtimestamp = originaltimestamp - (round * 60 * 1000);
                            }
                            else if(round >= half){ //round up.
                                adjustedtimestamp = originaltimestamp + ((s.getInterval() - round)* 60 * 1000);
                            }
                        adjustedtimestamp = adjustedtimestamp - (originaltimestamp.toLocalTime().getSecond() * 1000);
                        adjustmenttype = "Interval Round"; 
                    }

                }
            }
            //Weekends
            else if ( "SAT".equals(strDay) || "SUN".equals(strDay)){
                
                //Interval Round. 
                long round = originaltimestamp.toLocalTime().getMinute() % s.getInterval()/2; 
                int half = s.getInterval()/2; 
                if(round != 0){
                        if(round < half){ //round down. 
                            adjustedtimestamp = originaltimestamp - (round * 60 * 1000);
                        }
                        else if(round >= half){ //round up.
                            adjustedtimestamp = originaltimestamp + ((s.getInterval() - round)* 60 * 1000);
                        }
                    adjustedtimestamp = adjustedtimestamp - (originaltimestamp.toLocalTime().getSecond() * 1000);
                    adjustmenttype = "Interval Round"; 
                }
            }
           
            
            //**********************PUNCHING IN************************
            if(!"SAT".equals(strDay) && !"SUN".equals(strDay)){
                
                //Early Clock In. 
                if (originaltimestamp.toLocalTime().isBefore(s.getStart()) && (Math.abs(s.getStart().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay()) <= s.getInterval() * 60)){
                    adjustedtimestamp = originaltimestamp + 1000 * Math.abs(s.getStart().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay());
                    adjustmenttype = "Shift Start";
                }
                //Lunch Stop.
                else if(originaltimestamp.toLocalTime().isAfter(s.getLunchStart()) && originaltimestamp.toLocalTime().isBefore(s.getLunchStop())){
                adjustedtimestamp = originaltimestamp + 1000 * Math.abs(s.getLunchStop().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay());
                adjustmenttype = "Lunch Stop";
                }
                //Late Clock in and Grace period.  
                else if(originaltimestamp.toLocalTime().isAfter(s.getStart()) && originaltimestamp.toLocalTime().isBefore(s.getLunchStart()) && Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getStart().toSecondOfDay()) <= s.getGracePeriod() * 60){
                    
                 adjustedtimestamp = originaltimestamp - 1000 * Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getStart().toSecondOfDay());
                 adjustmenttype = "Shift Start"; 
                }
                //Dock 15 min
                else if(originaltimestamp.toLocalTime().isAfter(s.getStart()) && originaltimestamp.toLocalTime().isBefore(s.getLunchStart()) && (Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getStart().toSecondOfDay())) > s.getGracePeriod() * 60 && (Math.abs(s.getStart().toSecondOfDay() - originaltimestamp.toLocalTime().toSecondOfDay()) <= s.getDock() * 60)) {
                    adjustedtimestamp = (originaltimestamp -(Math.abs(originaltimestamp.toLocalTime().toSecondOfDay() - s.getStart().toSecondOfDay())) * 1000) + (s.getDock() * 60 * 1000);
                    adjustmenttype = "Shift Dock"; 
                }
                //None
                else if((originaltimestamp.toLocalTime().getMinute() % s.getInterval()) ==0){ 
                    adjustedtimestamp = originaltimestamp - (originaltimestamp.toLocalTime().getSecond() * 1000);
                    adjustmenttype = "None";
                }
                else{
                    //Interval Round
                    
                    long round = originaltimestamp.toLocalTime().getMinute() % s.getInterval()/2; 
                    int half = s.getInterval()/2; 
                    if(round != 0){
                        if(round < half){ //round down. 
                            adjustedtimestamp = originaltimestamp - (round * 60 * 1000);
                        }
                        else if(round >= half){ //round up.
                            adjustedtimestamp = originaltimestamp + ((s.getInterval() - round)* 60 * 1000);
                        }
                    adjustedtimestamp = adjustedtimestamp - (originaltimestamp.toLocalTime().getSecond() * 1000);
                    adjustmenttype = "Interval Round"; 
                    }
                }
            }
            else if("SAT".equals(strDay) || "SUN".equals(strDay)){
                long round = originaltimestamp.toLocalTime().getMinute() % s.getInterval()/2; 
                    int half = s.getInterval()/2; 
                    if(round != 0){
                        if(round < half){ //round down. 
                            adjustedtimestamp = originaltimestamp - (round * 60 * 1000);
                        }
                        else if(round >= half){ //round up.
                            adjustedtimestamp = originaltimestamp + ((s.getInterval() - round)* 60 * 1000);
                        }
                    adjustedtimestamp = adjustedtimestamp - (originaltimestamp.toLocalTime().getSecond() * 1000);
                    adjustmenttype = "Interval Round"; 
                    }
            }
                
            
            
           
            
            //Make adjust here 
            //assertEquals("#28DC3FB8 CLOCK IN: FRI 09/07/2018 06:50:35", p1.printOriginal());
            //assertEquals("#28DC3FB8 CLOCK IN: FRI 09/07/2018 07:00:00 (Shift Start)", p1.printAdjusted());
         

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

//	public String printOriginalTimestamp()
        public String printOriginal() {
            
            StringBuilder s = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
            
            // #D2C39273 CLOCK IN: WED 09/05/2018 07:00:07
            
            s.append('#').append(badgeid.getId()).append(" ").append(punchtypeid);
            s.append(": ").append((formatter.format(originaltimestamp)).toUpperCase());
            
            System.out.println(s.toString());
            // Date date = new Date(originaltimestamp);

            //Found easier code online to make string building easier. 
            //Created case statement for each new instance of employee clocking out and in. 
            //If neither happens then default case will time out. 
            
            //String strDate = formatter.format(date);

            //originaltimestamp is the time to adjust. 
            //

            //output.append(strDate.toUpperCase());

            return s.toString();
     } 
}
