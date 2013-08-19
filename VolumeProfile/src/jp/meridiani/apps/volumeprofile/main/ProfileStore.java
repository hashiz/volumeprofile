package jp.meridiani.apps.volumeprofile.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileStore {

	private static ProfileStore mInstance = null;

	private SQLiteDatabase mDB;

	private static final String DATABASE_NAME = "profilelist.db";
	private static final int    DATABASE_VERSION = 1;
	  
	private static final String MISC_TABLE_NAME = "misc";
	private static final String COL_KEY         = "key";
	private static final String COL_VALUE       = "value";
	
	private static final String LIST_TABLE_NAME = "profilelist";
	private static final String COL_UUID        = "uuid";
	private static final String COL_DISPORDER   = "displayorder";
	
	private static final String DATA_TABLE_NAME = "profiledata";
	
	public static final String KEY_CURRENTPROFILE      = "CurrentProfile"     ;
	public static final String KEY_UUID                = "Uuid"               ;
	public static final String KEY_DISPLAYORDER        = "DisplayOrder"       ;
	public static final String KEY_PROFILENAME         = "ProfileName"        ;
	public static final String KEY_RINGERMODE          = "RingerMode"         ;
	public static final String KEY_RINGERMODELOCK      = "RingerModeLock"     ;
	public static final String KEY_ALARMVOLUME         = "AlarmVolume"        ;
	public static final String KEY_ALARMVOLUMELOCK     = "AlarmVolumeLock"    ;
	public static final String KEY_MUSICVOLUME         = "MusicVolume"        ;
	public static final String KEY_MUSICVOLUMELOCK     = "MusicVolumeLock"    ;
	public static final String KEY_RINGVOLUME          = "RingVolume"         ;
	public static final String KEY_RINGVOLUMELOCK      = "RingVolumeLock"     ;
	public static final String KEY_VOICECALLVALUME     = "VoiceCallValume"    ;
	public static final String KEY_VOICECALLVALUMELOCK = "VoiceCallValumeLock";

	private static final String[] PROFILE_DATA_KEYS = new String[] {
		KEY_PROFILENAME         ,
		KEY_RINGERMODE          ,
		KEY_RINGERMODELOCK      ,
		KEY_ALARMVOLUME         ,
		KEY_ALARMVOLUMELOCK     ,
		KEY_MUSICVOLUME         ,
		KEY_MUSICVOLUMELOCK     ,
		KEY_RINGVOLUME          ,
		KEY_RINGVOLUMELOCK      ,
		KEY_VOICECALLVALUME     ,
		KEY_VOICECALLVALUMELOCK ,
	};

	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(String.format(
					"CREATE TABLE %1$s ( _id INTEGER PRIMARY KEY AUTOINCREMENT, %2$s TEXT NOT NULL UNIQUE, %3$s TEXT);",
						MISC_TABLE_NAME, COL_KEY, COL_VALUE));

			db.execSQL(String.format(
					"CREATE TABLE %1$s ( _id INTEGER PRIMARY KEY AUTOINCREMENT, %2$s TEXT NOT NULL UNIQUE, %3$s INTEGER);",
						LIST_TABLE_NAME, COL_UUID, COL_DISPORDER));

			db.execSQL(String.format(
					"CREATE TABLE %1$s ( _id INTEGER PRIMARY KEY AUTOINCREMENT, %2$s TEXT NOT NULL, %3$s TEXT NOT NULL, %4$s TEXT NOT NULL);",
						DATA_TABLE_NAME, COL_UUID, COL_KEY, COL_VALUE));
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}

	private ProfileStore(Context context) {
		mDB = new DBHelper(context).getWritableDatabase();
	}

	public static synchronized ProfileStore getInstance(Context context) {
		if ( mInstance == null ) {
			mInstance = new ProfileStore(context);
		}
		return mInstance;
	}

	public ArrayList<VolumeProfile> listProfiles() {
		ArrayList<VolumeProfile> list = new ArrayList<VolumeProfile>();

		Cursor listCur = mDB.query(LIST_TABLE_NAME, null, null, null, null, null, COL_DISPORDER);
		while (listCur.moveToNext()) {
			UUID uuid = UUID.fromString(listCur.getString(listCur.getColumnIndex(COL_UUID)));
			int order = listCur.getInt(listCur.getColumnIndex(COL_DISPORDER));
			VolumeProfile profile = new VolumeProfile(uuid);
			profile.setDisplayOrder(order);
			loadProfileInternal(profile);
			list.add(profile);
		}
		return list;
	}

	public void updateDisplayOrder(List<VolumeProfile> list) {

		mDB.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			for (VolumeProfile profile : list) {
				// update/insert list
				values.clear();
				values.put(COL_DISPORDER, profile.getDisplayOrder());
				mDB.update(LIST_TABLE_NAME, values,
						String.format("%1$s=?", COL_UUID),
						new String[]{profile.getUuid().toString()});
			}
			mDB.setTransactionSuccessful();
		}
		finally {
			mDB.endTransaction();
		}
	}

	private VolumeProfile loadProfileInternal(VolumeProfile profile) {
		String uuid = profile.getUuid().toString();
		Cursor dataCur = mDB.query(DATA_TABLE_NAME, null, COL_UUID + "=?", new String[]{uuid}, null, null, null);
		while (dataCur.moveToNext()) {
			String key = dataCur.getString(dataCur.getColumnIndex(COL_KEY));
			String value = dataCur.getString(dataCur.getColumnIndex(COL_VALUE));
			profile.setValue(key, value);
		}
		return profile;
	}

	public VolumeProfile loadProfile(UUID uuid) {
		Cursor listCur = mDB.query(LIST_TABLE_NAME, null, COL_UUID + "=?", new String[] {uuid.toString()}, null, null, null);
		if (listCur.moveToFirst()) {
			int order = listCur.getInt(listCur.getColumnIndex(COL_DISPORDER));
			VolumeProfile profile = new VolumeProfile(uuid);
			profile.setDisplayOrder(order);
			return loadProfileInternal(profile);
		}
		return null;
	}

	public void storeProfile(VolumeProfile profile) {
		mDB.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			// update/insert list
			{
				values.clear();
				values.put(COL_DISPORDER, profile.getDisplayOrder());
				int rows = mDB.update(LIST_TABLE_NAME, values,
						String.format("%1$s=?", COL_UUID),
						new String[]{profile.getUuid().toString()});
				if (rows < 1) {
					values.put(COL_UUID, profile.getUuid().toString());
					mDB.insert(LIST_TABLE_NAME, null, values);
				}
			}
			// update/insert data
			for (String key : PROFILE_DATA_KEYS) {
				values.clear();
				values.put(COL_VALUE, profile.getValue(key));
				int rows = mDB.update(DATA_TABLE_NAME, values,
						String.format("%1$s=? and %2$s=?", COL_UUID, COL_KEY),
						new String[]{profile.getUuid().toString(), key});
				if (rows < 1) {
					values.put(COL_UUID, profile.getUuid().toString());
					values.put(COL_KEY, key);
					mDB.insert(DATA_TABLE_NAME, null, values);
				}
			}
			mDB.setTransactionSuccessful();
		}
		finally {
			mDB.endTransaction();
		}
	}

	public void deleteProfile(UUID profileId) {
		mDB.beginTransaction();

		try {
			// delete existent profile

			// delete data
			mDB.delete(DATA_TABLE_NAME, COL_UUID+"=?", new String[]{profileId.toString()});

			// delete list
			mDB.delete(LIST_TABLE_NAME, COL_UUID+"=?", new String[]{profileId.toString()});

			mDB.setTransactionSuccessful();
		}
		finally {
			mDB.endTransaction();
		}
	}

	private int getMaxOrder() {
		Cursor listCur = mDB.rawQuery(String.format(
				"select max(%2$s) from %1$s;", LIST_TABLE_NAME, COL_DISPORDER),null);
		if (listCur.moveToFirst()) {
			return listCur.getInt(0);
		}
		return 0;
	}

	public VolumeProfile newProfile() {
		VolumeProfile profile = new VolumeProfile();
		profile.setDisplayOrder(getMaxOrder()+1);
		return profile;
	}

	public UUID getCurrentProfile() {
		Cursor cur = mDB.query(MISC_TABLE_NAME, null, COL_KEY + "=?", new String[] {KEY_CURRENTPROFILE}, null, null, null);
		if (cur.moveToFirst()) {
			return UUID.fromString(cur.getString(cur.getColumnIndex(COL_VALUE)));
		}
		return null;
	}

	public void setCurrentProfile(UUID uuid) {
		mDB.beginTransaction();

		try {
			ContentValues values = new ContentValues();

			// update/insert data
			values.put(COL_KEY,   KEY_CURRENTPROFILE);
			values.put(COL_VALUE, uuid.toString());
			int rows = mDB.update(MISC_TABLE_NAME, values,
					String.format("%1$s=?", COL_KEY),
					new String[]{KEY_CURRENTPROFILE});
			if (rows < 1) {
				values.put(COL_KEY,   KEY_CURRENTPROFILE);
				values.put(COL_VALUE, uuid.toString());
				mDB.insert(MISC_TABLE_NAME, null, values);
			}

			mDB.setTransactionSuccessful();
		}
		finally {
			mDB.endTransaction();
		}
	}
}