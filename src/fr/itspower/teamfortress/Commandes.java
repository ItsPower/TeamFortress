package fr.itspower.teamfortress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import fr.itspower.teamfortress.comp.CompKill;
import fr.itspower.teamfortress.types.Status;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.utils.Utils;

public class Commandes implements CommandExecutor {
	
	//private BukkitTask taskId;
	
	List<Entity> chickens = new ArrayList<>();
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("teamfortress")) {
        	
        	if (!(s instanceof Player)) {
                s.sendMessage(Main.getPrefix() + "Plugin non utilisable sur la console.");
                return true;
            }
        	
        	if (!s.isOp()) {
        		s.sendMessage(Main.getPrefix() + "Vous n'avez pas les permissions requises.");
        		return true;
        	}
        	
            Player p = (Player) s;
            
        	if (args.length == 0) {

        		p.sendMessage(Main.getPrefix() + "§e/tf setLobby §f- §7définis le point de téléportation du lobby.");
        		p.sendMessage(Main.getPrefix() + "§e/tf setSpawnPoint <R/B> §f- §7définis les points de spawn des équipes rouge et bleu.");
        		p.sendMessage(Main.getPrefix() + "§e/tf setSafeZone <R/B> §f- §7définis les safezone des équipes rouge et bleu.");
        		p.sendMessage(Main.getPrefix() + "§e/tf addRail <R/B> §f- §7Ajoute une position de rail. demander les détails à Its_Power.");
        		p.sendMessage(Main.getPrefix() + "§e/tf removeRail <R/B> §f- §7Supprime tous les rails.");
        		p.sendMessage(Main.getPrefix() + "§e/tf start §f- §7force le démarrage d'une partie.");
        		return true;
        		
        	} else if (args.length == 1) {
        		if (args[0].equalsIgnoreCase("setLobby")) {
        			setLobby(p.getLocation());
					p.sendMessage(Main.getPrefix() + "Position du lobby définie.");
        		}

        		if(args[0].equalsIgnoreCase("next")) {
        			TFPlayer tfp = Main.getGM().getTFPlayer(p);
        			tfp.setClasse(Main.getGM().getNextClasse(tfp));
        			  Utils.resetPlayer(p);
        			  
        			  tfp.giveStuff();
        		}
        		
        		if(args[0].equalsIgnoreCase("sort")) {
        			ArrayList<TFPlayer> oldlist = Main.getGM().getPlayers();
        			CompKill[] topkill = new CompKill[oldlist.size()];
        			for(int i=0; i<oldlist.size(); i++) topkill[i] = new CompKill(oldlist.get(i));
        			
        			Arrays.sort(topkill);
        			
        			
        			
        			/*for(CompKill tfp : topkill) {
        				//Bukkit.broadcastMessage(tfp.getPlayer().getName()+" "+tfp.getPlayer().getKills());
        			}*/
        		}
        		
        		if(args[0].equalsIgnoreCase("cap")) {
        			TFPlayer tfp = Main.getGM().getTFPlayer(p);
        	    	AbilityManager.setCooldown(tfp);
        		}

        		
        		if(args[0].equalsIgnoreCase("bloc")) {
        			p.sendBlockChange(p.getLocation(), Material.BARRIER, (byte) 0);
        			p.sendMessage("bloc placé");
        		}
        		
        		/*if (args[0].equalsIgnoreCase("w")) {
					p.sendMessage(Main.getPrefix() + "Wagon");
					Location l = p.getLocation();
					
					HashMap<Integer, Location> path = new HashMap<Integer, Location>();
					
					path.put(1, new Location(l.getWorld(), 0,0,0));
					
					final Minecart cart = (Minecart) l.getWorld().spawnEntity(l, EntityType.MINECARTEntityType.MINECART_CHEST);
		            cart.setMaxSpeed(0.1);
		            //cart.setVelocity(new Vector(10, 0, 0));
		            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		                int i = 1;
		                public void run() {
		                    if(p.isDead()) {
		                        Bukkit.getScheduler().cancelTask(taskId);
		                        return;
		                    }
		                    if(p.getLocation().distance(cart.getLocation())<5) {
		                    	if(p.getLocation().distance(path.get(i))<1) {
		                    		i = i + 1 ;
		                    	}
		                    	Location dirVect = Pathfinder.setLocationDirection(cart.getLocation(), p.getLocation());
		                    	cart.setVelocity(dirVect.toVector());
		                    }
		                }
		            }, 20, 10);
        		}*/
        		if (args[0].equalsIgnoreCase("tp")) {
        			Location l = p.getLocation();
        			l.setX(l.getBlockX()+0.5);
        			l.setZ(l.getBlockZ()+0.5);
        			p.teleport(l);
        		}
        		if(args[0].equalsIgnoreCase("chicken")) {
        			Location l = new Location(p.getWorld(), 
        					p.getLocation().getBlockX()+.5,
        					p.getLocation().getBlockY()+15,
        					p.getLocation().getBlockZ()+.5);
                    ArmorStand a = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
                    
                    a.setHelmet(new ItemStack(Material.CHEST));
                    a.setVisible(false);
                    a.setGravity(false);
                    
                    
                    
                    Location tg = new Location(p.getWorld(), 
                    		p.getLocation().getBlockX()+1,
                    		p.getLocation().getBlockY()+16,
                    		p.getLocation().getBlockZ());
                    ArmorStand b = (ArmorStand) l.getWorld().spawnEntity(tg, EntityType.ARMOR_STAND);

                    b.setVisible(false);
                    b.setGravity(false);
                    
                    for (int i = 0; i < 10; i++) {
            			Chicken chicken = (Chicken) l.getWorld().spawnEntity(l.clone().add(new Random().nextDouble()*2-1, 4, new Random().nextDouble()*2-1), EntityType.CHICKEN);
            			chickens.add(chicken);
            			chicken.setLeashHolder(b);
            		}
                    chickens.add(a);
        			
        			new BukkitRunnable() {
						@Override
						public void run() {
							l.add(0, -0.15, 0);
							tg.add(0, -0.15, 0);
							
							if(l.getBlock().getType().isSolid()) {
								for(Entity tg : chickens) {
									tg.remove();
								}
								l.add(0, 1, 0);
								l.getBlock().setType(Material.CHEST);
								a.remove();
								b.remove();
								cancel();
								return;
							}
							a.teleport(l);
							b.teleport(tg);
						}
        			}.runTaskTimer(Main.getInstance(), 0, 1);
        			
        		}

        		/*if (args[0].equalsIgnoreCase("a")) {
					Location l = p.getLocation();
					
					for(Entity en : l.getWorld().getEntities()) {
						if(en instanceof ArmorStand) en.remove();
					}
					
				    HashMap<Integer, Location> path = new HashMap<Integer, Location>();
				    FileConfiguration cfg = Main.getInstance().getConfig();
				    Set<String> ids = cfg.getConfigurationSection("tf.rails.r").getKeys(false);
				    int num = 1;
				    for(String tg : ids) {
				    	Location tmp = Utils.stringToLoc(cfg.getString("tf.rails.r."+tg));
				    	tmp.setX(tmp.getBlockX()+0.5);
				    	tmp.setY(tmp.getBlockY()+0.5);
				    	path.put(num, tmp);
				    	num = num + 1;
				    }
				    
					path.put(0, new Location(l.getWorld(), -9.5,69.35,266.5));
					path.put(1, new Location(l.getWorld(), -7.5,69.35,266.5));

					path.put(2, new Location(l.getWorld(), -7.5,69.35,269.5));
					path.put(3, new Location(l.getWorld(), -7.5,70.35,271.5));
					path.put(4, new Location(l.getWorld(), -7.5,70.35,273.5));
					path.put(5, new Location(l.getWorld(), -7.5,69.35,275.5));
					path.put(6, new Location(l.getWorld(), -7.5,69.35,283.5));
					path.put(7, new Location(l.getWorld(), -4.5,69.35,283.5));
					path.put(8, new Location(l.getWorld(), -2.5,69.35,281.5));
					path.put(9, new Location(l.getWorld(), -2.5,69.35,271.5));
					path.put(10, new Location(l.getWorld(), -1.5,69.35,271.5));
					
					path.put(11, new Location(l.getWorld(), 3.5,70.35,271.5));
					path.put(12, new Location(l.getWorld(), 3.5,69.35,263.5));
					path.put(13, new Location(l.getWorld(), -2.5,69.35,263.5));
					path.put(14, new Location(l.getWorld(), -2.5,69.35,254.5));
				    
					//Location from = path.get(0);
					//Location dir = Pathfinder.setLocationDirection(from, path.get(1));
					////Bukkit.broadcastMessage("Dir calculated: Yaw:"+dir.getYaw()+" pitch:"+dir.getPitch());
					  
					ArmorStand e = (ArmorStand) l.getWorld().spawnEntity(path.get(1), EntityType.ARMOR_STAND);
				    e.setCustomName("§9§l[]§f§l[]§c§l[]");
				    e.setCustomNameVisible(true);
				    e.setGravity(false);
				    e.setHelmet(new ItemStack(Material.LAPIS_ORE));
				    e.setVisible(false);
				    e.setSmall(true);
				    
				    taskId = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
						int id = 0;
						double temp = 0.0;
						@Override
						public void run() {
							////Bukkit.broadcastMessage("DistanceToNext: "+path.get(id).distanceSquared(e.getLocation()));
							if(e.getLocation().distance(p.getLocation()) < 4) {
								////Bukkit.broadcastMessage("Player in radius "+id+" "+temp+" "+e.getLocation().distance(p.getLocation()));
								temp = temp + 0.1;
								Location tmp2 = Ray.from(Pathfinder.setLocationDirection(path.get(id), path.get(id+1))).getPoint(temp).toLocation(l.getWorld());
								////Bukkit.broadcastMessage(""+e.getLocation().distance(path.get(id+1)));
								e.teleport(tmp2);
								//e.setHeadPose(directionToEuler(Pathfinder.setLocationDirection(p.getLocation(), e.getLocation())));
								
								
								
								
								
								Location tg = Pathfinder.setLocationDirection(path.get(id), path.get(id+1));
								int rot = (int) ((tg.getYaw() - 90) % 360);
						        if (rot < 0) {
						            rot += 360.0;
						        }
								//Bukkit.broadcastMessage("§c§l"+rot);
						        
						        double angle;
						        if(path.get(id).getY()==path.get(id+1).getY()) {
						        	angle = 0.0;
						        } else if(path.get(id).getY()>path.get(id+1).getY()) {
							        angle = -0.7;
					        	} else {
					        		angle = 0.7;
					        	}
						        EulerAngle ea = new EulerAngle(0,0,0);
						        if (rot == 0 || rot == -0) { // west
						        	ea = new EulerAngle(angle,4.7,0);
						        } else
						        if (rot == 90 || rot == -90) { // north
						        	ea = new EulerAngle(angle,0,0);
						        } else
						        if (rot == 180 || rot == -180) { // east
						        	ea = new EulerAngle(angle,1.5,0);
						        } else
							    if (rot == 270 || rot == -270) { // south
						            ea = new EulerAngle(angle,3.2,0);
						        }
						        e.setHeadPose(ea);
						        if(e.getLocation().distance(path.get(id+1)) < 0.05) {
							    	temp = 0.0;
							    	id = id + 1;
							    }
							    //e.setCustomName("r:"+rot+",dnxt:"+new BigDecimal(e.getLocation().distance(path.get(id+1))).setScale(1, RoundingMode.HALF_UP));
							}
							if(path.get(id+1)==null) {
								taskId.cancel();
								return;
							}
							double dist = e.getLocation().distance(path.get(id+1));
							if(dist < 0.05) {
								temp = 0.0;
								id = id + 1;
							}
						}
				    }, 0, 1);
        		}
        		*/
        		/*if (args[0].equalsIgnoreCase("b")) {
					Location l = p.getLocation();
					
					for(Entity en : l.getWorld().getEntities()) {
						if(en instanceof ArmorStand) en.remove();
					}
					
				    HashMap<Integer, Location> path = new HashMap<Integer, Location>();
				    FileConfiguration cfg = Main.getInstance().getConfig();
				    Set<String> ids = cfg.getConfigurationSection("tf.rails.r").getKeys(false);
				    int num = 1;
				    for(String tg : ids) {
				    	Location tmp = Utils.stringToLoc(cfg.getString("tf.rails.r."+tg));
				    	tmp.setX(tmp.getBlockX()+0.5);
				    	tmp.setY(tmp.getBlockY()+0.5);
				    	path.put(num, tmp);
				    	num = num + 1;
				    	//Bukkit.broadcastMessage("§a"+tmp.toString());
				    }
				    Location spawn = Pathfinder.setLocationDirection(path.get(1), path.get(2));
					ArmorStand e = (ArmorStand) l.getWorld().spawnEntity(spawn, EntityType.ARMOR_STAND);
					//ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.5f, 200, e.getLocation(), 32);
				    e.setCustomName("§fminecart");
				    e.setCustomNameVisible(true);
				    e.setGravity(false);
				    e.setHelmet(new ItemStack(Material.LAPIS_ORE));
				    e.setVisible(false);
				    e.setSmall(true);
				    
				    Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							e.teleport(spawn);
							int rot = (int) ((spawn.getYaw() - 90) % 360);
						    if (rot < 0) {
						        rot += 360.0;
						    }
						    EulerAngle ea = null;
						    if (rot == 0 || rot == -0) { // west
						        ea = new EulerAngle(0,4.7,0);
						    } else
						    if (rot == 90 || rot == -90) { // north
						        ea = new EulerAngle(0,0,0);
						    } else
						    if (rot == 180 || rot == -180) { // east
						        ea = new EulerAngle(0,1.5,0);
						    } else
							if (rot == 270 || rot == -270) { // south
						        ea = new EulerAngle(0,3.2,0);
						    }
							e.setHeadPose(ea);
							//Bukkit.broadcastMessage(rot+"");
						}
				    }, 2);
				    
				    
				    taskId = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
						int id = 1;
						double temp = 0.0;
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							////Bukkit.broadcastMessage("§d§lDistance: "+new BigDecimal(e.getLocation().distance(p.getLocation())).setScale(1, RoundingMode.HALF_UP));
							if(e.getLocation().distance(p.getLocation()) < 3.15) {
								
								if(id==path.size()) {
									anim(e.getLocation());
									taskId.cancel();
									return;
								}
								Location curr = path.get(id);
								Location next = path.get(id+1);
								temp = temp + 0.08;
								
								Location tmp2 = Ray.from(Pathfinder.setLocationDirection(curr, next)).getPoint(temp).toLocation(l.getWorld());
								e.teleport(tmp2);
						    	//Bukkit.broadcastMessage("§2"+tmp2.toString());
								
								Location tg = Pathfinder.setLocationDirection(curr, next);
							    Double dist = e.getLocation().distance(next);
							    
								int rot = (int) ((tg.getYaw() - 90) % 360);
							    if (rot < 0) {
							        rot += 360.0;
							    }
							    boolean isAngle = false;
							    double angle = 0;
							    
							    Block rail = tmp2.getBlock();
							    ////Bukkit.broadcastMessage(rail.getType().equals(Material.RAILS)+" "+(dist < 0.8) +" "+(rail.getData() > 5)); 
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
							    
							    //Bukkit.broadcastMessage("§7["+id+"] §4r:"+rot+" §cdnxt:"
							    +new BigDecimal(e.getLocation().distance(next)).setScale(1, RoundingMode.HALF_UP)
							    +" §6isA:"+isAngle+" §etmp:"+new BigDecimal(temp).setScale(1, RoundingMode.HALF_UP));
								
							    e.setHeadPose(ea);
							    //ParticleEffect.SMOKE_LARGE.display(0f, 0.2f, 0f, 0, 3, tmp2, 32);
							    
							    if(dist < 0.09) {
							    	temp = 0.0;
							    	id = id + 1;
							    }
							}
						}
				    }, 0, 1);
        		}
        		
        		*/
        		
        		/*
        		
        		if(args[0].equalsIgnoreCase("d")) {
        			Location l = p.getLocation();
				    HashMap<Integer, Location> path = new HashMap<Integer, Location>();
					for(Entity en : l.getWorld().getEntities()) {
						if(en instanceof Minecart) en.remove();
					}
        			path.put(1, new Location(l.getWorld(), -7.5,69.0625,266.5));
					path.put(2, new Location(l.getWorld(), -7.5,69.0625,283.5));
					path.put(3, new Location(l.getWorld(), -4.5,69.0625,283.5));
					path.put(4, new Location(l.getWorld(), -2.5,69.0625,281.5));
					path.put(5, new Location(l.getWorld(), -2.5,69.0625,271.5));
					path.put(6, new Location(l.getWorld(),-2.5,69.0625,271.5));
					
					Minecart en = (Minecart) l.getWorld().spawnEntity(p.getLocation(), EntityType.MINECART);
					en.setDisplayBlock(new MaterialData(Material.LAPIS_ORE));
				    en.setCustomNameVisible(false);
				    
					new BukkitRunnable() {
						int id = 1;
						@Override
						public void run() {
							int num = 0;
							for(Entity ent : en.getNearbyEntities(3.15, 3, 3.15)){
								if(ent instanceof Player){
									num++;
									//num += 9; // 1 player would be as 10 players for testing
								}
							}
							
							if(num != 0) {
								if(!p.isSneaking()) {
									//Maximum speed (3)
									Double vel = new BigDecimal((Math.abs(en.getVelocity().getX()) + Math.abs(en.getVelocity().getY()) + Math.abs(en.getVelocity().getZ()))).setScale(2, RoundingMode.HALF_UP).doubleValue();
									Bukkit.broadcastMessage("§cid:"+id+" §eVEL: "+vel+
											"§6   X:"+new BigDecimal(en.getVelocity().getX()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
											" Y:"+new BigDecimal(en.getVelocity().getY()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
											" Z:"+new BigDecimal(en.getVelocity().getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue());
									if(vel > 0.5) {//Bukkit.broadcastMessage(">0.5");
										return;
									}
									
									if(vel == 0.0) { //Bukkit.broadcastMessage("==0.0");
										Location loc = path.get(id);
										Location entityLoc = en.getLocation();
										double v_x = (1.0D) * (loc.getX() - entityLoc.getX()) / 1;
										if(v_x>0) {
											v_x = 0.02;
										} else {
											v_x = -0.02;
										}
									    double v_z = (1.0D) * (loc.getZ() - entityLoc.getZ()) / 1;
									    if(v_z>0) {
									    	v_z = 0.02;
										} else {
											v_z = -0.02;
										}
									    Vector v = en.getVelocity();
									    //Bukkit.broadcastMessage(v_x+" "+v_z+" "+v);
									    v.setX(v_x);
									    v.setY(0);
									    v.setZ(v_z);
									    en.setVelocity(v);
									}
									//Minimum speed
									else if(vel < 0.08) { //Bukkit.broadcastMessage("<0.08");
										double oldY = en.getVelocity().getY();
										en.setVelocity(en.getVelocity().multiply(0.08 / vel));
										en.setVelocity(en.getVelocity().setY(oldY));
									}
									if(path.get(id).distance(en.getLocation())<2) {
										id = id + 1;
										//Bukkit.broadcastMessage("WAYPOINT PASSED");
									}
								} else {
									//Maximum speed (3)
									Double vel = new BigDecimal((Math.abs(en.getVelocity().getX()) + Math.abs(en.getVelocity().getY()) + Math.abs(en.getVelocity().getZ()))).setScale(2, RoundingMode.HALF_UP).doubleValue();
									Bukkit.broadcastMessage("§cid:"+id+" §eVEL: "+vel+
											"§6   X:"+new BigDecimal(en.getVelocity().getX()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
											" Y:"+new BigDecimal(en.getVelocity().getY()).setScale(2, RoundingMode.HALF_UP).doubleValue()+
											" Z:"+new BigDecimal(en.getVelocity().getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue());
									if(vel > 0.5) { //Bukkit.broadcastMessage(">0.5");
										return;
									}
									
									if(vel == 0.0) { //Bukkit.broadcastMessage("==0.0");
										Location loc = path.get(id);
										Location entityLoc = en.getLocation();
										double v_x = (1.0D) * (loc.getX() - entityLoc.getX()) / 1;
										if(v_x>0) {
											v_x = 0.02;
										} else {
											v_x = -0.02;
										}
									    double v_z = (1.0D) * (loc.getZ() - entityLoc.getZ()) / 1;
									    if(v_z>0) {
									    	v_z = 0.02;
										} else {
											v_z = -0.02;
										}
									    Vector v = en.getVelocity();
									    //Bukkit.broadcastMessage(v_x+" "+v_z+" "+v);
									    v.setX(v_x);
									    v.setY(0);
									    v.setZ(v_z);
									    en.setVelocity(v);
									}
									//Minimum speed
									else if(vel < 0.08) { //Bukkit.broadcastMessage("<0.08");
										double oldY = en.getVelocity().getY();
										en.setVelocity(en.getVelocity().multiply(0.08 / vel));
										en.setVelocity(en.getVelocity().setY(oldY));
									}
									if(path.get(id).distance(en.getLocation())<2) {
										id = id + 1;
										//Bukkit.broadcastMessage("WAYPOINT PASSED");
									}
								}
							}
						}
					}.runTaskTimer(Main.getInstance(), 20, 1);
        		}
        		
        		
        		
        		
        		
        		
        		
        		if (args[0].equalsIgnoreCase("c")) {
					Location l = p.getLocation();
					
					for(Entity en : l.getWorld().getEntities()) {
						if(en instanceof Minecart) en.remove();
					}
					
				    HashMap<Integer, Location> path = new HashMap<Integer, Location>();
				    
					path.put(0, new Location(l.getWorld(), -9.5,69.35,266.5));
					path.put(1, new Location(l.getWorld(), -7.5,69.35,266.5));

					path.put(2, new Location(l.getWorld(), -7.5,69.35,269.5));
					path.put(3, new Location(l.getWorld(), -7.5,70.35,271.5));
					path.put(4, new Location(l.getWorld(), -7.5,70.35,273.5));
					path.put(5, new Location(l.getWorld(), -7.5,69.35,275.5));
					path.put(6, new Location(l.getWorld(), -7.5,69.35,283.5));
					path.put(7, new Location(l.getWorld(), -4.5,69.35,283.5));
					path.put(8, new Location(l.getWorld(), -2.5,69.35,281.5));
					path.put(9, new Location(l.getWorld(), -2.5,69.35,271.5));
					path.put(10, new Location(l.getWorld(), -1.5,69.35,271.5));
					
					path.put(11, new Location(l.getWorld(), 3.5,70.35,271.5));
					path.put(12, new Location(l.getWorld(), 3.5,69.35,263.5));
					path.put(13, new Location(l.getWorld(), -2.5,69.35,263.5));
					path.put(14, new Location(l.getWorld(), -2.5,69.35,254.5));
				    
					//Location from = path.get(0);
					//Location dir = Pathfinder.setLocationDirection(from, path.get(1));
					////Bukkit.broadcastMessage("Dir calculated: Yaw:"+dir.getYaw()+" pitch:"+dir.getPitch());
					  
					Minecart e = (Minecart) l.getWorld().spawnEntity(path.get(0), EntityType.MINECART);
					e.setDisplayBlock(new MaterialData(Material.LAPIS_ORE));
				    e.setCustomNameVisible(false);
				    
				    taskId = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
						int id = 0;
						@Override
						public void run() {
							if(e.getLocation().distance(p.getLocation()) < 5) {
								Location tg = Pathfinder.setLocationDirection(path.get(id), path.get(id+1));
						        double dist = e.getLocation().distance(path.get(id+1));
								int rot = (int) ((tg.getYaw() - 90) % 360);
						        if (rot < 0) {
						            rot += 360.0;
						        }
						        
						        boolean up = false;
						        boolean down = false;
						        double angle;
						        if(path.get(id).getY()==path.get(id+1).getY()) {
						        	angle = 0.0;
						        } else if(path.get(id).getY()>path.get(id+1).getY()) {
							        angle = -1;
							        down = true;
					        	} else {
					        		angle = 1;
					        		up = true;
					        	}
						        
						        if (rot == 0) { // west
							    	////Bukkit.broadcastMessage("§6WEST  " + dist);
						            e.teleport(e.getLocation().clone().add(-0.2, angle, 0));
						        } else
						        if (rot == 90) { // north
							    	////Bukkit.broadcastMessage("§6NORTH " + dist);
						            e.teleport(e.getLocation().clone().add(0, angle, -0.2));
						        } else
						        if (rot == 180) { // east
							    	////Bukkit.broadcastMessage("§6EST   " + dist);
						            e.teleport(e.getLocation().clone().add(0.2, angle, 0));
						        } else
							    if (rot == 270) { // south
							    	////Bukkit.broadcastMessage("§6SOUTH " + dist);
						            e.teleport(e.getLocation().clone().add(0, angle, 0.2));
						        }
						        Bukkit.broadcastMessage("§7["+id+"] §4r:"+rot+" §cdnxt:"
									    +new BigDecimal(dist).setScale(2, RoundingMode.HALF_UP)
									    +" up:"+up+" down:"+down);
								if(dist < 0.5) {
									id = id + 1;
								}
							}
						}
				    }, 0, 1);
        		}*/
        		
        		if(args[0].equalsIgnoreCase("start")  || args[0].equalsIgnoreCase("s")) {
        			p.sendMessage("§a§lPartie démarrée.");
        			if(Main.getGM().getStatus().equals(Status.WAITING) || Main.getGM().getStatus().equals(Status.STARTING)) {
	        			p.sendMessage("§c§lPartie démarrée.");
	        			Main.getGM().startGameTask();
        			}
        		}
        		
        		if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
        			p.chat("/plugman reload tf");
        		}
        		

        		if(args[0].equalsIgnoreCase("test")) {
        			//Bukkit.broadcastMessage(""+(Main.getGM()==null));
        			//Bukkit.broadcastMessage(""+Main.getGM().getLobby().toString());
        		}
        		
        	} else if (args.length == 2) {
        		if(args[0].equalsIgnoreCase("setSafeZone")) {
        			if(args[1].equalsIgnoreCase("R")) {
        				setRedPoint1(p.getLocation());
        				p.sendMessage(Main.getPrefix() + "Premier point de la zone ROUGE définis. 2eme placement dans 10s.");
        				Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
        					@Override
        					public void run() {
        						setRedPoint2(p.getLocation());
        						p.sendMessage(Main.getPrefix() + "deuxieme point de la zone ROUGE définis.");
        					}
        				}, 200);
        			} else if(args[1].equalsIgnoreCase("B")) {
        				setBluePoint1(p.getLocation());
        				p.sendMessage(Main.getPrefix() + "Premier point de la zone BLEU définis. 2eme placement dans 10s.");
        				Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
        					@Override
        					public void run() {
        						setBluePoint2(p.getLocation());
        						p.sendMessage(Main.getPrefix() + "deuxieme point de la zone BLEU définis.");
        					}
        				}, 200);
        			}
        		}
        		if(args[0].equalsIgnoreCase("multi")) {
        			CartManager.multi = Double.parseDouble(args[1]);
        			//Bukkit.broadcastMessage("multi "+CartManager.multi);
        		}
        		if (args[0].equalsIgnoreCase("setSpawnPoint")) {
        			if(args[1].equalsIgnoreCase("R")) {
        				setRedSpawn(p.getLocation());
        				p.sendMessage(Main.getPrefix() + "Point de respawn Rouge définis.");
        			} else if(args[1].equalsIgnoreCase("B")) {
        				setBlueSpawn(p.getLocation());
        				p.sendMessage(Main.getPrefix() + "Point de respawn Bleu définis.");
            		} else { 
                		p.sendMessage(Main.getPrefix() + "§e/tf setSpawnPoint <R/B> §f- §7définis les points de spawn des équipes rouge et bleu.");
            		}
        		}
        		if (args[0].equalsIgnoreCase("removeRails")) {
        			if(args[1].equalsIgnoreCase("R")) {
        				FileConfiguration cfg = Main.getInstance().getConfig();
        				cfg.set("tf.rails.r", null);
    					Main.getInstance().saveConfig();
        				p.sendMessage(Main.getPrefix() + "Rails rouge supprimés.");
        			} else
        			if(args[1].equalsIgnoreCase("B")) {
        				FileConfiguration cfg = Main.getInstance().getConfig();
        				cfg.set("tf.rails.b", null);
    					Main.getInstance().saveConfig();
        				p.sendMessage(Main.getPrefix() + "Rails bleu supprimés.");
        			}
        		}
        		if (args[0].equalsIgnoreCase("addRail")) {
        			if(args[1].equalsIgnoreCase("R")) {
        				FileConfiguration cfg = Main.getInstance().getConfig();
        				int nid;
    					Location l = p.getLocation(); l.setX(l.getBlockX()+0.5); l.setY(l.getBlockY()+0.0625); l.setZ(l.getBlockZ()+0.5); l.setPitch(0); l.setYaw(0);
        				if(cfg.contains("tf.rails.r")) {
        					Set<String> ids = cfg.getConfigurationSection("tf.rails.r").getKeys(false);
        					nid = ids.size()+1;
        					cfg.set("tf.rails.r."+nid, Utils.locToString(l));
        					Main.getInstance().saveConfig();
        				} else {
        					nid = 1;
        					cfg.set("tf.rails.r."+1, Utils.locToString(l));
        					Main.getInstance().saveConfig();
        				}
        				p.sendMessage(Main.getPrefix() + "Rails rouge ajouté. "+nid);
        			} else if(args[1].equalsIgnoreCase("B")) {
        				FileConfiguration cfg = Main.getInstance().getConfig();
    					Location l = p.getLocation(); l.setX(l.getBlockX()+0.5); l.setY(l.getBlockY()+0.0625); l.setZ(l.getBlockZ()+0.5); l.setPitch(0); l.setYaw(0);
        				int nid;
        				if(cfg.contains("tf.rails.b")) {
        					Set<String> ids = cfg.getConfigurationSection("tf.rails.b").getKeys(false);
        					nid = ids.size()+1;
        					cfg.set("tf.rails.b."+nid, Utils.locToString(l));
        					Main.getInstance().saveConfig();
        				} else {
            				nid = 1;
        					cfg.set("tf.rails.b."+1, Utils.locToString(l));
        					Main.getInstance().saveConfig();
        				}
        				p.sendMessage(Main.getPrefix() + "Rails bleu ajouté. "+nid);
        			} else { 
                		p.sendMessage(Main.getPrefix() + "§e/tf addRail <R/B> §f- §7Ajoute une position de rail. demander les détails à Its_Power.");
            		}
        		}
        	} else if (args.length == 3) {
        		
        	}
		}
		return true;
	}
	
	protected void setBluePoint2(Location location) {
		Main.getInstance().getConfig().set("tf.safezone.bleu.2", Utils.locToString(location));
	    Main.getInstance().saveConfig();
	}

	private void setBluePoint1(Location location) {
		Main.getInstance().getConfig().set("tf.safezone.bleu.1", Utils.locToString(location));
	    Main.getInstance().saveConfig();
	}

	protected void setRedPoint2(Location location) {
		Main.getInstance().getConfig().set("tf.safezone.rouge.2", Utils.locToString(location));
	    Main.getInstance().saveConfig();
	}

	private void setRedPoint1(Location location) {
		Main.getInstance().getConfig().set("tf.safezone.rouge.1", Utils.locToString(location));
	    Main.getInstance().saveConfig();
	}

	@SuppressWarnings("unused")
	private EulerAngle directionToEuler(Location dir) {
	    double xzLength = Math.sqrt(dir.getX()*dir.getX() + dir.getZ()*dir.getZ());
	    double pitch = Math.atan2(xzLength, dir.getY()) - Math.PI / 2;
	    double yaw = -Math.atan2(dir.getX(), dir.getZ()) + Math.PI / 4;
	    return new EulerAngle(pitch, yaw, 0);
	}
	
	private void setLobby(Location loc) {
		Main.getInstance().getConfig().set("tf.lobby", Utils.locToString(loc));
	    Main.getInstance().saveConfig();
	}
	
	private void setRedSpawn(Location loc) {
		Main.getInstance().getConfig().set("tf.respawn.rouge", Utils.locToString(loc));
	    Main.getInstance().saveConfig();
	}
	private void setBlueSpawn(Location loc) {
		Main.getInstance().getConfig().set("tf.respawn.bleu", Utils.locToString(loc));
	    Main.getInstance().saveConfig();
	}
	public Location lookTowardsLocation(Location from, Location to){
		if(from != null && to != null){
			Location loc = from.clone();
			double x = to.getX() - from.getX();
			double y = to.getY() - from.getY();
			double z = to.getZ() - from.getZ();
			if(x == 0 && z == 0){
				loc.setPitch(y > 0 ? -90 : 90);
				return loc;
			}
			loc.setYaw((float) Math.toDegrees((Math.atan2(-x, z) + (Math.PI * 2)) % (Math.PI * 2)));
			loc.setPitch((float) Math.toDegrees(Math.atan(-y / Math.sqrt((x * x) + (z * z)))));
			return loc;
		}
		return null;
	}
	
	public void anim(Location loc) {
		//ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
		//ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
		//ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
		loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				//ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
				//ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
				//ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
				loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						//ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
						//ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
						//ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
						loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
						Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
							@Override
							public void run() {
								//ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
								//ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
								//ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
								loc.getWorld().playSound(loc, Sound.EXPLODE, 1f, 1f);
								Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
									@Override
									public void run() {
										//ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0.3f, 100, loc, 32);
										//ParticleEffect.LAVA.display(0.7f, 0.0f, 0.7f, 0.1f, 60, loc, 32);
										//ParticleEffect.EXPLOSION_LARGE.display(0.3f, 0.3f, 0.3f, 0.0f, 15, loc, 32);
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
}
