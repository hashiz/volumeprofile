package jp.meridiani.volumeprofile.profile;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileStore {
	private Context mContext;

	private static final String DATABASE_NAME = "profilelist.db";
	private static final int DATABASE_VERSION = 1;
	  
	private static final String LIST_TABLE_NAME = "profilelist";
	private static final String LIST_COL_ID     = "id";
	
	private static final String DATA_TABLE_NAME = "profiledata";
	private static final String DATA_COL_ID     = "id";
	private static final String DATA_COL_KEY    = "key";
	private static final String DATA_COL_VALUE  = "value";
	
	public static final String KEY_PROFILENAME         = "ProfileName"        ;
	public static final String KEY_RINGERMODE          = "RingerMode"         ;
	public static final String KEY_ALARMVOLUME         = "AlarmVolume"        ;
	public static final String KEY_DTMFVOLUME          = "DTMFVolume"         ;
	public static final String KEY_MUSICVOLUME         = "MusicVolume"        ;
	public static final String KEY_NOTIFICATIONVOLUME  = "NotificationVolume" ;
	public static final String KEY_RINGVOLUME          = "RingVolume"         ;
	public static final String KEY_SYSTEMVOLUME        = "SystemVolume"       ;
	public static final String KEY_VOICECALLVALUME     = "VoiceCallValume"    ;

	private static final String[] KEYLIST = new String[] {
		KEY_RINGERMODE          ,
		KEY_ALARMVOLUME         ,
		KEY_DTMFVOLUME          ,
		KEY_MUSICVOLUME         ,
		KEY_NOTIFICATIONVOLUME  ,
		KEY_RINGVOLUME          ,
		KEY_SYSTEMVOLUME        ,
		KEY_VOICECALLVALUME     ,
	};

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(String.format(
					"CREATE TABLE %1$s ( %2$s INTEGER PRIMARY KEY AUTOINCREMENT);",
						          LIST_TABLE_NAME, LIST_COL_ID));

			db.execSQL(String.format(
					"CREATE TABLE %1$s ( %2$s INTEGER, %3$s TEXT NOT NULL, %4$s TEXT NOT NULL, PRIMARY KEY( %2$s,%3$s );",
						          DATA_TABLE_NAME, DATA_COL_ID, DATA_COL_KEY, DATA_COL_VALUE));
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}

	public ProfileStore(Context context) {
		mContext = context;
	}

	public ArrayList<VolumeProfile> listProfiles() {
		SQLiteDatabase db = new DBHelper(mContext).getReadableDatabase();

		ArrayList<VolumeProfile> list = new ArrayList<VolumeProfile>();

		Cursor listCur = db.query(LIST_TABLE_NAME, null, null, null, null, null, DATA_COL_ID);
		while (listCur.moveToNext()) {
			int id = listCur.getInt(listCur.getColumnIndex(LIST_COL_ID));
			VolumeProfile profile = loadProfile(mContext, id);
			if (profile != null) {
				list.add(profile);
			}
		}
		return list;
	}

	public VolumeProfile loadProfile(Context context, int id) {
		SQLiteDatabase db = new DBHelper(context).getReadableDatabase();

		Cursor listCur = db.query(LIST_TABLE_NAME, null, LIST_COL_ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
		if (listCur.moveToFirst()) {
			Cursor dataCur = db.query(DATA_TABLE_NAME, null, DATA_COL_ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
			VolumeProfile profile = new VolumeProfile(id);
			while (dataCur.moveToNext()) {
				String key = dataCur.getString(dataCur.getColumnIndex(DATA_COL_KEY));
				String value = dataCur.getString(dataCur.getColumnIndex(DATA_COL_VALUE));
				profile.setValue(key, value);
			}
			return profile;
		}
		return null;
	}

	public void storeProfile(Context context, VolumeProfile profile) {
		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

		db.beginTransaction();

		try {
			if (profile.getProfileId() < 0) {
				// create new profile
	
				// insert list
				ContentValues values = new ContentValues();
				db.insert(LIST_TABLE_NAME, null, values);
	
				// insert data
				values.clear();
				for (String key : KEYLIST) {
					values.put(DATA_COL_ID, profile.getProfileId());
					values.put(DATA_COL_KEY, key);
					db.insert(DATA_TABLE_NAME, null, values);
				}
			}
			else {
				// update existent profile
	
				ContentValues values = new ContentValues();
	
				// update data
				values.clear();
				for (String key : KEYLIST) {
					values.put(DATA_COL_VALUE, profile.getValue(key));
					int rows = db.update(DATA_TABLE_NAME, values, "id=? and key=?", new String[]{Integer.toString(profile.getProfileId()), key});
					if (rows < 1) {
						values.put(DATA_COL_ID, profile.getProfileId());
						values.put(DATA_COL_KEY, key);
						db.insert(DATA_TABLE_NAME, null, values);
					}
				}
			}
		}
		finally {
			db.endTransaction();
		}
	}

	public void deleteProfile(Context context, VolumeProfile profile) {
		if (profile.getProfileId() < 0) {
			return;
		}

		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

		db.beginTransaction();

		try {
			// delete existent profile

			// delete data
			db.delete(DATA_TABLE_NAME, "id=?", new String[]{Integer.toString(profile.getProfileId())});

			// delete list
			db.delete(LIST_TABLE_NAME, "id=?", new String[]{Integer.toString(profile.getProfileId())});
		}
		finally {
			db.endTransaction();
		}
	}

	public VolumeProfile newProfile() {
		return new VolumeProfile(-1);
	}
}
