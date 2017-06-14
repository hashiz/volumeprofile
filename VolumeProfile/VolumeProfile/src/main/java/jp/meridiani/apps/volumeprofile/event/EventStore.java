package jp.meridiani.apps.volumeprofile.event;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventStore {

	private static EventStore mInstance = null;

	private SQLiteDatabase mDB;
	private Context mContext;

	private static final String DATABASE_NAME = "eventlist.db";
	private static final int    DATABASE_VERSION = 1;

	private static final String LIST_TABLE_NAME = "eventlist";
	private static final String COL_UUID        = "uuid";
	private static final String COL_ORDER       = "order";

	private static final String DATA_TABLE_NAME = "eventdata";
	private static final String COL_KEY          = "key";
	private static final String COL_VALUE        = "value";

	private static final String EVENTS_START = "<events>";
	private static final String EVENTS_END   = "</events>";

	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(String.format(
					"CREATE TABLE %1$s ( _id INTEGER PRIMARY KEY AUTOINCREMENT, %2$s TEXT NOT NULL UNIQUE, %3$s INTEGER);",
						LIST_TABLE_NAME, COL_UUID, COL_ORDER));

			db.execSQL(String.format(
					"CREATE TABLE %1$s ( _id INTEGER PRIMARY KEY AUTOINCREMENT, %2$s TEXT NOT NULL, %3$s TEXT NOT NULL, %4$s TEXT NOT NULL);",
						DATA_TABLE_NAME, COL_UUID, COL_KEY, COL_VALUE));
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			return;
		}

	}

	private EventStore(Context context) {
		mDB = new DBHelper(context).getWritableDatabase();
		mContext = context;
	}

	public static synchronized EventStore getInstance(Context context) {
		if ( mInstance == null ) {
			mInstance = new EventStore(context);
		}
		return mInstance;
	}

	public ArrayList<Event> listEvents() {
		ArrayList<Event> list = new ArrayList<Event>();

		Cursor listCur = mDB.query(LIST_TABLE_NAME, null, null, null, null, null, COL_ORDER);
		try {
			while (listCur.moveToNext()) {
				UUID uuid = UUID.fromString(listCur.getString(listCur.getColumnIndex(COL_UUID)));
				int order = listCur.getInt(listCur.getColumnIndex(COL_ORDER));
				Event event = new Event(uuid);
				event.setOrder(order);
				loadEventData(event);
				list.add(event);
			}
		}
		finally {
			listCur.close();
		}
		return list;
	}

	public void updateOrder(List<Event> list) {

		mDB.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			for (Event event : list) {
				// update/insert list
				values.clear();
				values.put(COL_ORDER, event.getOrder());
				mDB.update(LIST_TABLE_NAME, values,
						String.format("%1$s=?", COL_UUID),
						new String[]{event.getId().toString()});
			}
			mDB.setTransactionSuccessful();
			requestBackup();
		}
		finally {
			mDB.endTransaction();
		}
	}

	private void loadEventData(Event event) {
		String uuid = event.getId().toString();
		Cursor dataCur = mDB.query(DATA_TABLE_NAME, null, COL_UUID + "=?", new String[]{uuid}, null, null, null);
		try {
			while (dataCur.moveToNext()) {
				String key = dataCur.getString(dataCur.getColumnIndex(COL_KEY));
				String value = dataCur.getString(dataCur.getColumnIndex(COL_VALUE));
				event.setValue(key, value);
			}
		}
		finally {
			dataCur.close();
		}
	}

	private Event loadEventInternal(UUID eventId) throws EventNotFoundException {
		Event event = null;
		Cursor listCur = mDB.query(LIST_TABLE_NAME, null, COL_UUID + "=?", new String[] {eventId.toString()}, null, null, null);
		try {
			if (listCur.moveToFirst()) {
				int order = listCur.getInt(listCur.getColumnIndex(COL_ORDER));
				event = new Event(eventId);
				event.setOrder(order);
			}
			else {
				throw new EventNotFoundException();
			}
		}
		finally {
			listCur.close();
		}

		loadEventData(event);
		return event;
	}

	public Event loadEvent(UUID eventId) throws EventNotFoundException {
		return loadEventInternal(eventId);
	}

	public void storeEvent(Event event) {
		mDB.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			// update/insert list
			{
				values.clear();
				values.put(COL_ORDER, event.getOrder());
				int rows = mDB.update(LIST_TABLE_NAME, values,
						String.format("%1$s=?", COL_UUID),
						new String[]{event.getId().toString()});
				if (rows < 1) {
					values.put(COL_UUID, event.getId().toString());
					mDB.insert(LIST_TABLE_NAME, null, values);
				}
			}
			// update/insert data
			for (Event.Key key : Event.listDataKeys()) {
				values.clear();
				values.put(COL_VALUE, event.getValue(key));
				int rows = mDB.update(DATA_TABLE_NAME, values,
						String.format("%1$s=? and %2$s=?", COL_UUID, COL_KEY),
						new String[]{event.getId().toString(), key.name()});
				if (rows < 1) {
					values.put(COL_UUID, event.getId().toString());
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

	public void deleteEvent(UUID eventId) {
		mDB.beginTransaction();

		try {
			// delete existent profile

			// delete data
			mDB.delete(DATA_TABLE_NAME, COL_UUID+"=?", new String[]{eventId.toString()});

			// delete list
			mDB.delete(LIST_TABLE_NAME, COL_UUID+"=?", new String[]{eventId.toString()});

			mDB.setTransactionSuccessful();
			requestBackup();
		}
		finally {
			mDB.endTransaction();
		}
	}

	private int getMaxOrder() {
		Cursor listCur = mDB.rawQuery(String.format(
				"select max(%2$s) from %1$s;", LIST_TABLE_NAME, COL_ORDER),null);
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

	public Event newEvent() {
		Event event = new Event();
		event.setOrder(getMaxOrder()+1);
		return event;
	}

	public void writeToText(BufferedWriter wtr) throws IOException {
		wtr.write(EVENTS_START); wtr.newLine();
		for (Event event : listEvents()) {
			event.writeToText(wtr);
		}
		wtr.write(EVENTS_END); wtr.newLine();
	}

	public void readFromText(BufferedReader rdr) throws IOException {
    	String line;
		while ((line = rdr.readLine()) != null) {
			if (EVENTS_START.equals(line)) {
	            Event event;
	            while ((event = Event.createFromText(rdr)) != null) {
	            	storeEvent(event);
	            }
			}
		}
	}

	private void requestBackup() {
		BackupManager.dataChanged(mContext.getPackageName());
	}
}
