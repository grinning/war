package com.tommytony.war.utility;


public class WarEvent {

   private com.tommytony.war.utility.Type type;
	
   public WarEvent(final Type type) {
    	this.type = type;
    }
    
    public final com.tommytony.war.utility.Type getType() {
    	return type;
    }
	
}
