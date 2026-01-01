/**
 * 
 */
package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logging.HurdleLogger;

/**
 * Configuration for financial year quarters
 * Manages quarter definitions and date ranges for tax year allocation
 * @author ajay
 *
 */
public class Quarter {
	private String name;
	private String code;
	private LocalDate startDate;
	private LocalDate endDate;
	
	public Quarter(String code, String name, LocalDate startDate, LocalDate endDate) {
		this.code = code;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}
	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	public LocalDate getEndDate() {
		return endDate;
	}
	
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Check if a date falls within this quarter
	 */
	public boolean contains(LocalDate date) {
		if (date == null) {
			return false;
		}
		return !date.isBefore(startDate) && !date.isAfter(endDate);
	}
	
	@Override
	public String toString() {
		return code + " (" + name + ") [" + startDate + " to " + endDate + "]";
	}
}
