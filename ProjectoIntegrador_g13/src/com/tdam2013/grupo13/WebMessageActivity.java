package com.tdam2013.grupo13;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tdam2013.grupo13.adapters.WebMessageAdapter;
import com.tdam2013.grupo13.dataBase.DataBaseManager;
import com.tdam2013.grupo13.location.LocationActivity;
import com.tdam2013.grupo13.messaging.WebMessageServiceListener;
import com.tdam2013.grupo13.messaging.WebMessageServiceWrapper;
import com.tdam2013.grupo13.model.Contact;
import com.tdam2013.grupo13.model.WebMessage;
import com.tdam2013.grupo13.notification.MyNotificationManager;

import java.util.ArrayList;

public class WebMessageActivity extends Activity implements
		WebMessageServiceListener {

	private Contact contact;
	private WebMessageServiceWrapper service;
	private EditText editTextMdg;
	private String user;
	private String pass;
	private ListView listView;
	private WebMessageAdapter webMessageAdapter;
	private SharedPreferences prefs;
	private WebMessageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_message);

		service = new WebMessageServiceWrapper(this, this);
		editTextMdg = (EditText) findViewById(R.id.editTextMsg);

		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		user = prefs.getString("user_name_pref", "");
		pass = prefs.getString("password_pref", "");


		Button sendButton = (Button) findViewById(R.id.buttonSendMsg);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editTextMdg.getText().toString();
				if (!text.equals("")) {
					user = prefs.getString("user_name_pref", "").trim();
					pass = prefs.getString("password_pref", "").trim();
					text = "MSG"+text;
					service.sendMessage(user, pass, contact.getName(), text);
				}
			}
		});

		Button gpsButton = (Button) findViewById(R.id.buttonGetPoss);
		gpsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
				startActivityForResult(intent,0);
			}
		});

		// Loading contact
		contact = (Contact) getIntent().getSerializableExtra("contact");

		listView = (ListView) findViewById(R.id.msgs_list);
		ArrayList<WebMessage> messages = getChatMessage();
		webMessageAdapter = new WebMessageAdapter(this, messages);
		listView.setAdapter(webMessageAdapter);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	protected void onResume(){
		super.onResume();
		//adapter.update(getChatMessage());
	}
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(contact.getName());

	}

	private ArrayList<WebMessage> getChatMessage(){
		ArrayList<WebMessage> messages = new ArrayList<WebMessage>();
		DataBaseManager db = new DataBaseManager(this.getApplicationContext());
		Cursor cursor = db.getMessageUser(contact.getName());
		WebMessage msg;

		if(cursor.moveToFirst()){
			do {
				String message = cursor.getString(0);
				String date = cursor.getString(1);
				String receiverName = cursor.getString(2);
				msg = new WebMessage(date, message,receiverName,contact.getName());
				messages.add(msg);
			} while (cursor.moveToNext());
		};
		return messages;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_contact_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMessageSent(String message, String time) {
		MyNotificationManager.notify(this, "Mensaje enviado",
				"Mensaje enviado a " + contact.getName());
		editTextMdg.setText("");
		webMessageAdapter.addItem(new WebMessage(time, message));
	}

	@Override
	public void onMessageError(String message, String time) {
		MyNotificationManager.notify(this, "Mensaje NO enviado",
				"Imposible enviar el mensaje: \"" + message + "\" al contacto: "
						+ contact.getName());
		editTextMdg.setText("");
		webMessageAdapter.addItem(new WebMessage(time, message));
	}

	@Override
	 protected void onActivityResult(int requestCode, int resultCode,Intent data){
		if(requestCode == 0 && resultCode == Activity.RESULT_OK){
			String myPosition = "LOC"+data.getStringExtra("latitud")+","+data.getStringExtra("longitud");
			user = prefs.getString("user_name_pref", "").trim();
			pass = prefs.getString("password_pref", "").trim();
			service.sendMessage(user, pass, contact.getName(), myPosition);
		}else{
			Toast.makeText(this, "Prueba de Nuevo", Toast.LENGTH_SHORT).show();
		}
	}
}
