package com.bryan.finance.beans;

import java.io.Serializable;

public class UpdatedRecord implements Serializable {

	private static final long serialVersionUID = -8766078026264736912L;

	public UpdatedRecord(){}

	private String ID;
	private String attribute;
	private String data;
	
	@Override
	public String toString(){
		return "ID: " + this.ID + "\tColumn: " + this.attribute + "\tUpdated Value: " + this.data;
	}
	
	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}
	/**
	 * @param iD the iD to set
	 */
	public void setID(String iD) {
		ID = iD;
	}
	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}
	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
}
