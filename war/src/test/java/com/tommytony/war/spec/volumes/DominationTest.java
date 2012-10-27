package com.tommytony.war.spec.volumes;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.junit.Test;

import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.TeamKind;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.structure.Monument;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class DominationTest {

	public static final byte DATA = 0;
	
	@Test
	public void testProximityAndPoints() {
		//Arrange
		World w = mock(World.class);
		Block b1 = mock(Block.class);
		Warzone zone = new Warzone(w, "TestZone");
		//constructor to BlockInfo... 
		/*this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.type = block.getTypeId();
		this.data = block.getData();*/
		//b1 coords = 0, 0, 0 type=air data=0
		//b2 coords = 64, 64, 64 type=air data=0
		when(w.getBlockAt(0, 0, 0)).thenReturn(b1);
		when(b1.getX()).thenReturn(0);
		when(b1.getY()).thenReturn(0);
		when(b1.getZ()).thenReturn(0);
		when(b1.getTypeId()).thenReturn(0);
		when(b1.getData()).thenReturn(DominationTest.DATA);
		zone.getVolume().setCornerOne(w.getBlockAt(0, 0, 0));
		Block b2 = mock(Block.class);
		when(w.getBlockAt(64, 64, 64)).thenReturn(b2);
		when(b2.getX()).thenReturn(64);
		when(b2.getY()).thenReturn(64);
		when(b2.getZ()).thenReturn(64);
		when(b2.getTypeId()).thenReturn(0);
		when(b2.getData()).thenReturn(DominationTest.DATA);
		Block[][][] worldBlocks = new Block[64][64][64];
		for(int i = 0, j = 0, k = 0; i < 64; i++, j++, k++) {
			worldBlocks[i][j][k] = mock(Block.class);
			when(worldBlocks[i][j][k].getX()).thenReturn(i);
			when(worldBlocks[i][j][k].getY()).thenReturn(j);
			when(worldBlocks[i][j][k].getZ()).thenReturn(k);
			when(worldBlocks[i][j][k].getTypeId()).thenReturn(0);
			when(worldBlocks[i][j][k].getData()).thenReturn(DominationTest.DATA);
			when(w.getBlockAt(worldBlocks[i][j][k].getX(), worldBlocks[i][j][k].getY(), 
					worldBlocks[i][j][k].getZ())).thenReturn(worldBlocks[i][j][k]);
		}
		zone.getVolume().setCornerTwo(w.getBlockAt(64, 64, 64));
		Block[][][] monumentBlocks = new Block[20][20][20];
		for(int x = 0, y = 0, z = 0; x < 20; x++, y++, z++) {
					monumentBlocks[x][y][z] = mock(Block.class);
					when(w.getBlockAt(x + 27, y + 27, z + 27)).thenReturn(monumentBlocks[x][y][z]);
					when(monumentBlocks[x][y][z].getX()).thenReturn(x + 27);
					when(monumentBlocks[x][y][z].getY()).thenReturn(y + 27);
					when(monumentBlocks[x][y][z].getZ()).thenReturn(z + 27);
					when(monumentBlocks[x][y][z].getTypeId()).thenReturn(0);
					when(monumentBlocks[x][y][z].getRelative(BlockFace.DOWN)).thenReturn(
							worldBlocks[x + 27 + BlockFace.DOWN.getModX()][y + 27 + BlockFace.DOWN.getModY()]
									[z + 27 + BlockFace.DOWN.getModZ()]);
					when(monumentBlocks[x][y][z].getRelative(BlockFace.EAST, 2)).thenReturn(
							worldBlocks[x + 27 + (BlockFace.EAST.getModX() << 1)][y + 27 + (BlockFace.EAST.getModY() << 1)]
									[z + 27 + (BlockFace.EAST.getModZ() << 1)]);
					when(monumentBlocks[x][y][z].getRelative(BlockFace.SOUTH, 2)).thenReturn(
							worldBlocks[x + 27 + (BlockFace.SOUTH.getModX() << 1)][y + 27 + (BlockFace.SOUTH.getModY() << 1)] 
									[z + 27 + (BlockFace.SOUTH.getModZ() << 1)]);
					when(monumentBlocks[x][y][z].getRelative(BlockFace.UP, 2)).thenReturn(
							worldBlocks[x + 27 + (BlockFace.UP.getModX() << 1)][y + 27 + (BlockFace.UP.getModY() << 1)]
									[z + 27 + (BlockFace.UP.getModZ() << 1)]);
					when(monumentBlocks[x][y][z].getRelative(BlockFace.WEST, 2)).thenReturn(
							worldBlocks[x + 27 + (BlockFace.WEST.getModX() << 1)][y + 27 + (BlockFace.WEST.getModY() << 1)]
									[z + 27 + (BlockFace.WEST.getModZ() << 1)]);
					when(monumentBlocks[x][y][z].getRelative(BlockFace.NORTH, 2)).thenReturn(
							worldBlocks[x + 27 + (BlockFace.NORTH.getModX() << 1)][y + 27 + (BlockFace.NORTH.getModY() << 1)]
									[z + 27 + (BlockFace.NORTH.getModZ() << 1)]);
					when(monumentBlocks[x][y][z].getData()).thenReturn(DominationTest.DATA);
		}
		zone.getMonuments().add(new Monument("testMon", zone, new Location(w, 32, 32, 32)));
		zone.getTeams().add(new Team("team1", TeamKind.GOLD, new Location(w, 6, 2, 6),
				zone));
		zone.getTeams().add(new Team("team2", TeamKind.DIAMOND, new Location(w, 48, 2, 48),
				zone));
		zone.getWarzoneConfig().put(WarzoneConfig.DOMENABLED, true);
	    try {
			Warzone.class.getDeclaredMethod("initZone", null).setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} finally {
			try {
				Warzone.class.getDeclaredMethod("initZone", null).invoke(zone, null);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Player p1 = mock(Player.class);
	    zone.getTeamByKind(TeamKind.GOLD).addPlayer(p1);
	    Player p2 = mock(Player.class);
	    zone.getTeamByKind(TeamKind.DIAMOND).addPlayer(p2);
	    
	    zone.getMonuments().get(0).capture(zone.getTeamByKind(TeamKind.GOLD));
	}
}
