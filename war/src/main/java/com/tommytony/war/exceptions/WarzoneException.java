package com.tommytony.war.exceptions;

public class WarzoneException extends RuntimeException {

	private int linNum;
	private String file;
	
	public WarzoneException(int linNum, String file) {
		this.linNum = linNum;
		this.file = file;
	}
	private static final long serialVersionUID = 1L;

	public void printStackTrace() {
		System.err.println("WarzoneException thrown");
		System.err.println("At file " + file);
		System.err.println("At line number " + Integer.toString(linNum));
	}
}
