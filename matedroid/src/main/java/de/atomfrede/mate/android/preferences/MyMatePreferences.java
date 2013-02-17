package de.atomfrede.mate.android.preferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(value=Scope.UNIQUE)
public interface MyMatePreferences {
	
	@DefaultString("http://192.168.0.101:8080/mate.application")
	String apiRoot();
	@DefaultString("fred")
	String username();
	@DefaultString("fred")
	String password();
	
	long userId();
	
	String firstname();
	
	String lastname();
	
	String email();
	
	long accountId();
	
	long accountValue();
	
	int allAvailableMates();
	
	
	

}
