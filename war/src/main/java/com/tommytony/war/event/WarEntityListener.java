package com.tommytony.war.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.config.WarConfig;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.job.CalculateDistanceJob;
import com.tommytony.war.job.DeferredBlockResetsJob;
import com.tommytony.war.spout.SpoutDisplayer;
import com.tommytony.war.structure.Bomb;
import com.tommytony.war.utility.DeferredBlockReset;
import com.tommytony.war.utility.Java6RandomThreadWrapper;
import com.tommytony.war.utility.PlayerStat;

/**
 * Handles Entity-Events
 *
 * @author tommytony, Tim Düsterhus, grinning
 * @package bukkit.tommytony.war
 */
public class WarEntityListener implements Listener {

	private final Random killSeed = new Random();

	private final Random java7KillSeed = new Random();
	private final Java6RandomThreadWrapper java6KillSeed = new Java6RandomThreadWrapper();
	
			
	/**
	 * Handles PVP-Damage
	 *
	 * @param event
	 *                fired event
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void handlerAttackDefend(EntityDamageByEntityEvent event) throws InterruptedException, ExecutionException {
		Entity attacker = event.getDamager();
		Entity defender = event.getEntity();
		
		//DamageCause cause = event.getCause();
		//War.war.log(cause.toString(), Level.INFO);
		// Maybe an arrow was thrown
		if (attacker != null && event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player){
			attacker = ((Player)((Projectile)event.getDamager()).getShooter());
		}

		if (attacker != null && defender != null && attacker instanceof Player && defender instanceof Player) {
			// only let adversaries (same warzone, different team) attack each other
			Player a = (Player) attacker;
			Player d = (Player) defender;
			Warzone attackerWarzone = Warzone.getZoneByPlayerName(a.getName());
			Team attackerTeam = Team.getTeamByPlayerName(a.getName());
			Warzone defenderWarzone = Warzone.getZoneByPlayerName(d.getName());
			Team defenderTeam = Team.getTeamByPlayerName(d.getName());
			
			if ((attackerTeam != null && defenderTeam != null && attackerTeam != defenderTeam && attackerWarzone == defenderWarzone)
					|| (attackerTeam != null && defenderTeam != null && attacker.getEntityId() == defender.getEntityId())) {
				// Make sure one of the players isn't in the spawn
				if (defenderTeam.getSpawnVolume().contains(d.getLocation())) { // attacking person in spawn
					if (!defenderWarzone.isFlagThief(d.getName()) 
							&& !defenderWarzone.isBombThief(d.getName())) { // thieves can always be attacked
						War.war.badMsg(a, "Can't attack a player that's inside his team's spawn.");
						event.setCancelled(true);
						return;
					}
				} else if (attackerTeam.getSpawnVolume().contains(a.getLocation()) && !attackerTeam.getSpawnVolume().contains(d.getLocation())) {
					// only let a player inside spawn attack an enemy player if that player enters the spawn
					if (!attackerWarzone.isFlagThief(a.getName())
							&& !defenderWarzone.isBombThief(d.getName())) { // thieves can always attack
						War.war.badMsg(a, "Can't attack a player from inside your spawn.");
						event.setCancelled(true);
						return;
					}
				// Make sure none of them are respawning
				} else if (defenderWarzone.isRespawning(d)) {
					War.war.badMsg(a, "The target is currently respawning!");
					event.setCancelled(true);
					return;
				} else if (attackerWarzone.isRespawning(a)) {
					War.war.badMsg(a, "You can't attack while respawning!");
					event.setCancelled(true);
					return;
				} 
				
				if(!defenderWarzone.getPvpReady()) {
					//if the timer is still tickin we gotta handle defense! (there be notchz in virgina) 
					event.setCancelled(true);
					War.war.badMsg((Player) attacker, "This zone is currently in the preptime phase! Help your team by preparing for battle!");
					Player p = (Player) attacker;
					ItemStack heldItem = p.getItemInHand(); //repair their tools
					short dura = heldItem.getDurability();
					heldItem.setDurability(++dura); //Remember to keep pre inc operator, not post!
					return;
				}
				if((defender instanceof Player) && (((Player)defender).getHealth() <= 10) 
					&&
				(Warzone.getZoneByLocation((Player)defender).getWarzoneConfig().
						getBoolean(WarzoneConfig.HEALERS))) {
					Player pp = (Player) defender;
					//we are a healing zone, and we are low on health, lets call out for healers
					Team.getTeamByPlayerName(pp.getName()).teamcast(new StringBuilder("Help!!! It's ")
					.append(pp.getName()).append(" and I'm low on health! Please heal me!").toString());
				}
				
				if (!attackerWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.PVPINZONE)) {
					// spleef-like, non-pvp, zone
					event.setCancelled(true);
					return;
				}
				
				if (attackerTeam != null && defenderTeam != null && attacker.getEntityId() == defender.getEntityId()) {
					War.war.badMsg(a, "You hit yourself!");
				}
				//we have GunsPlus running on the server
				if((event.getDamage() >= d.getHealth()) && War.war.hasGunsPlus()) {
					if(defenderWarzone.getReallyDeadFighters().contains(d.getName())) {
						if(d.getHealth() != 0)
							d.setHealth(0);
						return;
					}
					//bug with gunsplus where they might not die! NOW THEY SHALL!
					defenderWarzone.handleDeath(d);
					
					if(attackerWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.DEATHMESSAGES)) {
						Future<Integer> task = Bukkit.getScheduler().callSyncMethod(War.war, new CalculateDistanceJob(a, d));
						String attackerString = new StringBuilder().append(attackerTeam.getKind().getColor()).append(a.getName()).toString();
					    String defenderString = new StringBuilder().append(attackerTeam.getKind().getColor()).append(a.getName()).toString();
						int dist = 0;
						try {
							dist = task.get(500, TimeUnit.NANOSECONDS);
						} catch (TimeoutException e) {
							e.printStackTrace();
						}
					    
					    //tommy's twin tower spawn<>
					    for (Team team : defenderWarzone.getTeams()) {
						    team.teamcast(attackerString.toString() + "'s smoking gun killed " + defenderString.toString() + "From " + dist + " meters away!");
						}
					}
					event.setCancelled(true);
				}

				// Detect death, prevent it and respawn the player
				if (event.getDamage() >= d.getHealth()) {
					if (defenderWarzone.getReallyDeadFighters().contains(d.getName())) {
						// don't re-kill a dead person 
						if (d.getHealth() != 0) {
							d.setHealth(0);
						}
						return;
					}
					
					if (attackerWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.DEATHMESSAGES)) {
						Future<Integer> task = Bukkit.getScheduler().callSyncMethod(War.war, new CalculateDistanceJob(a, d));
						String attackerString = attackerTeam.getKind().getColor() + a.getName();
						String defenderString = defenderTeam.getKind().getColor() + d.getName();
						Material killerWeapon = a.getItemInHand().getType();
						String weaponString = killerWeapon.toString();
						StringBuffer killMessage = null;
						if (attacker.getEntityId() != defender.getEntityId()) {
							if (killerWeapon == Material.AIR) {
								weaponString = "hand";
							} else if (killerWeapon == Material.BOW || event.getDamager() instanceof Arrow) {
								if(War.java7) {
									//if we have java7 installed then we can optimize the random number gen
									int rand = java7KillSeed.nextInt(3);
									if(rand == 0) {
										weaponString = "arrow";
									} else if(rand == 1) {
										weaponString = "bow";
									} else {
										weaponString = "aim";
									}
								} else {
								
								int rand = java6KillSeed.nextRandomInt(3);
								if (rand == 0) {
									weaponString = "arrow";
								} else if (rand == 1) {
									weaponString = "bow";
								} else {
									weaponString = "aim";
								}
							  }
								
							} else if (event.getDamager() instanceof Projectile) {
								weaponString = "aim";
							}
							String adjectiveString;
							String verbString;
							if(War.java7) {
							  //java7 is go, we can optimize
							    adjectiveString = War.war.getDeadlyAdjectives().get(this.java7KillSeed.nextInt(War.war.getDeadlyAdjectives().size()));
							    verbString = War.war.getKillerVerbs().get(this.java7KillSeed.nextInt(War.war.getKillerVerbs().size()));
							} else {
							  adjectiveString = War.war.getDeadlyAdjectives().get(this.killSeed.nextInt(War.war.getDeadlyAdjectives().size()));
							  verbString = War.war.getKillerVerbs().get(this.killSeed.nextInt(War.war.getKillerVerbs().size()));
							}
							killMessage.append(attackerString).append(ChatColor.WHITE + "'s ").append(adjectiveString)
							.append( weaponString.toLowerCase().replace('_', ' ')). 
													append( " ").append( verbString).append( " ").append(defenderString);
						} else {
							killMessage.append(defenderString).append(ChatColor.WHITE + " committed accidental suicide");
						}
						int dist = task.get().intValue();
						
						for (Team team : defenderWarzone.getTeams()) {
							if(weaponString.equals("bow") || weaponString.equals("aim") || weaponString.equals("arrow")) {
								team.teamcast(killMessage.toString() + "From " + dist + " meters away!");
							} else {
							team.teamcast(killMessage.toString());
							}
						}
					}
					
					defenderWarzone.handleDeath(d);
					
					if (!defenderWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.REALDEATHS)) {
						// fast respawn, don't really die
						event.setCancelled(true);
						return;
					}
					
				} else if (defenderWarzone.isBombThief(d.getName()) && d.getLocation().distance(a.getLocation()) < 2) {
					// Close combat, close enough to detonate					
					Bomb bomb = defenderWarzone.getBombForThief(d.getName());
										
					// Kill the bomber 
					defenderWarzone.handleDeath(d);
					
					if (defenderWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.REALDEATHS)) {
						// and respawn him and remove from deadmen (cause realdeath + handleDeath means no respawn and getting queued up for onPlayerRespawn)
						defenderWarzone.getReallyDeadFighters().remove(d.getName());
						defenderWarzone.respawnPlayer(defenderTeam, d);
					}
					
					// Blow up bomb
					defenderWarzone.getWorld().createExplosion(a.getLocation(), 2F);

					// bring back tnt
					bomb.getVolume().resetBlocks();
					bomb.addBombBlocks();
					
					// Notify everyone
					for (Team t : defenderWarzone.getTeams()) {
						if (War.war.isSpoutServer()) {
							for (Player p : t.getPlayers()) {
								SpoutPlayer sp = SpoutManager.getPlayer(p);
								if (sp.isSpoutCraftEnabled()) {
					                sp.sendNotification(
					                		SpoutDisplayer.cleanForNotification(attackerTeam.getKind().getColor() + a.getName() + ChatColor.YELLOW + " made "),
					                		SpoutDisplayer.cleanForNotification(defenderTeam.getKind().getColor() + d.getName() + ChatColor.YELLOW + " blow up!"),
					                		Material.TNT,
					                		(short)0,
					                		10000);
								}
							}
						}
						
						t.teamcast(attackerTeam.getKind().getColor() + a.getName() + ChatColor.WHITE
								+ " made " + defenderTeam.getKind().getColor() + d.getName() + ChatColor.WHITE + " blow up!");
					}
					
				}
			} else if (attackerTeam != null && defenderTeam != null && attackerTeam == defenderTeam && attackerWarzone == defenderWarzone && attacker.getEntityId() != defender.getEntityId()) {
				// same team, but not same person
				if (attackerWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.FRIENDLYFIRE)) {
					War.war.badMsg(a, "Friendly fire is on! Please, don't hurt your teammates."); // if ff is on, let the attack go through
				} else {
					War.war.badMsg(a, "Your attack missed! Your target is on your team.");
					event.setCancelled(true); // ff is off
				}
			} else if (attackerTeam == null && defenderTeam == null && War.war.canPvpOutsideZones(a)) {
				// let normal PVP through is its not turned off or if you have perms
			} else if (attackerTeam == null && defenderTeam == null && !War.war.canPvpOutsideZones(a)) {
				if (!War.war.getWarConfig().getBoolean(WarConfig.DISABLEPVPMESSAGE)) {
					if(!(event.getDamager() instanceof org.bukkit.entity.EnderPearl)) {
					War.war.badMsg(a, "You need the 'war.pvp' permission to attack players outside warzones.");
					}
					return;
				}
				event.setCancelled(true); // global pvp is off
			} else {
				War.war.badMsg(a, "Your attack missed!");
				if (attackerTeam == null) {
					War.war.badMsg(a, "You must join a team, then you'll be able to damage people " + "in the other teams in that warzone.");
				} else if (defenderTeam == null) {
					War.war.badMsg(a, "Your target is not in a team.");
				} else if (attacker != null && defender != null && attacker.getEntityId() == defender.getEntityId()) {
					// You just hit yourself, probably with a bouncing arrow
				} else if (attackerTeam == defenderTeam) {
					War.war.badMsg(a, "Your target is on your team.");
				} else if (attackerWarzone != defenderWarzone) {
					War.war.badMsg(a, "Your target is playing in another warzone.");
				}
				event.setCancelled(true); // can't attack someone inside a warzone if you're not in a team
			}
		} else if (defender instanceof Player) {
			// attacked by dispenser arrow most probably
			// Detect death, prevent it and respawn the player
			Player d = (Player) defender;
			Warzone defenderWarzone = Warzone.getZoneByPlayerName(d.getName());
			if (d != null && defenderWarzone != null && event.getDamage() >= d.getHealth()) {
				if (defenderWarzone.getReallyDeadFighters().contains(d.getName())) {
					// don't re-kill a dead person 
					if (d.getHealth() != 0) {
						d.setHealth(0);
					}
					return;
				}
								
				if (defenderWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.DEATHMESSAGES)) {
					String deathMessage = "";
					String defenderString = Team.getTeamByPlayerName(d.getName()).getKind().getColor() + d.getName();
					
					deathMessage = event.getDamager() instanceof TNTPrimed ? new StringBuilder().append(defenderString)
							.append(ChatColor.WHITE.getChar()).append("exploded").toString() : new StringBuilder().append(defenderString)
							.append(ChatColor.WHITE.getChar()).append("died").toString();

					for (Team team : defenderWarzone.getTeams()) {
						team.teamcast(deathMessage);
					}
				}
				
				defenderWarzone.handleDeath(d);
				
				if (!defenderWarzone.getWarzoneConfig().getBoolean(WarzoneConfig.REALDEATHS)) {
					// fast respawn, don't really die
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	/**
	 * Protects important structures from explosions
	 *
	 * @see EntityListener.onEntityExplode()
	 */
	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		if (!War.war.isLoaded()) {
			return;
		}
		// protect zones elements, lobbies and warhub from creepers and tnt
		List<Block> explodedBlocks = event.blockList();
		List<Block> dontExplode = new ArrayList<Block>();
		
