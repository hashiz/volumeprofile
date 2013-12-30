package jp.meridiani.apps.volumeprofile.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.profile.VolumeProfile.Key;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileStore {

	private static ProfileStore mInstance = null;
	
	private SQLiteDatabase mDB;
	private Context mContext;

	private static final String DATABASE_NAME = "profilelist.db";
	private static final int    DATABASE_VERSION = 3;
	  
	private static final String MISC_TABLE_NAME = "misc";
	private static final String COL_KEY         = "key";
	private static final String COL_VALUE       = "value";
	
	private static final String LIST_TABLE_NAME = "profilelist";
	private static final String COL_UUID        = "uuid";
	private static final String COL_DISPORDER   = "displayorder";
	
	private static final String DATA_TABLE_NAME = "profiledata";
	
	private static final String KEY_CURRENTPROFILE      = "CurrentProfile"     ;
	private static final String KEY_VOLUMELOCKED        = "VolumeLocked"     ;

	private static final String PROFILES_START = "<profiles>";
	private static final String PROFILES_END   = "</profiles>";

	private static List<OnProfileSwitchedListener> mOnProfileSwitchedListeners = new ArrayList<OnProfileSwitchedListener>();

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
			if (oldVersion < 2) {
				db.beginTransaction();
				try {
					db.execSQL(
							String.format("INSERT INTO %1$s (%2$s, %3$s, %4$s) SELECT %2$s, '%5$s', %4$s FROM %1$s WHERE %3$s='%6$s';",
							DATA_TABLE_NAME, COL_UUID, COL_KEY, COL_VALUE, VolumeProfile.Key.NOTIFICATIONVOLUME, VolumeProfile.Key.RINGVOLUME)
							);
					db.setTransactionSuccessful();
				}
				finally {
					db.endTransaction();
				}
			}
			if (oldVersion < 3) {
				db.beginTransaction();
				try {
					db.execSQL(
							String.format("UPDATE %1$s SET %2$s='%3$s' WHERE %2$s='%4$s';",
							DATA_TABLE_NAME, COL_KEY, VolumeProfile.Key.VOICECALLVOLUME, "VOICECALLVALUME"));
					db.execSQL(
							String.format("UPDATE %1$s SET %2$s='%3$s' WHERE %2$s='%4$s';",
							DATA_TABLE_NAME, COL_KEY, VolumeProfile.Key.VOICECALLVOLUMELOCK, "VOICECALLVALUMELOCK"));
					db.execSQL(
							String.format("INSERT INTO %1$s (%2$s, %3$s, %4$s) SELECT %2$s, '%5$s', %4$s FROM %1$s WHERE %3$s='%6$s';",
							DATA_TABLE_NAME, COL_UUID, COL_KEY, COL_VALUE, VolumeProfile.Key.SYSTEMVOLUME, VolumeProfile.Key.RINGVOLUME));
					db.execSQL(
							String.format("INSERT INTO %1$s (%2$s, %3$s, %4$s) SELECT %2$s, '%5$s', %4$s FROM %1$s WHERE %3$s='%6$s';",
							DATA_TABLE_NAME, COL_UUID, COL_KEY, COL_VALUE, VolumeProfile.Key.SYSTEMVOLUMELOCK, VolumeProfile.Key.RINGVOLUMELOCK));
					db.setTransactionSuccessful();
				}
				finally {
					db.endTransaction();
				}
			}
			return;
		}
		
	}

	private ProfileStore(Context context) {
		mDB = new DBHelper(context).getWritableDatabase();
		mContext = context;
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
		try {
			while (listCur.moveToNext()) {
				UUID uuid = UUID.fromString(listCur.getString(listCur.getColumnIndex(COL_UUID)));
				int order = listCur.getInt(listCur.getColumnIndex(COL_DISPORDER));
				VolumeProfile profile = new VolumeProfile(uuid);
				profile.setDisplayOrder(order);
				loadProfileInternal(profile);
				list.add(profile);
			}
		}
		finally {
			listCur.close();
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
			requestBackup();
		}
		finally {
			mDB.endTransaction();
		}
	}

	private VolumeProfile loadProfileInternal(VolumeProfile profile) {
		String uuid = profile.getUuid().toString();
		Cursor dataCur = mDB.query(DATA_TABLE_NAME, null, COL_UUID + "=?", new String[]{uuid}, null, null, null);
		try {
			while (dataCur.moveToNext()) {
				String key = dataCur.getString(dataCur.getColumnIndex(COL_KEY));
				String value = dataCur.getString(dataCur.getColumnIndex(COL_VALUE));
				profile.setValue(key, value);
			}
		}
		finally {
			dataCur.close();
		}
		return profile;
	}

	public VolumeProfile loadProfile(UUID uuid) {
		Cursor listCur = mDB.query(LIST_TABLE_NAME, null, COL_UUID + "=?", new String[] {uuid.toString()}, null, null, null);
		try {
			if (listCur.moveToFirst()) {
				int order = listCur.getInt(listCur.getColumnIndex(COL_DISPORDER));
				VolumeProfile profile = new VolumeProfile(uuid);
				profile.setDisplayOrder(order);
				return loadProfileInternal(profile);
			}
		}
		finally {
			listCur.close();
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
			for (Key key : VolumeProfile.listDataKeys()) {
				values.clear();
				values.put(COL_VALUE, profile.getValue(key));
				int rows = mDB.update(DATA_TABLE_NAME, values,
						String.format("%1$s=? and %2$s=?", COL_UUID, COL_KEY),
						new String[]{profile.getUuid().toString(), key.name()});
				if (rows < 1) {
					values.put(COL_UUID, profile.getUuid().toString());
					values.put(COL_KEY, key.name());
					mDB.insert(DATA_TABLE_NAME, null, values);
				}
			}
			mDB.setTransactionSuccessful();
			requestBackup();
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
			requestBackup();
		}
		finally {
			mDB.endTransaction();
		}
	}

	private int getMaxOrder() {
		Cursor listCur = mDB.rawQuery(String.format(
				"select max(%2$s) from %1$s;", LIST_TABLE_NAME, COL_DISPORDER),null);
		try {
			if (listCur.moveToFirst()) {
				return listCur.getInt(0);
			}
		}
		finally {
			listCur.close();
		}
		return 0;
	}

	public VolumeProfile newProfile() {
		VolumeProfile profile = new VolumeProfile();
		profile.setDisplayOrder(getMaxOrder()+1);
		return profile;
	}

	public boolean isVolumeLocked() {
		Cursor cur = mDB.query(MISC_TABLE_NAME, null, COL_KEY + "=?", new String[] {KEY_VOLUMELOCKED}, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return Boolean.parseBoolean(cur.getString(cur.getColumnIndex(COL_VALUE)));
			}
		}
		finally {
			cur.close();
		}
		return false;
	}

	public void setVolumeLocked(boolean locked) {
		mDB.beginTransaction();

		try {
			ContentValues values = new ContentValues();

			// update/insert data
			values.put(COL_KEY,   KEY_VOLUMELOCKED);
			values.put(COL_VALUE, Boolean.toString(locked));
			int rows = mDB.update(MISC_TABLE_NAME, values,
					String.format("%1$s=?", COL_KEY),
					new String[]{KEY_VOLUMELOCKED});
			if (rows < 1) {
				values.put(COL_KEY,   KEY_VOLUMELOCKED);
				values.put(COL_VALUE, Boolean.toString(locked));
				mDB.insert(MISC_TABLE_NAME, null, values);
			}

			mDB.setTransactionSuccessful();
		}
		finally {
			mDB.endTransaction();
		}
	}

	public UUID getCurrentProfile() {
		Cursor cur = mDB.query(MISC_TABLE_NAME, null, COL_KEY + "=?", new String[] {KEY_CURRENTPROFILE}, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return UUID.fromString(cur.getString(cur.getColumnIndex(COL_VALUE)));
			}
		}
		finally {
			cur.close();
		}
		return null;
	}

	public void setCurrentProfile(UUID uuid) {
		UUID prevId = getCurrentProfile();
		if (uuid.equals(prevId)) {
			return;
		}
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
			fireOnProfileSwitchedListener(uuid, prevId);
		}
		finally {
			mDB.endTransaction();
		}
	}

	public void writeToText(BufferedWriter wtr) throws IOException {
		wtr.write(PROFILES_START); wtr.newLine();
		for (VolumeProfile profile : listProfiles()) {
			profile.writeToText(wtr);
		}
		wtr.write(PROFILES_END); wtr.newLine();
	}

	public void readFromText(BufferedReader rdr) throws IOException {
    	String line;
		while ((line = rdr.readLine()) != null) {
			if (PROFILES_START.equals(line)) {
	            VolumeProfile profile;
	            while ((profile = VolumeProfile.createFromText(rdr)) != null) {
	            	storeProfile(profile);
	            }
			}
		}
	}

	private void requestBackup() {
		BackupManager.dataChanged(mContext.getPackageName());
	}

	public interface OnProfileSwitchedListener {
		public void onProfileSwitched(UUID newId, UUID prevId);
	}

	public void registerOnProfileSwitchedListener(OnProfileSwitchedListener listener) {
		synchronized (mOnProfileSwitchedListeners) {
			mOnProfileSwitchedListeners.add(listener);
		}
	}
	public void unregisterOnProfileSwitchedListener(OnProfileSwitchedListener listener) {
		synchronized (mOnProfileSwitchedListeners) {
			while(mOnProfileSwitchedListeners.remove(listener));
		}
	}

	protected void fireOnProfileSwitchedListener(UUID newId, UUID prevId) {
		synchronized (mOnProfileSwitchedListeners) {
			for (OnProfileSwitchedListener listener : mOnProfileSwitchedListeners) {
				listener.onProfileSwitched(newId, prevId);
			}
		}
	}
}
