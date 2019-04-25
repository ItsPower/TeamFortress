package fr.itspower.teamfortress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import fr.itspower.teamfortress.AbilityManager.AbilityState;
import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.Cuboid;
import fr.itspower.teamfortress.types.Status;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.TFTurret;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.types.msg;
import fr.itspower.teamfortress.types.Cuboid.CuboidIterator;
import fr.itspower.teamfortress.utils.ItemBuilder;
import fr.itspower.teamfortress.utils.TFCooldown;
import fr.itspower.teamfortress.utils.Title;
import fr.itspower.teamfortress.utils.Utils;
import fr.itspower.teamfortress.weapon.Gun;
import fr.itspower.teamfortress.weapon.GunListener;
import fr.itspower.teamfortress.weapon.GunManager;
import fr.itspower.teamfortress.weapon.WeaponType;

public class Events implements Listener {


	public static void init() {
		jumpcd = new HashMap<String, Long>();
	}

	@EventHandler
	public void onCollide(VehicleEntityCollisionEvent e) {
		Vehicle v = e.getVehicle();
		if(v instanceof Minecart) {
			e.setCollisionCancelled(true);
		}
	}
	/*
	@EventHandler
	public void onPreJoin(PlayerLoginEvent e) {
		if(Main.getGM().getStatus().equals(Status.INGAME) && !Main.getGM().offlines.contains(e.getPlayer().getName())) {
			e.setKickMessage("Une partie de TeamFortress est déjà en cours...");
			e.setResult(Result.KICK_OTHER);
		}
	}
	 */
	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		if(e.getItem().getType().equals(Material.GOLDEN_APPLE)) {
			TFPlayer kp = Main.getGM().getTFPlayer(e.getPlayer());
			if(kp.getClasse().equals(Classe.Heavy)) {
				AbilityManager.executeAbility(kp);
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage("");
		Player p = e.getPlayer();
		p.setAllowFlight(true);
		if(Main.getGM().getStatus().equals(Status.STARTING) || Main.getGM().getStatus().equals(Status.WAITING)) {
			Utils.resetPlayer(p);
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
			p.getInventory().setLeggings(new ItemStack(Material.AIR));
			p.getInventory().setBoots(new ItemStack(Material.AIR));
			p.getInventory().setChestplate(new ItemStack(Material.AIR));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+p.getName()+" prefix &7");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+p.getName()+" suffix &f");
			p.teleport(Main.getGM().getLobby());
			Utils.giveLobbyItems(p);
			Main.getGM().addPlayer(p);
			p.setFlying(false);
			p.setAllowFlight(false);

		} else if(Main.getGM().getStatus().equals(Status.INGAME)) {
			TFPlayer kp = Main.getGM().getTFPlayer(p);
			if(kp != null) {
				Main.getGM().addBoard(kp);
				kp.giveStuff();
				kp.tpSpawn();
			} else {
				kp = new TFPlayer(p.getName());
				Utils.resetPlayer(kp.getPlayer());
				p.setFlying(false);
				p.setAllowFlight(false);
				Main.getGM().addBoard(kp);
				Main.getTM().forceTeam(kp);
				kp.tpSpawn();
				kp.getPlayer().playSound(kp.getPlayer().getLocation(), "jeu.fight", 1.0f, 1.0f);
				kp.getPlayer().getInventory().setHelmet(kp.getClasse().getItem().build());
				kp.getPlayer().getInventory().setChestplate(new ItemBuilder(kp.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_CHESTPLATE:Material.IRON_CHESTPLATE, 1, (short) 0).unbreakable().hideA().hideU().build());
				kp.getPlayer().getInventory().setLeggings(new ItemBuilder(kp.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_LEGGINGS:Material.IRON_LEGGINGS, 1, (short) 0).unbreakable().hideA().hideU().build());
				kp.getPlayer().getInventory().setBoots(new ItemBuilder(kp.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_BOOTS:Material.IRON_BOOTS, 1, (short) 0).unbreakable().hideA().hideU().build());
				kp.giveStuff();
				GameManager.players.add(kp);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(Main.getGM().getStatus().equals(Status.INGAME)) {
			TFPlayer kp = Main.getGM().getTFPlayer(p);
			Main.getGM().offlines.add(p.getName());
			if(Turrets.hasTFTurret(p)) {
				ArmorStand a = Turrets.getTFTurret(p);
				TFTurret.remove(a);
			}
			if(Main.getGM().getMinesOf(kp) != null) {
				List<Location> tg = Main.getGM().getMinesOf(kp);
				for(Location l : tg) {
					//Bukkit.broadcastMessage(l.toString());
					l.getBlock().setType(Material.AIR);
				}
				Main.getGM().getMinesOf(kp).clear();
			}
			if(Main.getGM().getTrampoLoc(p.getName()) != null) {
				Main.getGM().removeTrampo(kp, Main.getGM().getTrampoLoc(p.getName()));
			}
			e.setQuitMessage(Main.getPrefix()+kp.getTeam()+" "+e.getPlayer().getName()+" §7s'est déconnecté.");

			Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
				@Override
				public void run() {
					if(Bukkit.getOnlinePlayers().size() == 0) {
						Main.getGM().endAll();
					}
				}
			}, 5);
			/*
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
				@Override
				public void run() {
					if(!p.isOnline()) {
						Main.getGM().removePlayer(p);
					} else {
						Main.getGM().offlines.remove(p.getName());
					}
				}
			}, 6000);*/
		} else {
			Main.getGM().removePlayer(p);
		}
	}
	@EventHandler
	public void onQuit(PlayerInteractAtEntityEvent e) {
		if(e.getRightClicked() instanceof Minecart) {
			e.setCancelled(true);
		}
	}

	/*
	@EventHandler
	public void tg(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
        Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
        double tg = 18;
        double multi = 0.4d;

        Ray r = Ray.from(p);

        for (int i = 0; i < tg; i++) {
        	Location l = /*p.getEyeLocation()  r.getPoint(0.3).toLocation(Main.w).add(x1.clone().multiply(multi * Math.sin((double)i / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)i / tg * Math.PI * 2d)));
        	//ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.14f, l, 100);
        }
        for (int i = 0; i < tg; i++) {
        	Location l = /*p.getEyeLocation() r.getPoint(1.3).toLocation(Main.w).add(x1.clone().multiply(multi * Math.sin((double)i / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)i / tg * Math.PI * 2d)));
        	//ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.14f, l, 100);
        }
        for (int i = 0; i < tg; i++) {
        	Location l = /*p.getEyeLocation() r.getPoint(2.3).toLocation(Main.w).add(x1.clone().multiply(multi * Math.sin((double)i / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)i / tg * Math.PI * 2d)));
        	//ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.14f, l, 100);
        }
        Map<AABB, LivingEntity> possibles = new HashMap<AABB, LivingEntity>();
	    for(LivingEntity en : p.getLocation().getWorld().getLivingEntities()) {
	    	if(en.getType().equals(EntityType.PIG_ZOMBIE) || en.getType().equals(EntityType.PIG) || (en.getType().equals(EntityType.PLAYER) && en.getName()!=p.getName())) {
	    		if(en.getLocation().distance(p.getLocation()) < 100) {
	    			possibles.put(AABB.from(en, 0.8), en);
	    		}
	    	}
	    }
	    for(double j=0.3;j<6;j+=0.2) {
	    	Location l = r.getPoint(j).toLocation(Main.w);
	    	for(AABB box : possibles.keySet()) {
	    		if(box.contains(l)) {
	    			possibles.get(box).setFireTicks(10);
	    			possibles.get(box).damage(1);
				}
	    	}
	    }
	}

	 */



	/*
    public void spawnCircle(Player p) {*/
	/*
        final Location location = p.getLocation();
        final double yaw = location.getYaw();
        final double[] time = {0.0};
        final double radius = 0.5;
        final double range = 30;

        new BukkitRunnable() {
            public void run() {
                Vector vec = location.getDirection().multiply(time[0]);
                Location center = new Location(location.getWorld(), location.getX() + vec.getX(), location.getY() + vec.getY(), location.getZ() + vec.getZ());
                for (double i = 0; i < 360; i += 5) {
                    Location n = new Location(center.getWorld(), xPos(Math.toRadians(i), radius, yaw) + center.getX(), yPos(Math.toRadians(i), radius) + center.getY(), center.getZ());
                    //ParticleEffect.REDSTONE.display(0, 0, 0, 0, 1, n, 8);
                }

                time[0] += 0.1;
                if (time[0] >= range)
                    this.cancel();
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
	 */
	/*
    	Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
        Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
        double tg = 18;
        double multi = 0.4d;

        Ray r = Ray.from(p);

        for (int i = 0; i < tg; i++) {
        	Location l = /*p.getEyeLocation() r.getPoint(0.3).toLocation(Main.w).add(x1.clone().multiply(multi * Math.sin((double)i / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)i / tg * Math.PI * 2d)));
        	//ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.1f, l, 100);
        }
        for (int i = 0; i < tg; i++) {
        	Location l = /*p.getEyeLocation() r.getPoint(1.3).toLocation(Main.w).add(x1.clone().multiply(multi * Math.sin((double)i / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)i / tg * Math.PI * 2d)));
        	//ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.1f, l, 100);
        }
        for (int i = 0; i < tg; i++) {
        	Location l = p.getEyeLocation() r.getPoint(2.3).toLocation(Main.w).add(x1.clone().multiply(multi * Math.sin((double)i / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)i / tg * Math.PI * 2d)));
        	//ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.1f, l, 100);
        }
        Map<AABB, LivingEntity> possibles = new HashMap<AABB, LivingEntity>();
	    for(LivingEntity en : p.getLocation().getWorld().getLivingEntities()) {
	    	if(en.getType().equals(EntityType.PIG_ZOMBIE) || (en.getType().equals(EntityType.PLAYER) && en.getName()!=p.getName())) {
	    		if(en.getLocation().distance(p.getLocation()) < 100) {
	    			possibles.put(AABB.from(en), en);
	    		}
	    	}
	    }
	    for(double j=0.3;j<3;j+=0.2) {
	    	Location l = r.getPoint(j).toLocation(Main.w);
	    	for(AABB box : possibles.keySet()) {
	    		if(box.contains(l)) {
	    			//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
				}
	    	}
	    }
    }*/

	public double xPos(double time, double radius, double yaw){
		return Math.sin(time) * radius * Math.cos(Math.PI/180 * yaw);
	}

	public double yPos(double time, double radius){
		return Math.cos(time)*radius;
	}


	/*
	  @EventHandler
	  public void sneak(PlayerToggleSneakEvent e) {
		  Player p = e.getPlayer();
		  //Bukkit.broadcastMessage("sneak");
		  if(e.isSneaking()) spawnCircle(p);
		  ////ParticleEffect.FLAME.display(p.getLocation().getDirection(), 0.5f, p.getLocation(), 100);
	  }
	 */



	public static HashMap<String, Long> jumpcd;
	@EventHandler
	public void Doublejump(PlayerToggleFlightEvent f)
	{

		Player p = f.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if(Main.getGM().getTFPlayer(p).getClasse().equals(Classe.Scout) && Main.getGM().getStatus().equals(Status.INGAME)) {
			if(jumpcd.containsKey(p.getName())) {
				//Bukkit.broadcastMessage(System.currentTimeMillis() - jumpcd.get(p.getName())+" "+(System.currentTimeMillis() - jumpcd.get(p.getName()) > 4000));
			}
			if(!jumpcd.containsKey(p.getName()) || System.currentTimeMillis() - jumpcd.get(p.getName()) > 5000) {
				f.setCancelled(true);
				p.setAllowFlight(false);
				p.setFlying(false);
				Vector dir = p.getLocation().getDirection()/*.setY(0)*/.normalize();
				//if(dir.getY()<=0.2) dir.setY(0.4);
				dir.multiply(2);
				dir.setY(dir.getY()*1.1);
				if(dir.getY()>1.6) {
					dir.setY(1.6);
				}
				//Bukkit.broadcastMessage(dir.getY()+" ");
				p.setVelocity(dir);
				jumpcd.put(p.getName(), System.currentTimeMillis());
			} else {
				f.setCancelled(true);
				p.setAllowFlight(false);
				p.setFlying(false);
			}
		}
	}

	@EventHandler
	public void Jump(PlayerMoveEvent e) {
		//e.getPlayer().sendMessage(e.getPlayer().getVelocity().toString());
		Block b = e.getTo().getBlock();
		if(e.getTo().getBlock().getType().equals(Material.IRON_PLATE)) {
			TFPlayer tfp = Main.getGM().getTFPlayer(e.getPlayer());
			if(tfp.getTeam().equals(Team.ROUGE)) {
				/*Vector v = e.getPlayer().getLocation().getDirection().multiply(2);
		    	e.getPlayer().setVelocity(new Vector(v.getX(), 1.1D, v.getZ()));*/
				//Vector v = e.getPlayer().getLocation().getDirection();
				e.getPlayer().setVelocity(new Vector(0, 1.1D, 0));
				return;
			}
		} else if(e.getTo().getBlock().getType().equals(Material.GOLD_PLATE)) {
			TFPlayer tfp = Main.getGM().getTFPlayer(e.getPlayer());
			if(tfp.getTeam().equals(Team.BLEU)) {
				/*Vector v = e.getPlayer().getLocation().getDirection().multiply(2);
		    	e.getPlayer().setVelocity(new Vector(v.getX(), 1.1D, v.getZ()));*/
				//Vector v = e.getPlayer().getLocation().getDirection();
				e.getPlayer().setVelocity(new Vector(0, 1.1D, 0));
				return;
			}
		} else if(b.getType().equals(Material.STONE_PLATE)) {
			//Bukkit.broadcastMessage("mine");
			TFPlayer tfp = Main.getGM().getTFPlayer(e.getPlayer());
			for(TFPlayer tg : Main.getGM().getMines()) {
				if(tg.getName().equals(tfp.getName())) continue;
				if(tfp.getTeam().equals(tg.getTeam())) continue;
				List<Location> mines = Main.getGM().getMinesOf(tg);
				if(mines == null || mines.isEmpty()) continue;
				if(mines.contains(b.getLocation())) {
					//Bukkit.broadcastMessage(tfp.getName()+" is on mine of : "+tg.getName());
					//Bukkit.broadcastMessage("team ok");
					b.getWorld().playSound(b.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
					Vector vector = e.getPlayer().getLocation().toVector().subtract(b.getLocation().toVector()).normalize();
					//tfp.getPlayer().damage(15);
					Main.getGM().damageTF(tg.getPlayer(), tfp.getPlayer(), 15);
					tfp.getPlayer().setVelocity(vector.multiply(0.9).setY(0.5));
					ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), b.getLocation(), 1, 1, 1, .01, 60);
					ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), b.getLocation(), 1, 1, 1, .01, 40);
					ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), b.getLocation(), 1.5, 1.5, 1.5, .01, 60);
					mines.remove(b.getLocation());
					Main.getGM().setMineLocations(tfp, mines);
					b.getLocation().getBlock().setType(Material.AIR);
					return;
				}
			}
		}

		if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) return;
		Player p = e.getPlayer();
		if(p.getAllowFlight()) return;
		if ((p.getGameMode() != GameMode.CREATIVE) && (p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR) && (!p.isFlying())) {
			if(Main.getGM().getTFPlayer(p).getClasse().equals(Classe.Scout)) {
				p.setAllowFlight(true);
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) { if(!e.getPlayer().isOp()) { e.setCancelled(true); } }

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(Main.getGM().getStatus().equals(Status.INGAME)) {
			Player p = e.getPlayer();
			TFPlayer tfp = Main.getGM().getTFPlayer(e.getPlayer());

			if(!tfp.estSorti()) {
				e.setCancelled(true);
				return;
			}

			if(tfp.getClasse().equals(Classe.Spy) && e.getBlockPlaced().getType().equals(Material.STONE_BUTTON)) {
				Gun g = GunManager.getGunByName("§6§lDétonateur");
				if(GunManager.hasPlacedC4(p.getName()) || GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
					e.setCancelled(true);
					return;
				}
				//Bukkit.broadcastMessage("c4");
				p.getInventory().setItem(1, g.getItemStack().build());
				GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
				GunManager.placeC4(p.getName(), e.getBlockPlaced().getLocation());
				return;
			}
			Block b = e.getBlockPlaced();
			if(tfp.getClasse().equals(Classe.Engineer)) {
				if(b.getType().equals(Material.GOLD_PLATE) || b.getType().equals(Material.IRON_PLATE)) {
					Main.getGM().setTrampoLocation(p.getName(), e.getBlock().getLocation());
					//Bukkit.broadcastMessage("trampo placé");
				} else if(b.getType().equals(Material.STONE_PLATE)) {
					List<Location> tg = new ArrayList<Location>();
					if(Main.getGM().getMinesOf(tfp) != null) {
						tg = Main.getGM().getMinesOf(tfp);
					}
					//Bukkit.broadcastMessage("mine place");

					tg.add(e.getBlock().getLocation());
					Main.getGM().setMineLocations(tfp, tg);
					//Bukkit.broadcastMessage("§4§l§nMines: "+Main.getGM().getMinesOf(tfp).size());
					/*for(Location l : Main.getGM().getMinesOf(tfp)) {
						//Bukkit.broadcastMessage("§4§l§nMines: "+l.toString());
					}*/
				} else if(b.getType().equals(Material.CARPET)) {
					if(!Turrets.hasTFTurret(p)) {
						//Bukkit.broadcastMessage("placmeent de tourelle pr "+p.getName());
						//TFTurret tft = new TFTurret(p, b.getLocation());
						Turrets.addTFTurret(p, b.getLocation());
					}
				}
				return;
			}

		}
		if(!e.getPlayer().isOp()) {
			e.setCancelled(true); 
		} 
	}

	@EventHandler
	public void doClick(InventoryClickEvent e) {
		int slot = e.getSlot(); 
		if(slot == -999) return;
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		String menuName = e.getWhoClicked().getOpenInventory().getTopInventory().getName();
		//p.sendMessage(""+slot+" "+menuName);

		if(menuName.equals("Choix des Classes")) {
			TFPlayer tfp = Main.getGM().getTFPlayer(p);
			if(slot<0||slot>32) return;
			if(Main.getGM().getStatus().equals(Status.INGAME)) {
				Main.getGM().setNextClasse(p, Classe.byId(slot-8));
				p.sendMessage("§7Vous aurez la classe §f§l"+Main.getGM().getNextClasse(p).getName() +" §7a votre réapparition.");
			} else {
				if(slot>=9&&slot<=17) {
					tfp.setClasse(Classe.byId(slot-8));
					new Title("", "§7Classe: §f§l"+tfp.getClasse().getName(), 0, 60, 10).send(p);
					p.sendMessage("§7Vous avez pris la classe: §f§l"+tfp.getClasse().getName());
				} else
					if(slot==30) {
						Main.getTM().changeTeam(p, Team.ROUGE); return;
					} else
						if(slot==32) {
							Main.getTM().changeTeam(p, Team.BLEU); return;
						}
			}
		}

	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) { e.setCancelled(true); }

	@EventHandler
	public void onJoin(PlayerLoginEvent e) {
		if(e.getPlayer().isOp()) return;
		if(Main.getGM().getStatus().equals(Status.DISABLED)) {
			e.setKickMessage(msg.kick_disabled.a());
			e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			return;
		}
		if(Main.getGM().getStatus().equals(Status.INGAME)) {
			if(Main.getGM().getPlayers().size() >= 20) {
				e.setKickMessage(msg.kick_ingame.a());
				e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(Main.getGM().getStatus().equals(Status.STARTING) || Main.getGM().getStatus().equals(Status.WAITING)) {
				e.setCancelled(true);
				return;
			} else {
				if(e.getCause().equals(DamageCause.FALL)) {
					e.setCancelled(true);
					return;
				}
				if(e.getCause().equals(DamageCause.FIRE_TICK)) {
					e.setDamage(e.getDamage()*2);
				}
				/*if(e.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
					e.setDamage(e.getDamage() * 1.5);
					e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(0.03));
				}*/
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath2lol(PlayerDeathEvent e) {
		e.setDeathMessage("");
		Player p = e.getEntity();
		TFPlayer kp = Main.getGM().getTFPlayer(p);
		if(Turrets.hasTFTurret(p)) {
			ArmorStand a = Turrets.getTFTurret(p);
			//Bukkit.broadcastMessage("rem turret of "+a.getCustomName());
			TFTurret.remove(a);
		}
		Cuboid z = (kp.getClasse().equals(Team.BLEU)?Main.getGm().ZoneBleu:Main.getGm().ZoneRouge);
		CuboidIterator it = z.iteratorr();
		for(Location l : it.getLocations()) {
			if(!l.getBlock().getType().isSolid())
				kp.getPlayer().sendBlockChange(l, Material.GLASS, (byte)0);
		}
		if(Main.getGM().getMinesOf(kp) != null) {
			for(Location l : Main.getGM().getMinesOf(kp)) {
				//Bukkit.broadcastMessage("§4§l§nMines: "+Main.getGM().getMinesOf(kp).size());
				//Bukkit.broadcastMessage("§4§l§nMines: "+l.toString());
				ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 60);
				ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 40);
				ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 60);
				l.getWorld().playSound(l, Sound.EXPLODE, 1.0f, 1.0f);
				for(TFPlayer tfp : Main.getGM().getPlayers()) {
					if(tfp.getPlayer().getLocation().distance(l) < 3) {
						if(!tfp.getTeam().equals(kp.getTeam())) {
							Vector vector = tfp.getPlayer().getLocation().toVector().subtract(l.toVector()).normalize();
							tfp.getPlayer().setVelocity(vector.multiply(1.8));
							Main.getGM().damageTF(p, tfp.getPlayer(), (int)16-(tfp.getPlayer().getLocation().distance(l)), true);
							//Bukkit.broadcastMessage("§6dmg: §l"+(int)(16-tfp.getPlayer().getLocation().distance(l)));
						}
					}
				}
				l.getBlock().setType(Material.AIR);
			}
			Main.getGM().setMineLocations(kp, null);
		}
		if(GunManager.hasPlacedC4(p.getName())) {
			Gun g = GunManager.getGunByName("§6§lC4");
			Location l = GunManager.getC4Loc(p.getName());
			l.getWorld().playSound(l, Sound.EXPLODE, 1.0f, 1.0f);
			Team t = kp.getTeam();
			for(TFPlayer tfpl : Main.getGM().getPlayers()) {
				if(tfpl.getPlayer().getLocation().distance(l) < 3 && !tfpl.getTeam().equals(t)) {
					//Bukkit.broadcastMessage("§4§lC4 "+16);
					Main.getGM().damageTF(kp.getPlayer(), tfpl.getPlayer(), 16);
				}
			}
			ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 100);
			ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 50);
			ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 100);
			l.getBlock().setType(Material.AIR);
			p.getInventory().setItem(1, g.getItemStack().build());
			GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
			GunManager.removeC4(p.getName());
		}
		if(GunListener.zoom.contains(p.getName())) {
			GunListener.zoom.remove(p.getName());
			p.removePotionEffect(PotionEffectType.SLOW);
		}

		if(Main.getGM().getTrampoLoc(p.getName()) != null) {
			Main.getGM().removeTrampo(kp, Main.getGM().getTrampoLoc(p.getName()));
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player killed = e.getEntity();
		if(killed.isDead() && !(e.getEntity().getLastDamageCause().getCause().equals(DamageCause.FIRE) || e.getEntity().getLastDamageCause().getCause().equals(DamageCause.FIRE_TICK))) {
			//Bukkit.broadcastMessage("     player already killed"+e.getDeathMessage());
			return;
		}

		Player killer = e.getEntity().getKiller();
		List<Player> damagers = Main.getGM().getLastDamagers(killed);
		TFPlayer tfkilled = Main.getGM().getTFPlayer(killed);
		tfkilled.setClasse(Main.getGM().getNextClasse(tfkilled));

		if(damagers == null) {
			Bukkit.broadcastMessage("§4§lkillers null");
			if(killer == null) {
				Bukkit.broadcastMessage("§4§lkiller = null");
				return;
			}
			damagers = new ArrayList<Player>();
			damagers.add(killer);
		} else {
			Bukkit.broadcastMessage("§4§lelse");
			if(killer != null) {
				Bukkit.broadcastMessage("§4§lkiller is not null");
				if(!damagers.contains(killer)) {
					Bukkit.broadcastMessage("§4§ldamagers not containing killer");
					damagers.add(killer);
				}
			}
		}


		if (Main.getGM().getStatus().equals(Status.INGAME)) {
			Bukkit.broadcastMessage("§4§lingame");
			Utils.playSound(killed.getLocation(), "player.mort");
			Main.getGM().setKiller(killed, null);
			Main.getGM().setLastDamagers(killed, null);

			//AbilityManager.resetCooldown(tfkilled); mis dans tfplayer

			//tfkilled.tpSpawn();

			TFPlayer first = Main.getGM().getTFPlayer(damagers.get(0));
			String str = first.getTeam().getColor() + first.getName()+" "+first.getClasse().getIcon();
			first.addKill();
			AbilityManager.setCooldown(first);
			damagers.remove(0);
			for(Player tg : damagers) {
				TFPlayer tgtmp = Main.getGM().getTFPlayer(tg);
				tgtmp.addKill();
				AbilityManager.setCooldown(tgtmp);
				str += ", "+tgtmp.getTeam().getColor() + tgtmp.getName()+" "+tgtmp.getClasse().getIcon();
			}

			Bukkit.broadcastMessage("§e§lTF §f"+tfkilled.getTeam().getColor()+ e.getEntity().getName() + " §7a été tué par "+str);

			tfkilled.addDeath();
		}
	}


	@EventHandler
	public void respawnEvent(PlayerRespawnEvent e) {
		TFPlayer tfp = Main.getGM().getTFPlayer(e.getPlayer());
		e.setRespawnLocation(tfp.getTeam().equals(Team.ROUGE) ? Main.getGM().getRedSpawn() : Main.getGM().getBlueSpawn());
		//tfp.setClasse(Main.getGM().getNextClasse(tfp));
		Utils.resetPlayer(e.getPlayer());

		tfp.giveStuff();
		//Bukkit.broadcastMessage("resp: "+tfp.getClasse().getName());
		tfp.setSorti(false);
	}

	@EventHandler
	public void onPVP(EntityDamageByEntityEvent e) {
		if(!Main.isEnabled) {
			e.setCancelled(true);
			e.setDamage(0);
			return;
		}
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();
		if(!(damager instanceof Player)) return;
		if(damaged.isDead()) return;
		if(damaged instanceof Player) {
			Player pdamaged = ((Player) damaged);
			Player pdamager = ((Player) damager);
			TFPlayer tdamager = Main.getGM().getTFPlayer(pdamager);
			TFPlayer tdamaged = Main.getGM().getTFPlayer(pdamaged);
			if(tdamager.getTeam().equals(tdamaged.getTeam())) {
				e.setCancelled(true);
				return;
			}
			ItemStack i = pdamager.getItemInHand();
			if (i == null) {
				e.setCancelled(true);
				return;
			}
			if ((!i.hasItemMeta()) || (!i.getItemMeta().hasDisplayName())) {
				e.setCancelled(true);
				return;
			}
			if(pdamaged.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
				fr.itspower.teamfortress.others.ParticleEffect.FIREWORKS_SPARK.display(0.5f, 0.3f, 0.5f, 0.01f, 20, pdamaged.getLocation().clone().add(0,1,0), 20.0);
				return;
			}
			e.setDamage(0);
			Double healthfinal = pdamaged.getHealth();
			if(i.getType().equals(Material.EMERALD)) { 
				//pdamager.playSound(pdamager.getLocation(), "guns.melee", 10f, 1f);
				Utils.playSound(pdamager.getLocation(), "guns.melee");
				if(tdamager.getAbility() != AbilityState.UNLOCKED) { 
					//Bukkit.broadcastMessage("§chasshootcooldown");
					e.setCancelled(true);
					return;
				}
				AbilityManager.executeAbility(tdamager);
				healthfinal = Math.max(0.0D, healthfinal - 12);
				pdamaged.setHealth(healthfinal);
				tdamager.addDmg(12);

				List<Player> damagers = Main.getGM().getLastDamagers(pdamaged);
				if(damagers == null) {
					//Bukkit.broadcastMessage("§4§ldamagers null");
					damagers = new ArrayList<Player>();
				}
				if(!damagers.contains(pdamager)) {
					//Bukkit.broadcastMessage("§4§ldamagers add "+damager.getName());
					damagers.add(pdamager);
				}
				//Bukkit.broadcastMessage("§4§ldamagers add "+pdamager.getName());

				Main.getGM().setLastDamagers(pdamaged, damagers);
				//Bukkit.broadcastMessage("§ccap sniper");
				return;
			}
			Gun g = GunManager.getGunByName(i.getItemMeta().getDisplayName().split(GunListener.sepa)[0]);
			if(g.getType().equals(WeaponType.MELEE)) {
				//Bukkit.broadcastMessage("§b§lmelee: "+healthfinal +" "+pdamaged.getHealth());
				//pdamager.playSound(pdamager.getLocation(), "guns.melee", 10f, 1f);
				Utils.playSound(pdamager.getLocation(), "guns.melee");
				if(i.getType().equals(Material.WOOD_HOE)) { // batte scout
					//pdamaged.damage(4);
					healthfinal = Math.max(0.0D, healthfinal - 4);
					tdamager.addDmg(4);
				} else if(i.getType().equals(Material.IRON_SPADE)) { // poings américains
					//pdamaged.damage(8);
					healthfinal = Math.max(0.0D, healthfinal - 8);
					tdamager.addDmg(8);
				} else if(i.getType().equals(Material.IRON_AXE)) { // scie a amputation
					//pdamaged.damage(4);
					healthfinal = Math.max(0.0D, healthfinal - 4);
					tdamager.addDmg(4);
				} else if(i.getType().equals(Material.GOLD_AXE)) { // hache pyro
					//pdamaged.damage(3);
					healthfinal = Math.max(0.0D, healthfinal - 3);
					tdamager.addDmg(3);
				} else if(i.getType().equals(Material.GOLD_NUGGET)) { // clé a molette
					//pdamaged.damage(6);
					healthfinal = Math.max(0.0D, healthfinal - 6);
					tdamager.addDmg(6);
				} else if(i.getType().equals(Material.DIAMOND_PICKAXE)) { // poignard
					//Bukkit.broadcastMessage("§cPoignard");
					if(GunManager.cd.containsKey(pdamager.getName()+"@"+g.getSlot())) {
						//Bukkit.broadcastMessage("§4hasshootcooldown");
						e.setCancelled(true);
						return;
					}
					if(GunManager.timeout.contains(pdamager.getName())) {
						//Bukkit.broadcastMessage("§dtimeout");
						for(int tg=0; tg<10; tg++) {
							fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.PURPLE), pdamager.getEyeLocation().clone().add(Math.random()*1.5-0.5, Math.random()*1.5-1.2, Math.random()*1.5-0.5), 100);
						}
						//pdamaged.damage(14);
						healthfinal = Math.max(0.0D, healthfinal - 14);
						tdamager.addDmg(14);
					} else {
						//Bukkit.broadcastMessage("§dno timeout");
						//pdamaged.damage(10);
						tdamager.addDmg(10);
						healthfinal = Math.max(0.0D, healthfinal - 10);
					}
					GunManager.cd.put(pdamager.getName()+"@"+g.getSlot(), new TFCooldown(pdamager.getName()+"@"+g.getSlot(), pdamager, g));
				}
				//Bukkit.broadcastMessage("§b§lmelee: "+healthfinal +" "+pdamaged.getHealth());
				List<Player> damagers = Main.getGM().getLastDamagers(pdamaged);
				if(damagers == null) {
					//Bukkit.broadcastMessage("§4§ldamagers null");
					damagers = new ArrayList<Player>();
				}
				if(!damagers.contains(pdamager)) {
					//Bukkit.broadcastMessage("§4§ldamagers add "+damager.getName());
					damagers.add(pdamager);
				}
				//Bukkit.broadcastMessage("§4§ldamagers add "+pdamager.getName());

				Main.getGM().setLastDamagers(pdamaged, damagers);

				pdamaged.setHealth(healthfinal);
			} else {
				e.setCancelled(true);
			}
		} else if(e.getDamager() instanceof Snowball) {
			//Bukkit.broadcastMessage("tg"+(e.getEntity() instanceof Snowball));
			e.setCancelled(true);
		} else if(e.getEntity() instanceof Fireball) {
			e.setCancelled(true);
			//Bukkit.broadcastMessage("fireball ");
		} else if(damaged instanceof ArmorStand) {
			//Bukkit.broadcastMessage("tourellehit");
			if(damaged.getName().contains("Tourelle")) {
				//Bukkit.broadcastMessage("is tf tourelle");
				TFPlayer tdamager = Main.getGM().getTFPlayer((Player) e.getDamager());

				if(Turrets.getTFTurret((Player) e.getDamager()).getName().contains(e.getDamager().getName()) && 
						((Player) damager).getInventory().getHeldItemSlot() == 4 &&
						!GunManager.cd.containsKey(e.getDamager().getName()+"@"+0)) {

					//Bukkit.broadcastMessage("yess");
					ArmorStand a = Turrets.getTFTurret((Player) e.getDamager());
					TFTurret.remove(a);
					Gun g = GunManager.getGunByName("§6§lCanon montable");
					tdamager.getPlayer().getInventory().setItem(0, g.getItemStack().build());
					//Bukkit.broadcastMessage("end ");
				}
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);

		Player p = e.getPlayer();
		String msg = e.getMessage();

		Team t = Main.getGM().getTFPlayer(p).getTeam();

		if(t.equals(Team.ROUGE)) {
			Bukkit.broadcastMessage("§c"+p.getName()+"§7 » §f"+msg);
		} else if(t.equals(Team.BLEU)) {
			Bukkit.broadcastMessage("§3"+p.getName()+"§7 » §f"+msg);
		} else {
			Bukkit.broadcastMessage("§7"+p.getName()+" » §f"+msg);
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onArrow(ProjectileHitEvent e) {
		if ((e.getEntity() instanceof Arrow)) {
			e.getEntity().remove();
		}
		if ((e.getEntity() instanceof Snowball)) {
			Entity s = (Entity) e.getEntity().getShooter();
			if(!(s instanceof Player)) return;
			TFPlayer tfp = Main.getGM().getTFPlayer((Player) s);
			Entity snowball = e.getEntity();
			Location loc = snowball.getLocation();
			for(TFPlayer kp : Main.getGM().getPlayers()) {
				if(!kp.getTeam().equals(tfp.getTeam())) {
					if(kp.getPlayer().getLocation().distance(loc) > 5) continue;
					if(kp.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
						fr.itspower.teamfortress.others.ParticleEffect.FIREWORKS_SPARK.display(0.5f, 0.3f, 0.5f, 0.01f, 20, kp.getPlayer().getLocation().clone().add(0,1,0), 20.0);
						return;
					}
					kp.getPlayer().setFireTicks(200);
					Main.getGM().damageTF((Player) s, kp.getPlayer(), 0.1);
				}
			}
			ParticleEffect.FLAME.send(Bukkit.getOnlinePlayers(), loc, 0, 0, 0, 0.12, 500);
			//ParticleEffect.FLAME.display(0,0,0, 0.12f, 500, loc, 50);


		}
		if ((e.getEntity() instanceof Fireball)) {
			Entity s = (Entity) e.getEntity().getShooter();
			if(!(s instanceof Player)) return;
			TFPlayer p = Main.getGM().getTFPlayer((Player) s);
			Location l = e.getEntity().getLocation();
			for(TFPlayer tfp : Main.getGM().getPlayers()) {
				if(tfp.getPlayer().getLocation().distance(l) < 4) {
					if(tfp.getName().equals(p.getName())) {
						Vector vector = tfp.getPlayer().getLocation().toVector().subtract(l.toVector()).normalize();
						//tfp.getPlayer().setVelocity(vector.multiply(1.4).setY(1.7));
						if(vector.getY()<0.1) vector.setY(0.7);
						tfp.getPlayer().setVelocity(vector.multiply(1.8));
					} else if(!tfp.getTeam().equals(p.getTeam())) {
						Vector vector = tfp.getPlayer().getLocation().toVector().subtract(l.toVector()).normalize();
						//tfp.getPlayer().setVelocity(vector.multiply(0.9).setY(0.5));
						tfp.getPlayer().setVelocity(vector.multiply(1.8));
						Main.getGM().damageTF(p.getPlayer(), tfp.getPlayer(), (int)16-(tfp.getPlayer().getLocation().distance(l)), true);
						//Bukkit.broadcastMessage("§6dmg: §l"+(int)(16-tfp.getPlayer().getLocation().distance(l)));
					}
				}
			}
			ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 150);
			ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 80);
			ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 150);
			//ParticleEffect.SMOKE_LARGE.display(1,1,1, 0.01f, 150, l, 50);
			//ParticleEffect.CLOUD.display(1,1,1, 0.01f, 80, l, 50);
			//ParticleEffect.LAVA.display(1.5f,1.5f,1.5f, 0.01f, 150, l, 50);
		}
		if ((e.getEntity() instanceof Egg)) {
			Entity s = (Entity) e.getEntity().getShooter();
			if(!(s instanceof Player)) return;
			TFPlayer p = Main.getGM().getTFPlayer((Player) s);
			Location l = e.getEntity().getLocation();
			l.getWorld().playSound(l, Sound.EXPLODE, 1.0f, 1.0f);
			Team t = p.getTeam();
			for(TFPlayer tfp : Main.getGM().getPlayers()) {
				if(tfp.getPlayer().getLocation().distance(l) <= 4) {
					if(!tfp.getName().equals(p.getName()) && !t.equals(tfp.getTeam())) {
						Vector vector = tfp.getPlayer().getLocation().toVector().subtract(l.toVector()).normalize();
						//tfp.getPlayer().setVelocity(vector.multiply(0.9).setY(0.5));
						tfp.getPlayer().setVelocity(vector.multiply(1.8));
						Main.getGM().damageTF(p.getPlayer(), tfp.getPlayer(), (int)16-(tfp.getPlayer().getLocation().distance(l)), true);
						Bukkit.broadcastMessage("§6dmg: §l"+(int)(16-tfp.getPlayer().getLocation().distance(l)));
					}
				}
				ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 100);
				ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 50);
				ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 100);
			}
		}
	}

	@EventHandler
	public void onEnter(VehicleEnterEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onEnter(PlayerPickupItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onVehiDamage(VehicleDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == SpawnReason.EGG)
		{
			event.setCancelled(true);
		}
	}
}
