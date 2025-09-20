package com.jason.microstream.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.jason.microstream.MsApplication;
import java.lang.reflect.Type;
import java.util.Map;

public class CommonSharePrefsManager {

	private static CommonSharePrefsManager sInstance;
	private SharedPreferences mManager;

    private static final String SP_NAME = "youban";

	private CommonSharePrefsManager() {
	}

	public static CommonSharePrefsManager getInstance() {
		if (sInstance == null) {
			sInstance = new CommonSharePrefsManager();
		}
		return sInstance;
	}

	public Map<String, ?> getAll(){
		check();
		return mManager.getAll();
	}
	
	public void putBoolean(String key, boolean value) {
		check();
		mManager.edit().putBoolean(key, value).apply();
	}

	public void putString(String key, String value) {
		check();
		mManager.edit().putString(key, value).apply();
	}

	public boolean contains(String key){
		check();
		return mManager.contains(key);
	}
	
	public void putInt(String key, int value){
		check();
		mManager.edit().putInt(key, value).apply();
	}
	
	public long getLong(String key){
		check();
		return mManager.getLong(key, 0);
	}
	
	public int getInt(String key){
		check();
		return mManager.getInt(key, 0);
	}

	public int getInt(String key, int val) {
		check();
		return mManager.getInt(key, val);
	}

	public void remove(String key) {
		check();
		mManager.edit().remove(key).apply();
	}
	
	public Editor editor(){
		check();
		return mManager.edit();
	}

	public String getString(String key) {
		check();
		return mManager.getString(key, "");
	}

	public String getString(String key,String deValue) {
		check();
		return mManager.getString(key, deValue);
	}
	
	public boolean getBoolean(String key){
		check();
		return mManager.getBoolean(key, false);
	}

	public boolean getBoolean(String key,boolean deValue){
		check();
		return mManager.getBoolean(key,deValue);
	}
	
	public void putLong(String key, long value){
		check();
		mManager.edit().putLong(key, value).apply();
	}

	public void setBean(String key, Object bean) {
		check();
		if(bean == null) {
			editor().remove(key).apply();
		} else {
			editor().putString(key, new Gson().toJson(bean)).commit();
		}
	}

	public <T> T getBean(String key, Type clazz) {
		check();
		String json = mManager.getString(key, null);
		if (TextUtils.isEmpty(json)) return null;
		return new Gson().fromJson(json, clazz);
	}

//	public void put(Context context, String fileName, String key, String value) {
//		SharedPreferences state = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
//		state.edit().putString(key, value).apply();
//	}
//
//	public String get(Context context, String fileName, String key, String defaultValue) {
//		SharedPreferences state = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
//		return state.getString(key,defaultValue);
//	}

	private void check(){
		if(mManager == null){
			mManager = MsApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
	}

	public void clear(){
		check();
		mManager.edit().clear().commit();
	}
}
