package fr.itspower.teamfortress.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.itspower.teamfortress.AbilityManager;
import fr.itspower.teamfortress.AbilityManager.AbilityState;
import fr.itspower.teamfortress.Events;
import fr.itspower.teamfortress.Main;
import fr.itspower.teamfortress.utils.ItemBuilder;
import fr.itspower.teamfortress.weapon.Gun;
import fr.itspower.teamfortress.weapon.GunManager;

public class TFPlayer {
	
	private Team team;
	private Integer kills;
	private Integer deaths;
	private String user;
	private Classe classe;
	private AbilityState Ability;
	private BukkitTask shootcd;
	private long last;
	private TFPlayer tg;

	private int AddHP;
	private int AddTimer;
	private double dmg;
	private int pushTimer;
	
	private boolean estSorti;
	
	public TFPlayer(String playername) {
		team    = Team.AUCUNE;
		kills   = 0;
		deaths  = 0;
		dmg = 0;
		last = 0;
		AddHP = 0;
		AddTimer = 0;
		user    = playername;
		classe = Classe.byId(new Random().nextInt(9)+1);
		Ability = AbilityState.LOCKED;
		shootcd = null;
		estSorti = false;
		tg = this;
	}
	
	public void addPushTimer() {
		pushTimer += 2;
		//Bukkit.broadcastMessage("PUSHTIMER: "+getName()+" "+pushTimer);
	}
	
