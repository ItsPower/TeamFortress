package fr.itspower.teamfortress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.itspower.teamfortress.comp.CompDmg;
import fr.itspower.teamfortress.comp.CompKill;
import fr.itspower.teamfortress.comp.CompPush;
import fr.itspower.teamfortress.others.ParticleEffect;
import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.Cuboid;
import fr.itspower.teamfortress.types.Cuboid.CuboidIterator;
import fr.itspower.teamfortress.types.Status;
import fr.itspower.teamfortress.types.TFBoard;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.utils.ActionBar;
import fr.itspower.teamfortress.utils.ItemBuilder;
import fr.itspower.teamfortress.utils.Title;
import fr.itspower.teamfortress.utils.Utils;
import fr.itspower.teamfortress.weapon.GunListener;
import fr.itspower.teamfortress.weapon.GunManager;

public class GameManager {

	private Main plugin;
	public static BukkitTask lobbytask;
	public static BukkitTask gametask;
	private Status status;

	public ArrayList<String> offlines;
	public static ArrayList<TFPlayer> players;
	private static HashMap<String, TFBoard> boards;
	private static HashMap<String, Classe> nextClasses;
	HashMap<Player, List<Player>> lastDamagers;
	private HashMap<Player, Player> killers;
	private HashMap<String, Location> trampoLocation;
	HashMap<TFPlayer, List<Location>> mineLocations;
	
	public Cuboid ZoneBleu;
	public Cuboid ZoneRouge;

	private int minPlayers = 8;
	private int maxPlayers = 20;

	private Location lobby;
	private Location redspawn;
	private Location bluespawn;

	public GameManager(Main plugin) {
		this.plugin = plugin;

		setStatus(Status.DISABLED);
		players = new ArrayList<TFPlayer>();
		boards = new HashMap<String, TFBoard>();
		offlines = new ArrayList<String>();
		nextClasses = new HashMap<String, Classe>();
		lastDamagers = new HashMap<Player, List<Player>>();
		killers = new HashMap<Player, Player>();
		mineLocations = new HashMap<TFPlayer, List<Location>>();
		String loc1 = Main.getInstance().getConfig().getString("tf.lobby");
		String loc2 = Main.getInstance().getConfig().getString("tf.respawn.rouge");
		String loc3 = Main.getInstance().getConfig().getString("tf.respawn.bleu");

		try {
			Location l1 = Utils.stringToLoc(Main.getInstance().getConfig().getString("tf.safezone.bleu.1"));
			Location l2 = Utils.stringToLoc(Main.getInstance().getConfig().getString("tf.safezone.bleu.2"));
			/*Bukkit.broadcastMessage("§c§lzone bleu "
									+l1.toVector().toBlockVector().toString()+" "
									+l2.toVector().toBlockVector().toString()+" ");*/
			ZoneBleu = new Cuboid(l1,l2);
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
		}
		//Bukkit.broadcastMessage("§c§l2eme zone");
		try {
			Location l3 = Utils.stringToLoc(Main.getInstance().getConfig().getString("tf.safezone.rouge.1"));
			Location l4 = Utils.stringToLoc(Main.getInstance().getConfig().getString("tf.safezone.rouge.2"));
			/*Bukkit.broadcastMessage("§c§lzone rouge"
					+l3.toVector().toBlockVector().toString()+" "
					+l4.toVector().toBlockVector().toString());*/
			ZoneRouge = new Cuboid(l3,l4);
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
		}
		if(ZoneBleu == null || ZoneRouge == null) {
			//Bukkit.broadcastMessage("§c§lVeuillez définir les SafeZones rouge et bleu.");
		}
		
		trampoLocation = new HashMap<String, Location>();

		if(loc1 != null && loc2 != null && loc3 != null) {
			lobby = Utils.stringToLoc(loc1);
			redspawn = Utils.stringToLoc(loc2);
			bluespawn = Utils.stringToLoc(loc3);/*
			//Bukkit.broadcastMessage(lobby+"");
			//Bukkit.broadcastMessage(redspawn+"");
			//Bukkit.broadcastMessage(bluespawn+"");*/

			Main.w = getLobby().getWorld();
			if(!System.getProperty("os.name").toLowerCase().startsWith("win"))
				new BukkitRunnable() {
					public void run() {
						for(double x = redspawn.getX() - 300; x <= redspawn.getX() + 300; x++){
							for(double y = redspawn.getY() - 100; y <= redspawn.getY() + 100; y++){
								for(double z = redspawn.getZ() - 300; z <= redspawn.getZ() + 300; z++){
									Location l = new Location(redspawn.getWorld(), x, y, z);
									Material m = l.getBlock().getType();
									if(m.equals(Material.STONE_PLATE) 
											|| m.equals(Material.STONE_BUTTON)
											|| m.equals(Material.CARPET)
											|| m.equals(Material.GOLD_PLATE)
											|| m.equals(Material.IRON_PLATE)) {
										l.getBlock().setType(Material.AIR);
									}
								}
							}
						}
						for(double x = bluespawn.getX() - 300; x <= bluespawn.getX() + 300; x++){
							for(double y = bluespawn.getY() - 100; y <= bluespawn.getY() + 100; y++){
								for(double z = bluespawn.getZ() - 300; z <= bluespawn.getZ() + 300; z++){
									Location l = new Location(bluespawn.getWorld(), x, y, z);
									Material m = l.getBlock().getType();
									if(m.equals(Material.STONE_PLATE) 
											|| m.equals(Material.STONE_BUTTON)
											|| m.equals(Material.CARPET)
											|| m.equals(Material.GOLD_PLATE)
											|| m.equals(Material.IRON_PLATE)) {
										l.getBlock().setType(Material.AIR);
									}
								}
							}
						}
					}
				}.runTask(plugin);
		} else {
			Bukkit.broadcastMessage("§c§lVeuillez définir les pts de respawn et le pt de lobby.");
			lobby = new Location(Bukkit.getWorlds().get(0), 0,100,0);
			redspawn = new Location(Bukkit.getWorlds().get(0), 0,100,0);
			bluespawn = new Location(Bukkit.getWorlds().get(0), 0,100,0);
		}
	}

