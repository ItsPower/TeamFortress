package fr.itspower.teamfortress.types;

public enum msg {
	
	item_choixequipe_rouge("§7Choisir l'équipe: §c§lRouge"),  
	item_choixequipe_bleu("§7Choisir l'équipe: §3§lBleu"),  

	item_anvil("§6Améliorer votre équipement"),  
	item_emerald("§6Améliorer votre équipement"),  
	item_enchbook("§6Choisir vos talents"),  
	
	kick_disabled("Démarrage du serveur en cours.."),  
	kick_ingame("Cette partie est pleine");
	
	private String value;
	
	private msg(String texte) {
		this.setValue(texte);
	}

	public String a() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
