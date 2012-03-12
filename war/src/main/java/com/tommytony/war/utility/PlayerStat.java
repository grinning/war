package com.tommytony.war.utility;

public class PlayerStat {

	private String name;
	private int kills;
	private int deaths;
	private byte killStreak;
	
	public PlayerStat(String name, int kills, int deaths) {
	    this.name = name;
	    this.kills = kills;
	    this.deaths = deaths;
	    this.killStreak = 0;
	}
	
	public PlayerStat(String name) {
		this.name = name;
		this.kills = 0;
		this.deaths = 0;
		this.killStreak = 0;
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
	
	public void incKillStreak() {
		this.killStreak++;
	}
	
	public void zeroKillStreak() {
		this.killStreak = 0;
	}
	
	public byte getKillStreak() {
		return this.killStreak;
	}
	
	@Override
	public void finalize() {
		this.name = null;
	}
}
