package it.polito.tdp.PremierLeague.model;

import java.util.LinkedList;
import java.util.List;

public class TopPlayer {
	
	private Player topPlayer;
	private List<Player> avversari;
	
	public TopPlayer(Player topPlayer) {
		this.topPlayer = topPlayer;
		this.avversari = new LinkedList<>();
	}

	public Player getTopPlayer() {
		return topPlayer;
	}

	public void aggiungiAvversario(Player avversario) {
		this.avversari.add(avversario);
	}
	
	public List<Player> getAvversari() {
		return avversari;
	}

	@Override
	public String toString() {
		return topPlayer.toString();
	}

}
