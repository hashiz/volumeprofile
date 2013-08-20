package jp.meridiani.apps.volumeprofile.backup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;

public class ProfileStoreBackupHelper implements BackupHelper {

	private Context mContext = null;
	private static final String ENTITYKEY = "profilelist";

	public ProfileStoreBackupHelper(Context context) {
		mContext = context;
	}

	@Override
	public void performBackup(ParcelFileDescriptor oldState,
			BackupDataOutput data, ParcelFileDescriptor newState) {
		ProfileStore store = ProfileStore.getInstance(mContext);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(buf));
		try {
			wtr.write("<profilelist>");
			wtr.newLine();
			for (VolumeProfile profile : store.listProfiles()) {
				profile.writeToText(wtr);
			}
			wtr.write("</profilelist>");
			wtr.newLine();
			wtr.flush();
			byte [] bytes = buf.toByteArray();
			data.writeEntityHeader(ENTITYKEY, bytes.length);
			data.writeEntityData(buf.toByteArray(), bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void restoreEntity(BackupDataInputStream data) {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(data));
		try {
			String key = data.getKey();
			if (ENTITYKEY.equals(key)) {
				VolumeProfile profile = VolumeProfile.createFromText(rdr);
				if (profile != null) {
					ProfileStore.getInstance(mContext).storeProfile(profile);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeNewStateDescription(ParcelFileDescriptor newState) {
	}
}
