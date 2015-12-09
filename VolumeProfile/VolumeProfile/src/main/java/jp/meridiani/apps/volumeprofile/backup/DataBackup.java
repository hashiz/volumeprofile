package jp.meridiani.apps.volumeprofile.backup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import jp.meridiani.apps.volumeprofile.prefs.Prefs;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

public class DataBackup extends BackupAgent {
	private static final String KEY_PREFERENCES = "preferences";
	private static final String KEY_PROFILES    = "profiles";

	
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {

		// backup prefs
		byte[] prefsData = getPrefsData();
		if (prefsData != null) {
			try {
				data.writeEntityHeader(KEY_PREFERENCES, prefsData.length);
				data.writeEntityData(prefsData, prefsData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// backup profiles
		byte[] profilesData = getProfilesData();
		if (profilesData != null) {
			try {
				data.writeEntityHeader(KEY_PROFILES, profilesData.length);
				data.writeEntityData(profilesData, profilesData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] getPrefsData() {
		Prefs prefs = Prefs.getInstance(getApplicationContext());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(buf));
		try {
			prefs.writeToText(wtr);
			wtr.flush();
			return buf.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] getProfilesData() {
		ProfileStore store = ProfileStore.getInstance(getApplicationContext());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(buf));
		try {
			store.writeToText(wtr);
			wtr.flush();
			return buf.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
		byte [] buffer = null;

		while (data.readNextHeader()) {
	         String key = data.getKey();
	         int dataSize = data.getDataSize();

	         if (KEY_PREFERENCES.equals(key)) {
	             // process this kind of record here
	        	 if (buffer == null || buffer.length < dataSize) {
		             buffer = new byte[dataSize];
	        	 }
	             data.readEntityData(buffer, 0, dataSize); // reads the entire entity at once
		         ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	             BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
		         Prefs prefs = Prefs.getInstance(getApplicationContext());
		         prefs.setFromText(rdr);
	         } else if (KEY_PROFILES.equals(key)) {
	        	 if (buffer == null || buffer.length < dataSize) {
		             buffer = new byte[dataSize];
	        	 }
	             data.readEntityData(buffer, 0, dataSize); // reads the entire entity at once
		         ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	             BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
	             ProfileStore store = ProfileStore.getInstance(getApplicationContext());
            	 store.readFromText(rdr);
	         }
	         else {
	             data.skipEntityData();
	         }
	    }
 	}
}
