package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if(!idMap.containsKey(res.getInt("PlayerID"))) {
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					idMap.put(player.getPlayerID(), player);
				}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Player> listAllPlayersWithGoals(double numeroMinimoGoal){
		String sql = "SELECT * "
				   + "FROM Players "
				   + "WHERE PlayerID IN (SELECT PlayerID "
				   + "FROM Actions "
				   + "GROUP BY PlayerID "
				   + "HAVING AVG(Goals) > ?)";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, numeroMinimoGoal);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> listAllAdiacenze(Map<Integer, Player> idMap, double numeroMinimoGoal){
		String sql = "SELECT a1.PlayerID AS g1, a2.PlayerID AS g2, SUM(a1.TimePlayed) AS s1, SUM(a2.TimePlayed) AS s2 "
				+ "FROM Actions a1, Actions a2 "
				+ "WHERE a1.PlayerID > a2.PlayerID AND a1.TeamID != a2.TeamID AND a1.MatchID = a2.MatchID "
				+ "AND a1.PlayerID IN (SELECT PlayerID FROM Actions GROUP BY PlayerID HAVING AVG(Goals) > ?) "
				+ "AND a2.PlayerID IN (SELECT PlayerID FROM Actions GROUP BY PlayerID HAVING AVG(Goals) > ?) "
				+ "AND a1.Starts = 1 AND a2.Starts = 1 "
				+ "GROUP BY a1.PlayerID, a2.PlayerID "
				+ "HAVING SUM(a1.TimePlayed) != SUM(a2.TimePlayed)";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, numeroMinimoGoal);
			st.setDouble(2, numeroMinimoGoal);
			ResultSet res = st.executeQuery();
			while (res.next()) {	
				
				Player p1 = idMap.get(res.getInt("g1"));
				Player p2 = idMap.get(res.getInt("g2"));
				
				int minutaggio1 = res.getInt("s1");
				int minutaggio2 = res.getInt("s2");
				
				if (minutaggio1 > minutaggio2) {
					Adiacenza a = new Adiacenza(p1, p2, minutaggio1-minutaggio2);
					result.add(a);
				} else if (minutaggio1 < minutaggio2) {
					Adiacenza a = new Adiacenza(p2, p1, minutaggio2-minutaggio1);
					result.add(a);
				} else {
					// NON inseriamo l'arco se la differenza è pari a 0
				}
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