	public ArrayList<TFPlayer> getPlayers() {
		return players;
	}

	public void damageTF(Player damager, Player victim, double damage, boolean orbsound) {
		damager.playSound(damager.getLocation(), "random.orb", 0.5f, 1.2f);
		damageTF(damager, victim, damage);
	}

	public void damageTF(Player damager, Player victim, double damage) {
		if(victim.isDead()) return;
		TFPlayer tfdamager = getTFPlayer(damager);
		if(!getTFPlayer(victim).estSorti()) return;
		tfdamager.addDmg(damage);
		/*
		TFPlayer tfdamaged = getTFPlayer(victim);
		if(tfdamager.getTeam().equals(tfdamaged.getTeam())) return;*/
		if(victim.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			ParticleEffect.FIREWORKS_SPARK.display(0.5f, 0.3f, 0.5f, 0.01f, 20, victim.getLocation().clone().add(0,1,0), 20.0);
			return;
		}
		Utils.playSound(victim.getLocation(), "player.shot");
		List<Player> damagers = Main.getGM().getLastDamagers(victim);
		if(damagers == null) {
			//Bukkit.broadcastMessage("§4§ldamagers null");
			damagers = new ArrayList<Player>();
		}
		if(!damagers.contains(damager)) {
			//Bukkit.broadcastMessage("§4§ldamagers add "+damager.getName());
			damagers.add(damager);
		}
		Main.getGM().setLastDamagers(victim, damagers);
		victim.damage(0);
		Bukkit.broadcastMessage("§d"+damage+" dmger:"+damager.getHealth()+" vict:"+victim.getHealth());
		victim.setHealth(victim.getHealth() - damage > 0.0D ? victim.getHealth() - damage : 0.0D);
		//Bukkit.broadcastMessage("§d"+damage+" dmger:"+damager.getHealth()+" vict:"+victim.getHealth());
	}

