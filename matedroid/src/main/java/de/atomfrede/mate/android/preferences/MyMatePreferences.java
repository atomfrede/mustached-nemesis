package de.atomfrede.mate.android.preferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MyMatePreferences {
	
	String apiRoot();
	
	String username();
	
	String password();
	
	long userId();
	
	String firstname();
	
	String lastname();
	
	String email();
	
	long accountId();
	
	long accountValue();
	
	int allAvailableMates();
	
	
	

}