		boolean explosionInAWarzone = event.getEntity() != null && Warzone.getZoneByLocation(event.getEntity().getLocation()) != null;
		
		if (!explosionInAWarzone && War.war.getWarConfig().getBoolean(WarConfig.TNTINZONESONLY) && event.getEntity() instanceof TNTPrimed) {
			// if tntinzonesonly:true, no tnt blows up outside zones
			event.setCancelled(true);
			return;
		}
		
		for (Block block : explodedBlocks) {
			if (War.war.getWarHub() != null && War.war.getWarHub().getVolume().contains(block)) {
				dontExplode.add(block);
			} else {
				boolean inOneZone = false;
				for (Warzone zone : War.war.getWarzones()) {
					if (zone.isImportantBlock(block)) {
						dontExplode.add(block);
						if (zone.isBombBlock(block)) {
							// tnt doesn't get reset like normal blocks, gotta schedule a later reset just for the Bomb
							// structure's tnt block
							DeferredBlockResetsJob job = new DeferredBlockResetsJob(block.getWorld());
							job.add(new DeferredBlockReset(block.getX(), block.getY(), block.getZ(), Material.TNT.getId(), (byte)0));
							War.war.getServer().getScheduler().scheduleSyncDelayedTask(War.war, job, 10);
						}
						inOneZone = true;
						break;
					} else if (zone.getLobby() != null && zone.getLobby().getVolume().contains(block)) {
						dontExplode.add(block);
						inOneZone = true;
						break;
					} else if (zone.getVolume().contains(block)) {
						inOneZone = true;
					}
				}
				
				if (!inOneZone && explosionInAWarzone) {
					// if the explosion originated in warzone, always rollback
					dontExplode.add(block);
				}
			}
		}
		
