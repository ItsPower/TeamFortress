package fr.itspower.teamfortress.weapon;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.particle.ParticleEffect;

import fr.itspower.teamfortress.AbilityManager;
import fr.itspower.teamfortress.Main;
import fr.itspower.teamfortress.Turrets;
import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.Status;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.TFTurret;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.utils.ItemBuilder;
import fr.itspower.teamfortress.utils.TFCooldown;
import fr.itspower.teamfortress.utils.Utils;

public class GunListener implements Listener {

	public static HashSet<String> zoom = new HashSet<String>();
	public static HashSet<String> heavyscoop = new HashSet<String>();

	private ItemStack itemrouge = new ItemBuilder(Material.INK_SACK, 1, (short)1).name("§7Choisir l'équipe: §cRouge").build();
	private ItemStack itembleu = new ItemBuilder(Material.INK_SACK, 1, (short)4).name("§7Choisir l'équipe: §9Bleu").build();
	public static String sepa = " §7|§e ";

	/*@EventHandler
	public void event(PlayerAnimationEvent e) {
		if(Main.getGM().getStatus().equals(Status.INGAME)) {
			if(e.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
				Player p = e.getPlayer();
				ItemStack i = p.getItemInHand();
				if(!i.hasItemMeta()) return;
				String[] name = i.getItemMeta().getDisplayName().split(sepa);
				Gun g = GunManager.getGunByName(name[0]);
				if(g == null) return;
				TFPlayer tfp = Main.getGM().getTFPlayer(p);
				if(tfp.getClasse().equals(Classe.Sniper)) {
					if(zoom.contains(p.getName()) && !g.isScopeable()) {
						zoom.remove(p.getName());
						p.removePotionEffect(PotionEffectType.SLOW);
						p.getInventory().setHelmet(new ItemStack(Classe.Sniper.getItem().build()));
						return;
					}
					if(g.isScopeable())
						if(!zoom.contains(p.getName())) {
							zoom.add(p.getName());
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5));
							p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
						} else {
							zoom.remove(p.getName());
							p.removePotionEffect(PotionEffectType.SLOW);
							p.getInventory().setHelmet(new ItemStack(Classe.Sniper.getItem().build()));
						}
				} else if(tfp.getClasse().equals(Classe.Heavy)) {
					if(g.getSlot()==0) {
						if(heavyscoop.contains(p.getName())) {
							heavyscoop.remove(p.getName());
							p.removePotionEffect(PotionEffectType.SLOW);
							ItemStack tg = p.getInventory().getItemInHand();
							tg.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
							p.getInventory().setItemInHand(tg);
							//Bukkit.broadcastMessage("§a§lheavy no scoop");
						} else {
							heavyscoop.add(p.getName());
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 1));
							ItemStack tg = p.getInventory().getItemInHand();
							tg.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
							p.getInventory().setItemInHand(tg);
							//Bukkit.broadcastMessage("§a§lheavy scoop");
						}
					}
				}
			}
		}
	}
	 */

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(!Main.isEnabled) {
			e.setCancelled(true);
			return;
		}
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.PHYSICAL)) return;
		if(Main.getGM().getStatus().equals(Status.WAITING) || Main.getGM().getStatus().equals(Status.STARTING)) {
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				ItemStack item = p.getItemInHand();
				if(item.equals(itemrouge)) {
					Main.getTM().changeTeam(p, Team.ROUGE);
				} else
					if(item.equals(itembleu)) {
						Main.getTM().changeTeam(p, Team.BLEU);
					} else 
						if(item.getType().equals(Material.NAME_TAG)) {
							Main.getGUI().openClassesGUI(p);
						}
			}

		} else if(Main.getGM().getStatus().equals(Status.INGAME)) {

			ItemStack i = p.getItemInHand();

			if ((!i.hasItemMeta()) || (!i.getItemMeta().hasDisplayName())) {
				return;
			}

			TFPlayer tfp = Main.getGM().getTFPlayer(p);

			if(!tfp.estSorti()) {
				e.setCancelled(true);
				return;
			}

			if (i.getType().equals(Material.EMERALD) ) {
				if(!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
					
					Utils.playSound(p.getLocation(), "guns.melee");
				}
				return;
			}
			if(p.getInventory().getHeldItemSlot() == 6 && !i.getType().equals(Material.BLAZE_ROD) && !i.getType().equals(Material.GOLDEN_APPLE)) {
				if(i.getType().equals(Material.QUARTZ) && !(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) return;
				if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
					if(tfp.getClasse().equals(Classe.Soldier))
						return;
				AbilityManager.executeAbility(tfp);
				return;
			}
			if(e.getPlayer().getInventory().getHeldItemSlot() == 8) {
				Main.getGUI().openClassesGUI(p);
				return;
			}
			String[] name = i.getItemMeta().getDisplayName().split(sepa);
			Gun g = GunManager.getGunByName(name[0]);
			if(g == null) return;
			if(/*i.getType().equals(Material.FLINT) ||*/ i.getType().equals(Material.CARPET)) return; //TODO ICI Détonateur FLINT
			if(!g.leftClick() && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {

				if(g.getType().equals(WeaponType.AUTOMATIC) 
						|| g.getType().equals(WeaponType.SEMIAUTO)
						|| g.getType().equals(WeaponType.LAUNCHER)
						|| g.getType().equals(WeaponType.GRENADE)) {
					if(GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
						//Bukkit.broadcastMessage("§chasreloadcooldown");
						return;
					}
					if(g.getClassType().equals(Classe.Medic) && g.getSlot()==0) { // medecine portable
						boolean isOK = false;
						//Bukkit.broadcastMessage("check");
						for(TFPlayer tfpp : Main.getGM().getPlayers()) {
							if(tfpp.getName().equals(tfp.getName())) continue;
							//Bukkit.broadcastMessage((tfpp.getPlayer().getLocation().distance(p.getLocation()) < 10)+" "+tfpp.getTeam().equals(tfp.getTeam()));
							if(tfpp.getPlayer().getLocation().distance(p.getLocation()) < 10 && tfpp.getTeam().equals(tfp.getTeam())) {
								isOK = true;
							}
						}
						if(!isOK) {
							//Bukkit.broadcastMessage("§cPas de joueurs alliés a proximité");
							return;
						}
					}
					if(!g.hasUnlimitedAmmo()) {
						if(GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
							//Bukkit.broadcastMessage("§4hasshootcooldown");
							return;
						}
						if(tfp.hasGunCooldown()) { 
							//Bukkit.broadcastMessage("§chasshootcooldown");
							return;
						}
						int mun = Integer.parseInt(name[2].split("/")[0]);
						ItemMeta im = i.getItemMeta();
						int newmun = (mun-1);
						im.setDisplayName(g.getName()+sepa+newmun+"/"+g.getMaxClip());
						i.setItemMeta(im);

						if(newmun == 0) {
							//Bukkit.broadcastMessage("plus de mun ");
							tfp.stopGunCooldown();
							GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
						} else {
							int ig = (mun-1);
							i.setAmount(ig);
							tfp.setGunCooldownTask(g.getShootDelay());
							//Bukkit.broadcastMessage("guncooldowntask = "+g.getShootDelay());
						}
						//Bukkit.broadcastMessage("tg "+tfp.hasGunCooldown());
					}
					GunManager.shoot(p, g, tfp);
				} else if(g.getType().equals(WeaponType.POTION)) {
					e.setCancelled(true);
					if(GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
						//Bukkit.broadcastMessage("§4hasshootcooldown");
						return;
					}
					if(p.getHealth()+8>p.getMaxHealth()) {
						p.setHealth(p.getMaxHealth());
					} else {
						p.setHealth(p.getHealth()+8);
					}
					//ParticleEffect.HEART.display(0.6f,0.5f,0.6f, 0, 25, p.getLocation(), 32);
					GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
				} else {
					if(tfp.getClasse().equals(Classe.Spy) && g.getName().contains("Détonateur")) {
						if(GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
							//Bukkit.broadcastMessage("§4hasshootcooldown");
							return;
						}
						if(!GunManager.hasPlacedC4(p.getName())) {
							//Bukkit.broadcastMessage("§4§lpas de C4 placé ?");
							return;
						}
						//p.playSound(p.getLocation(), g.getSound(), 10f, 1f);
						Utils.playSound(p.getLocation(), g.getSound());
						g = GunManager.getGunByName("§6§lC4");
						Location l = GunManager.getC4Loc(p.getName());
						l.getWorld().playSound(l, Sound.EXPLODE, 1, 0);
						Team t = tfp.getTeam();
						for(TFPlayer tfpl : Main.getGM().getPlayers()) {
							if(tfpl.getPlayer().getLocation().distance(l) < 3 && !tfpl.getTeam().equals(t)) {
								//Bukkit.broadcastMessage("§4§lC4 "+g.getDamage());
								Main.getGM().damageTF(tfp.getPlayer(), tfpl.getPlayer(), g.getDamage());
							}
						}
						Utils.playSound(p.getLocation(), g.getSound());
						//p.playSound(p.getLocation(), g.getSound(), 10f, 1f);
						ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 100);
						ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 50);
						ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 100);
						l.getBlock().setType(Material.AIR);
						p.getInventory().setItem(1, g.getItemStack().build());
						GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
						GunManager.removeC4(p.getName());
					} else if(tfp.getClasse().equals(Classe.Engineer) && g.getSlot() == 0) {
						GunManager.shoot(p, g, tfp);
					}
				}
			} else if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if(g.isScopeable()) {
					//Bukkit.broadcastMessage("§6"+zoom.contains(p.getName()));
					if(!zoom.contains(p.getName())) {
						zoom.add(p.getName());
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5));
						p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
					} else {
						zoom.remove(p.getName());
						p.removePotionEffect(PotionEffectType.SLOW);
						p.getInventory().setHelmet(new ItemStack(Classe.Sniper.getItem().build()));
					}
				} else {
					Bukkit.broadcastMessage("left");
					if(g.getType().equals(WeaponType.MELEE)) {
						if(tfp.getClasse().equals(Classe.Spy) && g.getSlot() == 0) {
							if(GunManager.timeout.contains(p.getName())) {
								for(int tg=0; tg<40; tg++) {
									fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.PURPLE), p.getEyeLocation().clone().add(Math.random()*1.5-0.5, Math.random()*1.5-1.2, Math.random()*1.5-0.5), 100);
								}
							}
						} else {
							Utils.playSound(p.getLocation(), "guns.melee");
						}
					}
					if(g.getClassType().equals(Classe.Medic) && g.getSlot()==0) { // medecine portable
						if(GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
							//Bukkit.broadcastMessage("§4hasshootcooldown");
							return;
						}
						//Bukkit.broadcastMessage("§a§lMATCH HEAL self");
						//p.playSound(p.getLocation(), g.getSound(), 10f, 1f);
						Utils.playSound(p.getLocation(), g.getSound());
						tfp.addAddHP(8);
						p.removePotionEffect(PotionEffectType.REGENERATION);
						p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
						ParticleEffect.VILLAGER_HAPPY.send(Bukkit.getOnlinePlayers(), p.getLocation().clone().add(0, 1, 0), 1, 1, 1, 0.0, 3);
						GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
						for (int j = 0; j < 100; j++){
							double angle1 = new Random().nextDouble() * 2.0D * 3.141592653589793D;
							double angle2 = new Random().nextDouble() * 2.0D * 3.141592653589793D - 1.5707963267948966D;
							double x = Math.cos(angle1) * Math.cos(angle2);
							double z = Math.sin(angle1) * Math.cos(angle2);
							double y = Math.sin(angle2);
							p.getWorld().spigot().playEffect(p.getLocation().clone().add(x * 1.5D, y * 1.5D + 1.0D, z * 1.5D), Effect.HAPPY_VILLAGER, 0, 1, 0.0F, 0.0F, 0.0F, 1.0F, 0, 200);
						}
					} else 
						if(g.getSlot()==0 && g.getClassType().equals(Classe.Heavy)) {
							if(heavyscoop.contains(p.getName())) {
								heavyscoop.remove(p.getName());
								p.removePotionEffect(PotionEffectType.SLOW);
								ItemStack tg = p.getInventory().getItemInHand();
								tg.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
								p.getInventory().setItemInHand(tg);
								//Bukkit.broadcastMessage("§a§lheavy no scoop");
							} else {
								heavyscoop.add(p.getName());
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 1));
								ItemStack tg = p.getInventory().getItemInHand();
								tg.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
								p.getInventory().setItemInHand(tg);
								//Bukkit.broadcastMessage("§a§lheavy scoop");
							}
						} else if(g.getClassType().equals(Classe.NULL) && g.getSlot()==0) { // Télécommande
							if(GunManager.cd.containsKey(p.getName()+"@"+g.getSlot())) {
								//Bukkit.broadcastMessage("§4hasshootcooldown");
								return;
							}
							//Bukkit.broadcastMessage("§dTélécommande");
							ArmorStand tftur = Turrets.getTFTurret(p);
							//Bukkit.broadcastMessage((tftur == null)+" ");
							if(tftur != null) {
								if(p.getLocation().distance(tftur.getLocation())>15) {
									p.sendMessage("§cVous êtes trop loin de votre tourelle.");
									return;
								}
								TFTurret.shoot(p, tftur);
								GunManager.cd.put(p.getName()+"@"+g.getSlot(), new TFCooldown(p.getName()+"@"+g.getSlot(), p, g));
							}
						} else if(g.getClassType().equals(Classe.Engineer) && g.getSlot() == 4) {
							if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
								Block b = e.getClickedBlock();
								Material targettramp = tfp.getTeam().equals(Team.ROUGE) ? Material.IRON_PLATE : Material.GOLD_PLATE;
								if(b.getType().equals(targettramp)) { // trampo
									//Bukkit.broadcastMessage("tg " + Main.getGM().getTrampoLoc(p.getName()));
									if(Main.getGM().getTrampoLoc(p.getName()) != null) {
										Main.getGM().removeTrampo(tfp, b.getLocation());
										g = GunManager.getGunByName("§6§lTrampoline");
										ItemBuilder tg = g.getItemStack();
										p.getInventory().setItem(2, tg.type(tfp.getTeam().equals(Team.ROUGE) ? Material.IRON_PLATE : Material.GOLD_PLATE).amount(1).name(g.getName()+sepa+1+"/"+g.getMaxClip()).build());
									}
								} else if(b.getType().equals(Material.STONE_PLATE)) { // mines
									//Bukkit.broadcastMessage("tg1");
									if(Main.getGM().getMinesOf(tfp) == null) return;
									if(Main.getGM().getMinesOf(tfp).contains(b.getLocation())) {
										//Bukkit.broadcastMessage("tg2");
										Main.getGM().removeMine(tfp, b.getLocation());
										//Bukkit.broadcastMessage("tg3");
										i = p.getInventory().getItem(3);
										if(i == null || !i.hasItemMeta()) {
											g = GunManager.getGunByName("§6§lMine");
											ItemBuilder tg = g.getItemStack();
											p.getInventory().setItem(3, tg.amount(1).name(g.getName()+sepa+1+"/"+g.getMaxClip()).build());
										} else {
											name = i.getItemMeta().getDisplayName().split(sepa);
											g = GunManager.getGunByName(name[0]);
											int mun = Integer.parseInt(name[2].split("/")[0]);
											ItemMeta im = i.getItemMeta();
											int newmun = (mun+1);
											im.setDisplayName(g.getName()+sepa+newmun+"/"+g.getMaxClip());
											i.setItemMeta(im);
											i.setAmount(i.getAmount()+1);
										}
									}
								}
							}
						}
				}
			}
		}
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void boumBlock(BlockExplodeEvent e)
	{
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerSlotSwitch(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		if(Main.getGM().getStatus().equals(Status.INGAME)) {
			ItemStack i = p.getInventory().getItem(event.getPreviousSlot());
			if(i != null) {
				if(GunManager.finalTasks.containsKey(p.getName())) {
					GunManager.finalTasks.get(p.getName()).cancel();
					GunManager.finalTasks.remove(p.getName());
				}
				if(i.getItemMeta().getDisplayName().contains("Sniper")) {
					zoom.remove(p);
					p.removePotionEffect(PotionEffectType.SLOW);
					p.getInventory().setHelmet(new ItemStack(Classe.Sniper.getItem().build()));
				} else if(heavyscoop.contains(p.getName())) {
					heavyscoop.remove(p.getName());
					p.removePotionEffect(PotionEffectType.SLOW);
					ItemStack tg = p.getInventory().getItemInHand();
					tg.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
					p.getInventory().setItemInHand(tg);
					//Bukkit.broadcastMessage("§a§lheavy no scoop");
				}
			}
		}
	}
}
