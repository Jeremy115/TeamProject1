/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
           
            
           
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");

           String strDay = formatter.format(adjustedtimestamp).toUpperCase();
           
            
            //Weekdays
            if ( !"SAT".equals(strDay) && !"SUN".equals(strDay)){
             
                
                
            }
            //lunch Start
            else if(originaltimestamp.toLocalTime().isAfter(s.getLunchStart()) || originaltimestamp.toLocalTime().isBefore(s.getLunchStart())){
                adjustmenttype = "Lunch Start";  
                adjustedtimestamp = null; 
            }
            //lunch Stop
            else if(originaltimestamp.toLocalTime().isAfter(s.getLunchStop()) || originaltimestamp.toLocalTime().isBefore(s.getLunchStop())){
                adjustmenttype = "Lunch Stop";
                adjustedtimestamp = null;
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
