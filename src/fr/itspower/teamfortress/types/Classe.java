package fr.itspower.teamfortress.types;

import org.bukkit.Material;

import fr.itspower.teamfortress.utils.ItemBuilder;

public enum Classe {
	
	Scout(1) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)8); }
		@Override
		public String getName() { return "Scout"; }
		@Override
		public double getHealth() { return 16.0; }
		@Override
		public float getSpeed() { return 0.32f; }
		@Override
		public Integer getMinecartValue() { return 2; }
		@Override
		public String getWeaponsLore() {
			return "§9Canon scié\n§9Batte\n§9Le défenseur";
		}
		@Override
		public String getIcon() { return "░"; }
	},
	Soldier(2) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)7); }
		@Override
		public String getName() { return "Soldier"; }
		@Override
		public double getHealth() { return 20.0; }
		@Override
		public float getSpeed() { return 0.24f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9Lance-roquettes\n§9Fusil à pompe\n§9Grenade flash";
		}
		@Override
		public String getIcon() { return "▒"; }
	},
	Pyroman(3) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)6); }
		@Override 
		public String getName() { return "Pyroman"; } 
		@Override
		public double getHealth() { return 24.0; }
		@Override
		public float getSpeed() { return 0.26f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9Lance-roquettes\n§9Fusil à pompe\n§9Grenade flash";
		}
		@Override
		public String getIcon() { return "▓"; }
	},
	Demoman(4) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)5); }
		@Override 
		public String getName() { return "Demoman"; } 
		@Override
		public double getHealth() { return 20.0; }
		@Override
		public float getSpeed() { return 0.24f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9Dynamite\n§9Fumigène\n§9Fusil à pompe\n§9Pistolet de détresse";
		}
		@Override
		public String getIcon() { return "│"; }
	},
	Heavy(5) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)4); }
		@Override 
		public String getName() { return "Heavy"; } 
		@Override
		public double getHealth() { return 32.0; }
		@Override
		public float getSpeed() { return 0.18f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9La tornade\n§9Fusil à pompe\n§9Poings américains";
		}
		@Override
		public String getIcon() { return "┤"; }
	},
	Engineer(6) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)3); }
		@Override 
		public String getName() { return "Engineer"; } 
		@Override
		public double getHealth() { return 18.0; }
		@Override
		public float getSpeed() { return 0.26f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9Canon montable\n§9Le défenseur\n§9Trampoline\n§9Mine";
		}
		@Override
		public String getIcon() { return "╡"; }
	},
	Medic(7) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)2); }
		@Override 
		public String getName() {  return "Medic"; } 
		@Override
		public double getHealth() { return 20.0; }
		@Override
		public float getSpeed() { return 0.24f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9Medecine portable\n§9Pistolet tranquilisant\n§9Scie à amputation";
		}
		@Override
		public String getIcon() { return "╢"; }
	},
	Sniper(8) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)1); }
		@Override 
		public String getName() {  return "Sniper"; } 
		@Override
		public double getHealth() { return 18.0; }
		@Override
		public float getSpeed() { return 0.24f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9Sniper\n§9Carabine du nettoyeur\n§9Potion de soin";
		}
		@Override
		public String getIcon() { return "╖"; }
	},
	Spy(9) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STAINED_CLAY, 1,(short)0); }
		@Override 
		public String getName() {  return "Spy";  } 
		@Override
		public double getHealth() { return 16.0; }
		@Override
		public float getSpeed() { return 0.32f; }
		@Override
		public Integer getMinecartValue() { return 2; }
		@Override
		public String getWeaponsLore() {
			return "§9Poignard\n§9C4\n§9Revolver";
		}
		@Override
		public String getIcon() { return "╕"; }
	},
	NULL(10) {
		@Override
		public ItemBuilder getItem() { return new ItemBuilder(Material.STONE, 1,(short)0); }
		@Override 
		public String getName() {  return "null";  } 
		@Override
		public double getHealth() { return 20.0; }
		@Override
		public float getSpeed() { return 0.3f; }
		@Override
		public Integer getMinecartValue() { return 1; }
		@Override
		public String getWeaponsLore() {
			return "§9lol";
		}
		@Override
		public String getIcon() { return "X"; }
	};

	public abstract String getName();
	public abstract ItemBuilder getItem();
	public abstract String getWeaponsLore();
	public abstract float getSpeed();
	public abstract Integer getMinecartValue();
	public abstract double getHealth();
	public abstract String getIcon();

	Classe(int id) {
		
	}

	public static Classe byId(int i) {
		switch (i) {
			case 1:
	        	return Scout;
			case 2:
	        	return Soldier;
			case 3:
	        	return Pyroman;
			case 4:
	        	return Demoman;
			case 5:
	        	return Heavy;
			case 6:
	        	return Engineer;
			case 7:
	        	return Medic;
			case 8:
	        	return Sniper;
			case 9:
	        	return Spy;
		}
		return Scout;
	}
}
