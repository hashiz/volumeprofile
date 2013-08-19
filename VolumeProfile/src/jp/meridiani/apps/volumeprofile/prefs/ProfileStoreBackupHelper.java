package jp.meridiani.apps.volumeprofile.prefs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

import jp.meridiani.apps.volumeprofile.main.ProfileStore;
import jp.meridiani.apps.volumeprofile.main.VolumeProfile;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;

public class ProfileStoreBackupHelper implements BackupHelper {

	private Context mContext = null;

	public ProfileStoreBackupHelper(Context context) {
		mContext = context;
	}

	@Override
	public void performBackup(ParcelFileDescriptor oldState,
			BackupDataOutput data, ParcelFileDescriptor newState) {
		ProfileStore store = ProfileStore.getInstance(mContext);
		ByteBuffer buf = ByteBuffer.allocate(5*1024);
		for (VolumeProfile profile : store.listProfiles()) {
			buf.clear();
			profile.writeBytesToBuf(buf);
			try {
				data.writeEntityHeader("profile:" + profile.getUuid().toString(), buf.limit());
				data.writeEntityData(buf.array(), buf.limit());
			} catch (BufferOverflowException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void restoreEntity(BackupDataInputStream data) {
		try {
			String key = data.getKey();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeNewStateDescription(ParcelFileDescriptor newState) {
	}
}
