package de.atomfrede.mate.android.preferences;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.atomfrede.mate.android.R;

@EActivity(R.layout.activity_preferences)
public class PreferencesActivity extends FragmentActivity {

	public static final String TAG = "PreferencesActivity";

	@Pref
	protected MyMatePreferences_ mPrefs;

	@ViewById(R.id.rest_root)
	protected EditText restRoot;

	@ViewById(R.id.user_name)
	protected EditText username;

	@ViewById(R.id.passwort)
	protected EditText password;

	@AfterViews
	protected void afterViews() {
		if (mPrefs.apiRoot().get() != null
				&& !"".equals(mPrefs.apiRoot().get().trim())) {
			restRoot.setText(mPrefs.apiRoot().get());
		}

		if (mPrefs.username().get() != null
				&& !"".equals(mPrefs.username().get().trim())) {
			username.setText(mPrefs.username().get());
		}
		
		if(mPrefs.password().get() != null && !"".equals(mPrefs.password().get().trim())){
			password.setText(mPrefs.password().get());
		}
	}
	
	@Override
	public void onBackPressed() {
		Log.d("Mate", "On Back Pressed!");
		readApiRoot();
		readUserName();
		readPassword();
		super.onBackPressed();
	}
	
	protected boolean checkForValidValues(){
		return false;
	}
	
	protected void readApiRoot(){
		mPrefs.edit().apiRoot().put(restRoot.getText().toString()).apply();
	}
	
	protected void readUserName(){
		mPrefs.edit().username().put(username.getText().toString()).apply();
	}
	
	protected void readPassword(){
		mPrefs.edit().password().put(password.getText().toString()).apply();
	}
}
