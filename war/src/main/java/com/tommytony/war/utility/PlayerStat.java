package com.tommytony.war.utility;

public class PlayerStat {

	private String name;
	private int kills;
	private int deaths;
	private byte killStreak;
	private int zoneKills;
	private int zoneDeaths;
	
	public PlayerStat(String name, int kills, int deaths) {
	    this.name = name;
	    this.kills = kills;
	    this.deaths = deaths;
	    this.killStreak = 0;
	    this.zoneKills = 0;
	    this.zoneDeaths = 0;
	}
	
	public PlayerStat(String name) {
		this.name = name;
		this.kills = 0;
		this.deaths = 0;
		this.killStreak = 0;
		this.zoneKills = 0;
		this.zoneDeaths = 0;
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
	
	//a zone independent object
	public int getZoneKills() {
		return this.zoneKills;
	}
	
	public int getZoneDeaths() {
		return this.zoneDeaths;
	}
	
	public void setZoneKills(int kills) {
		this.zoneKills = kills;
	}
	
	public void setZoneDeaths(int deaths) {
		this.zoneDeaths = deaths;
	}
	
	public void incZoneKills() {
		this.zoneKills++;
	}
	
	public void incZoneDeaths() {
		this.zoneDeaths++;
	}
	
	@Override
	public void finalize() {
		this.name = null;
	}
}
