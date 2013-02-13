package de.atomfrede.mate.android;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.atomfrede.mate.android.preferences.MyMatePreferences_;
import de.atomfrede.mate.android.preferences.PreferencesActivity_;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@OptionsMenu(R.menu.menu_main)
@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	public static final String API_LOGIN = "/api/login";
	public static final String API_AVAILABLE_MATES = "/api/bottle/count";
	public static final String API_CONSUME = "/api/consume";
	public static final String API_MY_BOTTLES = "/api/consume/count";

	@Pref
	protected MyMatePreferences_ mPrefs;

	@ViewById(R.id.my_account_layout)
	protected RelativeLayout myAccountPanel;
	
	@ViewById(R.id.get_refreshed_button)
	protected Button getRefreshedButton;

	@ViewById(R.id.available_mates)
	protected TextView availableMates;

	@ViewById(R.id.mate_count)
	protected TextView myMates;

	@ViewById(R.id.value)
	protected TextView myCredit;

	protected boolean startedFromTag = false;

	protected boolean pendingConsume = false;

	@Click(R.id.get_refreshed_button)
	public void getRefreshed() {
		getBottleOfMate();
	}

	public void onResume() {
		super.onResume();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.d(TAG, "Started from NFS Tag");
			startedFromTag = true;
			getIntent().setAction(null);
		}
	}

	@AfterInject
	public void afterInject() {
		if (mPrefs.apiRoot().get() == null || "".equals(mPrefs.apiRoot().get())) {
			showSettings();
		}
		if(mPrefs.username().get() == null || "".equals(mPrefs.username().get())){
			showSettings();
		}
		if(mPrefs.password().get() == null || "".equals(mPrefs.password().get())){
			showSettings();
		}
		loadAccountData();
		getAvailableMates();
	}

	@AfterViews
	public void afterViews() {
		// loadAccountData();
	}

	@Background
	public void getConsumedMates() {
		String response = HttpRequest
				.get(mPrefs.apiRoot().get() + API_MY_BOTTLES)
				.basic(mPrefs.username().get(), mPrefs.password().get())
				.acceptJson().body();

		int consumedMates = Integer.parseInt(response);

		onConsumedMatesReceived(consumedMates);

	}

	@Background
	public void getAvailableMates() {
		String response = HttpRequest
				.get(mPrefs.apiRoot().get() + API_AVAILABLE_MATES)
				.basic(mPrefs.username().get(), mPrefs.password().get())
				.acceptJson().body();

		Log.d(TAG, "Response was " + response);

		int numberOfMates = Integer.parseInt(response);

		onMatesReceived(numberOfMates);

	}

	@Background
	public void getBottleOfMate() {
		pendingConsume = true;
		getAvailableMates();

	}

	@Background
	public void getRealBottleOfMate() {
		String response = HttpRequest.put(mPrefs.apiRoot().get() + API_CONSUME)
				.basic(mPrefs.username().get(), mPrefs.password().get())
				.acceptJson().body();

		Log.d(TAG, "Response was " + response);
		onBottleRetrieved();
	}

	@Background
	public void loadAccountData() {
		HttpRequest request = HttpRequest.get(
				mPrefs.apiRoot().get() + API_LOGIN).basic(
				mPrefs.username().get(), mPrefs.password().get());
		try {
			int code = request.code();

			if (code == HttpURLConnection.HTTP_OK) {
				// go one with normal stuff
				String response = request.acceptJson().body();

				Log.d(TAG, "Response was " + response);

				try {
					JSONObject jsono = new JSONObject(response);
					String firstname = jsono.getString("firstname");
					String lastname = jsono.getString("lastname");
					String username = jsono.getString("username");
					String email = jsono.getString("email");
					Long userId = jsono.getLong("id");

					JSONObject jsonAccount = jsono.getJSONObject("account");
					long accountValue = jsonAccount.getLong("value");
					Long accountId = jsonAccount.getLong("id");

					Log.d(TAG, "Account Value " + accountValue);

					mPrefs.edit().userId().put(userId).username().put(username)
							.firstname().put(firstname).lastname()
							.put(lastname).email().put(email).accountId()
							.put(accountId).accountValue().put(accountValue)
							.apply();

					onAccountReceived();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				onConnectionError();
			}
		} catch (HttpRequestException eq) {
			onConnectionError();
		}
	}

	@UiThread
	public void onConsumedMatesReceived(int consumedMates) {
		Resources res = getResources();
		String consumedMatesText = String.format(
				res.getString(R.string.my_account_consumes), consumedMates);
		myMates.setText(consumedMatesText);
		
	}

	@UiThread
	public void onMatesReceived(int mates) {
		Resources res = getResources();
		String matesText = String.format(
				res.getString(R.string.all_bottles_text), mates);
		availableMates.setText(matesText);

		if (pendingConsume) {
			if (mates <= 0) {
				onNoMatesAvailable();
			} else {
				getRealBottleOfMate();
			}

			pendingConsume = false;
		}
	}

	@UiThread
	public void onAccountReceived() {

		getActionBar().setSubtitle(
				mPrefs.firstname().get() + " " + mPrefs.lastname().get() + " ("
						+ mPrefs.username().get() + ")");

		Resources res = getResources();
		String accountCredit = String.format(res
				.getString(R.string.my_account_credit), mPrefs.accountValue()
				.get())
				+ "€";
		Log.d(TAG, "Account Credit " + accountCredit);
		myCredit.setText(accountCredit);

		if (startedFromTag) {
			getRefreshed();
			startedFromTag = false;
		}

		myAccountPanel.setVisibility(View.VISIBLE);
		getRefreshedButton.setVisibility(View.VISIBLE);
		getConsumedMates();
	}

	@UiThread
	public void onBottleRetrieved() {
		Crouton.makeText(this, R.string.bottle_success, Style.CONFIRM).show();
		startedFromTag = false;
		getIntent().setAction(null);
		getAvailableMates();
		getConsumedMates();
		loadAccountData();
	}

	@UiThread
	public void onNoMatesAvailable() {
		Crouton.makeText(this, R.string.error_no_mates, Style.ALERT).show();
		getAvailableMates();
		getConsumedMates();
		loadAccountData();
	}

	@UiThread
	public void onConnectionError() {
		Log.d(TAG, "On Connection Error");
		Resources res = getResources();
		availableMates.setText(res.getString(R.string.error_connection_failed));
		availableMates.setTextColor(res.getColor(R.color.error_color));
		
		getRefreshedButton.setVisibility(View.INVISIBLE);
		myAccountPanel.setVisibility(View.INVISIBLE);
	}

	public void showSettings() {
		Intent intent = new Intent(this, PreferencesActivity_.class);
		startActivity(intent);
	}

}
