package fr.itspower.teamfortress.types;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import fr.itspower.teamfortress.CartManager;
import fr.itspower.teamfortress.Main;

public class TFBoard {
	private TFPlayer kp;
	private BukkitTask loop;

	public TFBoard(TFPlayer p) {
		kp = p;
		startScheduler();
	}

	private void startScheduler() {
		this.loop = new BukkitRunnable() {
			public void run() {
				if(kp.isOnline()) {
	            	if(Main.getGM().getStatus().equals(Status.WAITING) || Main.getGM().getStatus().equals(Status.STARTING)) {
	            		
	            		final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	            		final Objective obj = board.registerNewObjective("prysmboard", "dummy");
	            		
	            		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	            		obj.setDisplayName("§f§lTeam Fortress");

	            		obj.getScore("§f§lEQUIPES:").setScore(9);
	            		obj.getScore("§7 » §cRouge: §c"+Main.getTM().getRedMembers().size()).setScore(8);
	            		obj.getScore("§7 » §9Bleu:   §9"+Main.getTM().getBlueMembers().size()).setScore(7);
	            		obj.getScore("§e   ").setScore(6);
	            		obj.getScore("§7VOTRE EQUIPE: "+kp.getTeam().toString()).setScore(5);
	            		obj.getScore("§e  ").setScore(4);
	            		obj.getScore("§7JOUEURS: "+Main.getGM().getPlayers().size()+"/"+Main.getGM().getMaxPlayers()).setScore(3);
	            		obj.getScore("§e ").setScore(2);
	            		obj.getScore("§7développé par §fIts_Power§7.").setScore(1);
	            		
	            		kp.getPlayer().setScoreboard(board);
	            		
	            	} else if(Main.getGM().getStatus().equals(Status.INGAME)) {
	            		
	            		final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	            		final Objective obj = board.registerNewObjective("prysmboard", "dummy");
	            		
	            		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	            		if(kp.getTeam().equals(Team.ROUGE)) { 
	            			obj.setDisplayName("§c§lTeam Fortress");
	            		} else {
	            			obj.setDisplayName("§3§lTeam Fortress");
	            		}

	            		obj.getScore("§e ").setScore(9);
	            		obj.getScore("§7» Temps: §f"+Main.getGM().getFormatedTime()).setScore(8);
	            		obj.getScore("§7» K/D: §f"+kp.getKills()+"/"+kp.getDeaths()).setScore(7);
	            		obj.getScore("§e   ").setScore(6);
	            		obj.getScore("§7» §cRouge: §c"+CartManager.RED_PERC+"%").setScore(5);
	            		obj.getScore("§7» §9Bleu:   §9"+CartManager.BLUE_PERC+"%").setScore(4);
	            		obj.getScore("§e    ").setScore(3);
	            		obj.getScore("§7développé par §fIts_Power§7.").setScore(2);
	            		
	            		kp.getPlayer().setScoreboard(board);
	            	}
				} else {
					stop();
				}
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
	public void stop() {
		loop.cancel();
	}
}
