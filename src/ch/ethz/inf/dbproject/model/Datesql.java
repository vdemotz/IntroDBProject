package ch.ethz.inf.dbproject.model;

import java.util.Date;

public class Datesql extends Date {

	/**
	 * @return Date in format that is compatible with sql type date : 'YYYY-MM-DD' (already with quotes)
	 */
	public String getDatesql(){
		String ret = "'"+Integer.toString((this.getYear()+1900))+"-"+Integer.toString(this.getMonth())+
				"-"+Integer.toString(this.getDay())+"'";
		return ret;
	}
	
	/**
	 * @return Date in format that is compatible with sql type datetime: 'YYYY-MM-DD HH:MM:SS' (already with quotes)
	 */
	public String getDateTimesql(){
		String ret = "'"+Integer.toString((this.getYear()+1900))+"-"+Integer.toString(this.getMonth())+
				"-"+Integer.toString(this.getDay())+" "+Integer.toString(this.getHours())+":"+
				Integer.toString(this.getMinutes())+":"+Integer.toString(this.getSeconds())+"'";
		return ret;
	}
}
