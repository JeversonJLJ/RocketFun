package com.jljsoluctions.rocketfun.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public final class Properties {


	private SharedPreferences preferences;
	public Properties(Context contexto) {


		preferences = PreferenceManager.getDefaultSharedPreferences(contexto);

	}

	public boolean Read(String nome) {
		synchronized (this)
		{
			return Read(nome, false);
		}
	}

	public boolean Read(String name, boolean defaultValue) {

		return preferences.getBoolean(name,defaultValue);
	}

	public void Save(String name, String value) {
		synchronized (this)
		{
			preferences.edit().putString(name, value).commit();
		}

	}
}
