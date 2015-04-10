// created by Lisa K. Fazio

package utils;

import java.util.GregorianCalendar;

public class Timer {
	private long startTime;
	
	public Timer() {
		start();
	}
	
	public void start() {
		this.startTime = new GregorianCalendar().getTimeInMillis();
	}
	
	public long get() {
		return new GregorianCalendar().getTimeInMillis() - startTime;
	}
	
	public String getFormatted() {       
	    long elapsed = get();  
	    String format = String.format("%%0%dd", 2);  
	    String temp = elapsed+"";
	    String ms = (temp.length()>2 ? temp.substring(temp.length()-3) : temp);
	    elapsed = elapsed/1000;
	    String seconds = String.format(format, elapsed % 60);
	    String minutes = String.format(format, (elapsed % 3600) / 60);
	    String hours = String.format(format, elapsed / 3600);
	    String time =  hours + ":" + minutes + ":" + seconds + "." + ms;
	    return time;  
	} 
}