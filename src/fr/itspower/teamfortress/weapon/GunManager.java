package fr.itspower.teamfortress.weapon;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import fr.itspower.teamfortress.Main;
import fr.itspower.teamfortress.Turrets;
import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.TFTurret;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.utils.AABB;
import fr.itspower.teamfortress.utils.Ray;
import fr.itspower.teamfortress.utils.TFCooldown;
import fr.itspower.teamfortress.utils.Utils;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;

public class GunManager {

	static int size = 0;
	static int size2 = 0;
	static ArrayList<Gun> guns = new ArrayList<Gun>();
	static HashMap<Integer, Gun> gun_ids = new HashMap<Integer, Gun>();
	static HashMap<String, BukkitTask> releaseTasks = new HashMap<String, BukkitTask>();
	public static HashMap<String, BukkitTask> finalTasks = new HashMap<String, BukkitTask>();
	public static HashMap<String, Location> c4 = new HashMap<String, Location>();
	public static List<String> timeout = new ArrayList<String>();
	public static HashMap<String, TFCooldown> cd = new HashMap<String, TFCooldown>();

	public static void registerGun(Gun gun) {
		try {
			gun_ids.put(size, gun);
			guns.add(gun);
			++size;
		}
		catch (Throwable throwable) {
			new Exception(throwable);
		}
	}

	public static Gun getGunByName(String name) {
		for (Gun gun : guns) {
			if (!name.equalsIgnoreCase(gun.getName())) continue;
			return gun;
		}
		return null;
	}

	public static Gun getGunAt(int index) {
		return guns.get(index);
	}

	public static ArrayList<Gun> getGunlist() {
		return guns;
	}

