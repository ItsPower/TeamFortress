package fr.itspower.teamfortress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.itspower.teamfortress.types.TFPlayer;
import fr.itspower.teamfortress.types.Team;
import fr.itspower.teamfortress.utils.Title;

public class TeamManager {

    private HashMap<UUID, Long> cooldown;
	private List<String> red;
	private List<String> blue;
	
	public TeamManager(Main pl) {
		reset();
	}
	
	public Team getTeam(String p) {
		for(String pl : red) {
			if(pl.equals(p)) {
				return Team.ROUGE;
			}
		}
		for(String pl : blue) {
			if(pl.equals(p)) {
				return Team.BLEU;
			}
		}
		return Team.AUCUNE;
	}
	
	public void forceTeam(TFPlayer kp) {
		Integer redcount = 0;
		Integer bluecount = 0;
		for(TFPlayer k : Main.getGM().getPlayers()) {
			if(!Main.getGM().isOnline(k.getName())) continue;
			if(k.getTeam().equals(Team.ROUGE)) {
				redcount++;
			} else if(k.getTeam().equals(Team.BLEU)) {
				bluecount++;
			}
		}
		if(redcount <= bluecount) {
			kp.setTeam(Team.ROUGE);
			new Title("", "§7Equipe: §c§lRouge", 0, 40, 10).send(kp.getPlayer());
			red.add(kp.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+kp.getName()+" prefix &c");
		} else {
			kp.setTeam(Team.BLEU);
			new Title("", "§7Equipe: §9§lBleu", 0, 40, 10).send(kp.getPlayer());
			blue.add(kp.getName());
			kp.setTeam(Team.BLEU);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+kp.getName()+" prefix &9");
		}
	}
	
	public void changeTeam(Player p, Team t) {
		if(!(onCooldown(p))) {
			TFPlayer kp = Main.getGM().getTFPlayer(p);
			if(!Main.getGM().isOnline(kp.getName())) return;
			Team oldteam = kp.getTeam();
			if(oldteam.equals(t)) {
				p.sendMessage(Main.getPrefix()+"Vous êtes déjà dans cette équipe.");
				return;
			}
			if(t.equals(Team.ROUGE)) {
				if((blue.size()+red.size()) == 0 || red.size()-1 <= blue.size() ) {
					new Title("", "§7Equipe: §c§lRouge", 0, 40, 10).send(p);
					p.sendMessage(Main.getPrefix()+"Vous choisissez l'équipe: §c§lRouge");
					red.add(p.getName());
					kp.setTeam(Team.ROUGE);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+p.getName()+" prefix &c");
					if(oldteam.equals(Team.BLEU)) {
						blue.remove(p.getName());
					}
					
				} else {
					p.sendMessage(Main.getPrefix()+"Cette équipe comporte trop de membres.");
				}
			} else if(t.equals(Team.BLEU)) {
				if((blue.size()+red.size()) == 0 || blue.size()-1 <= red.size()) {
					new Title("", "§7Equipe: §9§lBleu", 0, 40, 10).send(p);
					p.sendMessage(Main.getPrefix()+"Vous choisissez l'équipe: §9§lBleu");
					blue.add(p.getName());
					kp.setTeam(Team.BLEU);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+p.getName()+" prefix &9");
					if(oldteam.equals(Team.ROUGE)) {
						red.remove(p.getName());
					}
				} else {
					p.sendMessage(Main.getPrefix()+"Cette équipe comporte trop de membres.");
				}
			}
		}
	}
	
	private boolean onCooldown(Player p) {
        UUID uuid = p.getUniqueId();
        if(cooldown.containsKey(uuid)) {
            float time = (System.currentTimeMillis() -  cooldown.get(uuid)) / 1000;
            if (time < 1.0f) {
                return true;
                 
            }else {
                cooldown.put(uuid, System.currentTimeMillis());
                return false;
            }
             
        } else {
            cooldown.put(uuid, System.currentTimeMillis());
            return false;
        }
    }
	
	public void reset() {
		red = new ArrayList<String>();
		blue = new ArrayList<String>();
		cooldown = new HashMap<>();
	}

	public void removePlayer(TFPlayer kp) {
		Team t = kp.getTeam();
		if(t.equals(Team.ROUGE)) {
			red.remove(kp.getName());
		} else if(t.equals(Team.BLEU)) {
			blue.remove(kp.getName());
		}
		
	}

	public List<String> getRedMembers() {
		return red;
	}

	public List<String> getBlueMembers() {
		return blue;
	}
	
}