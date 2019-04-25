package fr.itspower.teamfortress.weapon;

import org.bukkit.Material;

import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.utils.ItemBuilder;

public class Gun {
    private WeaponType type;
    private String name = null;
    private String lore = null;
    private Material item = null;
    private String sound = null;
    private double damage = 0.0;
    private double distance = 0.0;
    private double recul = 0.0;
    private int maxClip;
    private int shootdelay;
    private int reloaddelay;
    private double accuracy;
    private boolean scopeable = false;
    private boolean shotgun = false;
    private boolean sniper = false;
    private int bulletamount = 1;
    private Classe classtype;
    private boolean leftclicktoshoot = false;
	private int slot;
	private boolean unlimitedammo = false;
	private boolean hasHeadShot;

    public Gun(WeaponType type, String name, String lore, Material item, String sound, double damage, 
    		double distance, double recul, int maxClip, int shootdelay, int reloaddelay, 
    		double accuracy, boolean scopeable, boolean shotgun, boolean sniper,
    		int bulletamount, Classe classtype, int slot, boolean unlimAmmo, boolean head ) {
        this.type = type;
        this.name = name;
        this.lore = lore;
        this.item = item;
        this.sound = sound;
        this.recul = recul;
        this.damage = damage;
        this.distance = distance;
        this.maxClip = maxClip;
        this.shootdelay = shootdelay;
        this.reloaddelay = reloaddelay;
        this.scopeable = scopeable;
        this.sniper = sniper;
        this.accuracy = accuracy;
        this.shotgun = shotgun;
        this.bulletamount = bulletamount;
        this.classtype = classtype;
        this.slot = slot;
        this.unlimitedammo = unlimAmmo;
        this.hasHeadShot = head;
    }

    public WeaponType getType() {
        return this.type;
    }

    public Material getMaterial() {
        return this.item;
    }

    public String getName() {
        return this.name;
    }
    
    public int getSlot() {
        return this.slot;
    }

    public String getLore() {
        return this.lore;
    }

    public double getDamage() {
        return this.damage;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getRecul() {
        return this.recul;
    }

    public boolean isScopeable() {
        return this.scopeable;
    }

    public String getSound() {
        return this.sound;
    }

    public int getMaxClip() {
        return this.maxClip;
    }

    public int getShootDelay() {
        return this.shootdelay;
    }

    public int getReloadDelay() {
        return this.reloaddelay;
    }

    public boolean isSniper() {
        return this.sniper;
    }

    public double getAccuracy() {
        return this.accuracy;
    }

    public Classe getClassType() {
        return this.classtype;
    }

    public boolean isShotgun() {
        return this.shotgun;
    }

    public int getBulletAmount() {
        return this.bulletamount;
    }
    
    public boolean hasUnlimitedAmmo() {
        return this.unlimitedammo;
    }

    public boolean leftClick() {
        return this.leftclicktoshoot;
    }

    private String un = "∞";
    public ItemBuilder getItemStack() {
    	if(hasUnlimitedAmmo()) {
			if(slot==0 && classtype.equals(Classe.Pyroman)) {
		    	return new ItemBuilder(item, getMaxClip(), (short)0).name(name+" §7|§e "+getMaxClip()+"/"+getMaxClip()).lore(lore).unbreakable().hideA();
			} else {
				return new ItemBuilder(item, 1, (short)0).name(name+" §7|§e "+un).lore(lore).unbreakable().hideA();
			}
    	}
    	return new ItemBuilder(item, getMaxClip(), (short)0).name(name+" §7|§e "+getMaxClip()+"/"+getMaxClip()).lore(lore).unbreakable().hideA();
    }

	public boolean hasHeadShot() {
		return hasHeadShot;
	}
}

