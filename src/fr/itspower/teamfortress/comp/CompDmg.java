package fr.itspower.teamfortress.comp;

import fr.itspower.teamfortress.types.TFPlayer;

public class CompDmg implements Comparable<CompDmg> {
	
	private TFPlayer p;

	public CompDmg(TFPlayer p) {
		super();
		this.p = p;
	}
	
	@Override
	public int compareTo(CompDmg comparaison) {
		int compareQuantity = comparaison.getPlayer().getDmg(); 
		// ordre croissant
		return compareQuantity - p.getDmg();
	}

	public TFPlayer getPlayer() {
		return p;
	}
}
