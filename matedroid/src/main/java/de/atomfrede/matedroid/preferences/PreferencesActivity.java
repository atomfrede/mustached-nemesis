package de.atomfrede.matedroid.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.atomfrede.matedroid.R;


@EActivity(R.layout.activity_preferences)
public class PreferencesActivity extends FragmentActivity {

    public static final String TAG = "PreferencesActivity";

    @Pref
    protected MatedroidPreferences_ mPrefs;

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

        if(mPrefs.password().get() != null && !"".equals(mPrefs.password().get().trim())) {
            password.setText(mPrefs.password().get());
        }

        getActionBar().setTitle("Matedroid Preferences");
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
