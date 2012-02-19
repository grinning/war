package com.tommytony.war.utility;

public class PlayerStat {

	private String name;
	private int kills;
	private int deaths;
	
	public PlayerStat(String name, int kills, int deaths) {
	    this.name = name;
	    this.kills = kills;
	    this.deaths = deaths;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getKills() {
		return this.kills;
	}
	
	public int getDeaths() {
		return this.deaths;
	}
	
	public void incKills() {
		this.kills++;
	}
	
	public void incDeaths() {
		this.deaths++;
	}
	
	@Override
	public void finalize() {
		this.name = null;
	}
}
