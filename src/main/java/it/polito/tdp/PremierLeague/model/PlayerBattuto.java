package it.polito.tdp.PremierLeague.model;

public class PlayerBattuto implements Comparable<PlayerBattuto>{
	
	private Player playerBattuto;
	private int peso;
	
	public PlayerBattuto(Player playerBattuto, int peso) {
		super();
		this.playerBattuto = playerBattuto;
		this.peso = peso;
	}

	public Player getPlayerBattuto() {
		return playerBattuto;
	}

	public void setPlayerBattuto(Player playerBattuto) {
		this.playerBattuto = playerBattuto;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	@Override
	public int compareTo(PlayerBattuto other) {
		return other.getPeso() - this.getPeso();
	}
	
}