	public static void shoot(Player p, Gun gun, TFPlayer tfp) {
		if(gun.getRecul() != 0.0) {
			Vector velToAdd = p.getLocation().getDirection().multiply(-gun.getRecul());
			velToAdd.multiply(new Vector(1, 0, 1));
			p.setVelocity(velToAdd);
		}
		Map<AABB, Player> possibles = new HashMap<AABB, Player>();
		for(Player en : Bukkit.getOnlinePlayers()) {
			if(!en.isDead() && en.getName()!=p.getName() && !Main.getGM().getTFPlayer(en).getTeam().equals(tfp.getTeam())) {
				possibles.put(AABB.from(en), en);
			}
		}
		if(!gun.getType().equals(WeaponType.AUTOMATIC)) {
			final Ray r = Ray.from(p);
			Utils.playSound(p.getLocation(), gun.getSound());
			//p.playSound(p.getLocation(), gun.getSound(), 10f, 1f);
			if(gun.isShotgun()) {
				for(double k=0;k<gun.getBulletAmount();k++) {
					Ray _r = r.changeDir(gun.getAccuracy());
					ligne:
						for(double j=0.2;j<gun.getDistance();j+=0.2) {
							Location l = _r.getPoint(j).toLocation(Main.w);
							if(l.getBlock().getType().isSolid()) {
								ParticleEffect.FLAME.send(Bukkit.getOnlinePlayers(), l, 0.1, 0.1, 0.1, 0.009, 10);
								break ligne;
							}
							ParticleEffect.SMOKE_NORMAL.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 1);
							for(AABB box : possibles.keySet()) {
								if(box.contains(l)) {
									//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
									Main.getGM().damageTF(p, possibles.get(box), gun.getDamage(), true);
									ParticleEffect.FLAME.send(Bukkit.getOnlinePlayers(), l, 0.1, 0.1, 0.1, 0.009, 10);
									break ligne;
								}
							}
						}
				}
			} else if(gun.isSniper()) {
				ligne:
					for(double j=0.3;j<gun.getDistance();j+=0.2) {
						Location l = r.getPoint(j).toLocation(Main.w);
						if(l.getBlock().getType().isSolid()) {
							ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 10);
							break ligne;
						}
						ParticleEffect.SUSPENDED_DEPTH.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 10);
						for(AABB box : possibles.keySet()) {
							if(box.contains(l)) {
								//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
								if(GunListener.zoom.contains(p.getName())) {
									Main.getGM().damageTF(p, possibles.get(box), gun.getDamage()*2, true);
								} else {
									Main.getGM().damageTF(p, possibles.get(box), gun.getDamage(), true);
								}
								ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 10);
								break ligne;
							}
						}
					}

			} else if(gun.getType().equals(WeaponType.LAUNCHER)) {

				Fireball f = p.launchProjectile(Fireball.class);
				f.setShooter(p);
				f.setVelocity(p.getLocation().getDirection().multiply(1.5));
				f.setBounce(false);
				f.setIsIncendiary(false);
				f.setFireTicks(0);
				f.setYield(0);
				new BukkitRunnable() {
					@Override
					public void run() {
						if(f==null || f.isDead()) {
							cancel();
							return;
						}
						Location l = f.getLocation();
						ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 0.1, 0.1, 0.1, 0.05, 5);
						ParticleEffect.FLAME.send(Bukkit.getOnlinePlayers(), l, 0.1, 0.1, 0.1, 0.05, 4);
					}
				}.runTaskTimer(Main.getInstance(), 0l, 1l);

			} else if(gun.getType().equals(WeaponType.GRENADE)) {
				if(gun.getDamage() == 1) {
					//Bukkit.broadcastMessage("grenade 1 flash");
					final Item grenade = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(gun.getMaterial()));
					grenade.setVelocity(p.getLocation().getDirection().multiply(1.0D));
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							Location explo = grenade.getLocation();
							//ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), explo, 0, 0, 0, 0.11, 50);
							//ParticleEffect.FIREWORKS_SPARK.send(Bukkit.getOnlinePlayers(), explo, 0, 0, 0, 0.11, 100);
							/*Firework fw = (Firework) explo.getWorld().spawn( explo, Firework.class );
	        		        FireworkMeta fwMeta = fw.getFireworkMeta();
	        		        fwMeta.addEffect(FireworkEffect.builder().trail(false).with(FireworkEffect.Type.BALL).withColor(Color.WHITE).build());
	        		        fw.setFireworkMeta(fwMeta);
	        		        fw.detonate();*/
							Firework f = (Firework) explo.getWorld().spawn(explo, Firework.class);
							FireworkMeta fm = f.getFireworkMeta();
							fm.addEffect(FireworkEffect.builder().trail(false).with(FireworkEffect.Type.BALL).withColor(Color.WHITE).build());
							f.setFireworkMeta(fm);
							try {
								Class<?> entityFireworkClass = getTg("net.minecraft.server.", "EntityFireworks");
								Class<?> craftFireworkClass = getTg("org.bukkit.craftbukkit.", "entity.CraftFirework");
								Object firework = craftFireworkClass.cast(f);
								Method handle = firework.getClass().getMethod("getHandle");
								Object entityFirework = handle.invoke(firework);
								Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
								Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
								ticksFlown.setAccessible(true);
								ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
								ticksFlown.setAccessible(false);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							Main.w.playSound(grenade.getLocation(), Sound.FIREWORK_LAUNCH, 1f, 1f);
							for(TFPlayer kp : Main.getGM().getPlayers()) {
								if(!Main.getGM().isOnline(kp.getName())) continue;
								if(!kp.getTeam().equals(tfp.getTeam()) && kp.getPlayer().getLocation().distance(explo) < 4) {
									new BukkitRunnable() {
										int tg = 0;
										@Override
										public void run() {
											if(tg<=90) {
												//ParticleEffect.VILLAGER_ANGRY.display(0.4f,0.3f,0.4f, 0.0f, 20, kp.getPlayer().getLocation().clone().add(0, 2.3, 0), 32);
												ParticleEffect.VILLAGER_ANGRY.send(Bukkit.getOnlinePlayers(), kp.getPlayer().getLocation().clone().add(0, 2.3, 0), 0.4, 0.3, 0.4, 0, 20);
											} else {
												cancel();
											}
											tg += 5;
										}
									}.runTaskTimer(Main.getInstance(), 0l, 5l);
									kp.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
									kp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 9));
								}
							}
							grenade.remove();
						}
					}, 40);




				} else if(gun.getDamage() == 2) {
					//Bukkit.broadcastMessage("grenade 2 dyna");
					final Item grenade = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(gun.getMaterial()));
					grenade.setVelocity(p.getLocation().getDirection().multiply(0.8).add(new Vector(0,0.1,0)));
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							Location explo = grenade.getLocation();
							for(Player pd : Bukkit.getOnlinePlayers())
								if(pd.getLocation().distance(explo) < 30)
									pd.playSound(explo, "guns.grenade", 1f, 1f);
							ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), explo, 2, 2, 2, .01, 150);
							ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), explo, 2, 2, 2, .01, 100);
							ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), explo, 2, 2, 2, 1, 150);
							for(TFPlayer kp : Main.getGM().getPlayers()) {
								if(!Main.getGM().isOnline(kp.getName())) continue;
								if(!tfp.getTeam().equals(kp.getTeam()) && kp.getPlayer().getLocation().distance(explo) < 4) {
									Vector vector = kp.getPlayer().getLocation().toVector().subtract(explo.toVector()).normalize();
									kp.getPlayer().setVelocity(vector.multiply(0.9).setY(0.5));
									Main.getGM().damageTF(p, kp.getPlayer(), (int)21-(kp.getPlayer().getLocation().distance(explo)), true);
									//Bukkit.broadcastMessage("§6dmg: §l"+(int)(21-kp.getPlayer().getLocation().distance(explo)));
								}
							}
							grenade.remove();
						}
					}, 60);




				} else if(gun.getDamage() == 3) {
					//Bukkit.broadcastMessage("grenade 2 fumi");
					final Item grenade = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(gun.getMaterial()));
					grenade.setVelocity(p.getLocation().getDirection().multiply(0.8).add(new Vector(0,0.1,0)));
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							Location explo = grenade.getLocation();
							for(Player pd : Bukkit.getOnlinePlayers())
								if(pd.getLocation().distance(explo) < 30)
									pd.playSound(explo, "guns.grenade", 1f, 1f);
							ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), explo, 0, 0, 0, .3, 100);
							ArrayList<Block> blocs = sphere(explo, 5);
							for(Block b : blocs) {
								if(b.getType().equals(Material.AIR)) {
									b.setType(org.bukkit.Material.TRIPWIRE);
									//Bukkit.broadcastMessage(b.getType().toString());
								}
							}
							grenade.remove();
							new BukkitRunnable() {
								int tg = 0;
								Team t = tfp.getTeam();
								@Override
								public void run() {
									if(tg<=100) {
										for(TFPlayer kp : Main.getGM().getPlayers()) {
											if(!kp.getTeam().equals(t) && kp.getPlayer().getLocation().distance(explo) < 4) {
												kp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40,1));
											}
										}
									} else {
										cancel();
									}
									tg += 5;
								}
							}.runTaskTimer(Main.getInstance(), 0l, 5l);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
								@Override
								public void run() {
									for(Block b : blocs) {
										if(b.getType().equals(Material.TRIPWIRE)) {
											b.setType(Material.AIR);
										}
									}
								}
							}, 100);
						}
					}, 60);
				}

			} else if(gun.getClassType().equals(Classe.Spy) && gun.getSlot()==3) { // time out
				Team t = tfp.getTeam();
				if(timeout.contains(p.getName())) {
					//Bukkit.broadcastMessage("timeout off");
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					for(TFPlayer tfploop : Main.getGM().getPlayers()) {
						tfploop.getPlayer().showPlayer(p);
					}
					timeout.remove(p.getName());
				} else {
					if(p.getInventory().getHelmet().getType().equals(Material.SKULL_ITEM)) { 
						//Bukkit.broadcastMessage("§cCAP SPE ACTIVE...");
						return;
					}

					//Bukkit.broadcastMessage("timeout on");
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 3));
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
					for(TFPlayer tfploop : Main.getGM().getPlayers()) {
						if(!tfploop.getTeam().equals(t)) {
							tfploop.getPlayer().hidePlayer(p);
						}
					}
					timeout.add(p.getName());
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							//Bukkit.broadcastMessage("timeout off");
							p.removePotionEffect(PotionEffectType.INVISIBILITY);
							for(TFPlayer tfploop : Main.getGM().getPlayers()) {
								tfploop.getPlayer().showPlayer(p);
							}
							timeout.remove(p.getName());
						}
					}, 100);
				}
			} else if(gun.getClassType().equals(Classe.Medic) && gun.getSlot()==0) { // medecine portable
				//Bukkit.broadcastMessage("med portable");
				for(Player en : Bukkit.getOnlinePlayers()) {
					if(en.getName()!=p.getName()) {
						possibles.put(AABB.from(en), en);
					}
				}
				Location l = p.getLocation();
				for(AABB box : possibles.keySet()) {
					TFPlayer tfploop = Main.getGM().getTFPlayer((Player) possibles.get(box));
					if(l.distance(tfp.getPlayer().getLocation()) < 10 && tfploop.getTeam().equals(tfp.getTeam())) {
						//Bukkit.broadcastMessage("§a§lMATCH HEAL "+possibles.get(box).getName());
						tfploop.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
						tfploop.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
						tfploop.addAddHP(8);
						for (int j = 0; j < 100; j++){
							double angle1 = new Random().nextDouble() * 2.0D * 3.141592653589793D;
							double angle2 = new Random().nextDouble() * 2.0D * 3.141592653589793D - 1.5707963267948966D;
							double x = Math.cos(angle1) * Math.cos(angle2);
							double z = Math.sin(angle1) * Math.cos(angle2);
							double y = Math.sin(angle2);
							p.getWorld().spigot().playEffect(tfploop.getPlayer().getLocation().clone().add(x * 1.5D, y * 1.5D + 1.0D, z * 1.5D), Effect.HAPPY_VILLAGER, 0, 1, 0.0F, 0.0F, 0.0F, 1.0F, 0, 200);
						}
						return;
					}
				}
			}else if(gun.getClassType().equals(Classe.Medic) && gun.getSlot()==1) { // pistolet tranquillisant
				//Bukkit.broadcastMessage("pistolet tranquili");
				ligne:
					for(double j=0.3;j<gun.getDistance();j+=0.2) {
						Location l = r.getPoint(j).toLocation(Main.w);
						if(l.getBlock().getType().isSolid()) {
							fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.RED), l, 100);
							break ligne;
						}
						Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
						Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
						double multi = 0.15d;
						Location l1 = r.getPoint(j).toLocation(Main.w);
						for (int tgg = 0; tgg < 5; tgg++) {
							Location lol = l1.clone().add(x1.clone().multiply(multi * Math.sin((double)tgg / 5 * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)tgg / 5 * Math.PI * 2d)));
							//ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), lol, 0, 0, 0, 0.0, 1);
							fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.FUCHSIA), lol, 100);
						}
						//fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.FUCHSIA), l, 100);
						for(AABB box : possibles.keySet()) {
							if(box.contains(l)) {
								if(!Main.getGM().getTFPlayer((Player) possibles.get(box)).getTeam().equals(tfp.getTeam())) {
									//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
									possibles.get(box).removePotionEffect(PotionEffectType.SLOW);
									possibles.get(box).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
									Main.getGM().damageTF(p, possibles.get(box), gun.getDamage(), true);
									fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.RED), l, 100);
									break ligne;
								}
							}
						}
					}

			} else if(gun.getClassType().equals(Classe.Demoman) && gun.getSlot()==3) { // pistolet de detresse
				new BukkitRunnable() {
					double tg = 0.3d;
					Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
					Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
					double multi = 0.33d;
					Team t = tfp.getTeam();
					@Override 
					public void run() {
						if(tg<=6) {
							Location l1 = r.getPoint(tg).toLocation(Main.w);
							for (int j = 0; j < 22; j++) {
								Location l = l1.clone().add(x1.clone().multiply(multi * Math.sin((double)j / 20 * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)j / 20 * Math.PI * 2d)));
								ParticleEffect.REDSTONE.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0.0, 1);
							}
							for(AABB box : possibles.keySet()) {
								if(box.contains(l1)) {
									//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
									if(!Main.getGM().getTFPlayer((Player) possibles.get(box)).getTeam().equals(t)) {
										if(possibles.get(box).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
											fr.itspower.teamfortress.others.ParticleEffect.FIREWORKS_SPARK.display(0.5f, 0.3f, 0.5f, 0.01f, 20, possibles.get(box).getLocation().clone().add(0,1,0), 20.0);
											return;
										}
										possibles.get(box).setFireTicks(120);
										Main.getGM().damageTF(tfp.getPlayer(), possibles.get(box), 0.001);
									}
								}
							}
						} else {
							cancel();
						}
						tg += 0.5;
					}
				}.runTaskTimer(Main.getInstance(), 0l, 1l);
			} else if(gun.getClassType().equals(Classe.NULL) && gun.getSlot()==0) { // Télécommande
				ArmorStand tftur = Turrets.getTFTurret(p);
				if(tftur != null) {
					if(p.getLocation().distance(tftur.getLocation())>15) {
						p.sendMessage("§cVous êtes trop loin de votre tourelle.");
						return;
					}
					TFTurret.updateDir(p, tftur);
				}

			} else {
				//Bukkit.broadcastMessage("tir normal");
				for(double k=0;k<gun.getBulletAmount();k++) {
					ligne:
						for(double j=0.3;j<gun.getDistance();j+=0.2) {
							Location l = r.getPoint(j).toLocation(Main.w);
							if(l.getBlock().getType().isSolid()) {
								ParticleEffect.SMOKE_NORMAL.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0.01, 3);
								break ligne;
							}
							ParticleEffect.SUSPENDED_DEPTH.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 2);
							for(AABB box : possibles.keySet()) {
								if(box.contains(l)) {
									//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
									double damage = gun.getDamage();
									if(gun.hasHeadShot() && box.isHeadShot(l)) {
										damage = 7;
										//Bukkit.broadcastMessage("§a§lHEADSHOT dmg x2");
									}
									Main.getGM().damageTF(tfp.getPlayer(), possibles.get(box), damage, true);
									ParticleEffect.SMOKE_NORMAL.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0.01, 3);
									break ligne;
								}
							}
						}
				}
			}
		} else {
			if(tfp.getLast() > 0) {
				if(!finalTasks.containsKey(p.getName())) {
					finalTasks.put(p.getName(), Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
						int tmp = 0;
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							p.playSound(p.getLocation(), gun.getSound(), 1f, 1f);
							Ray r = Ray.from(p);
							if(gun.getSlot()==0 && gun.getClassType().equals(Classe.Pyroman)) {
								ItemStack i = p.getItemInHand();
								String[] name = i.getItemMeta().getDisplayName().split(GunListener.sepa);
								int mun = Integer.parseInt(name[2].split("/")[0]);
								ItemMeta im = i.getItemMeta();
								int newmun = (mun-1);
								im.setDisplayName(gun.getName()+GunListener.sepa+newmun+"/"+gun.getMaxClip());
								i.setItemMeta(im);
								if(newmun == 0) {
									//Bukkit.broadcastMessage("plus de mun ");
									tfp.stopGunCooldown();
									GunManager.cd.put(p.getName()+"@"+gun.getSlot(), new TFCooldown(p.getName()+"@"+gun.getSlot(), p, gun));
								} else {
									int ig = (mun-1);
									i.setAmount(ig);
								}
								Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
								Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
								double tg = 22;
								double multi = 0.4d;
								Location l1 = r.getPoint(0.4).toLocation(Main.w);
								Location l2 = r.getPoint(1.6).toLocation(Main.w);
								Location l3 = r.getPoint(2.8).toLocation(Main.w);
								Vector pvec = p.getLocation().getDirection();

								for (int j = 0; j < tg; j++) {
									Location l = /*p.getEyeLocation()*/  l1.clone().add(x1.clone().multiply(multi * Math.sin((double)j / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)j / tg * Math.PI * 2d)));
									fr.itspower.teamfortress.others.ParticleEffect.FLAME.display(pvec, 0.21f, l, 100);
								}
								for (int j = 0; j < tg; j++) {
									Location l = /*p.getEyeLocation()*/ l2.clone().add(x1.clone().multiply(multi * Math.sin((double)j / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)j / tg * Math.PI * 2d)));
									fr.itspower.teamfortress.others.ParticleEffect.FLAME.display(pvec, 0.21f, l, 100);
								}
								for (int j = 0; j < tg; j++) {
									Location l = /*p.getEyeLocation()*/ l3.clone().add(x1.clone().multiply(multi * Math.sin((double)j / tg * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)j / tg * Math.PI * 2d)));
									fr.itspower.teamfortress.others.ParticleEffect.FLAME.display(pvec, 0.21f, l, 100);
								}
								Map<AABB, Player> possibles = new HashMap<AABB, Player>();
								for(Player en : Bukkit.getOnlinePlayers()) {
									if(en.getName()!=p.getName()) {
										possibles.put(AABB.from(en, 0.8), en);
									}
								}
								for(double j=0.3;j<6;j+=0.2) {
									Location l = r.getPoint(j).toLocation(Main.w);
									for(AABB box : possibles.keySet()) {
										if(box.contains(l)) {
											possibles.get(box).setFireTicks(60);
											Main.getGM().damageTF(p, possibles.get(box), gun.getDamage());
										}
									}
								}
							} else if(gun.getSlot()==0 && gun.getClassType().equals(Classe.Heavy)) {
								Map<AABB, Player> possibles = new HashMap<AABB, Player>();
								for(Player en : Bukkit.getOnlinePlayers()) {
									if(en.getName()!=p.getName()) {
										possibles.put(AABB.from(en, 0.8), en);
									}
								}
								if(!GunListener.heavyscoop.contains(p.getName())) {
									r = r.changeDir(gun.getAccuracy());
									//Bukkit.broadcastMessage("traj mod");
								}
								Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
								Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
								double multi = 0.23d;
								Vector add1 = x1.clone().multiply(multi * Math.sin((double)tmp / 10 * Math.PI * 2d));
								Vector add2 = x2.clone().multiply(multi * Math.cos((double)tmp / 10 * Math.PI * 2d));
								Location l = r.getPoint(0.5).toLocation(Main.w).clone().add(add1).add(add2);
								p.getWorld().spigot().playEffect(l, Effect.FLAME, Effect.FLAME.getId(), 1, 200.0F, 0.0F, 0.0F, 0.0F, 0, 100);
								//p.playSound(p.getLocation(), gun.getSound(), 1f, 1f);
								Utils.playSound(p.getLocation(), gun.getSound());
								if(tmp==10) tmp=0;
								for(double j=0.55;j<gun.getDistance();j+=0.3) {
									Location hitpoint = r.getPoint(j).toLocation(Main.w).clone().add(add1).add(add2);
									p.getWorld().spigot().playEffect(hitpoint, Effect.PARTICLE_SMOKE, Effect.PARTICLE_SMOKE.getId(), 1, 0.0F, 0.0F, 0.0F, 0.0F, 0, 100);
									if(hitpoint.getBlock().getType().isSolid()) {
										break;
									}
									for(AABB box : possibles.keySet()) {
										if(box.contains(hitpoint)) {
											if(!Main.getGM().getTFPlayer((Player) possibles.get(box)).getTeam().equals(tfp.getTeam())) {
												Player hitt = possibles.get(box);
												possibles.remove(box);
												//Bukkit.broadcastMessage("§a§lMATCH BULLET "+hitt.getName());
												Main.getGM().damageTF(p, hitt, gun.getDamage(), true);

												Location center = p.getLocation();
												Vector vel = hitt.getLocation().subtract(center).toVector();
												if (vel.lengthSquared() != 0.0D) {
													vel.multiply(4 / vel.lengthSquared());
													hitt.setVelocity(vel);
												}


												//hitt.getVelocity().multiply(2D);
												//Vector tg = hitt.getVelocity();
												//if(tg.getY()<0.1) tg.add(new Vector(0,0.5,0));
												//hitt.setVelocity(hitt.getVelocity().add(p.getEyeLocation().getDirection().multiply(10)));
											}
										}
									}
								}
								tmp++;

							} else {
								for(double k=0;k<1;k++) {
									ligne:
										for(double j=0.3;j<gun.getDistance();j+=0.22) {
											Location l = r.getPoint(j).toLocation(Main.w);
											//Bukkit.broadcastMessage("§d§lAUTO "+(int)l.distance(p.getEyeLocation()));
											Map<AABB, Player> possibles = new HashMap<AABB, Player>();
											for(Player en : Bukkit.getOnlinePlayers()) {
												if(!en.isDead() && en.getName()!=p.getName() && !Main.getGM().getTFPlayer(en).getTeam().equals(tfp.getTeam())) {
													possibles.put(AABB.from(en), en);
												}
											}
											if(l.getBlock().getType().isSolid()) {
												//ParticleEffect.SMOKE_NORMAL.display(0,0,0, 0.01f, 3, l, 32);
												ParticleEffect.SMOKE_NORMAL.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0.01, 3);
												break ligne;
											}
											//ParticleEffect.SUSPENDED_DEPTH.display(0,0,0, 0, 2, l, 32);
											ParticleEffect.SMOKE_NORMAL.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 1);
											for(AABB box : possibles.keySet()) {
												if(box.contains(l)) {
													//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
													Main.getGM().damageTF(p, possibles.get(box), gun.getDamage(), true);
													//ParticleEffect.SMOKE_LARGE.display(0,0,0, 0, 10, l, 64);
													ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 0, 0, 0, 0, 10);
													break ligne;
												}
											}
										}
								}
							}
						}
					}, 0, gun.getShootDelay()));
				}
				if((System.currentTimeMillis() - tfp.getLast()) <= 250){
					if(releaseTasks.get(p.getName()) != null && Bukkit.getScheduler().isQueued(releaseTasks.get(p.getName()).getTaskId())){
						//p.sendMessage("canceltask: "+releaseTasks.get(p.getName()).getTaskId());
						Bukkit.getScheduler().cancelTask(releaseTasks.get(p.getName()).getTaskId());
					}
					releaseTasks.put(p.getName(), Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							//p.sendMessage("cancelled 1");
							if(finalTasks.get(p.getName()) != null && Bukkit.getScheduler().isQueued(finalTasks.get(p.getName()).getTaskId())){
								//p.sendMessage("cancelled 2");
								Bukkit.getScheduler().cancelTask(finalTasks.get(p.getName()).getTaskId());
								finalTasks.remove(p.getName());
							}
						}
					}, 6));
				} else {
					//p.sendMessage("§6long: "+(System.currentTimeMillis() - tfp.getLast()));
					if(finalTasks.containsKey(p.getName())){
						finalTasks.get(p.getName()).cancel();
						finalTasks.remove(p.getName());
					}
				}
			}
			tfp.setLast(System.currentTimeMillis()); 
		}
	}




	public static void sendBreakPacket(Location loc) {
		for (Player p2 : Bukkit.getServer().getOnlinePlayers()) {
			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, (BlockPosition) loc.getBlock(), 5);
			((CraftPlayer)p2).getHandle().playerConnection.sendPacket((Packet<?>)packet);
		}
	}


	public static ArrayList<Block> sphere(final Location center, final int radius) {
		ArrayList<Block> sphere = new ArrayList<Block>();
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
				for (int Z = -radius; Z < radius; Z++)
					if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
						final Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(), Z + center.getBlockZ());
						sphere.add(block);
					}
		return sphere;
	}

	public static Location getC4Loc(String name) {
		return c4.get(name);
	}

	public static Location placeC4(String name, Location l) {
		return c4.put(name, l);
	}

	public static boolean hasPlacedC4(String name) {
		return c4.containsKey(name);
	}

	public static void removeC4(String name) {
		c4.remove(name);
	}

	private static Class<?> getTg(String prefix, String nmsClassString) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = prefix + version + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}
}
