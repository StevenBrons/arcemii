package client.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.debernardi.archemii.R;

import java.io.StreamCorruptedException;

import client.activities.GameActivity;
import client.activities.JoinPartyActivity;
import client.activities.LobbyActivity;
import server.general.ServerGameHandler;
import shared.entities.Entity;
import shared.entities.Player;
import shared.general.Level;
import shared.general.Party;
import shared.messages.GameUpdateMessage;
import shared.messages.Message;
import shared.messages.PlayerInfoMessage;

import static android.content.Context.MODE_PRIVATE;

public class ClientGameHandler {

	public static ClientGameHandler handler;

	private Connection connection;
	private Player player = new Player(0,0,0);

	private Context context;

	private SharedPreferences.OnSharedPreferenceChangeListener listener;

	private JoinPartyActivity joinPartyActivity;
	private LobbyActivity lobbyActivity;
	private GameActivity gameActivity;

	private Party party;
	private Level level;

	private ClientGameHandler(Context context) {
		this.context = context;

		newConnection();
		listenForServermode();
		gameLoop();
	}

	public void gameLoop(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				int TICKSPEED = ServerGameHandler.TICKSPEED;
				while (true) {
					long start = System.currentTimeMillis();

					if (gameActivity != null && level != null) {
						gameActivity.draw(level);
						//player.sendMessage(new ActionMessage(player.getActions()));
					}

					long timeTook = System.currentTimeMillis() - start;
					try {
						if (timeTook > TICKSPEED) {
							timeTook = TICKSPEED;
						}
						Thread.sleep(TICKSPEED - timeTook);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void newConnection(){
		if(connection != null){
			connection.stop();
		}

		connection = new Connection(isSingleplayer(), context);
		listenForMessages();
	}

	public static void init(Context context) {
		if (handler == null) {
			handler = new ClientGameHandler(context);
		}
	}

	public void listenForMessages() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Wait until the connection is established.
				while(connection.isStarting()){
				}

				while (connection.isConnected()) {
					try {
						Message m = (Message) connection.getInputStream().readObject();
						handleInput(m);
					} catch (Exception e) {
						if ((e.getMessage() != null && e.getMessage().equals("Socket closed")) || e instanceof StreamCorruptedException) {
							Log.d("CONNECTION", "Connection to server was lost.");
							break;
						}
						e.printStackTrace();
					}
				}
				Log.d(Connection.TAG, "Stopped listening for messages.");
			}
		}).start();
	}


	/**
	 * Listen to the shared preferences if the servermode changes.
	 * If the servermode changes create a new connection.
	 */
	private void listenForServermode(){
		SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedpref_servermodeinfo), MODE_PRIVATE);

		// Set up a listener for connection information.
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				Log.d("CONNECTION", "Changing connection to: " + (isSingleplayer() ? "singleplayer" : "multiplayer"));
				newConnection();
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * Use the shared preferences to determine if the servermode is singleplayer.
	 * @return if the servermode is singleplayer.
	 */
	private boolean isSingleplayer(){
		SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedpref_servermodeinfo), MODE_PRIVATE);
		String currentmode = prefs.getString(context.getString(R.string.sharedpref_servermode), context.getString(R.string.online));
		return currentmode.equals(context.getString(R.string.offline));
	}

	/**
	 * Send a message to the server from the client.
	 * @param msg message
	 */
	public static void sendMessage(Message msg) {
		handler.connection.sendMessage(msg);
	}

	/**
	 * This method received the messages send by the server.
	 * @param m
	 * @author Steven Bronsveld and Bram Pulles
	 */
	public void handleInput(Message m) {
		Log.d(Connection.TAG, "Client receives message: " + m);

		switch (m.getType()){
			case "PartyJoinedMessage":
				partyJoinedMessage();
				break;
			case "Party":
				partyMessage((Party)m);
				break;
			case "PlayerInfoMessage":
				playerInfoMessage((PlayerInfoMessage) m);
				break;
			case "Level":
				startGame((Level) m);
				break;
			case "GameUpdateMessage":
				updateGame((GameUpdateMessage) m);
				break;
		}
	}

	private void updateGame(GameUpdateMessage m) {
		for (Entity e : m.getChanges()) {
			if (e.getUUID().equals(player.getUUID())) {
				player = (Player) e;
			}
			level.updateEntity(e);
		}
	}


	private void startGame(final Level level) {
		this.level = level;
	}


	/**
	 * Open the lobby activity.
	 * @author Bram Pulles
	 */
	private void partyJoinedMessage(){
		joinPartyActivity.openLobby();
	}

	/**
	 * Send the new info to the lobby activity.
	 * @param party update party.
	 * @author Steven Bronsveld
	 */
	private void partyMessage(Party party){
		this.party = party;
		if(lobbyActivity != null)
			lobbyActivity.updateParty(party);
	}

	/**
	 * This function is called when the server and client established a connection
	 * and the player is created on the server. The player can send his custom
	 * name to the server. The settings screen can also call this function for a name update.
	 * @author Bram Pulles
	 */
	public void playerInfoMessage(PlayerInfoMessage m){
		this.player = m.getPlayer();
		sendPlayerInfoMessage();
	}

	/**
	 * Send a message to the server with the player information.
	 * @author Bram Pulles and Steven Bronsveld
	 */
	public void sendPlayerInfoMessage() {
		SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.sharedpref_playerinfo), MODE_PRIVATE);
		String username = sharedPrefs.getString("username", "-");

		Player player = new Player(-1,-1,-1);
		player.setName(username);

		sendMessage(new PlayerInfoMessage(player));
	}

	/**
	 * @param joinPartyActivity
	 * @author Bram Pulles
	 */
	public void setJoinPartyActivity(JoinPartyActivity joinPartyActivity){
		this.joinPartyActivity = joinPartyActivity;
	}

	/**
	 * @param lobbyActivity
	 * @author Bram Pulles
	 */
	public void setLobbyActivity(LobbyActivity lobbyActivity){
		this.lobbyActivity = lobbyActivity;
	}

	public void setGameActivity(GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	public Player getPlayer(){
		return player;
	}

	public Party getParty() {
		return party;
	}
}
