package client.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.debernardi.archemii.R;

import client.controller.ClientGameHandler;
import client.controller.Connection;
import shared.messages.CreatePartyMessage;
import shared.messages.JoinPartyMessage;

public class MainActivity extends AppCompatActivity {

	/**
	 * Initialize the client game handler.
	 * @param savedInstanceState
	 * @author Bram Pulles and Steven Bronsveld
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.sharedpref_playerinfo), MODE_PRIVATE);
		ClientGameHandler.init(sharedPrefs);
	}

	public void onCreateParty(View v){
		ClientGameHandler.sendMessage(new CreatePartyMessage());

		Intent intCreateParty = new Intent(this, LobbyActivity.class);
		startActivity(intCreateParty);
	}

	public void onJoinParty(View v){
		Intent intJoinParty = new Intent(this, JoinPartyActivity.class);
		startActivity(intJoinParty);
	}

	public void onSettings(View v){
		Intent intSettings = new Intent(this, SettingsActivity.class);
		startActivity(intSettings);
	}
}
