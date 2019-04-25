package fr.itspower.teamfortress;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import fr.itspower.teamfortress.others.ParticleEffect;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.utils.Utils;
import fr.itspower.teamfortress.weapon.GunManager;

public class CartManager {
	
	private static BukkitTask task;
	private static Minecart Be;
	private static Minecart Re;
	public static HashMap<Integer, Location> Bpath;
	public static HashMap<Integer, Location> Rpath;
	private static World w;

	public static int RED_PERC;
	public static int BLUE_PERC;
	private static int REDrails;
	private static int BLUErails;
	private static HashMap<Integer, Integer> Rdists;
	private static HashMap<Integer, Integer> Rtot;
	private static HashMap<Integer, Integer> Bdists;
	private static HashMap<Integer, Integer> Btot;

	public static Location Rend;
	public static Location Bend;
	
	public static void init() {
		RED_PERC = 0;
		BLUE_PERC = 0;
		REDrails = 0;
		BLUErails = 0;
		Bpath = new HashMap<Integer, Location>();
		Rpath = new HashMap<Integer, Location>();
		Rdists = new HashMap<Integer, Integer>();
		Rtot = new HashMap<Integer, Integer>();
		Bdists = new HashMap<Integer, Integer>();
		Btot = new HashMap<Integer, Integer>();
		FileConfiguration cfg = Main.getInstance().getConfig();
		Set<String> id1 = cfg.getConfigurationSection("tf.rails.b").getKeys(false);
		for(String id : id1) {
			Location temp = Utils.stringToLoc(cfg.getString("tf.rails.b."+id));
			Bpath.put(Integer.parseInt(id), temp);
		}
		Set<String> id2 = cfg.getConfigurationSection("tf.rails.r").getKeys(false);
		for(String id : id2) {
			Location temp = Utils.stringToLoc(cfg.getString("tf.rails.r."+id));
			Rpath.put(Integer.parseInt(id), temp);
		}
		for(int i=1 ; i<1000;i++) {
			if(i==1) {
				REDrails = 0;
				Rdists.put(i, 0);
				Rtot.put(i, REDrails);
			} else {
				Integer dist = (int) Rpath.get(i-1).distance(Rpath.get(i));
				REDrails = REDrails+dist;
				Rdists.put(i, dist);
				Rtot.put(i, REDrails);
			}
			//Bukkit.broadcastMessage("§cRdists: "+i+" Rdists:"+Rdists.get(i)+" Rtot:"+Rtot.get(i));
			
			if(i==Rpath.size()) break;
		}
		
		for(int i=1 ; i<1000;i++) {
			if(i==1) {
				BLUErails = 0;
				Bdists.put(i, 0);
				Btot.put(i, BLUErails);
			} else {
				Integer dist2 = (int) Bpath.get(i-1).distance(Bpath.get(i));
				BLUErails = BLUErails+dist2;
				Bdists.put(i, dist2);
				Btot.put(i, BLUErails);
			}
			//Bukkit.broadcastMessage("§3Bdists: "+i+" Bdists:"+Bdists.get(i)+" Btot:"+Btot.get(i));
			
			if(i==Bpath.size()) break;
		}
		//Bukkit.broadcastMessage("§cREDrails: "+REDrails);
		
		Location Bspawn = Bpath.get(1);
		Location Rspawn = Rpath.get(1);
		Bend = Bpath.get(Bpath.size());
		Rend = Rpath.get(Rpath.size());
		w = Rspawn.getWorld();
		
		for(Entity en : Bspawn.getWorld().getEntities()) {
			if(en instanceof ArmorStand || en instanceof Minecart) 
				en.remove();
		}
	}
	public static void run() {
		Location Bspawn = Bpath.get(1);
		Location Rspawn = Rpath.get(1);
		task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				Be = (Minecart) w.spawnEntity(Bspawn, EntityType.MINECART);
				Be.setDisplayBlock(new MaterialData(Material.LAPIS_ORE));
			    Be.setCustomNameVisible(false);
			   
				Re = (Minecart) w.spawnEntity(Rspawn, EntityType.MINECART);
				Re.setDisplayBlock(new MaterialData(Material.LAPIS_ORE));
				Re.setCustomNameVisible(false);
			}
			
		}, 60);
		task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			Integer Rid = 1;
			Integer Bid = 1;
			int Rplayers;
			int Bplayers;

			private boolean hasPlayedRSound80 = false;
			private boolean hasPlayedBSound80 = false;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(Re.getLocation().distance(Rend) < 1) {
					anim(Re.getLocation());
					RED_PERC = 100;
					for(TFPlayer tfp : Main.getGM().getPlayers()) {
						if(tfp.getTeam().equals(Team.ROUGE)) {
							tfp.getPlayer().playSound(tfp.getPlayer().getPlayer().getLocation(), "jeu.victoire", 1.0f, 1.0f);
						} else {
							tfp.getPlayer().playSound(tfp.getPlayer().getPlayer().getLocation(), "jeu.defaite", 1.0f, 1.0f);
						}
					}
					Main.getGM().winGame(Team.ROUGE);
					CartManager.stop();
					//Bukkit.broadcastMessage("§aEND");
					return;
				} else
				if(Be.getLocation().distance(Bend) < 1) {
					anim(Be.getLocation());
					BLUE_PERC = 100;
					for(TFPlayer tfp : Main.getGM().getPlayers()) {
						if(tfp.getTeam().equals(Team.BLEU)) {
							tfp.getPlayer().playSound(tfp.getPlayer().getPlayer().getLocation(), "jeu.victoire", 1.0f, 1.0f);
						} else {
							tfp.getPlayer().playSound(tfp.getPlayer().getPlayer().getLocation(), "jeu.defaite", 1.0f, 1.0f);
						}
					}
					Main.getGM().winGame(Team.BLEU);
					CartManager.stop();
					//Bukkit.broadcastMessage("§aEND");
					return;
				}
				
				Rplayers = 0;
				Bplayers = 0;
				for(TFPlayer tfp : Main.getGM().getPlayers()) {
					if(!Main.getGM().isOnline(tfp.getName())) continue;
					Location l = tfp.getPlayer().getLocation();
					if(l.distance(Be.getLocation()) < 3.5) {
						if(tfp.getTeam().equals(Team.BLEU)) {
							Bplayers = Bplayers + tfp.getClasse().getMinecartValue();
							tfp.addPushTimer();
							
						} else {
							Bplayers = Bplayers - tfp.getClasse().getMinecartValue();
						}
						if(GunManager.timeout.contains(tfp.getName()))
							continue;
						Location temp = Be.getLocation();
						Location point1 = tfp.getPlayer().getLocation();
					    double distance = point1.distance(temp);
					    Vector p1 = point1.toVector();
					    Vector p2 = temp.toVector();
					    Vector vector = p2.clone().subtract(p1).normalize().multiply(0.3);
					    double length = 0;
					    for (; length < distance; p1.add(vector)) {
					    	ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(tfp.getTeam().equals(Team.BLEU) ? Color.AQUA:Color.RED), p1.toLocation(Main.w).add(0, 1, 0), 100);
					        length += 0.5;
					    }
					} else if(l.distance(Re.getLocation())<3.5) {
						if(tfp.getTeam().equals(Team.ROUGE)) {
							Rplayers = Rplayers + tfp.getClasse().getMinecartValue();
							tfp.addPushTimer();
						} else {
							Rplayers = Rplayers - tfp.getClasse().getMinecartValue();
						}
						Location temp = Re.getLocation();
						Location point1 = tfp.getPlayer().getLocation();
					    double distance = point1.distance(temp);
					    Vector p1 = point1.toVector();
					    Vector p2 = temp.toVector();
					    Vector vector = p2.clone().subtract(p1).normalize().multiply(0.3);
					    double length = 0;
					    for (; length < distance; p1.add(vector)) {
					    	ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(tfp.getTeam().equals(Team.BLEU) ? Color.AQUA:Color.RED), p1.toLocation(Main.w).add(0, 1, 0), 100);
					        length += 0.5;
					    }
					}
				}

				if(Rplayers>5) Rplayers = 5;
				if(Bplayers>5) Bplayers = 5;
				
				
				/*
				 *       LE MINECART AVANCE PR LES ROUGES
				 */
				Location nextR = Rpath.get(Rid+1);
				Location entityLoc = Re.getLocation();
				double distR = entityLoc.distance(nextR);
				
				if(Rplayers > 0) {
				    //Vector v = Re.getVelocity();
					/*Bukkit.broadcastMessage("§aid:"+Rid+" "+new BigDecimal(nextR.distance(Re.getLocation())).setScale(1, RoundingMode.HALF_UP).doubleValue()+
							"§c X:"+new BigDecimal(v.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Y:"+new BigDecimal(v.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Z:"+new BigDecimal(v.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							"§c "+Rplayers+" §3"+Bplayers);*/
				    Re.setVelocity(getVelocityDir(entityLoc,nextR,Rplayers));
				    /*Bukkit.broadcastMessage(Re.getLocation().getBlock().getData()+" "+
				    		(Re.getLocation().getBlockZ()>nextR.getBlockZ())+" "+
				    		(Re.getLocation().getBlockZ()<nextR.getBlockZ())+" "+
				    		(Re.getLocation().getBlockX()>nextR.getBlockX())+" "+
				    		(Re.getLocation().getBlockX()<nextR.getBlockX()) );*/
					
				    if(Rplayers == 1) {
						byte data = Re.getLocation().getBlock().getData();
						if(data!=0) {
							if(		   (data==(byte)4 && Re.getLocation().getBlockZ()>nextR.getBlockZ())
									|| (data==(byte)5 && Re.getLocation().getBlockZ()<nextR.getBlockZ())
									|| (data==(byte)3 && Re.getLocation().getBlockX()>nextR.getBlockX())
									|| (data==(byte)2 && Re.getLocation().getBlockX()<nextR.getBlockX())) {
								//Bukkit.broadcastMessage("§cSTOP");
								Re.setVelocity(new Vector(0,0,0));
								Re.teleport(Re.getLocation());
							}
						}
					}
				    ParticleEffect.SMOKE_LARGE.display(0f, 0.2f, 0f, 0, 3, Re.getLocation(), 32);
				    distR = entityLoc.distance(nextR); //0 12 6.0 6%
				    
				    ////Bukkit.broadcastMessage("§cRID:"+Rid+" §2"+Rtot.get(Rid)+"§a+"+Rdists.get(Rid+1)+"§b-"+dist+"§2*100/"+REDrails+"  §c"+RED_PERC);
				    
				    
				    	/*if(Rid==Rpath.size()) {
							anim(Re.getLocation());
							//Bukkit.broadcastMessage("§cRED WINS");
							RED_PERC = 100;
							task.cancel();
							return;
						}*/
				    
				
				}
				RED_PERC = (int) (((Rtot.get(Rid) + (Rdists.get(Rid+1) - distR))) * 100) / REDrails;
				
				if(distR < 1) {
				    	Rid = Rid + 1;
				}
				/*
				 *       LE MINECART RECULE PR LES ROUGES
				 */
				/* else if(Rplayers<0) {
					
					Location curr = Rpath.get(Rid);
					Location next = Rpath.get(Rid+1);
				    Location entityLoc = Re.getLocation();
				    Vector v = Re.getVelocity();
					//Bukkit.broadcastMessage("§aid:"+Rid+" "+new BigDecimal(curr.distance(Re.getLocation())).setScale(1, RoundingMode.HALF_UP).doubleValue()+
							"§c X:"+new BigDecimal(v.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Y:"+new BigDecimal(v.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Z:"+new BigDecimal(v.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							"§c "+Rplayers+" §3"+Bplayers);
				    Re.setVelocity(getVelocityDir(entityLoc,curr,Math.abs(Rplayers)));
				    ParticleEffect.CLOUD.display(0.4f, 0.3f, 0.4f, 0.05f, 5, Re.getLocation(), 32);
				    
				    double dist = entityLoc.distance(next); //0 12 6.0 6%
				    RED_PERC = (int) (((Rtot.get(Rid) + (Rdists.get(Rid+1) - dist))) * 100) / REDrails;
				    ////Bukkit.broadcastMessage("§cRID:"+Rid+" §2"+Rtot.get(Rid)+"§a+"+Rdists.get(Rid+1)+"§b-"+dist+"§2*100/"+REDrails+"  §c"+RED_PERC);
				    
				    if(entityLoc.distance(curr) < 1) {
				    	if(Rid == 1) {
							Re.setVelocity(new Vector(0,0,0));
				    	} else {
				    		Rid = Rid - 1;
				    	}
				    }
				} else {
					Re.setVelocity(new Vector(0,0,0));
				}
				*/
				
				
				/*
				 *       LE MINECART AVANCE PR LES BLEUS
				 */
				Location nextB = Bpath.get(Bid+1);
				Location entityLocB = Be.getLocation();
				double distB = entityLocB.distance(nextB);
				if(Bplayers>0) {
				    //Vector v = Be.getVelocity();
				    Be.setVelocity(getVelocityDir(entityLocB,nextB,Bplayers));
					/*Bukkit.broadcastMessage("§aid:"+Rid+" "+new BigDecimal(nextB.distance(Re.getLocation())).setScale(1, RoundingMode.HALF_UP).doubleValue()+
							"§c X:"+new BigDecimal(v.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Y:"+new BigDecimal(v.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Z:"+new BigDecimal(v.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							"§c "+Rplayers+" §3"+Bplayers);*/
				    if(Bplayers == 1) {
						byte data = Be.getLocation().getBlock().getData();
						if(data!=0) {
							if(		   (data==(byte)4 && Be.getLocation().getBlockZ()>nextB.getBlockZ())
									|| (data==(byte)5 && Be.getLocation().getBlockZ()<nextB.getBlockZ())
									|| (data==(byte)3 && Be.getLocation().getBlockX()>nextB.getBlockX())
									|| (data==(byte)2 && Be.getLocation().getBlockX()<nextB.getBlockX())) {
								//Bukkit.broadcastMessage("§3STOP");
								Be.setVelocity(new Vector(0,0,0));
								Be.teleport(Be.getLocation());
							}
						}
					}
				    ParticleEffect.SMOKE_LARGE.display(0f, 0.2f, 0f, 0, 3, Be.getLocation(), 32);
				    distB = entityLocB.distance(nextB); //0 12 6.0 6%
				    ////Bukkit.broadcastMessage("§3BID:"+Bid+" §2"+Btot.get(Bid)+"§a+"+Bdists.get(Bid+1)+"§b-"+dist+"§2*100/"+BLUErails+"  §c"+BLUE_PERC);
				    
				    /*
				    	if(Bid==Bpath.size()) {
							anim(Be.getLocation());
							//Bukkit.broadcastMessage("§3BLUE WINS");
							BLUE_PERC = 100;
							task.cancel();
							return;
						}
				    }*/
				    
				/*
				 *       LE MINECART RECULE PR LES BLEUS
				 */
				}
				
				BLUE_PERC = (int) (((Btot.get(Bid) + (Bdists.get(Bid+1) - distB))) * 100) / BLUErails;
				if(distB < 1) {
				    Bid = Bid + 1;
				}
				
				/* else if(Bplayers<0) {
					
					Location curr = Bpath.get(Bid);
					Location next = Bpath.get(Bid+1);
				    Location entityLoc = Be.getLocation();
				    Vector v = Be.getVelocity();
					//Bukkit.broadcastMessage("§aid:"+Rid+" "+new BigDecimal(curr.distance(Re.getLocation())).setScale(1, RoundingMode.HALF_UP).doubleValue()+
							"§c X:"+new BigDecimal(v.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Y:"+new BigDecimal(v.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							" Z:"+new BigDecimal(v.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
							"§c "+Rplayers+" §3"+Bplayers);
				    Be.setVelocity(getVelocityDir(entityLoc,curr,Math.abs(Bplayers)));
				    ParticleEffect.CLOUD.display(0.4f, 0.3f, 0.4f, 0.05f, 5, Be.getLocation(), 32);
				    
				    double dist = entityLoc.distance(next); //0 12 6.0 6%
				    BLUE_PERC = (int) (((Btot.get(Bid) + (Bdists.get(Bid+1) - dist))) * 100) / BLUErails;
				    ////Bukkit.broadcastMessage("§3BID:"+Rid+" §2"+Btot.get(Bid)+"§a+"+Bdists.get(Bid+1)+"§b-"+dist+"§2*100/"+BLUErails+"  §c"+BLUE_PERC);
				    
				    if(entityLoc.distance(curr) < 1) {
				    	if(Bid == 1) {
							Be.setVelocity(new Vector(0,0,0));
				    	} else {
				    		Bid = Bid - 1;
				    	}
				    }
				} else {
					Be.setVelocity(new Vector(0,0,0));
				}*/
				
				
				////Bukkit.broadcastMessage("§cPUSH: §c"+Rplayers+" §3"+Bplayers+" ");
				
				if(!hasPlayedRSound80) {
					if(RED_PERC>75 && RED_PERC<85) {
						hasPlayedRSound80 = true;
						for(Player p : Bukkit.getOnlinePlayers())
							Utils.playSound(p.getLocation(), "minecart.galerte");
					}
				}
				if(!hasPlayedBSound80) {
					if(BLUE_PERC>75 && BLUE_PERC<85) {
						hasPlayedBSound80 = true;
						for(Player p : Bukkit.getOnlinePlayers())
							Utils.playSound(p.getLocation(), "minecart.alerte");
					}
				}
				
			}

			protected Vector getVelocityDir(Location entityLoc, Location to, int addition) {
				Vector v = new Vector();
				double v_x = (1.0D) * (to.getX() - entityLoc.getX()) / 1;
				double base = 0.0;
				//double parjoueur = 0.11;
				if(v_x>0) {
					v_x = base + (multi*addition);
				} else {
					v_x = -base - (multi*addition);
				}
			    double v_z = (1.0D) * (to.getZ() - entityLoc.getZ()) / 1;
			    if(v_z>0) {
			    	v_z = base + (multi*addition);
				} else {
					v_z = -base - (multi*addition);
				}
			    v.setX(v_x);
			    v.setY(0);
			    v.setZ(v_z);
			    return v;
			}
			
		}, 80, 2);
	}
	
	public static double multi = 0.075;
	
	public static void stop() {
		Main.isEnabled = false;
		task.cancel();
	}
	
	public static enum CartState {
		FORWARD, STOPPED, BACKWARD
	}
	
	public static void anim(Location loc) {
		ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
		ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
		ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
		loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
				ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
				ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
				loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
						ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
						ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
						loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
						Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
							@Override
							public void run() {
								ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
								ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
								ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
								loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
								Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
									@Override
									public void run() {
										ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
										ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
										ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
										loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
									}
							    }, 10);
							}
					    }, 10);
					}
			    }, 10);
			}
	    }, 10);
	}
	
	
	/*
	@SuppressWarnings({ "deprecation", "unused" })
	protected EulerAngle getMinecartDir(Location curr, Location next, Location tmp2, Double dist) {
		Location tg = Pathfinder.setLocationDirection(curr, next);
		Block rail = tmp2.getBlock();
		int rot = (int) ((tg.getYaw() - 90) % 360);
		if (rot < 0) {
		    rot += 360.0;
		}
		boolean isAngle = false;
		double angle = 0;
		if(rail.getType().equals(Material.RAILS) && dist < 0.8 && rail.getData() > 5) {
	    	isAngle = true;
	    }
		
	    if(curr.getY()==next.getY()) {
	    	angle = 0.0;
	    } else if(curr.getY()>next.getY()) {
		    angle = -0.7;
		    ////Bukkit.broadcastMessage("montée");
		    //if(isAngle) angle = angle*0.5;
	    } else {
        	angle = 0.7;
		    ////Bukkit.broadcastMessage("descente");
		    //if(isAngle) angle = angle*0.5;
        }
	    EulerAngle ea = null;
	    if (rot == 0 || rot == -0) { // west
	        ea = new EulerAngle(angle,4.7,0);
	        if(isAngle && rail.getData() == 9) {
	        	ea = new EulerAngle(angle,5.4,0);
	        } else if(isAngle && rail.getData() == 6) {
	        	ea = new EulerAngle(angle,4.0,0);
	        }
	    } else
	    if (rot == 90 || rot == -90) { // north
	        ea = new EulerAngle(angle,0,0);
	        if(isAngle && rail.getData() == 6) {
	        	ea = new EulerAngle(angle,0.9,0);
	        } else if(isAngle && rail.getData() == 7) {
	        	ea = new EulerAngle(angle,-0.9,0);
	        }
	    } else
	    if (rot == 180 || rot == -180) { // east
	        ea = new EulerAngle(angle,1.5,0);
	        if(isAngle && rail.getData() == 8) {
	        	ea = new EulerAngle(angle,0.7,0);
	        } else if(isAngle && rail.getData() == 7) {
	        	ea = new EulerAngle(angle,2.2,0);
	        }
	    } else
		if (rot == 270 || rot == -270) { // south
	        ea = new EulerAngle(angle,3.2,0);
	        if(isAngle && rail.getData() == 9) {
	        	ea = new EulerAngle(angle,2.7,0);
	        } else if(isAngle && rail.getData() == 8) {
	        	ea = new EulerAngle(angle,3.7,0);
	        }
	    }
		return ea;
	}
	*/
}
