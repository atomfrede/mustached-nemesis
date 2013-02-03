package de.atomfrede.mate.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;

import com.github.kevinsawicki.http.HttpRequest;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.atomfrede.mate.android.preferences.MyMatePreferences_;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	@Pref
	MyMatePreferences_ mPrefs;

	@ViewById(R.id.get_refreshed_button)
	protected Button getRefreshedButton;

	@Click(R.id.get_refreshed_button)
	public void getRefreshed() {
		getBottleOfMate();
	}

	@AfterInject
	public void afterInject() {
		loadAccountData();
	}

	public void afterViews() {

	}

	@Background
	public void getBottleOfMate(){
		String response = HttpRequest.put("http://10.0.2.2:8080/mate.application/api/consume")
		.basic("fred", "fred").acceptJson().body();
		
		Log.d(TAG, "Response was " + response);
	}
	
	@Background
	public void loadAccountData() {
		String response = HttpRequest
				.get("http://10.0.2.2:8080/mate.application/api/login")
				.basic("fred", "fred").acceptJson().body();
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

			mPrefs.edit().userId().put(userId).username().put(username)
					.firstname().put(firstname).lastname().put(lastname)
					.email().put(email).accountId().put(accountId)
					.accountValue().put(accountValue).apply();

			onAccountReceived();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@UiThread
	public void onAccountReceived() {

		getActionBar().setSubtitle(
				mPrefs.firstname().get() + " " + mPrefs.lastname().get() + " ("
						+ mPrefs.username().get() + ")");
	}
	
	@UiThread
	public void onBottleRetrieved(){
		
	}

}
