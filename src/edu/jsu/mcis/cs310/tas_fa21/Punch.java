/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

      public Punch(Badge badge, int terminalid, PunchType punchtypeid) {
        //this.badgeid = badge.getId();
        this.terminalid = terminalid;
        this.originaltimestamp = LocalDateTime.now();
        this.punchtypeid = punchtypeid;
        this.id = 0;
        this.adjustedtimestamp = LocalDateTime.now(); 
        this.adjustmenttype = null;
    }

//Getters

	public int getId()
	{
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
        

//	public String printOriginalTimestamp()
        public String printOriginal() {
            
            StringBuilder s = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
            
            // #D2C39273 CLOCK IN: WED 09/05/2018 07:00:07
            
            s.append("#").append(badgeid).append(" ").append(punchtypeid);
            s.append(": ").append((formatter.format(originaltimestamp)).toUpperCase());
            
            
            // Date date = new Date(originaltimestamp);

            //Found easier code online to make string building easier. 
            //Created case statement for each new instance of employee clocking out and in. 
            //If neither happens then default case will time out. 
            
            //String strDate = formatter.format(date);



            //output.append(strDate.toUpperCase());

            return s.toString();
     } 
}