	public void setGunCooldownTask(int ticks) {
		TFPlayer tg = this;
		shootcd = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
            	//Bukkit.broadcastMessage("guncd task cancelled");
            	tg.stopGunCooldown();
            }
        }, ticks);
	}
	public void stopGunCooldown() {
		if(shootcd != null && Bukkit.getScheduler().isQueued(shootcd.getTaskId())) Bukkit.getScheduler().cancelTask(shootcd.getTaskId());
		//Bukkit.broadcastMessage("guncd task cancelled 2");
		shootcd = null; 
	}
	public boolean hasGunCooldown() {
		if(shootcd == null) { 
			return false;
		} else if(Bukkit.getScheduler().isQueued(shootcd.getTaskId())) {
			return true;
		}
		return false;
	}
	
	public void addAddHP(int i) {
		double tmp = classe.getHealth() + AddHP + i;
		if(tmp>=40) tmp = 40;
		getPlayer().setMaxHealth(tmp);
		this.AddHP += i;
		this.AddTimer = 15;
	}
	
	public void applyAddHP() {
		if(AddHP != 0)
			if(AddTimer > 0) {
				AddTimer--;
				//Bukkit.broadcastMessage("ADDHP: "+AddTimer);
			} else {
				AddHP = 0;
				getPlayer().setMaxHealth(classe.getHealth());
			}
	}
	public long getLast() {
		return last;
	}
	public void setLast(long l) {
		last = l;
	}
	public void setTeam(Team t) {
		team = t;
	}
	public void setClasse(Classe cl) {
		classe = cl;
	}
	public Team getTeam() {
		return team;
	}
	public AbilityState getAbility() {
		return Ability;
	}
	public Integer getKills() {
		return kills;
	}
	public Classe getClasse() {
		return classe;
	}
	public Integer getDeaths() {
		return deaths;
	}
	
	
	
	public void addKill() {
		kills++;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+getName()+" suffix &7 [" + getKills()+"]");
	}

	public void addDeath() {
		deaths++;
	}
	
	public void addDmg(double i) {
		dmg += i;
	}
	
	public int getDmg() {
		return (int) dmg;
	}

	public void setAbility(AbilityState t) {
		if(t.equals(AbilityState.UNLOCKED)) {
			if(classe.equals(Classe.Scout)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.MILK_BUCKET,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(classe.equals(Classe.Sniper)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.EMERALD,2,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(classe.equals(Classe.Soldier)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.SULPHUR,2,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(classe.equals(Classe.Pyroman)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.COAL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(classe.equals(Classe.Demoman)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.QUARTZ,4,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(classe.equals(Classe.Engineer)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.CLAY_BALL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(classe.equals(Classe.Heavy)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_APPLE,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			}else
			if(classe.equals(Classe.Medic)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST,1,(short)0).name("§e§lCapacité spéciale").build());
			}else
			if(classe.equals(Classe.Spy)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.RABBIT_HIDE,1,(short)0).name("§e§lCapacité spéciale").build());
			}
		} else if(t.equals(AbilityState.USED)) {/*
			if(classe.equals(Classe.Scout)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.MILK_BUCKET,1,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(classe.equals(Classe.Sniper)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.EMERALD,2,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(classe.equals(Classe.Soldier)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.SULPHUR,2,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(classe.equals(Classe.Pyroman)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.COAL,1,(short)0).name("§e§lCapacité spéciale").build());
			} else
			if(classe.equals(Classe.Demoman)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.QUARTZ,4,(short)0).name("§e§lCapacité spéciale").build());
			}  else
			if(classe.equals(Classe.Engineer)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.CLAY_BALL,1,(short)0).name("§e§lCapacité spéciale").hideE().build());
			} else
			if(classe.equals(Classe.Heavy)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GOLDEN_APPLE,1,(short)0).name("§e§lCapacité spéciale").enchantment(Enchantment.LUCK).hideE().build());
			}else
			if(classe.equals(Classe.Medic)) {
				getPlayer().getInventory().setItem(6, new ItemBuilder(Material.GLOWSTONE_DUST,1,(short)0).name("§e§lCapacité spéciale").build());
			}*/
		} else {
			getPlayer().getInventory().setItem(6, new ItemBuilder(Material.BLAZE_ROD,1,(short)0).name("§c§lCapacité spéciale").lore("§7Débloquez là en faisant un kill.").build());
		}
		Ability = t;
	}
	
	public boolean isOnline() {
		Player p = Bukkit.getPlayer(user);
		if(p != null) {
			if(p.isOnline()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean estSorti() {
		return estSorti;
	}
	
	public void setSorti(boolean t) {
		estSorti = t;
	}
	
	public String getName() {
		return user;
	}
	
	public void tpSpawn() {
		if(team.equals(Team.ROUGE)) {
			getPlayer().teleport(Main.getGM().getRedSpawn());
		}
		else if(team.equals(Team.BLEU)) {
			getPlayer().teleport(Main.getGM().getBlueSpawn());
		}
		else {
			Bukkit.broadcastMessage("§c§lPrysmKOTH erreur: 01");
		}
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(user);
	}

	public void giveStuff() {
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				Player p = getPlayer();
				//Color c = getTeam().equals(Team.ROUGE) ? Color.RED : Color.BLUE;
				/*p.getInventory().setHelmet(new ItemStack(Material.AIR));
		        p.getInventory().setChestplate(new ItemStack(Material.AIR));
		        p.getInventory().setLeggings(new ItemStack(Material.AIR));
		        p.getInventory().setBoots(new ItemStack(Material.AIR));*/
				AbilityManager.resetCooldown(tg);
				
				p.getInventory().setHelmet(classe.getItem().build());
				
				for(Gun g : GunManager.getGunlist()) {
					if(g.getClassType().equals(getClasse())) {
						ItemBuilder ib = g.getItemStack();
						if(classe.equals(Classe.Engineer) && g.getSlot()==2) {
							p.setGameMode(GameMode.SURVIVAL);
							if(team.equals(Team.ROUGE)) {
								ib.type(Material.IRON_PLATE);
							} else {
								ib.type(Material.GOLD_PLATE);
							}
							p.setGameMode(GameMode.SURVIVAL);
						}
						p.getInventory().setItem(g.getSlot(), ib.build());
						GunManager.cd.remove(p.getName()+"@"+g.getSlot());
					}
					
				}
				p.getInventory().setItem(8, new ItemBuilder(Material.NAME_TAG, 1, (short)0).name("§7Changer de classe").build());
				p.setMaxHealth(classe.getHealth());
				p.setHealth(classe.getHealth());
				p.setWalkSpeed(classe.getSpeed());
				if(getClasse().equals(Classe.Spy)) {
					p.setGameMode(GameMode.SURVIVAL);
				} 
				if(!getClasse().equals(Classe.Scout)) {
					Events.jumpcd.remove(p.getName());
					p.setFlying(false);
				    p.setAllowFlight(false);
				}
				setAbility(AbilityState.LOCKED);
		        p.updateInventory();
			}
		}, 10);
	}

	public int getPushs() {
		return pushTimer;
	}
	
	public String getPushsFormatted() {
		return (int) (pushTimer / 10) +"s";
	}
}
