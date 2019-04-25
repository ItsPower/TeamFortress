package fr.itspower.teamfortress.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.itspower.teamfortress.Main;
import fr.itspower.teamfortress.weapon.GunListener;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;

public class Utils {

	@SuppressWarnings("deprecation")
	public static String blockToString(Block bloc) {
        return bloc.getWorld().getName() + "!" + bloc.getLocation().getX() + "!" + bloc.getLocation().getY() + "!" + bloc.getLocation().getZ() + "!" + bloc.getTypeId() + "!" + bloc.getData();
    }
    
	public static String locToString(Location loc){
        return loc.getWorld().getName() + "!" + loc.getX() + "!" + loc.getY() + "!" + loc.getZ() + "!" + loc.getYaw() + "!" + loc.getPitch();
    }
	
    public static Location stringToLoc(String str) {
    	if(str == null) return Bukkit.getWorld("TF").getSpawnLocation();
        return new Location(Bukkit.getWorld(str.split("!")[0]), Double.parseDouble(str.split("!")[1]), Double.parseDouble(str.split("!")[2]), Double.parseDouble(str.split("!")[3]), Float.parseFloat(str.split("!")[4]), Float.parseFloat(str.split("!")[5]));
    }
    
	public static void resetPlayer(Player p) {
		if(GunListener.heavyscoop.contains(p.getName())) {
			GunListener.heavyscoop.remove(p.getName());
		}
		if(GunListener.zoom.contains(p.getName())) {
			GunListener.zoom.remove(p.getName());
		}
		p.removePotionEffect(PotionEffectType.SLOW);
		p.removePotionEffect(PotionEffectType.SPEED);
		p.setMaxHealth(20);
    	p.setLevel(0);
    	p.setExp(0.0f);
    	p.setTotalExperience(0);
    	p.getInventory().clear();
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setWalkSpeed(0.2f);
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().setHeldItemSlot(0);
    }
    
    public static void giveLobbyItems(Player p) {
		//p.getInventory().setItem(0, new ItemBuilder(Material.INK_SACK, 1, (short)1).name("§7Choisir l'équipe: §cRouge").build());
		//p.getInventory().setItem(1, new ItemBuilder(Material.INK_SACK, 1, (short)4).name("§7Choisir l'équipe: §9Bleu").build());
		p.getInventory().setItem(8, new ItemBuilder(Material.NAME_TAG, 1, (short)0).name("§7Choisir une classe").build());
	}
    
    
    public static void playSound(Location loc, String sound) {
    	PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 1.0F, 1.0F);
        //PacketPlayOutCustomSoundEffect packett = new PacketPlayOutCustomSoundEffect("note.barrett_shoot", SoundCategory.VOICE, l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), 1.0F, 1.0F);
        for(Player p : Bukkit.getOnlinePlayers()) 
        	((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }
    
    
    
    public static boolean isInRegion(Location loc, String region) {
        if (WorldGuardPlugin.inst() != null) {
            return WorldGuardPlugin.inst().getRegionManager(loc.getWorld()).getApplicableRegionsIDs(BukkitUtil.toVector(loc)).contains(region);
        }
        return false;
    }
    
    public static ProtectedRegion getRegion(World w, String name) {
    	if (Main.wgPl != null) {
    		RegionManager a2 = Main.wgPl.getRegionManager(w);
    		return a2.getRegion(name);
    	}
    	return null;
    }
     
     
     
    public static String getRegionName(Location location) {
 
        try {
        	RegionManager a2 = WorldGuardPlugin.inst().getRegionManager(location.getWorld());
            ApplicableRegionSet set = a2.getApplicableRegions(location);
            if (set.size() == 0)
                return "";
 
            String returning = "";
            int priority = -1;
            for (ProtectedRegion s : set) {
                if (s.getPriority() > priority) {
                    if (!s.getId().equals("")) {
                        returning = s.getId();
                        priority = s.getPriority();
                    }
                }
            }
 
            return returning;
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}