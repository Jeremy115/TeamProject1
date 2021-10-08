/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        private String badgeid; 
        private long originaltimestamp; 
        private int punchtypeid;  
        private String adjustmenttype;
        private long adjustedtimestamp;

//Constructor

      public Punch(Badge badge, int terminalid, int punchtypeid) {
        this.badgeid = badge.getId();
        this.terminalid = terminalid;
        this.originaltimestamp = System.currentTimeMillis();
        this.punchtypeid = punchtypeid;
        this.id = 0;
        this.adjustedtimestamp = 0; 
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
        public String getBadgeid(){
            return badgeid;
        }
        public long getOriginaltimestamp() {
                return originaltimestamp;
        }

        public int getPunchtypeid() {
                return punchtypeid;
        }

        public String getAdjustmenttype() {
                return adjustmenttype;
        }
        
        public void setOriginalTimeStamp(long originaltimestamp) {
                this.originaltimestamp = originaltimestamp;
        }
    
        public void setId(int id) {
                this.id = id;
        }

        public void setAdjustmenttype(String adjustmenttype) {
                this.adjustmenttype = adjustmenttype;
        }
        

//	public String printOriginalTimestamp()
        public String printOriginal()
        {
            StringBuilder output = new StringBuilder();
            Date date = new Date(originaltimestamp);

            //Found easier code online to make string building easier. 
            //Created case statement for each new instance of employee clocking out and in. 
            //If neither happens then default case will time out. 
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");
            String strDate = formatter.format(date);
        
        switch (punchtypeid) {
            case 1:
                output.append("#").append(badgeid).append(" ");
                output.append("CLOCKED IN: ");
                break;
            case 0:
                output.append("#").append(badgeid).append(" ");
                output.append("CLOCKED OUT: ");
                break;
            default:
                output.append("#").append(badgeid).append(" ");
                output.append("TIMED OUT: ");
                break;
        }
 
        output.append(strDate.toUpperCase());
            
        return output.toString();
     } 
}
