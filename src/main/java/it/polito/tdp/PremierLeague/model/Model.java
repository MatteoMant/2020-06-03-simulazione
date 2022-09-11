package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	
	// variabili utili per il metodo ricorsivo
	private List<Player> dreamTeam;
	private Integer bestDegree;
	
	public Model() {
		dao = new PremierLeagueDAO();
		idMap = new HashMap<>();
		this.dao.listAllPlayers(idMap);
	}
	
	public void creaGrafo(double numeroMinimoGoal) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, this.dao.listAllPlayersWithGoals(numeroMinimoGoal));
		
		// Aggiunta degli archi
		for (Adiacenza a : this.dao.listAllAdiacenze(idMap, numeroMinimoGoal)) {
			Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getPeso());
		}
		
	}
	
	public List<Player> calcolaDreamTeam(int k){
		this.dreamTeam = null;
		this.bestDegree = 0;
		
		List<Player> parziale = new LinkedList<>();
		
		// punto di partenza della ricorsione
		cerca(parziale, new LinkedList<Player>(this.grafo.vertexSet()), k);
		
		return dreamTeam;
	}
	
	public void cerca(List<Player> parziale, List<Player> players, int k) {
		// Condizione di terminazione
		if (parziale.size() == k) {
			int degree = calcolaGradoTitolaritaTeam(parziale);
			if (degree > this.bestDegree) {
				dreamTeam = new LinkedList<>(parziale);
				this.bestDegree = degree;
				return;
			} else {
				return;
			}
		}
		
		for (Player p : players) {
			if (!parziale.contains(p)) {
				parziale.add(p);
	
				// devo rimuovere dalla lista di giocatori che possono essere aggiunti al dream-team
				// tutti quei giocatori che sono stati battuti dal player 'p' 
				List<Player> rimanenti = new LinkedList<>(players);
				rimanenti.removeAll(Graphs.successorListOf(this.grafo, p));
				
				cerca(parziale, rimanenti, k);
				parziale.remove(p);
			}
		}
		
	}
	
	public int calcolaGradoTitolaritaTeam(List<Player> team) {
		int grado = 0;
		
		for (Player p : team) {
			
			int sommaUscenti = 0;
			for (DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p)) {
				sommaUscenti += this.grafo.getEdgeWeight(e);
			}
			
			int sommaEntranti = 0;
			for (DefaultWeightedEdge e : this.grafo.incomingEdgesOf(p)) {
				sommaEntranti += this.grafo.getEdgeWeight(e);
			}
			
			grado += (sommaUscenti - sommaEntranti);
		}
		
		return grado;
	}
	
	public TopPlayer getTopPlayer() {
		List<TopPlayer> lista = new LinkedList<>();
		
		for (Player p : this.grafo.vertexSet()) {
			TopPlayer topPlayer = new TopPlayer(p);
			lista.add(topPlayer);
			for (DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p)) {
				Player avversarioBattuto = Graphs.getOppositeVertex(this.grafo, e, p);
				topPlayer.aggiungiAvversario(avversarioBattuto); 
			}
		}
		
		TopPlayer top = null;
		int max = 0;
		for (TopPlayer topPlayer : lista) {
			if (topPlayer.getAvversari().size() > max) {
				max = topPlayer.getAvversari().size();
				top = topPlayer;
			}
		}
		
		return top;
	}
	
	public int getPesoArco(Player player1, Player player2) {
		return (int)this.grafo.getEdgeWeight(this.grafo.getEdge(player1, player2));
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
}
