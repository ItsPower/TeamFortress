package fr.itspower.teamfortress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.Cuboid;
import fr.itspower.teamfortress.types.Cuboid.CuboidIterator;
import fr.itspower.teamfortress.types.Status;
import fr.itspower.teamfortress.utils.Utils;
import fr.itspower.teamfortress.weapon.Gun;
import fr.itspower.teamfortress.weapon.GunListener;
import fr.itspower.teamfortress.weapon.GunManager;
import fr.itspower.teamfortress.weapon.WeaponType;

public class Main extends JavaPlugin {

	private static Main plugin;
	private static TeamManager tm;
	private static GameManager gm;
	private static GUIManager gui;
	public static World w;
	public static boolean isEnabled;
	
	private final static String prefix = "§f§lTeamFortress §7";
	public static WorldGuardPlugin wgPl;
	public static WorldEdit wePl;
	
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(new Events(), this);
		getServer().getPluginManager().registerEvents(new GunListener(), this);
		getCommand("teamfortress").setExecutor(new Commandes());
		init();
	}
	
	@SuppressWarnings("deprecation")
	void init() {
		
		this.reloadConfig();
		
		setGm(new GameManager(this));
		setTm(new TeamManager(this));
		setGui(new GUIManager(this));
		AbilityManager.init();
		Turrets.init();
		Events.init();
		CartManager.init();
		
		if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            this.getLogger().severe("Error: " + "WorldEdit not found!");
        }

        if (this.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            this.getLogger().severe("Error: " + "WorldGuard not found!");
        }

        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().severe("Error: " + "Vault not found!");
        }
		
		for(Player p : Bukkit.getOnlinePlayers()) {
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
		    Cuboid z = getGM().ZoneBleu;
			CuboidIterator it = z.iteratorr();
			for(Location l : it.getLocations()) {
				if(!l.getBlock().getType().isSolid())
					p.sendBlockChange(l, Material.AIR, (byte)0);
			}
		    z = getGM().ZoneRouge;
			it = z.iteratorr();
			for(Location l : it.getLocations()) {
				if(!l.getBlock().getType().isSolid())
					p.sendBlockChange(l, Material.AIR, (byte)0);
			}
		}
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule announceAdvancements false");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule spectatorsGenerateChunks false");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doTileDrops false");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule mobGriefing false");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobSpawning false");
		
		
		
		
		double pompe = 0.015;
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lCanon scié", null, Material.STONE_SPADE, 
				"guns.canonscie", 4/*dmg*/, 15/*dist*/, 0.0/*recul*/, 2/*clip*/, 8/*shootd*/, 48/*reloadd*/,
				0.03/*accu*/, false/*scop*/, true/*shotg*/, false/*snip*/, 3/*bullamou*/, Classe.Scout, 
				0/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.AUTOMATIC, "§6§lDéfenseur", null, Material.WOOD_PICKAXE, 
				"guns.defenseur", 2/*dmg*/, 30/*dist*/, 0/*recul*/, 0/*clip*/, 13/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Scout, 
				2/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.MELEE, "§6§lBatte", null, Material.WOOD_HOE, 
				"", 5/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Scout, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		
		
		
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lSniper", null, Material.STICK, 
				"guns.sniper", 10/*dmg*/, 60/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 80/*reloadd*/,
				0/*accu*/, true/*scop*/, false/*shotg*/, true/*snip*/, 1/*bullamou*/, Classe.Sniper, 
				0/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.AUTOMATIC, "§6§lCarabine", null, Material.IRON_HOE, 
				"guns.carabine", 2/*dmg*/, 30/*dist*/, 0/*recul*/, 0/*clip*/, 13/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Sniper, 
				1/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.POTION, "§6§lPotion de Soin", null, Material.POTION, 
				"", 1/*1=soin*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 440/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Sniper, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));
		

		GunManager.registerGun(new Gun(WeaponType.LAUNCHER, "§6§lLance Roquette", null, Material.STONE_AXE, 
				"guns.liberateur", 7/*dmg*/, 100/*dist*/, 0.0/*recul*/, 1/*clip*/, 0/*shootd*/, 144/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Soldier, 
				0/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lFusil a pompe", null, Material.WOOD_AXE, 
				"guns.canonscie", 4/*dmg*/, 15/*dist*/, 0.0/*recul*/, 1/*clip*/, 8/*shootd*/, 64/*reloadd*/,
				pompe/*accu*/, false/*scop*/, true/*shotg*/, false/*snip*/, 5/*bullamou*/, Classe.Soldier, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.GRENADE, "§6§lGrenade Flash", null, Material.IRON_INGOT, 
				"", 1/*1=flash*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 340/*340reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Soldier, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));		

		
		GunManager.registerGun(new Gun(WeaponType.AUTOMATIC, "§6§lBarbecue", null, Material.STONE_HOE, 
				"", 0.01/*dmg*/, 6/*dist*/, 0.0/*recul*/, 32/*clip*/, 5/*shootd*/, 200/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Pyroman, 
				0/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lFusil a pompe", null, Material.WOOD_AXE, 
				"guns.canonscie", 4/*dmg*/, 15/*dist*/, 0.0/*recul*/, 1/*clip*/, 8/*shootd*/, 64/*reloadd*/,
				pompe/*accu*/, false/*scop*/, true/*shotg*/, false/*snip*/, 5/*bullamou*/, Classe.Pyroman, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.MELEE, "§6§lHache d'Incendie", null, Material.GOLD_AXE, 
				"", 3/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Pyroman, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));
		

		GunManager.registerGun(new Gun(WeaponType.GRENADE, "§6§lDynamite", null, Material.CLAY_BRICK, 
				"", 2/*1=flash, 2=dynamite*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 400/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Demoman, 
				0/*slot*/, false/*unlimammo*/, false/*headshot*/));	
		GunManager.registerGun(new Gun(WeaponType.GRENADE, "§6§lFumigène", null, Material.NETHER_BRICK_ITEM, 
				"", 3/*1=flash, 2=dynamite*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 280/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Demoman, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lFusil a pompe§a", null, Material.WOOD_AXE, 
				"guns.canonscie", 4/*dmg*/, 15/*dist*/, 0.0/*recul*/, 1/*clip*/, 8/*shootd*/, 64/*reloadd*/,
				pompe/*accu*/, false/*scop*/, true/*shotg*/, false/*snip*/, 5/*bullamou*/, Classe.Demoman, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lPistolet de détresse", null, Material.DIAMOND_SPADE, 
				"guns.detresse", 0/*dmg*/, 10/*dist*/, 0.0/*recul*/, 1/*clip*/, 0/*shootd*/, 80/*reloadd*/,
				0.03/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Demoman, 
				3/*slot*/, false/*unlimammo*/, false/*headshot*/));
		

		GunManager.registerGun(new Gun(WeaponType.AUTOMATIC, "§6§lLa tornade", null, Material.GOLD_PICKAXE, 
				"guns.latornade", 2/*dmg*/, 30/*dist*/, 0.1/*recul*/, 0/*clip*/, 3/*shootd*/, 0/*reloadd*/,
				0.05/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Heavy, 
				0/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lFusil a pompe", null, Material.WOOD_AXE, 
				"guns.canonscie", 4/*dmg*/, 15/*dist*/, 0.0/*recul*/, 1/*clip*/, 8/*shootd*/, 64/*reloadd*/,
				pompe/*accu*/, false/*scop*/, true/*shotg*/, false/*snip*/, 5/*bullamou*/, Classe.Heavy, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.MELEE, "§6§lPoings Américains", null, Material.IRON_SPADE, 
				"", 3/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Heavy, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));
		

		GunManager.registerGun(new Gun(WeaponType.MELEE, "§6§lPoignard", null, Material.DIAMOND_PICKAXE, 
				"", 10/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 80/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Spy, 
				0/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.NULL, "§6§lC4", null, Material.STONE_BUTTON, 
				"", 16/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 340/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Spy, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.NULL, "§6§lDétonateur", null, Material.FLINT, 
				"guns.detonateur", 0/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 40/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.NULL, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lRevolver", null, Material.GOLD_HOE, 
				"guns.revolver", 5/*dmg*/, 30/*dist*/, 0.0/*recul*/, 1/*clip*/, 0/*shootd*/, 32/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Spy, 
				2/*slot*/, false/*unlimammo*/, true/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lTime Out", null, Material.GOLD_INGOT, 
				"", 0/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 320/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Spy, 
				3/*slot*/, false/*unlimammo*/, false/*headshot*/));
		

		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lMedecine portable", null, Material.IRON_PICKAXE, 
				"guns.remedy", 10/*dmg*/, 10/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 100/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Medic, 
				0/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.SEMIAUTO, "§6§lPistolet Tranquillisant", null, Material.WOOD_SPADE, 
				"guns.arbalete", 1/*dmg*/, 20/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 64/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Medic, 
				1/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.MELEE, "§6§lScie à amputation", null, Material.IRON_AXE, 
				"", 4/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Medic, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));
		

		GunManager.registerGun(new Gun(WeaponType.NULL, "§6§lCanon montable", null, Material.CARPET, 
				"", 1/*dmg*/, 30/*dist*/, 0.0/*recul*/, 0/*clip*/, 1/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Engineer, 
				0/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.NULL, "§6§lTélécommande", null, Material.WHEAT, 
				"", 1/*dmg*/, 30/*dist*/, 0.0/*recul*/, 1/*clip*/, 0/*shootd*/, 140/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.NULL, 
				0/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.AUTOMATIC, "§6§lDéfenseur", null, Material.WOOD_PICKAXE, 
				"guns.defenseur", 2/*dmg*/, 30/*dist*/, 0/*recul*/, 0/*clip*/, 10/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 1/*bullamou*/, Classe.Engineer, 
				1/*slot*/, true/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.NULL, "§6§lTrampoline", null, Material.STONE, 
				"", 0/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Engineer, 
				2/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.NULL, "§6§lMine", null, Material.STONE_PLATE, 
				"", 0/*dmg*/, 0/*dist*/, 0/*recul*/, 3/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Engineer, 
				3/*slot*/, false/*unlimammo*/, false/*headshot*/));
		GunManager.registerGun(new Gun(WeaponType.MELEE, "§6§lClé à molette", null, Material.GOLD_NUGGET, 
				"", 6/*dmg*/, 0/*dist*/, 0/*recul*/, 1/*clip*/, 0/*shootd*/, 0/*reloadd*/,
				0/*accu*/, false/*scop*/, false/*shotg*/, false/*snip*/, 0/*bullamou*/, Classe.Engineer, 
				4/*slot*/, false/*unlimammo*/, false/*headshot*/));
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println("DEMARRAGE DE TEAMFORTRESS TERMINE, BON JEU :D");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				System.out.println(" ");
				isEnabled = true;
				getGM().setStatus(Status.WAITING);
			}
		}, 20);
	}
	
	public void onDisable() {
		plugin = null;
	}

	public static TeamManager getTM() {
		return tm;
	}
	
	public static GameManager getGM() {
		return getGm();
	}
	
	public static GUIManager getGUI() {
		return gui;
	}
	
	public static Main getInstance() {
	    return plugin;
	}
	
	public static String getPrefix() {
		return prefix;
	}

	public static GameManager getGm() {
		return gm;
	}

	public static void setGm(GameManager gm) {
		Main.gm = gm;
	}

	public static void setTm(TeamManager tm) {
		Main.tm = tm;
	}

	public static void setGui(GUIManager guiManager) {
		Main.gui = guiManager;
		
	}
}
