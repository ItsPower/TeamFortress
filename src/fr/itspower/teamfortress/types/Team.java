package fr.itspower.teamfortress.types;

public enum Team {
	BLEU("§9§lBleu", "§9"),
	ROUGE("§c§lRouge", "§c"),
	AUCUNE("§f§lAucune", "§8");

	private String value;
	private String color;
	
	private Team(String texte, String col) { 
		value = texte;
		color = col;
	}
	
	public String toString() {
		return value;
	}

	public String getColor() {
		return color;
	}
}