		int dontExplodeSize = dontExplode.size();
		if (dontExplode.size() > 0) {
			// Reset the exploded blocks that shouldn't have exploded (some of these are zone artifacts, if rollbackexplosion some may be outside-of-zone blocks 
			DeferredBlockResetsJob job = new DeferredBlockResetsJob(dontExplode.get(0).getWorld());
			List<Block> doors = new ArrayList<Block>(); 
			for (Block dont : dontExplode) {
				DeferredBlockReset deferred = null;
				if (dont.getState() instanceof Sign) {
					String[] lines = ((Sign)dont.getState()).getLines();
					deferred = new DeferredBlockReset(dont.getX(), dont.getY(), dont.getZ(), dont.getTypeId(), dont.getData(), lines);
				} else if (dont.getState() instanceof ContainerBlock) {
					ItemStack[] contents = ((ContainerBlock)dont.getState()).getInventory().getContents();
					Block worldBlock = dont.getWorld().getBlockAt(dont.getLocation());
					if (worldBlock.getState() instanceof ContainerBlock) {
						((ContainerBlock)worldBlock.getState()).getInventory().clear();
					}
					deferred = new DeferredBlockReset(dont.getX(), dont.getY(), dont.getZ(), dont.getTypeId(), dont.getData(), copyItems(contents));
				} else if (dont.getTypeId() == Material.NOTE_BLOCK.getId()) {
					Block worldBlock = dont.getWorld().getBlockAt(dont.getLocation());
					if (worldBlock.getState() instanceof NoteBlock) {
						NoteBlock noteBlock = ((NoteBlock)worldBlock.getState());
						if (noteBlock != null) {
							deferred = new DeferredBlockReset(dont.getX(), dont.getY(), dont.getZ(), dont.getTypeId(), dont.getData(), noteBlock.getRawNote());
						}
					}
				} else if (dont.getTypeId() != Material.TNT.getId()) {				
					deferred = new DeferredBlockReset(dont.getX(), dont.getY(), dont.getZ(), dont.getTypeId(), dont.getData());
					if (dont.getTypeId() == Material.WOODEN_DOOR.getId() || dont.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
						doors.add(dont);
					}
				}
				if (deferred != null) {
					job.add(deferred);
				}
			}
			War.war.getServer().getScheduler().scheduleSyncDelayedTask(War.war, job);
			
			// Changed explosion yield following proportion of explosion prevention (makes drops less buggy too) 
			int explodedSize = explodedBlocks.size();
			float middleYeild = (float)(explodedSize - dontExplodeSize) / (float)explodedSize;
			float newYeild = middleYeild * event.getYield();
			
			event.setYield(newYeild);
		}
	}

	private List<ItemStack> copyItems(ItemStack[] contents) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack stack : contents) {
			if (stack != null) {
				if (stack.getData() != null) {
					list.add(new ItemStack(stack.getType(), stack.getAmount(), stack.getDurability(), stack.getData().getData()));
				} else {
					list.add(new ItemStack(stack.getType(), stack.getAmount(), stack.getDurability()));
				}
			}
		}
		return list;
	}

	/**
	 * Handles damage on Players
	 *
	 * @see EntityListener.onEntityDamage()
	 */
	@EventHandler
	public void onEntityDamage(final EntityDamageEvent event) {
		if (!War.war.isLoaded()) {
			return;
		}

		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;

		
		// prevent godmode
		if (Warzone.getZoneByPlayerName(player.getName()) != null) {
			event.setCancelled(false);
		}

		// pass pvp-damage
		if (event instanceof EntityDamageByEntityEvent) {
			if((Warzone.getZoneByPlayerName(player.getName()).getWarzoneConfig()
					.getBoolean(WarzoneConfig.HEALERS)) && (((Player)((EntityDamageByEntityEvent)
					event).getDamager()).getItemInHand().getType() == Material.GOLD_SWORD) &&
					(Team.getTeamByPlayerName(player.getName()).getPlayers().contains(((Player)
							((EntityDamageByEntityEvent)event).getDamager()).getName()))) {
				//a lot of complicated type-casting! It basically checks the healing status and the weapon
				//and the team, not very readable though!
				player.setHealth(player.getHealth() + 5); // + 2.5 hearts
				event.setCancelled(true);
				return;
			} else {
			try {
				this.handlerAttackDefend((EntityDamageByEntityEvent) event);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			}
		} else  {
			Warzone zone = Warzone.getZoneByPlayerName(player.getName());
			
			if (zone != null && event.getDamage() >= player.getHealth()) {
				if (zone.getReallyDeadFighters().contains(player.getName())) {
					// don't re-count the death points of an already dead person, make sure they are dead though
					// (reason for this is that onEntityDamage sometimes fires more than once for one death)
					if (player.getHealth() != 0) {
						player.setHealth(0);
					}
					/*Dinosaur*/
					
					
					return;
				}
				
				// Detect death, prevent it and respawn the player
				if (zone.getWarzoneConfig().getBoolean(WarzoneConfig.DEATHMESSAGES)) {
					String deathMessage = "";
					String cause = " died";
					if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK 
							|| event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.LIGHTNING) {
						cause = " burned to a crisp";
					} else if (event.getCause() == DamageCause.DROWNING) {
						cause = " drowned";
					} else if (event.getCause() == DamageCause.FALL) {
						cause = " fell to an untimely death";
					}
					deathMessage = Team.getTeamByPlayerName(player.getName()).getKind().getColor() + player.getName() + ChatColor.WHITE + cause;
					for (Team team : zone.getTeams()) {
						team.teamcast(deathMessage);
					}
				}
				
				zone.handleDeath(player);
				
				if (!zone.getWarzoneConfig().getBoolean(WarzoneConfig.REALDEATHS)) {
					// fast respawn, don't really die
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityCombust(final EntityDamageEvent event) {
		if (!War.war.isLoaded()) {
			return;
		}
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			Team team = Team.getTeamByPlayerName(player.getName());
			if(event.getCause() == DamageCause.PROJECTILE || event.getCause()
					== DamageCause.ENTITY_ATTACK) {
				player.setFireTicks(0);
			}
			if (team != null && team.getSpawnVolume().contains(player.getLocation())) {
				// smother out the fire that didn't burn out when you respawned
				// Stop fire (upcast, watch out!)
				if (player instanceof Player) {
					Entity playerEntity = ((Player) player);
					playerEntity.setFireTicks(0);
				}
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Prevents creatures from spawning in warzones if no creatures is active
	 *
	 * @see EntityListener.onCreatureSpawn()
	 */
	@EventHandler
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		if (!War.war.isLoaded()) {
			return;
		}

		Location location = event.getLocation();
		Warzone zone = Warzone.getZoneByLocation(location);
		if (zone != null && zone.getWarzoneConfig().getBoolean(WarzoneConfig.NOCREATURES)) {
			event.setCancelled(true);
		}
	}

	/**
	 * Prevents health regaining caused by peaceful mode
	 *
	 * @see EntityListener.onEntityRegainHealth()
	 */
	@EventHandler
	public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
		if (!War.war.isLoaded() || 
				(event.getRegainReason() != RegainReason.REGEN 
						&& event.getRegainReason() != RegainReason.EATING 
						&& event.getRegainReason() != RegainReason.SATIATED)) {
			return;
		}

		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}

		Player player = (Player) entity;
		Warzone zone = Warzone.getZoneByPlayerName(player.getName());
		if (zone != null) {
			Team team = Team.getTeamByPlayerName(player.getName());
			if ((event.getRegainReason() == RegainReason.EATING 
					|| event.getRegainReason() != RegainReason.SATIATED ) 
				&& team.getTeamConfig().resolveBoolean(TeamConfig.NOHUNGER)) {
				// noHunger setting means you can't auto-heal with full hunger bar (use saturation instead to control how fast you get hungry)
				event.setCancelled(true);
			} else if (event.getRegainReason() == RegainReason.REGEN) {
				// disable peaceful mode regen
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		if (!War.war.isLoaded() || !(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		Warzone zone = Warzone.getZoneByPlayerName(player.getName());
		Team team = Team.getTeamByPlayerName(player.getName());
		if (zone != null && team.getTeamConfig().resolveBoolean(TeamConfig.NOHUNGER)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDeath(final EntityDeathEvent event) {
		if (!War.war.isLoaded() || !(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		Warzone zone = Warzone.getZoneByPlayerName(player.getName());
		if (zone != null) {
			//ok now we need to update the deaths for each player
			PlayerStat personWhoDied = War.war.getPlayerStats(player.getDisplayName());
			personWhoDied.incDeaths();
			personWhoDied.zeroKillStreak();
			PlayerStat personWhoKilled = War.war.getPlayerStats(player.getKiller().getDisplayName());
			personWhoKilled.incKills();
			personWhoKilled.incKillStreak();
			personWhoKilled.incZoneKills();
			personWhoDied.incZoneDeaths();
			//see updated this stuff so ya :)
			
			//check for kill streaks
			if(personWhoKilled.getKillStreak() == 3) {
				ItemStack stack = player.getKiller().getItemInHand();
				if(stack.getType() == Material.WOOD_SWORD) {
					stack.addEnchantment(Enchantment.DAMAGE_ALL, 2);
					stack.setDurability((short) (stack.getDurability() + 100));
				} else if(stack.getType() == Material.STONE_SWORD) {
					stack.addEnchantment(Enchantment.DAMAGE_ALL, 2);
					stack.setDurability((short) (stack.getDurability() + 100));
				} else if(stack.getType() == Material.IRON_SWORD) {
					stack.addEnchantment(Enchantment.DAMAGE_ALL, 2);
					stack.setDurability((short) (stack.getDurability() + 100));
				} else if(stack.getType() == Material.DIAMOND_SWORD) {
					stack.addEnchantment(Enchantment.DAMAGE_ALL, 2);
					stack.setDurability((short) (stack.getDurability() + 100));
				} else if(stack.getType() == Material.BOW) {
					stack.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
					stack.setDurability((short) (stack.getDurability() + 100));
				} else {
					player.getKiller().setHealth(20);
					War.war.msg(player.getKiller(), "Good Job for achieving a 3 kill streak, you had no weapons so we decided to give you health");
				}
				War.war.msg(player.getKiller(), "Congrats on the 3 kill streak!");
			} else if(personWhoKilled.getKillStreak() == 5) {
				player.getKiller().getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
				War.war.msg(player.getKiller(), "Use this blaze rod to call in a CREEPER-STRIKE!!!!\n" +
						"And congrats on the 5 kill streak!");
				Warzone.getZoneByLocation(player.getKiller()).addKillStreakPerson(player.getKiller());
			} else if(personWhoKilled.getKillStreak() == 7) {
				War.war.msg(player.getKiller(), "Calling in the dogs! Also, Congrats on Sexy Seven!");
				Location dropPoint = Team.getTeamByPlayerName(player.getName()).getTeamSpawn();
				Wolf dog1 = (Wolf) dropPoint.getWorld().spawnCreature(dropPoint, CreatureType.WOLF);
				Wolf dog2 = (Wolf) dropPoint.getWorld().spawnCreature(dropPoint, CreatureType.WOLF);
				Wolf dog3 = (Wolf) dropPoint.getWorld().spawnCreature(dropPoint, CreatureType.WOLF);
				Wolf dog4 = (Wolf) dropPoint.getWorld().spawnCreature(dropPoint, CreatureType.WOLF);
				dog1.setAngry(true);
				dog2.setAngry(true);
				dog3.setAngry(true);
				dog4.setAngry(true);
				Wolf[] wolfs = new Wolf[4];
				wolfs[0] = dog1;
				wolfs[1] = dog2;
				wolfs[2] = dog3;
				wolfs[3] = dog4;
				giveDogNewTarget(Team.getTeamByPlayerName(player.getKiller().getName()), Warzone.getZoneByLocation(player), wolfs);
			}
			
			event.getDrops().clear();
			if (!zone.getWarzoneConfig().getBoolean(WarzoneConfig.REALDEATHS)) {
				// catch the odd death that gets away from us when usually intercepting and preventing deaths
				zone.handleDeath(player);
				
				if (zone.getWarzoneConfig().getBoolean(WarzoneConfig.DEATHMESSAGES)) {
					for (Team team : zone.getTeams()) {
						team.teamcast(player.getName() + " died");
					}
				}
			}
		}
	}
	
	@EventHandler
    public void onExplosionPrime(final ExplosionPrimeEvent event) {
		if (!War.war.isLoaded()) {
			return;
		}
		
		Location eventLocation = event.getEntity().getLocation();
		
		for (Warzone zone : War.war.getWarzones()) {
			if (zone.isBombBlock(eventLocation.getBlock())) {
				// prevent the Bomb from exploding on its pedestral
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onProjectileHitEvent(final ProjectileHitEvent event) {
		if(War.war.isLoaded() && event.getEntity() instanceof Snowball) {
			Snowball grenade = (Snowball) event.getEntity();
			Player player = (Player) grenade.getShooter();
			if(Warzone.getZoneByLocation(player) != null 
					&& Warzone.getZoneByLocation(player).getWarzoneConfig().getBoolean(WarzoneConfig.SNOWGRENADE)) {
			  grenade.setBounce(true); //we bounce
			  grenade.setVelocity(grenade.getVelocity().multiply(-1)); //Vector arithmetic, will reverse direction
              try {
				grenade.wait(1000); //wait for a second
			  } catch (InterruptedException e) {
				War.war.getLogger().log(Level.SEVERE, "War> Something is about to break");
				//being serious
			}  
            grenade.setVelocity(grenade.getVelocity().multiply(-1)); //Again reverse direction
            try {
				grenade.wait(4000); //wait for 4 seconds 
				//Grenade explosion time == 5 seconds.
			} catch (InterruptedException e) {
				War.war.getLogger().log(Level.SEVERE, "War> Something is about to break");
			}
            grenade.getLocation().getWorld().createExplosion(grenade.getLocation(), 20F);
			}
		} else {
			return;
		}
	}
	
	public static void giveDogNewTarget(Team senderTeam, Warzone zone, Wolf[] wolfs) {
		Team targetTeam = null;
		Iterator<Team> a = zone.getTeams().iterator();
		while(a.hasNext()) {
			targetTeam = a.next();
			if(targetTeam != senderTeam) {
				break;
			}
		}
		Player[] players = (Player[]) targetTeam.getPlayers().toArray();
		Random rand = new Random();
		int num = rand.nextInt(players.length - 1);
		wolfs[0].setTarget(players[num]);
		wolfs[1].setTarget(players[num]);
		wolfs[2].setTarget(players[num]);
		wolfs[3].setTarget(players[num]);
	}

}