	public Set<TFPlayer> getMines() {
		return mineLocations.keySet();
	}

	public List<Location> getMinesOf(TFPlayer p)
	{
		return mineLocations.get(p);
	}

	public void setMineLocations(TFPlayer p, List<Location> tg)
	{
		if (this.mineLocations.get(p) == null) {
			this.mineLocations.put(p, tg);
		}
		this.mineLocations.replace(p, tg);
	}

	public Location getTrampoLoc(String p) {
		return (Location)this.trampoLocation.get(p);
	}

	public void setTrampoLocation(String p, Location l) {
		if (this.trampoLocation.get(p) == null) {
			this.trampoLocation.put(p, l);
		}
		this.trampoLocation.replace(p, l);
	}

	public Player getKiller(Player p)
	{
		return (Player)this.killers.get(p);
	}

	public void setKiller(Player p, Player killer)
	{
		if (this.killers.get(p) == null) {
			this.killers.put(p, killer);
		} else {
			this.killers.replace(p, killer);
		}
	}

	public List<Player> getLastDamagers(Player p)
	{
		return lastDamagers.get(p);
	}

	public void setLastDamagers(Player victim, List<Player> killers)
	{
		if(killers == null) {
			lastDamagers.remove(victim);
			return;
		}

		if (lastDamagers.get(victim) == null) {
			lastDamagers.put(victim, killers);
		} else {
			lastDamagers.replace(victim, killers);
		}
		Bukkit.broadcastMessage("dmgers: "+lastDamagers.get(victim).size());
	}

	public void setNextClasse(Player p, Classe k)
	{
		if (nextClasses.get(p.getName()) == null) {
			nextClasses.put(p.getName(), k);
		}
		nextClasses.replace(p.getName(), k);
	}

	public Classe getNextClasse(Player p) {
		return nextClasses.containsKey(p.getName()) ?
				nextClasses.get(p.getName())
				:
					Main.getGM().getTFPlayer(p).getClasse();
	}

	public Classe getNextClasse(TFPlayer tfp) {
		return nextClasses.containsKey(tfp.getPlayer().getName()) ?
				nextClasses.get(tfp.getPlayer().getName())
				:
					tfp.getClasse();
	}

	public void removePlayer(Player p) {
		removeBoard(p.getName());
		TFPlayer tfp = getTFPlayer(p);
		players.remove(tfp);
		//Bukkit.broadcastMessage("REMOVED: "+p.getName()+" "+players.size());
		Main.getTM().removePlayer(tfp);
		if(players.size() == 0) {
			endAll();
		}
	}

	void stopLobbyTask() {
		lobbytask.cancel();
	}

	private static void stopGameTask() {
		Turrets.removeAll();
		gametask.cancel();
	}

	public void addPlayer(Player p) {

		TFPlayer tfp = new TFPlayer(p.getName());
		players.add(tfp);

		addBoard(tfp);

		if(players.size() == 1) {
			startLobbyTask();
		}

	}

