package fr.itspower.teamfortress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import fr.itspower.teamfortress.others.ParticleEffect;
import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.utils.AABB;
import fr.itspower.teamfortress.utils.ActionBar;
import fr.itspower.teamfortress.utils.ItemBuilder;
import fr.itspower.teamfortress.utils.Ray;
import fr.itspower.teamfortress.utils.Utils;
import fr.itspower.teamfortress.weapon.Gun;
import fr.itspower.teamfortress.weapon.GunListener;
import fr.itspower.teamfortress.weapon.GunManager;

public class AbilityManager {
	
	private static BukkitTask task;
	private static HashMap<String, Integer> cd;
	
	public static void init(){
		cd = new HashMap<String, Integer>();
	}
	
	public static void run() {
		task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for(TFPlayer p : Main.getGM().getPlayers()) {
					if(!Main.getGM().isOnline(p.getName())) continue;
					
					List<Player> damagers = Main.getGM().lastDamagers.get(p.getPlayer());
					if(damagers != null) {
						//Bukkit.broadcastMessage(p.getPlayer().getHealth()+" health "+p.getClasse().getHealth());
						if((int)p.getPlayer().getHealth() == (int)p.getClasse().getHealth()) {
							Main.getGM().setLastDamagers(p.getPlayer(), null);
						}
					}
					
					p.applyAddHP();
					if(p.getAbility().equals(AbilityState.USED) && cd.containsKey(p.getName())) {  
						//Bukkit.broadcastMessage("task cooldown capp spé");
						int dur = getCd().get(p.getName()) - 1;
						if(dur <= 0) {
							cd.remove(p.getName());
							p.setAbility(AbilityState.UNLOCKED);
							Classe classe = p.getClasse();
							if(classe.equals(Classe.Scout)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.MILK_BUCKET,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
							} else
							if(classe.equals(Classe.Sniper)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.EMERALD,2,(short)0).name("§e§lCapacité spéciale").hideE().build());
							} else
							if(classe.equals(Classe.Soldier)) {
								if(p.getPlayer().getInventory().getItem(6).getAmount()==1) {
									
								} else {
									p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.SULPHUR,2,(short)0).name("§e§lCapacité spéciale").build());
								}
							} else
							if(classe.equals(Classe.Pyroman)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.COAL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
							} else
							if(classe.equals(Classe.Demoman)) {
								if(p.getPlayer().getInventory().getItem(6).getAmount()<1)
									p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.QUARTZ,4,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(classe.equals(Classe.Engineer)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.CLAY_BALL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
							} else
							if(classe.equals(Classe.Heavy)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_APPLE,1,(short)0).name("§e§lCapacité spéciale").enchantment(Enchantment.LUCK).hideE().build());
							}else
							if(classe.equals(Classe.Medic)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST,1,(short)0).name("§e§lCapacité spéciale").build());
							}else if(classe.equals(Classe.Spy)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.RABBIT_HIDE,1,(short)0).name("§e§lCapacité spéciale").build());
							}
						} else {
							Classe c = p.getClasse();
							//Bukkit.broadcastMessage(""+c.getName());
							if(c.equals(Classe.Scout)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.MILK_BUCKET,-dur,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(c.equals(Classe.Sniper)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.EMERALD,-dur,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(c.equals(Classe.Soldier)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.SULPHUR,-dur,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(c.equals(Classe.Pyroman)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.COAL,-dur,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(c.equals(Classe.Demoman)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.QUARTZ,-dur,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(c.equals(Classe.Engineer)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.CLAY_BALL,-dur,(short)0).name("§e§lCapacité spéciale").build());
							} else
							if(c.equals(Classe.Heavy)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_APPLE,-dur,(short)0).name("§e§lCapacité spéciale").enchantment(Enchantment.LUCK).hideE().build());
							}else
							if(c.equals(Classe.Medic)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST,-dur,(short)0).name("§e§lCapacité spéciale").build());
							}else
							if(c.equals(Classe.Spy)) {
								p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.RABBIT_HIDE,-dur,(short)0).name("§e§lCapacité spéciale").build());
							}
							cd.replace(p.getName(), dur);
						}
					}
				}
			}
		}, 0, 20);
	}

	public static void setCooldown(TFPlayer p) {
		
		boolean first = false;
		if(p.getAbility().equals(AbilityState.USED) && cd.containsKey(p.getName())) {
			Bukkit.broadcastMessage("is on cd");
			int coold = cd.get(p.getName()) - 5;
			if(coold<0) {
				cd.remove(p.getName());
				p.setAbility(AbilityState.UNLOCKED);
			} else {
				cd.put(p.getName(), coold);
			}
			return;
		}
		Classe c = p.getClasse();
		if(p.getAbility().equals(AbilityState.LOCKED)) {
			Bukkit.broadcastMessage("0");
			p.setAbility(AbilityState.USED);
			first = true;
		} else if (p.getAbility().equals(AbilityState.UNLOCKED)) {
			Bukkit.broadcastMessage("1");
			if(c.equals(Classe.Scout)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.MILK_BUCKET,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(c.equals(Classe.Sniper)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.EMERALD,2,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(c.equals(Classe.Soldier)) {
				if(!cd.containsKey(p.getName()))
					p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.SULPHUR,2,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(c.equals(Classe.Pyroman)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.COAL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(c.equals(Classe.Demoman)) {
				if(!cd.containsKey(p.getName()))
					p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.QUARTZ,4,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(c.equals(Classe.Engineer)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.CLAY_BALL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(c.equals(Classe.Heavy)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_APPLE,1,(short)0).name("§e§lCapacité spéciale").enchantment(Enchantment.LUCK).hideE().build());
			}else
			if(c.equals(Classe.Medic)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST,1,(short)0).name("§e§lCapacité spéciale").build());
			}else if(c.equals(Classe.Spy)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.RABBIT_HIDE,1,(short)0).name("§e§lCapacité spéciale").build());
			}
		} else {
			Bukkit.broadcastMessage("2");
			if(p.equals(Classe.Scout)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.MILK_BUCKET,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
			} else if(c.equals(Classe.Sniper)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.EMERALD,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
			} else if(c.equals(Classe.Soldier)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.SULPHUR,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
			} else if(c.equals(Classe.Pyroman)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.COAL,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
			} else if(c.equals(Classe.Demoman)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.QUARTZ,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
			}  else if(c.equals(Classe.Engineer)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.CLAY_BALL,first?-15:-25,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else if(c.equals(Classe.Heavy)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_APPLE,first?-12:-24,(short)0).name("§e§lCapacité spéciale").enchantment(Enchantment.LUCK).hideE().build());
			}else if(c.equals(Classe.Medic)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
			}else if(c.equals(Classe.Spy)) {
				p.getPlayer().getInventory().setItem(6, new ItemBuilder(Material.RABBIT_HIDE,first?-12:-25,(short)0).name("§e§lCapacité spéciale").build());
				}
		}

		Bukkit.broadcastMessage("ends");
		switch(p.getClasse()) {
			case Scout:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Engineer:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Heavy:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),24);
				}
				break;
			case Medic:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Pyroman:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Demoman:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Sniper:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Soldier:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			case Spy:
				if(first) {
					getCd().put(p.getName(),12);
				} else {
					getCd().put(p.getName(),25);
				}
				break;
			default:
				break;
		}
	}
	
	public static boolean hasCooldown(TFPlayer tfp) {
		return getCd().get(tfp) != null;
		
	}
	
	public static void stop() {
		task.cancel();
	}
	
	public static enum AbilityState {
		LOCKED, UNLOCKED, USED
	}
	
	public static void executeAbility(TFPlayer tfp) {
		Player p = tfp.getPlayer();
		if(tfp.getAbility().equals(AbilityState.USED)) {
			//p.sendMessage("Capacité en cours de rechargement... "+getCd().get(tfp));
			return;
		}
		ItemStack i;
		switch(tfp.getClasse()) {
			case Scout:
				AbilityManager.setCooldown(tfp);
				new ActionBar("§fCapacité spéciale §e§lSpeed Race§f activée.").sendToPlayer(p);
				//ParticleEffect.CLOUD.display(0.5f,0.1f,0.5f, 0.01f, 40, p.getLocation().clone().add(0, 0.2, 0), 32);
				tfp.getPlayer().removePotionEffect(PotionEffectType.SPEED);
				tfp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 240, 2));
				break;
				
			case Engineer:
				AbilityManager.setCooldown(tfp);
				tfp.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				tfp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 4));
				i = p.getInventory().getItem(3);
				////Bukkit.broadcastMessage(i.getType().toString());
				if(i == null || !i.hasItemMeta()) {
					//Bukkit.broadcastMessage("1");
					Gun g = GunManager.getGunByName("§6§lMine");
					ItemBuilder tg = g.getItemStack();
					p.getInventory().setItem(3, tg.amount(1).name(g.getName()+GunListener.sepa+1+"/"+g.getMaxClip()).build());
				} else {
					Gun g = GunManager.getGunByName("§6§lMine");
					ItemBuilder tg = g.getItemStack();
					p.getInventory().setItem(3, tg.amount(i.getAmount()+1).name(g.getName()+GunListener.sepa+(i.getAmount()+1)+"/"+(g.getMaxClip()+1)).build());
				}
				
				new BukkitRunnable() {
					private int tg = 0;
					@Override
					public void run() {
						if(tg>24) cancel();
						org.inventivetalent.particle.ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), p.getLocation().clone().add(0, 1, 0), 0.2, 0.2, 0.2, 0.1, 20);
						tg++;
					}
				}.runTaskTimer(Main.getInstance(), 0l, 5l);
				
				break;
			case Heavy:
				AbilityManager.setCooldown(tfp);
				tfp.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
				tfp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 1));
				/*tfp.getPlayer().removePotionEffect(PotionEffectType.HEALTH_BOOST);
				tfp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 310, 1));*/
				tfp.addAddHP(8);
				//Bukkit.broadcastMessage("cap heavy");
				
				break;
			case Medic:
				AbilityManager.setCooldown(tfp);
				//p.playSound(p.getLocation(), "guns.remedy", 1f, 1f);
    			Utils.playSound(p.getLocation(), "guns.remedy");
				for(TFPlayer tfpp : Main.getGM().getPlayers()) {
					if(tfpp.getTeam().equals(tfp.getTeam())) {
						tfpp.getPlayer().playSound(tfpp.getPlayer().getLocation(), "guns.remedy", 1f, 1f);
						tfpp.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
						tfpp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 165, 1)); 
						
						new BukkitRunnable() {
							private int tg = 0;
							@Override
							public void run() {
								if(tg > 51 || !tfpp.isOnline() || !tfp.isOnline()) cancel();
								
								Location temp = tfpp.getPlayer().getLocation();
								Location point1 = tfp.getPlayer().getLocation();
							    double distance = point1.distance(temp);
							    Vector p1 = point1.toVector();
							    Vector p2 = temp.toVector();
							    Vector vector = p2.clone().subtract(p1).normalize().multiply(0.8);
							    double length = 0;
							    for (; length < distance; p1.add(vector)) {
							    	ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.LIME), p1.toLocation(Main.w).add(0, 1, 0), 100);
							        length += 0.5;
							    }
							    
								for (int j = 0; j < 40; j++) {
								      double angle1 = new Random().nextDouble() * 2.0D * 3.141592653589793D;
								      double angle2 = new Random().nextDouble() * 2.0D * 3.141592653589793D - 1.5707963267948966D;
								      double x = Math.cos(angle1) * Math.cos(angle2);
								      double z = Math.sin(angle1) * Math.cos(angle2);
								      double y = Math.sin(angle2);
								      //p.getWorld().spigot().playEffect(temp.clone().add(x * 1.5D, y * 1.5D + 1.0D, z * 1.5D), Effect.HAPPY_VILLAGER, 0, 1, 0.0F, 0.0F, 0.0F, 1.0F, 0, 200);
								    	ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.LIME), temp.clone().add(x * 1.5D, y * 1.5D + 1.0D, z * 1.5D), 100);
								}
								tg++;
							}
						}.runTaskTimer(Main.getInstance(), 0l, 3l);
						
					}
				}
				
				break;
			case Pyroman:
				AbilityManager.setCooldown(tfp);
				Snowball snowball = tfp.getPlayer().getWorld().spawn(p.getEyeLocation(), Snowball.class);
				snowball.setShooter(p);
				snowball.setVelocity(p.getLocation().getDirection().multiply(0.5));
				break;
			case Demoman:
				i = p.getItemInHand();
				//Bukkit.broadcastMessage("cap demo");
				
				if(tfp.hasGunCooldown()) { 
					//Bukkit.broadcastMessage("§chasshootcooldown");
					return;
				}
				//p.playSound(p.getLocation(), "guns.grenadelauncher", 1f, 1f);
    			Utils.playSound(p.getLocation(), "guns.grenadelauncher");
				if(i.getAmount() == 1) {
					tfp.setAbility(AbilityState.USED);
					AbilityManager.setCooldown(tfp);
					//Bukkit.broadcastMessage("cap 1");
				} else {
					i.setAmount(i.getAmount()-1);
					//Bukkit.broadcastMessage("cap 1+");
				}
				Egg egg = tfp.getPlayer().getWorld().spawn(p.getEyeLocation(), Egg.class);
				egg.setShooter(p);
				egg.setVelocity(p.getLocation().getDirection().multiply(2));
				tfp.setGunCooldownTask(20);
				return;
			case Sniper:
				////Bukkit.broadcastMessage("§fCapacité spéciale §e§lMachette§f activée.");
				i = p.getItemInHand();
				//Bukkit.broadcastMessage("cap sniper");
				if(i.getAmount() == 2) {
					i.setAmount(1);
					//Bukkit.broadcastMessage("cap 1");
				} else {
					tfp.setAbility(AbilityState.USED);
					AbilityManager.setCooldown(tfp);
					//Bukkit.broadcastMessage("cap 2");
				}
				return;
			case Soldier:
				i = p.getItemInHand();
				if(tfp.hasGunCooldown()) { 
					//Bukkit.broadcastMessage("§chasshootcooldown");
					return;
				}
				//Bukkit.broadcastMessage("cap soldier");
				//nd(p.getLocation(), "guns.scavenger", 1f, 1f);
    			Utils.playSound(p.getLocation(), "guns.scavenger");
				if(i.getAmount() == 2) {
					i.setAmount(1);
					//Bukkit.broadcastMessage("cap 1");
				} else {
					tfp.setAbility(AbilityState.USED);
					AbilityManager.setCooldown(tfp);
					//Bukkit.broadcastMessage("cap 2");
				}
				final Ray r = Ray.from(p);
				Map<AABB, Player> possibles = new HashMap<AABB, Player>();
		        for(LivingEntity en : p.getLocation().getWorld().getLivingEntities()) {
		        	if(en.getType().equals(EntityType.PLAYER)) {
		        		if(en.getLocation().distance(p.getLocation()) < 100) {
		        			possibles.put(AABB.from(en), (Player) en);
		        		}
		        	}
		        }
		        //ParticleEffect.OrdinaryColor col = new ParticleEffect.OrdinaryColor(Color.ORANGE);
				ligne:
				for(double j=0.3;j<100;j+=0.2) {
					Location l = r.getPoint(j).toLocation(Main.w);
					if(l.getBlock().getType().isSolid()) {
						//ParticleEffect.REDSTONE.display(new //ParticleEffect.OrdinaryColor(Color.RED), l, 100);
						break ligne;
					}
					for(AABB box : possibles.keySet()) {
						if(box.contains(l)) {
							if(!Main.getGM().getTFPlayer(possibles.get(box)).getTeam().equals(tfp.getTeam())) {
								Player tg = possibles.get(box);
								if(tg.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
									fr.itspower.teamfortress.others.ParticleEffect.FIREWORKS_SPARK.display(0.5f, 0.3f, 0.5f, 0.01f, 20, tg.getLocation().clone().add(0,1,0), 20.0);
									return;
								}
								//Bukkit.broadcastMessage("§a§lMATCH BULLET "+possibles.get(box).getName());
								Main.getGM().damageTF(p, tg, 1, true);
								tg.removePotionEffect(PotionEffectType.POISON);
								tg.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 110, 1));
								//ParticleEffect.REDSTONE.display(new //ParticleEffect.OrdinaryColor(Color.RED), l, 100);
								break ligne;
							}
						}
					}
					org.inventivetalent.particle.ParticleEffect.REDSTONE.sendColor(Bukkit.getOnlinePlayers(), l.clone().add(Math.random()*0.5-0.25, Math.random()*0.5-0.25, Math.random()*0.5-0.25), Color.ORANGE);
				}
				tfp.setGunCooldownTask(10);
		        return;
			case Spy:
				if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) { 
					//Bukkit.broadcastMessage("§cEFFET INVISIBLE ACTIVE...");
					return;
				}
				
				AbilityManager.setCooldown(tfp);
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				List<String> ennemis = new ArrayList<String>();
				for(TFPlayer tfloop : Main.getGM().getPlayers()) {
					if(!tfloop.getTeam().equals(tfp.getTeam())) {
						ennemis.add(tfloop.getName());
					}
				}
				meta.setOwner(ennemis.size()==0?"Notch":ennemis.get(new Random().nextInt(ennemis.size())));
				skull.setItemMeta(meta);
				p.getInventory().setHelmet(skull);
				
				p.getInventory().setChestplate(new ItemBuilder(tfp.getTeam().equals(Team.BLEU)?Material.CHAINMAIL_CHESTPLATE:Material.IRON_CHESTPLATE, 1, (short) 0).unbreakable().hideA().hideU().build());
				p.getInventory().setLeggings(new ItemBuilder(tfp.getTeam().equals(Team.BLEU)?Material.CHAINMAIL_LEGGINGS:Material.IRON_LEGGINGS, 1, (short) 0).unbreakable().hideA().hideU().build());
				p.getInventory().setBoots(new ItemBuilder(tfp.getTeam().equals(Team.BLEU)?Material.CHAINMAIL_BOOTS:Material.IRON_BOOTS, 1, (short) 0).unbreakable().hideA().hideU().build());
				
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						if(p.isOnline()) {
							p.getInventory().setHelmet(tfp.getClasse().getItem().build());
							p.getInventory().setChestplate(new ItemBuilder(tfp.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_CHESTPLATE:Material.IRON_CHESTPLATE, 1, (short) 0).unbreakable().hideA().hideU().build());
							p.getInventory().setLeggings(new ItemBuilder(tfp.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_LEGGINGS:Material.IRON_LEGGINGS, 1, (short) 0).unbreakable().hideA().hideU().build());
							p.getInventory().setBoots(new ItemBuilder(tfp.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_BOOTS:Material.IRON_BOOTS, 1, (short) 0).unbreakable().hideA().hideU().build());
						}
					}
				}, 300);
				
				break;
			default:
				break;
		}
		tfp.setAbility(AbilityState.USED);
	}

	public static void resetCooldown(TFPlayer tfp) {
		tfp.setAbility(AbilityState.LOCKED);
		getCd().replace(tfp.getName(), 0);
	}

	public static HashMap<String, Integer> getCd() {
		return cd;
	}

	public static void setCd(HashMap<String, Integer> cd) {
		AbilityManager.cd = cd;
	}
}
