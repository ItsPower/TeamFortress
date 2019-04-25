package fr.itspower.teamfortress.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import fr.itspower.teamfortress.Main;
import fr.itspower.teamfortress.Turrets;
import fr.itspower.teamfortress.utils.Utils;
import fr.itspower.teamfortress.weapon.GunManager;

public class TFTurret {

	public static HashMap<String, Vector> lastDir = new HashMap<String, Vector>();
	/*
    private Player p;
	private Location loc;
	private Vector lastDir;
	private BukkitTask loop;
	private static ArmorStand c;

	public TFTurret(Player tg, Location l) {
		this.p = tg;
		this.loc = l.add(0.5, 0, 0.5);
		this.lastDir = l.getDirection();
		Bukkit.broadcastMessage("tg1");
		build();
	}
	 */	
	public static void remove(ArmorStand a) {
		//this.loop.cancel();
		String name = a.getName().split(" ")[2];
		a.getLocation().getBlock().setType(Material.AIR);
		a.remove();
		Turrets.removeTFTurret(name);
	}
	/*
	public Location getLocation() {
		return c.getLocation();
	}
	 */
	public static ArmorStand build(Player p, Location loc) {
		//Bukkit.broadcastMessage("tg2");
		ArmorStand c = (ArmorStand)loc.getWorld().spawn(loc.add(0.5, 0, 0.5), ArmorStand.class);
		c.setGravity(false);
		c.setCustomName("Tourelle de "+p.getName());
		c.setCustomNameVisible(true);
		c.setVisible(false);
		//Bukkit.broadcastMessage("construction de tourelle commencé");

		c.teleport(loc);

		@SuppressWarnings("unused")
		BukkitTask loop = new BukkitRunnable() {
			private int nbTours = 0;
			public void run() {
				if(p.getPlayer().isDead() || !p.getPlayer().isOnline()) {
					remove(c);
					cancel();
					return;
				}
				this.nbTours += 1;
				//Bukkit.broadcastMessage("tg4 "+nbTours);

				if (this.nbTours >= 101) {

					//c.setCustomNameVisible(false);
					c.setHelmet(new ItemStack(Material.DROPPER));
					//Bukkit.broadcastMessage("construction de tourelle terminé");

					p.getPlayer().getInventory().setItem(0, GunManager.getGunByName("§6§lTélécommande").getItemStack().build());

					updateDir(p, c);
					cancel();
				} else if(!Turrets.hasTFTurret(p)) {
					//Bukkit.broadcastMessage("construction de tourelle annulée");
					remove(c);
					cancel();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 1, 1);
		
		ArmorStand b = (ArmorStand)loc.getWorld().spawn(loc.add(0, 1, 0), ArmorStand.class);
		b.setGravity(false);
		b.setCustomName("0%");
		b.setCustomNameVisible(true);
		b.setVisible(false);
		new BukkitRunnable() {
			private short nbTours = 0;
			public void run() {
				if(nbTours >= 100 || p.getPlayer().isDead() || !p.getPlayer().isOnline()) {
					b.remove();
					cancel();
					return;
				}
				nbTours += 1;
				b.setCustomName(nbTours+"%");
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		
		
		
		return c;
	}

	/* if(p.getPlayer().isDead()) {
	    		  cancel();
	    		  return;
	    	  }
	        this.nbTours += 1;

	        c.setCustomName(this.nbTours + "%");
	        if (this.nbTours >= 101) {

	          c.setCustomNameVisible(false);
	          c.setHelmet(new ItemStack(Material.DROPPER));
	          c.setCustomName("Tourelle de "+p.getName());
	 */
	/*ArmorStand ready = (ArmorStand) loc.getWorld().spawn(c.getLocation(), ArmorStand.class);

	          ready.setCustomName("Tourelle de "+p.getName());
	          ready.setCustomNameVisible(true);
	          ready.setVisible(false);
	          ready.setGravity(false);
	 */
	/*Bukkit.broadcastMessage("construction de tourelle terminé");
				p.getPlayer().getInventory().setItem(0, GunManager.getGunByName("§6§lTélécommande").getItemStack().build());

	          cancel();*/

	public static void updateDir(Player p, ArmorStand c)
	{
		//Bukkit.broadcastMessage("updatedir de tourelle");
		p.playSound(c.getLocation(), "mortier.direction", 10f, 1f);
		EulerAngle eu = new EulerAngle(p.getPlayer().getLocation().getPitch() / 57.5D, p.getPlayer().getLocation().getYaw() / 57.5D, 0.0D);
		//lastDir = p.getPlayer().getEyeLocation().getDirection();
		lastDir.put(p.getName(), p.getPlayer().getEyeLocation().getDirection());
		c.setHeadPose(eu);
	}

	public static void shoot(Player p, ArmorStand c)
	{
		TFPlayer launcher = Main.getGM().getTFPlayer(p);
		for(Player pd : Bukkit.getOnlinePlayers())
			if(pd.getLocation().distance(c.getLocation()) < 30)
				pd.playSound(c.getLocation(), "mortier.lancer", 1f, 1f);

		//Bukkit.broadcastMessage("shoot de tourelle");
		c.getWorld().playEffect(c.getEyeLocation(), Effect.EXPLOSION_LARGE, 0);

		Random rand = new Random();
		Vector cod = lastDir.get(p.getName()).clone();

		cod.multiply(1.45);

		float x = (float)c.getEyeLocation().clone().subtract(cod).subtract(c.getEyeLocation()).getX();
		float y = (float)c.getEyeLocation().clone().subtract(cod).subtract(c.getEyeLocation()).getY();
		float z = (float)c.getEyeLocation().clone().subtract(cod).subtract(c.getEyeLocation()).getZ();
		for (int i = 0; i < 100; i++) {
			c.getWorld().spigot().playEffect(c.getEyeLocation(), Effect.CLOUD, 0, 0, (float)(x + rand.nextDouble() - 0.5D), (float)(y + rand.nextDouble() - 0.5D), (float)(z + rand.nextDouble() - 0.5D), Math.abs(rand.nextFloat()), 0, 10);
		}
		final ArmorStand ball = (ArmorStand)p.getPlayer().getWorld().spawn(c.getLocation().add(0.0D, 0.0D, 0.0D), ArmorStand.class);

		ball.setHelmet(new ItemStack(Material.QUARTZ_ORE));
		ball.setGravity(false);
		ball.setCustomName("Ball launched by " + p.getName());
		ball.setVisible(false);

		ball.teleport(c.getLocation().add(cod));

		List<Location> list = calculateTrajectory(cod, ball.getEyeLocation());
		//Bukkit.broadcastMessage("§4§l§n"+list.size());

		if(list.size() > 1) {
			new BukkitRunnable()
			{
				int nbTours = 0;

				public void run()
				{
					this.nbTours += 1;
					boolean explode = false;
					Location loctmp = list.get(nbTours);
					//Bukkit.broadcastMessage("§5EXPLODE CHECK " +launcher.getTeam());
					for(Player loop : Bukkit.getOnlinePlayers()) {
						//Bukkit.broadcastMessage("§5EXPLODE DIST "+loop.getName()+" "+loop.getPlayer().getLocation().clone().add(0, 0.5, 0).distance(loctmp));
						if(loop.getPlayer().getLocation().clone().add(0, 0.5, 0).distance(loctmp) < 2) {
							explode = true;
							//Bukkit.broadcastMessage("EXPLODE PLAYER");
							break;
						}
					}

					if (list.size() <= nbTours + 1 || explode) {
						Location l = list.get(nbTours - 1);

						l.getWorld().playSound(l, Sound.EXPLODE, 1.0F, 1.0f);
						/*for (int i = 0; i < 300; i++) {
		            ball.getWorld().playEffect(ball.getEyeLocation(), Effect.FLAME, 0);
		          }
		          for (int i = 0; i < 300; i++) {
		            ball.getWorld().playEffect(ball.getEyeLocation().clone().add(new Random().nextInt(200) / 100.0D - 1.0D, new Random().nextInt(200) / 100.0D - 1.0D, new Random().nextInt(200) / 100.0D - 1.0D), Effect.LARGE_SMOKE, 0);
		          }
		          for (int i = 0; i < 300; i++) {
		            ball.getWorld().playEffect(ball.getEyeLocation().clone().add(new Random().nextInt(200) / 100.0D - 1.0D, new Random().nextInt(200) / 100.0D - 1.0D, new Random().nextInt(200) / 100.0D - 1.0D), Effect.CLOUD, 0);
		          }
		          for (int i = 0; i < 50; i++) {
		            ball.getWorld().playEffect(ball.getEyeLocation().clone().add(new Random().nextInt(500) / 100.0D - 2.5D, new Random().nextInt(500) / 100.0D - 2.5D, new Random().nextInt(500) / 100.0D - 2.5D), Effect.LAVA_POP, 0);
		          }*/
						for(TFPlayer tfp : Main.getGM().getPlayers()) {
							if(tfp.getPlayer().getLocation().distance(l) < 6) {
								if(!tfp.getTeam().equals(launcher.getTeam())) {
									Vector vector = tfp.getPlayer().getLocation().toVector().subtract(l.toVector()).normalize();
									//tfp.getPlayer().setVelocity(vector.multiply(0.9).setY(0.5));
									tfp.getPlayer().setVelocity(vector.multiply(1.8));
									Main.getGM().damageTF(launcher.getPlayer(), tfp.getPlayer(), (int)24-(tfp.getPlayer().getLocation().distance(l)), true);
									//Bukkit.broadcastMessage("§6dmg: §l"+(int)(24-tfp.getPlayer().getLocation().distance(l)));
								}
							}
						}
						ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 250);
						ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 200);
						ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 250);
						ball.setHealth(0.0D);
						ball.setHelmet(new ItemStack(Material.AIR));
						ball.remove();
						Utils.playSound(l, "mortier.obus");
						cancel();
						return;
					}
					ball.teleport(loctmp);
				}
			}.runTaskTimer(Main.getInstance(), 1L, 1L);
		} else {
			Location l = list.get(0);
			for(TFPlayer tfp : Main.getGM().getPlayers()) {
				if(tfp.getPlayer().getLocation().distance(l) < 6) {
					if(!tfp.getTeam().equals(launcher.getTeam())) {
						Vector vector = tfp.getPlayer().getLocation().toVector().subtract(l.toVector()).normalize();
						//tfp.getPlayer().setVelocity(vector.multiply(0.9).setY(0.5));
						tfp.getPlayer().setVelocity(vector.multiply(1.8));
						Main.getGM().damageTF(launcher.getPlayer(), tfp.getPlayer(), (int)24-(tfp.getPlayer().getLocation().distance(l)), true);
						//Bukkit.broadcastMessage("§6dmg: §l"+(int)(24-tfp.getPlayer().getLocation().distance(l)));
					}
				}
			}
			ParticleEffect.SMOKE_LARGE.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 250);
			ParticleEffect.CLOUD.send(Bukkit.getOnlinePlayers(), l, 1, 1, 1, .01, 200);
			ParticleEffect.LAVA.send(Bukkit.getOnlinePlayers(), l, 1.5, 1.5, 1.5, .01, 250);
			ball.setHealth(0.0D);
			ball.remove();
			Utils.playSound(l, "mortier.obus");
		}

		Location loc = c.getLocation().clone().add(0.0D, 0.5D, 0.0D);
		ArmorStand a_timer = (ArmorStand)loc.getWorld().spawn(loc, ArmorStand.class);
		a_timer.setGravity(false);
		a_timer.setCustomName("§c8s");
		a_timer.setCustomNameVisible(true);
		a_timer.setVisible(false);
		//Bukkit.broadcastMessage("construction de tourelle commencé");

		a_timer.teleport(loc);

		@SuppressWarnings("unused")
		BukkitTask loop = new BukkitRunnable() {
			private int nbTours = 8;
			public void run() {
				if(p.getPlayer().isDead() || nbTours <= 1) {
					a_timer.remove();
					if(ball != null || ball.isDead()) {
						ball.remove();
					}
					cancel();
					return;
				}
				//Bukkit.broadcastMessage("§5tg5 "+nbTours);
				this.nbTours -= 1;
				a_timer.setCustomName("§c"+nbTours+"s");
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}




	private static List<Location> calculateTrajectory(Vector v, Location start)
	{
		Location point = start.clone();

		List<Location> list = new ArrayList<Location>();

		for (int i = 0; (point.getBlock().getType() == Material.AIR) || (point.getBlock().isLiquid()); i++)
		{
			point.add(v);
			point.subtract(0.0D, i / 75.0D, 0.0D);
			list.add(point.clone());
		}
		if (list.isEmpty()) {
			list.add(start.add(v));
		}
		return list;
	}

	/*
	public Player getPlayer() {
		return this.p;
	}

	public ArmorStand getArmorStand() {
		return c;
	}*/
}
