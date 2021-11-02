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
        private LocalDateTime ots;

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
           //LocalDateTime shiftstartdockminus = shiftstart.minusMinutes(s.getDock()); //Anything less than shift start minus falls in the interval round
                   
           //LocalDateTime shiftstopdockplus = shiftstop.plusMinutes(s.getDock()); //Anything greater than shift stop plus falls in the interval round
           LocalDateTime shiftstopdockminus = shiftstop.minusMinutes(s.getDock()); //Shift stop dock time
           
           int dayofweek = originaltimestamp.get(usweekday);
           //Punchtypes
           if(punchtypeid == PunchType.CLOCK_IN){
               
               //weekdays 
               if(dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY){
                    //conditioned if statements.
                   
                    if (originaltimestamp.withSecond(0).withNano(0).equals(shiftstart)) {
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
                       //round to lunch stop
                    }
                   

                   
                } 
                else{
                    if (originaltimestamp.isBefore(shiftstartminus) || (originaltimestamp.isAfter(shiftstartplus) && originaltimestamp.isBefore(lunchstart)) ||
                       (originaltimestamp.isAfter(lunchstop) && originaltimestamp.isBefore(shiftstopminus))) {
                       adjustmenttype = "Interval Round"; 
                       adjustedtimestamp = lunchstop; 
                    } 
                   
                   //If punch occurs zones on weekend round up or down to nearest increment.  
                       
                }
            }
           
            else if (punchtypeid == PunchType.CLOCK_OUT){
               
                //weekdays
                if(dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY){
                    //conditioned if statements. 
                   
                    //System.out.println("OTS clock_out " + originaltimestamp.withSecond(0).withNano(0));
                    //System.out.println("clock_out shift stop " + shiftstop);
                   
                    if (originaltimestamp.withSecond(0).withNano(0).equals(shiftstop)) {
                       adjustmenttype = "None";
                       adjustedtimestamp = shiftstop;
                    } 
                   
                    else if (originaltimestamp.withSecond(0).withNano(0).isEqual(lunchstart)) {
                       adjustmenttype = "None";
                       adjustedtimestamp = lunchstart;
                    }
                    
                    //Early departure outside grace period.
                    else if(originaltimestamp.isAfter(shiftstopminus) && (originaltimestamp.isBefore(shiftstopgrace) || originaltimestamp.isEqual(shiftstopgrace))){
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
                    
                else{
                    //interval rule. 
                   
                    //If punch occurs zones on weekend round up or down to nearest increment. 
                   
                }
           }
           
           
/*
            
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
         
*/
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
