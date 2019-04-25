package fr.itspower.teamfortress.comp;

import fr.itspower.teamfortress.types.TFPlayer;

public class CompKill implements Comparable<CompKill> {
	
	private TFPlayer p;

	public CompKill(TFPlayer p) {
		super();
		this.p = p;
	}
	
	@Override
	public int compareTo(CompKill comparaison) {
		int compareQuantity = comparaison.getPlayer().getKills(); 
		// ordre croissant
		return compareQuantity - p.getKills();
	}

	public TFPlayer getPlayer() {
		return p;
	}
}
