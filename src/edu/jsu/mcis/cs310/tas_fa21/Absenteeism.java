/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_fa21;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Absenteeism {
    
    private String badgeid; 
    private LocalDateTime payperiodtimestamp; 
    private double abpercent; 
    
    public Absenteeism(String badgeid, LocalDateTime payperiodtimestamp, double abpercent){
        
        this.badgeid = badgeid;
        this.payperiodtimestamp = payperiodtimestamp; 
        this.abpercent = abpercent; 
    }
    
//Getters.     
    public String getBadgeid(){
        return badgeid; 
    }
    
    public LocalDateTime getPayperiod(){
        return payperiodtimestamp;
    }
    
    public double getPercentage(){
        return abpercent; 
    }
    
    public void setBadgeid(String badgeid){
        this.badgeid = badgeid; 
    }
    
    public void setPayPeriod(LocalDateTime payperiodtimestamp){
        this.payperiodtimestamp = payperiodtimestamp;
    }
    
    public void setPercentage(double abpercent){
        this.abpercent = abpercent; 
    }
    
    
    @Override
    public String toString(){
        
        
        StringBuilder a = new StringBuilder(); 
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern(" LL-dd-uuuu");
        
        // ("#F1EE0555 (Pay Period Starting 08-05-2018): -20.00%"
        a.append("#").append(badgeid).append(" (").append("Pay Period Starting");
        a.append(payperiodtimestamp.format(format)).append("): ").append(abpercent).append("%");
        
        return a.toString(); 
    }
    
}
