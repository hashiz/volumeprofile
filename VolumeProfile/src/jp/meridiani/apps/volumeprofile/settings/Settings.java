package jp.meridiani.apps.volumeprofile.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Settings {

	private static Settings mInstance = null;
	private SQLiteDatabase   mDB = null;
	
	private static final String DATABASE_NAME = "settings.db";
	private static final int    DATABASE_VERSION = 1;
	  
	private static final String SETTINGS_TABLE_NAME = "settings";
	private static final String SETTINGS_COL_ID     = "id";
	private static final String SETTINGS_COL_KEY    = "key";
	private static final String SETTINGS_COL_VALUE  = "value";
	
	private static final String KEY_VIBRATEONPROFILECHANGE      = "VibrateOnProfileChange"        ;
	private static final String KEY_DISPLAYTOASTONPROFILECHANGE = "DisplayToastOnProfileChange"   ;
	private static final String KEY_PLAYSOUNDONVOLUMECHANGE     = "PlaySoundOnVolumeChange"       ;
	private static final String KEY_CURRENTPROFILEID            = "CurrentProfileId"              ;
	private static final String KEY_VOLUEMLOCK                  = "VoluemLock"                    ;
	private static final String KEY_SOUNDALERTHACK              = "SoundAlertHack"                ;

	private boolean mVibrateOnProfileChange;
	private boolean mDisplayToastOnProfileChange;
	private boolean mPlaySoundOnVolumeChange;
	private int      mCurrentProfileId;
	private boolean mVoluemLock;
	private boolean mSoundAlertHack;

	private static class DBHelper extends SQLiteOpenHelper {

		private static class Insert {
			private SQLiteDatabase mDB ;
			private ContentValues  mValue;
			public Insert(SQLiteDatabase db) {
				mDB = db;
				mValue = new ContentValues();
			}
			private void insert(ContentValues values) {
				mDB.insert(SETTINGS_TABLE_NAME, null, values);
			}
			public void insert(String key, Boolean value) {
				mValue.clear();
				mValue.put(key, value);
				insert(mValue);
			}
			public void insert(String key, int value) {
				mValue.clear();
				mValue.put(key, value);
				insert(mValue);
			}
		}

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// set default value and create record
		private void initialize(SQLiteDatabase db) {
			Insert i = new Insert(db);
			i.insert(KEY_VIBRATEONPROFILECHANGE      , false);
			i.insert(KEY_DISPLAYTOASTONPROFILECHANGE , false);
			i.insert(KEY_PLAYSOUNDONVOLUMECHANGE     , false);
			i.insert(KEY_CURRENTPROFILEID            , -1);
			i.insert(KEY_VOLUEMLOCK                  , false);
			i.insert(KEY_SOUNDALERTHACK              , false);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(String.format(
					"CREATE TABLE %1$s ( %2$s INTEGER AUTOINCREMENT, %3$s TEXT PRIMARY KEY, %4$s TEXT);",
						          SETTINGS_TABLE_NAME, SETTINGS_COL_ID, SETTINGS_COL_KEY, SETTINGS_COL_VALUE));

			initialize(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			initialize(db);
		}
		
	}

	private Settings(Context context) {
		mDB = new DBHelper(context).getWritableDatabase();

		// load values
		Cursor cur = mDB.query(SETTINGS_TABLE_NAME, null, null, null, null, null, null);
		while (cur.moveToNext()) {
			String key   = cur.getString(cur.getColumnIndex(SETTINGS_COL_KEY));
			String value = cur.getString(cur.getColumnIndex(SETTINGS_COL_VALUE));

			if (key.equals(KEY_VIBRATEONPROFILECHANGE)) {
				mVibrateOnProfileChange = Boolean.parseBoolean(value);
			}
			else if (key.equals(KEY_DISPLAYTOASTONPROFILECHANGE)) {
				mDisplayToastOnProfileChange = Boolean.parseBoolean(value);
			}
			else if (key.equals(KEY_PLAYSOUNDONVOLUMECHANGE)) {
				mPlaySoundOnVolumeChange = Boolean.parseBoolean(value);
			}
			else if (key.equals(KEY_CURRENTPROFILEID)) {
				mCurrentProfileId = Integer.parseInt(value);
			}
			else if (key.equals(KEY_VOLUEMLOCK)) {
				mVoluemLock = Boolean.parseBoolean(value);
			}
			else if (key.equals(KEY_SOUNDALERTHACK)) {
				mSoundAlertHack = Boolean.parseBoolean(value);
			}
		}
	}

	public static synchronized Settings getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Settings(context);
		}
		return mInstance;
	}

	private void store(ContentValues values) {
		mDB.beginTransaction();
		try {
			mDB.replace(SETTINGS_TABLE_NAME, null, values);
		}
		finally {
			mDB.endTransaction();
		}
	}

	private void store(String key, Boolean value) {
		ContentValues values = new ContentValues();
		values.put(key, value);
		store(values);
	}
	private void store(String key, int value) {
		ContentValues values = new ContentValues();
		values.put(key, value);
		store(values);
	}

	public boolean isVibrateOnProfileChange() {
		return mVibrateOnProfileChange;
	}

	public void setVibrateOnProfileChange(boolean value) {
		mVibrateOnProfileChange = value;
		store(KEY_VIBRATEONPROFILECHANGE, value);
	}

	public boolean isDisplayToastOnProfileChange() {
		return mDisplayToastOnProfileChange;
	}

	public void setDisplayToastOnProfileChange(boolean value) {
		mDisplayToastOnProfileChange = value;
		store(KEY_DISPLAYTOASTONPROFILECHANGE, value);
	}

	public boolean isPlaySoundOnVolumeChange() {
		return mPlaySoundOnVolumeChange;
	}

	public void setPlaySoundOnVolumeChange(boolean value) {
		mPlaySoundOnVolumeChange = value;
		store(KEY_PLAYSOUNDONVOLUMECHANGE, value);
	}

	public int getCurrentProfileId() {
		return mCurrentProfileId;
	}

	public void setCurrentProfileId(int value) {
		mCurrentProfileId = value;
		store(KEY_CURRENTPROFILEID, value);
	}

	public boolean isVoluemLock() {
		return mVoluemLock;
	}

	public void setVoluemLock(boolean value) {
		mVoluemLock = value;
		store(KEY_VOLUEMLOCK, value);
	}

	public boolean isSoundAlertHack() {
		return mSoundAlertHack;
	}

	public void setSoundAlertHack(boolean value) {
		mSoundAlertHack = value;
		store(KEY_SOUNDALERTHACK, value);
	}
	
}
