package client.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.debernardi.archemii.R;

import java.util.ArrayList;

import client.controller.ClientGameHandler;
import shared.entities.Player;
import shared.messages.LeavePartyMessage;
import shared.messages.UpdatePartyMessage;

public class LobbyActivity extends AppCompatActivity {

	private Button btnGameSelect, btnRandomGame;

	private TextView gamePin;
	private TextView txtPlayers;

	private ArrayList<Player> players;

	/**
	 * This method also sets this activity available in the client game handler.
	 * @param savedInstanceState
	 * @author Jelmer Firet and Bram Pulles
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);

		gamePin = findViewById(R.id.lobbyGamePin);
		txtPlayers = findViewById(R.id.playersTxtView);

		ClientGameHandler.handler.setLobbyActivity(this);

		// Get the latest update party message send by the server, if available.
		if(ClientGameHandler.handler.getUpdatePartyMessage() != null)
			updatePartyMessage(ClientGameHandler.handler.getUpdatePartyMessage());

		btnGameSelect = findViewById(R.id.btnGameSelect);
		btnRandomGame = findViewById(R.id.btnRandomGame);
		toggleButtons();
	}

	/**
	 * If the client is the master show the buttons and make them clickable.
	 * If not fade the buttons and make them not clickable.
	 * @author Bram Pulles
	 */
	private void toggleButtons(){
		if(btnGameSelect != null && btnRandomGame != null) {
			if (isMaster()) {
				btnGameSelect.setAlpha(1);
				btnRandomGame.setAlpha(1);
				btnGameSelect.setClickable(true);
				btnRandomGame.setClickable(true);
			} else {
				btnGameSelect.setAlpha(0.5f);
				btnRandomGame.setAlpha(0.5f);
				btnGameSelect.setClickable(false);
				btnRandomGame.setClickable(false);
			}
		}
	}

	/**
	 * @return if the client is the master of the party.
	 * @author Bram Pulles
	 */
	private boolean isMaster(){
		SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.sharedpref_playerinfo), MODE_PRIVATE);
		String username = sharedPrefs.getString(getString(R.string.sharedpref_username), "-");

		return players != null && players.get(0) != null && username.equals(players.get(0).getName());
	}

	/**
	 * Start the game selection activity if the client is the master.
	 * @param v
	 * @author Bram Pulles and Jelmer Firet
	 */
	public void onGameSelect(View v){
		if(isMaster()){
			Intent intGameSelect = new Intent(this, GameSelectActivity.class);
			startActivity(intGameSelect);
		}
	}

	/**
	 * Start a random game if the client is the master.
	 * @param v
	 * @author Bram Pulles and Jelmer Firet
	 */
	public void onRandomGame(View v){
		if(isMaster()){
			Intent intGame = new Intent(this, GameActivity.class);
			startActivity(intGame);
		}
	}

	/**
	 * Set the game pin and the players accordingly on the screen.
	 * Also change the view of the buttons if needed.
	 * @param m
	 * @author Bram Pulles
	 */
	public void updatePartyMessage(final UpdatePartyMessage m){
		players = m.getPlayers();
		toggleButtons();

		// This is necessary because we are not invoking this method from the main thread.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String players = "MASTER: ";
				for(int i = 0; i < m.getPlayers().size(); i++){
					players += m.getPlayers().get(i).getName() + "\n";
				}

				txtPlayers.setText(players);
				gamePin.setText("" + m.getPartyId());
			}
		});
	}

	/**
	 * Show a dialog screen when the back button is pressed.
	 * If the player leaves a leave party message is send to the server.
	 * @author Bram Pulles
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
			.setTitle("Really Exit?")
			.setMessage("Are you sure you want to exit the party?")
			.setNegativeButton(android.R.string.no, null)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					LobbyActivity.super.onBackPressed();
					ClientGameHandler.handler.sendMessage(new LeavePartyMessage());
				}
			}).create().show();
	}
}