	public static int timer;
	private void startLobbyTask() {
		timer = 60;
		lobbytask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(timer != 60) {
					new ActionBar("§fDémarrage de la partie dans §l"+timer+"§fs").sendToAll();
				} else {
					int nb = getMinPlayers()-players.size();
					new ActionBar("§fIl manque §l"+nb+"§f joueur...").sendToAll();
				}
				if(players.size() >= getMinPlayers()) {
					if(timer == 31) {
						new Title("", "§7Début de la partie dans §a§l30§7sec", 0, 30, 10).sendToAll();
					} else if(timer == 11) {
						new Title("", "§7Début de la partie dans §a§l10§7sec", 0, 30, 10).sendToAll();
					} else if(timer == 6) {
						new Title("", "§7Début de la partie dans §e§l5§7sec", 0, 30, 10).sendToAll();
						Utils.playSound(Main.getGM().getLobby(), "jeu.5");
						for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), "jeu.5", 1.0f, 1.0f);
					} else if(timer == 5) {
						new Title("", "§7Début de la partie dans §e§l4§7sec", 0, 30, 10).sendToAll();
						for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), "jeu.4", 1.0f, 1.0f);
					} else if(timer == 4) {
						new Title("", "§7Début de la partie dans §6§l3§7sec", 0, 30, 10).sendToAll();
						for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), "jeu.3", 1.0f, 1.0f);
					} else if(timer == 3) {
						new Title("", "§7Début de la partie dans §6§l2§7sec", 0, 30, 10).sendToAll();
						for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), "jeu.2", 1.0f, 1.0f);
					} else if(timer == 2) {
						new Title("", "§7Début de la partie dans §c§l1§7sec", 0, 30, 10).sendToAll();
						for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), "jeu.1", 1.0f, 1.0f);
					}
					timer = timer - 1;

				} else if(timer != 60) {
					timer = 60;
				}

				if(timer == 0) {
					startGameTask();
				}
			}
		}, 0, 20);
	}

	public int time;

	public void startGameTask() {
		stopLobbyTask();

		new Title("", "§7Démarrage de la partie!", 0, 30, 10).sendToAll();
		for(TFPlayer kp : getPlayers()) {
			if(!Main.getGM().isOnline(kp.getName())) continue;
			if(!kp.getTeam().equals(Team.AUCUNE)) {
				kp.tpSpawn();
			} else {
				Main.getTM().forceTeam(kp);
				kp.tpSpawn();
			}
			kp.getPlayer().playSound(kp.getPlayer().getLocation(), "jeu.fight", 1.0f, 1.0f);
			Utils.resetPlayer(kp.getPlayer());
			kp.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
			kp.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
			kp.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
			kp.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
		}

		setStatus(Status.INGAME);

		time = 0;

		for(TFPlayer tf : getPlayers()) {
			tf.getPlayer().getInventory().setHelmet(tf.getClasse().getItem().build());
			tf.getPlayer().getInventory().setChestplate(new ItemBuilder(tf.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_CHESTPLATE:Material.IRON_CHESTPLATE, 1, (short) 0).unbreakable().hideA().hideU().build());
			tf.getPlayer().getInventory().setLeggings(new ItemBuilder(tf.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_LEGGINGS:Material.IRON_LEGGINGS, 1, (short) 0).unbreakable().hideA().hideU().build());
			tf.getPlayer().getInventory().setBoots(new ItemBuilder(tf.getTeam().equals(Team.ROUGE)?Material.CHAINMAIL_BOOTS:Material.IRON_BOOTS, 1, (short) 0).unbreakable().hideA().hideU().build());
			tf.giveStuff();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+tf.getName()+" suffix &7 [0]");
		}

		AbilityManager.run();
		CartManager.run();

		/*Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				for(Location loc : CartManager.Rpath.values()) {
					fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.RED), loc, 100);
					//loc.getWorld().spigot().playEffect(p.getLocation().clone().add(x * 1.5D, y * 1.5D + 1.0D, z * 1.5D), Effect.HAPPY_VILLAGER, 0, 1, 0.0F, 0.0F, 0.0F, 1.0F, 0, 200);
				}
			}
		}, 0, 1);*/
		/*Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				for(Location loc : CartManager.Bpath.values()) {
					fr.itspower.teamfortress.others.ParticleEffect.REDSTONE.display(new fr.itspower.teamfortress.others.ParticleEffect.OrdinaryColor(Color.BLUE), loc, 100);
				}
			}
		}, 0, 1);*/

		gametask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				time = time + 1;
				for(TFPlayer kp : getPlayers()) {
					if(!Main.getGM().isOnline(kp.getName())) continue;
					if(kp.getClasse().equals(Classe.Pyroman)) {
						if(!kp.hasGunCooldown()) { 
							ItemStack i = kp.getPlayer().getInventory().getItem(0);
							if(!GunManager.finalTasks.containsKey(kp.getPlayer().getName()) && i.getType().equals(Material.STONE_HOE) && i.getAmount()<32) {
								ItemMeta im = i.getItemMeta();
								im.setDisplayName("§6§lBarbecue"+GunListener.sepa+(i.getAmount()+1)+"/32");
								i.setItemMeta(im);
								i.setAmount(i.getAmount()+1);
							}
						}
					}
					boolean vtt = kp.getTeam().equals(Team.ROUGE);
					if(!kp.estSorti()) {
						Cuboid zone = vtt?ZoneRouge:ZoneBleu;
						if(!zone.hasPlayerInside(kp.getPlayer())) {
							kp.setSorti(true);
						}
					} else {
						Cuboid z = (kp.getTeam().equals(Team.BLEU)?ZoneBleu:ZoneRouge);
						CuboidIterator t = z.iteratorr();
						for(Location l : t.getLocations()) {
							if(!l.getBlock().getType().isSolid())
								kp.getPlayer().sendBlockChange(l, Material.BARRIER, (byte)0);
						}
					}
					if(vtt) {
						CuboidIterator t = ZoneBleu.iteratorr();
						for(Location l : t.getLocations()) {
							if(!l.getBlock().getType().isSolid())
								kp.getPlayer().sendBlockChange(l, Material.BARRIER, (byte)0);
						}
					} else  {
						CuboidIterator t = ZoneRouge.iteratorr();
						for(Location l : t.getLocations()) {
							if(!l.getBlock().getType().isSolid())
								kp.getPlayer().sendBlockChange(l, Material.BARRIER, (byte)0);
						}
					}
				}
			}
		}, 20, 20);
	}

	public String getFormatedTime() {
		return String.format("%02d:%02d", time / 60, time % 60);
	}

	public ArrayList<TFPlayer> getTFTeam(Team t) {
		ArrayList<TFPlayer> list = new ArrayList<TFPlayer>();
		for(TFPlayer tfp : getPlayers()) {
			if(tfp.getTeam().equals(t)) {
				list.add(tfp);
			}
		}
		return list;
	}

	public TFPlayer getTFPlayer(Player p) {
		for(TFPlayer tfp : getPlayers()) {
			if(tfp.getName().equals(p.getName())) {
				return tfp;
			}
		}
		return null;
	}
	public boolean isOnline(Player p) {
		return !Main.getGM().offlines.contains(p.getName());
	}
	public boolean isOnline(String p) {
		return !Main.getGM().offlines.contains(p);
	}

	public void winGame(Team t) {
		ArrayList<TFPlayer> oldlist = Main.getGM().getPlayers();

		CompKill[] topkill = new CompKill[oldlist.size()];
		CompDmg[] topdmg = new CompDmg[oldlist.size()];
		CompPush[] toppush = new CompPush[oldlist.size()];
		for(int i=0; i<oldlist.size(); i++) topkill[i] = new CompKill(oldlist.get(i));
		for(int i=0; i<oldlist.size(); i++) topdmg[i] = new CompDmg(oldlist.get(i));
		for(int i=0; i<oldlist.size(); i++) toppush[i] = new CompPush(oldlist.get(i));

		Arrays.sort(topkill);
		Arrays.sort(topdmg);
		Arrays.sort(toppush);

		StringBuilder tg1 = new StringBuilder(topkill[0].getPlayer().getTeam().getColor()+topkill[0].getPlayer().getName()+" §7[§e"+topkill[0].getPlayer().getKills()+"§7]");
		for(int i=1; i<Math.min(topkill.length, 3); i++) {
			tg1.append(", "+topkill[i].getPlayer().getTeam().getColor()+topkill[i].getPlayer().getName()+" §7[§e"+topkill[i].getPlayer().getKills()+"§7]");
		}
		StringBuilder tg2 = new StringBuilder(topdmg[0].getPlayer().getTeam().getColor()+topdmg[0].getPlayer().getName()+" §7[§e"+topdmg[0].getPlayer().getDmg()+"§7]");
		for(int i=1; i<Math.min(topdmg.length, 3); i++) {
			tg2.append(", "+topdmg[i].getPlayer().getTeam().getColor()+topdmg[i].getPlayer().getName()+" §7[§e"+topdmg[i].getPlayer().getDmg()+"§7]");
		}
		StringBuilder tg3 = new StringBuilder(toppush[0].getPlayer().getTeam().getColor()+toppush[0].getPlayer().getName()+" §7[§e"+toppush[0].getPlayer().getPushsFormatted 	()+"§7]");
		for(int i=1; i<Math.min(toppush.length, 3); i++) {
			tg3.append(", "+toppush[i].getPlayer().getTeam().getColor()+toppush[i].getPlayer().getName()+" §7[§e"+toppush[i].getPlayer().getPushsFormatted()+"§7]");
		}
		new Title("§6§lFIN DE LA PARTIE!", "§eVictoire de l'équipe: "+t.toString(), 0, 200, 60).sendToAll();

		String meneur = "";
		if(topkill[0].getPlayer().getName().equals(toppush[0].getPlayer().getName())) {
			meneur = "\n"+topkill[0].getPlayer().getTeam().getColor()+topkill[0].getPlayer().getName()+"§a est Meneur de la partie!\n ";
		}

		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage("§7               §7L'équipe "+t.toString()+" §7remporte la partie!");
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(" §7» §f§lCLASSEMENTS:");
		Bukkit.broadcastMessage(" §6TOP Push: "+tg3.toString());
		Bukkit.broadcastMessage(" §6TOP Kills: "+tg1.toString());
		Bukkit.broadcastMessage(" §6TOP Dégâts: "+tg2.toString());
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(" §7» §f§lVOS SCORES:");
		for(TFPlayer kp : getPlayers()) {
			kp.getPlayer().sendMessage("   §6K/D: §e"+kp.getKills()+"/"+kp.getDeaths()+" §6Capture: §e"+kp.getPushsFormatted()+" §6Dégâts: §e"+kp.getDmg());
		}
		Bukkit.broadcastMessage(" "+meneur);

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				endAll();
			}
		}, 800);
	}

	public void endAll() {
		Bukkit.broadcastMessage("§c§lREDEMARRAGE DU SERVEUR");
		for(TFBoard b : boards.values()) {
			b.stop();
		}
		stopGameTask();
		AbilityManager.stop();
		CartManager.stop();
		Main.getInstance().init();
	}

	public void removeBoard(String p) {
		boards.get(p).stop();
		boards.remove(p);
	}

	public void reset() {

		Main.getTM().reset();
		players = new ArrayList<TFPlayer>();
		boards = new HashMap<String, TFBoard>();
		Bukkit.getScheduler().cancelAllTasks();
		Main.setGm(new GameManager(Main.getInstance()));
		Main.setTm(new TeamManager(Main.getInstance()));
		Main.setGui(new GUIManager(Main.getInstance()));

	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public Location getLobby() {
		return lobby;
	}

	public void setStatus(Status st) {
		status = st;
	}
	public Status getStatus() {
		return status;
	}
	public Location getRedSpawn() {
		return redspawn;
	}
	public Location getBlueSpawn() {
		return bluespawn;
	}

	public void addBoard(TFPlayer tf) {
		TFBoard pb = new TFBoard(tf);
		boards.put(tf.getName(), pb);
	}

	public void removeMine(TFPlayer tfp, Location location) {
		location.getBlock().setType(Material.AIR);
		List<Location> tg = getMinesOf(tfp);
		tg.remove(location);
		setMineLocations(tfp, tg);
	}
	public void removeTrampo(TFPlayer tfp, Location location) {
		location.getBlock().setType(Material.AIR);
		this.trampoLocation.remove(tfp.getName());
	}

}
