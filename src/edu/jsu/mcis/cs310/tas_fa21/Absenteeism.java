/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;


public class Absenteeism {
    
  
    private Badge badgeid; 
    private LocalDate payperiod; 
    private double percentage; 
    
     
    public Absenteeism(Badge badgeid, LocalDate payperiodtimestamp, double abpercent){
        
        this.badgeid = badgeid;
        this.percentage = abpercent; 
        
        TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
        this.payperiod = payperiodtimestamp.with(fieldUS, Calendar.SUNDAY);
        
    }
    
//Getters.     
    public Badge getBadgeid(){
        return badgeid; 
    }
    
    public LocalDate getPayperiod(){
        return payperiod;
    }
    
    public double getPercentage(){
        return percentage; 
    }
    
    public void setBadgeid(Badge badgeid){
        this.badgeid = badgeid; 
    }
    
    public void setPayPeriod(LocalDate payperiodtimestamp){
        this.payperiod = payperiodtimestamp;
    }
    
    public void setPercentage(double abpercent){
        this.percentage = abpercent; 
    }
    
    
    @Override
    public String toString(){
        
        
        StringBuilder a = new StringBuilder(); 
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern(" MM-dd-yyyy");
        
        // ("#F1EE0555 (Pay Period Starting 08-05-2018): -20.00%"
        
        a.append("#").append(badgeid.getId()).append(" (").append("Pay Period Starting");
        a.append(payperiod.format(format)).append("): ").append(percentage).append("%");
        
        System.out.println(a);
        return a.toString(); 
    }
    
}
