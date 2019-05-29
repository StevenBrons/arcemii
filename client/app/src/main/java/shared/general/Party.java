package shared.general;

import java.util.ArrayList;

import shared.entities.Player;
import shared.messages.Message;

public class Party extends Message {

	private transient Level curLevel;
	private int partyId;
	private ArrayList<Player> players;
	public transient boolean inLobby = true;

	public Party() {
		partyId = (int)(Math.random()*99999);
		players = new ArrayList<>();
	}

	public void addPlayer(Player client) {
		if (!players.contains(client)) {
			players.add(client);
		}
		messageAll(this);
	}

	/**
	 * Remove the player from the party.
	 * @param player
	 * @author Bram Pulles
	 */
	public void removePlayer(Player player){
		if(players.contains(player)){
			players.remove(player);
		}
		messageAll(this);
	}

	public void messageAll(Message message) {
		for (Player p : players) {
			p.sendMessage(message);
		}
	}

	public int getPartyId() {
		return partyId;
	}

	public boolean containsPlayer(Player player){
		return players.contains(player);
	}

	public void setCurrentLevel(Level level){
		curLevel = level;
	}

	public boolean isEmpty(){
		return players.size() == 0;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void startGame() {
		this.inLobby = false;
	}

	public void update() {
		curLevel.invoke();
		curLevel.execute();
	}


}