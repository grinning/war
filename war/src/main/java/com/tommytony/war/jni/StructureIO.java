package com.tommytony.war.jni;

public class StructureIO {
    static String sep = System.getProperty("path.separator");
    String path = "plugins" + sep + "war" + sep + "structures";
	
	static {
    	System.loadLibrary("plugins" + sep + "war" + sep + "libraries" + sep + "libWar.dylib");
    }
    
    public native void makeFiles(String path, String sep);
    
    public void makeStructureFiles(String path, String sep) {
    	this.makeFiles(path, sep);
    }
    
}

   

	

