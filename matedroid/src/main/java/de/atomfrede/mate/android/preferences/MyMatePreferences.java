package de.atomfrede.mate.android.preferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(value=Scope.UNIQUE)
public interface MyMatePreferences {
	
	@DefaultString("http://simpletour.de:8080/mate")
	String apiRoot();
	@DefaultString("")
	String username();
	@DefaultString("")
	String password();
	
	long userId();
	
	String firstname();
	
	String lastname();
	
	String email();
	
	long accountId();
	
	long accountValue();
	
	int allAvailableMates();
	
	
	

}
