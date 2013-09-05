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
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

public class DataBackup extends BackupAgent {
	private static final String KEY_PREFS = "prefs";
	private static final String KEY_PROFS = "profiles";
	private static final String PREFS_START = "<preferences>";
	private static final String PREFS_END   = "</preferences>";
	private static final String PROFS_START = "<profilelist>";
	private static final String PROFS_END   = "</profilelist>";

	
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {

		// backup prefs
		byte[] prefsData = getPrefsData();
		if (prefsData != null) {
			try {
				data.writeEntityHeader(KEY_PROFS, prefsData.length);
				data.writeEntityData(prefsData, prefsData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// backup profiles
		byte[] profilesData = getProfilesData();
		if (profilesData != null) {
			try {
				data.writeEntityHeader(KEY_PROFS, profilesData.length);
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
			
			wtr.write(PREFS_START);
			wtr.newLine();
			prefs.writeToText(wtr);
			wtr.write(PREFS_END);
			wtr.newLine();
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
			for (VolumeProfile profile : store.listProfiles()) {
				wtr.write(PROFS_START);
				wtr.newLine();
				profile.writeToText(wtr);
				wtr.write(PROFS_END);
				wtr.newLine();
			}
			wtr.flush();
			return buf.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
	     while (data.readNextHeader()) {
	         String key = data.getKey();
	         int dataSize = data.getDataSize();

	         if (key.equals(KEY_PREFS)) {
	             // process this kind of record here
	             byte[] buffer = new byte[dataSize];
	             data.readEntityData(buffer, 0, dataSize); // reads the entire entity at once
		         ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	             BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
		         Prefs prefs = Prefs.getInstance(getApplicationContext());
		         prefs.setFromText(rdr, PREFS_START, PREFS_END);
	         } else if (key.equals(KEY_PROFS)) {
	             byte[] buffer = new byte[dataSize];
	             data.readEntityData(buffer, 0, dataSize); // reads the entire entity at once
		         ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	             BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
	             VolumeProfile profile;
	             while ((profile = VolumeProfile.createFromText(rdr, PROFS_START, PROFS_END)) != null) {
	            	 ProfileStore.getInstance(getApplicationContext()).storeProfile(profile);
	             }
	         }
	         else {
	             data.skipEntityData();
	         }
	    }
 	}
}
