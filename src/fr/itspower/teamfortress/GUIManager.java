package fr.itspower.teamfortress;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.itspower.teamfortress.types.Classe;
import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.utils.ItemBuilder;

public class GUIManager {
	
	public GUIManager(Main pl) {
	}
	
	public void openClassesGUI(Player p) {
		TFPlayer kp = Main.getGM().getTFPlayer(p);
		openClassesGUI(kp);
	}
	
	public void openClassesGUI(TFPlayer kp) {
		Inventory inv = Bukkit.createInventory(null, 9*4, "Choix des Classes");
		
		for(int classe = 1; classe<10; classe++) {
			//Bukkit.broadcastMessage(""+classe);
			ArrayList<String> lores = new ArrayList<String>();
			Classe c = Classe.byId(classe);
			for(String t : c.getWeaponsLore().split("\n")) {
				lores.add(t);
			}
			inv.setItem(8+classe, c.getItem().name("§6§l"+c.getName()).lore(lores).lore("").lore("§7Points de Vie: §e"+c.getHealth()).lore("§7Valeur Minecart: §e"+c.getMinecartValue()).lore("§7Vitesse: §e"+c.getSpeed()).build());
		}
		inv.setItem(30, new ItemBuilder(Material.INK_SACK, 1, (short)1).name("§7Choisir l'équipe: §cRouge").build());
		inv.setItem(32, new ItemBuilder(Material.INK_SACK, 1, (short)4).name("§7Choisir l'équipe: §9Bleu").build());
		
		kp.getPlayer().openInventory(inv);
	}
}