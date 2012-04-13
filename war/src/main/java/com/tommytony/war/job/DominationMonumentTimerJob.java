package com.tommytony.war.job;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.Team;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.structure.Monument;

public class DominationMonumentTimerJob implements Runnable {

	private List<Monument> points = new ArrayList<Monument>();
	private final int sec;
	private final Warzone zone;
	private boolean winnerFound;
	
	public DominationMonumentTimerJob(final int sec, final Warzone warzone) {
		this.sec = sec;
		this.zone = warzone;
		this.points = this.zone.getMonuments();
		this.winnerFound = false;
	}
	
	@Override
	public void run() {
		//sleep for a bit, (seconds to be exact)
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			Bukkit.getServer().getLogger().log(Level.WARNING, "War> Your computer is stupid!");
			e.printStackTrace();
		}
        //ok now time to add scores to teams
		for(Monument mon : this.points) {
			try {
			Team owner = mon.getOwnerTeam();
			owner.addPoint();
			//detect a win for the team getting the points
			if(owner.getTeamConfig().getInt(TeamConfig.MAXSCORE) <= owner.getPoints()) {
				zone.initializeZone();
				this.winnerFound = true;
				break; //we have a winner, time to exit the loop
			 }
			}catch(NullPointerException e) {
				//monument has no owner
				continue;
			}
		 } 
		
		if(!this.winnerFound) { //still no winner! We need to assign another one of these threads!
		    	DominationMonumentTimerJob thread = new DominationMonumentTimerJob(this.sec, this.zone);
		    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(War.war, thread);
		}
		
		//now we need to free up memory in this thread so we don't accollate amazing amounts of memory every 10 seconds
		this.points.clear();
		this.points = null;
	}


}
