package com.tommytony.war.spec.volumes;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.tommytony.war.utility.PlayerStat;

public class PlayerStatsTest {

	PlayerStat stat;
	@Before
	public void setup() {
		stat = mock(PlayerStat.class);
		when(stat.getDeaths()).thenReturn(0);
		when(stat.getKills()).thenReturn(3);
		when(stat.getKillStreak()).thenReturn((byte) 2);
	}
	
	@Test
	public void checkStatTest() {
		int combine = stat.getDeaths() + stat.getKills() + stat.getKillStreak();
		
		assertEquals(combine, 5);
	}
}
