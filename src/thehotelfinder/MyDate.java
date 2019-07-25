/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thehotelfinder;

import static java.lang.Math.abs;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Rohith
 */
public class MyDate{
    public static Date toDate(String s){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        Date resDate = new Date();
        try{
            resDate = formatter.parse(s);
        }catch(ParseException e){
            System.out.println(e);
        }
        return resDate;
    }
    
    public static boolean hasOverlap(Date a, Date b, Date x, Date y){
        if(((a.after(y) || a.equals(y)) && b.after(a)) ||
            (a.before(x) && (b.before(x) || b.equals(x)))){
            return false;
        }
        return true;
    }
    
    public static String getRefString(Date d){
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyhhmmss");
        String s = formatter.format(d);
        return s;
    }
    
    public static String getBookingDate(String ref){
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyhhmmss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("hh:mm:ss dd MMM, yyyy");
        Date resDate = new Date();
        try{
            resDate = formatter.parse(ref);
        }catch(ParseException e){
            System.out.println(e);
        }
        return formatter1.format(resDate);
    }
    
    public static String toStringInit(Date d){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        String s = formatter.format(d);
        return s;
    }
    
    public static int getDays(String a, String b){
        int nights = (int)(((MyDate.toDate(a).getTime()) - (MyDate.toDate(b).getTime()))/(1000 * 60 * 60 * 24));
        return abs(nights);
    }
    
    public static Date addDay(Date d, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
    
    public static Date removeTime(Date date) {    
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.set(Calendar.HOUR_OF_DAY, 0);  
        cal.set(Calendar.MINUTE, 0);  
        cal.set(Calendar.SECOND, 0);  
        cal.set(Calendar.MILLISECOND, 0);  
        return cal.getTime(); 
    }
}
