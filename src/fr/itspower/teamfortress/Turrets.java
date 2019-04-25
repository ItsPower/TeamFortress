package fr.itspower.teamfortress;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import fr.itspower.teamfortress.types.TFTurret;

public class Turrets {
	
	public static void init()
	{
		sentries = new HashMap<String, ArmorStand>();
	}
    static HashMap<String, ArmorStand> sentries;

    public static void addTFTurret(Player p, Location tg) {
    	p.playSound(tg, "mortier.poser", 1f, 1f);
    	ArmorStand tam = TFTurret.build(p, tg);
        sentries.put(p.getName(), tam);
    }

    public static void removeTFTurret(Player p) {
        sentries.remove(p.getName());
    }
    public static void removeTFTurret(String p) {
        sentries.remove(p);
    }
    /*public static void removeTFTurret(ArmorStand TFTurret) {
            sentries.remove(TFTurret);
            TFTurret_id.remove(getTFTurretId(TFTurret));
            size -= 1;
    }*/ //TODO ICI SUPPRIM2E RECEMENT
    
    /*public static int getTFTurretId(ArmorStand TFTurret) {
            for (int i = 0; i < TFTurret_id.size(); i++) {
                if (TFTurret_id.get(i) == TFTurret) {
                    return i;
                } else {
                }
            }
//            for (int id : TFTurret_id.keySet()) {
//                if (TFTurret_id.get(id) == TFTurret) {
//                    return id;
//                }
//            }
            return -1;
    }*/
    /*
    public static ArmorStand getTFTurret(int id) {
            if (TFTurret_id.keySet().contains(id)) {
                return sentries.get(id);
            } else {
                return null;
            }
    }*/
/*
    public static ArmorStand getTFTurret(ArmorStand armorStand) {
            for (ArmorStand TFTurret : TFTurret_id.values()) {
                if (TFTurret.getName().contains(s)) {
                    return TFTurret;
                }
            }
            return null;
    }*/

    public static ArmorStand getTFTurret(Player player) {
            return sentries.get(player.getName());
    }
/*
    public static boolean isObjectTFTurret(ArmorStand object) {
            for (TFTurret TFTurret : TFTurret_id.values()) {
                if (TFTurret.getArmorStand() == object) {
                    return true;
                }
            }
            return false;
    }
*/
    public static boolean hasTFTurret(Player player) {
        return sentries.containsKey(player.getName());
    }

    public static void removeAll() {
        for (ArmorStand user : sentries.values()) {
        	TFTurret.remove(user);
        }
    }
}
