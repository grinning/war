package com.tommytony.war.spec.volumes;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jbenchx.annotations.Bench;

import com.tommytony.war.utility.PlayerStat;

public class PlayerStatsTest {

	protected PlayerStat stat;

	
	@Test
	public void checkStatTest() {
		stat = mock(PlayerStat.class);
		when(stat.getDeaths()).thenReturn(0);
		when(stat.getKills()).thenReturn(3);
		when(stat.getKillStreak()).thenReturn((byte) 2);
		
		int combine = stat.getDeaths() + stat.getKills() + stat.getKillStreak();
		this.testBench();
		
		assertEquals(combine, 5);
	}
	
	@Bench
	public void testBench() {
		stat = mock(PlayerStat.class);
		when(stat.getDeaths()).thenReturn(20);
		int checkBenchFramework = stat.getDeaths() + 1;
		double sinBench = Math.ceil(Math.sin(Math.sqrt(checkBenchFramework)));
	}
	
}
