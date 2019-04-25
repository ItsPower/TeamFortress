package fr.itspower.teamfortress.comp;

import fr.itspower.teamfortress.types.TFPlayer;

public class CompPush implements Comparable<CompPush> {
	
	private TFPlayer p;

	public CompPush(TFPlayer p) {
		super();
		this.p = p;
	}
	
	@Override
	public int compareTo(CompPush comparaison) {
		int compareQuantity = comparaison.getPlayer().getPushs();
		return compareQuantity - p.getPushs();
	}

	public TFPlayer getPlayer() {
		return p;
	}
}
