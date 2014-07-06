package de.atomfrede.matedroid.preferences;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface MatedroidPreferences {

    @DefaultString("http://www.matetracker.de")
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
