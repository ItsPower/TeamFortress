package fr.itspower.teamfortress.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import fr.itspower.teamfortress.Main;
import fr.itspower.teamfortress.weapon.Gun;
import fr.itspower.teamfortress.weapon.GunManager;

public class TFCooldown {

	private BukkitTask task;
	private String id;
	private Player p;
	private Gun g;

	public TFCooldown(String id, Player p, Gun g) {
		this.id = id;
		this.p = p;
		this.g = g;
		setAmnt(g.getReloadDelay()/20);
		
		task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			int ms = g.getReloadDelay();
			@Override
			public void run() {
				if(p == null || !p.isOnline() || p.isDead()) {
					//Bukkit.broadcastMessage("§5§lCooldown "+id+" §5§lSUPPRIME");
					remove();
				}
				
				if(ms==20
						||ms==40
						||ms==60
						||ms==80
						||ms==100
						||ms==120
						||ms==140
						||ms==160
						||ms==180
						||ms==200
						||ms==220
						||ms==240
						||ms==260
						||ms==280
						||ms==300
						||ms==320
						||ms==340
						||ms==360
						||ms==380
						||ms==400
						||ms==420
						||ms==440) {
					setAmnt(ms/20);
					//Bukkit.broadcastMessage("§5§lCooldown "+id+" §5§lSECONDE REST: "+ms/20);
				}
				
				if(ms==0) {
					//Bukkit.broadcastMessage("§5§lCooldown "+id+" §5§lterminé.");
					ItemStack it = p.getInventory().getItem(g.getSlot());
					ItemMeta im = it.getItemMeta();
					im.setDisplayName(g.getName()+" §7|§e "+g.getMaxClip()+"/"+g.getMaxClip());
					it.setItemMeta(im);
					it.setAmount(g.getMaxClip());
					remove();
				}
				
				ms = ms-1;
			}
		}, 0, 1);
	}
	
	protected void setAmnt(int i) {
		ItemStack it = p.getInventory().getItem(g.getSlot());
		it.setAmount(-i);
	}
	
	protected void remove() {
		task.cancel();
		GunManager.cd.remove(id);
	}
}
