package com.tommytony.war.structure;

/**
 * @author grinning
 * @since 1.8
 */

public enum StructureType {
	BOMB     ("bomb"),
	CAKE     ("cake"),
	MONUMENT ("monument");
	
	private final String name;
	
	StructureType(String name) {
		this.name = name;
	}
	
	public final String getString() {
		return this.name;
	}
}
