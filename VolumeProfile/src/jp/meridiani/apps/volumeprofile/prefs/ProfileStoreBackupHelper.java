package jp.meridiani.apps.volumeprofile.prefs;

import java.io.IOException;

import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.os.Parcel;
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
		for (VolumeProfile profile : store.listProfiles()) {
			Parcel parcel = Parcel.obtain();
			profile.writeToParcel(parcel, 0);
			try {
				data.writeEntityHeader(profile.getUuid().toString(), parcel.dataSize());
				data.writeEntityData(parcel.marshall(), parcel.dataSize());
			} catch (IOException e) {
				e.printStackTrace();
			}
			parcel.recycle();
		}
	}

	@Override
	public void restoreEntity(BackupDataInputStream data) {
		data.
	}

	@Override
	public void writeNewStateDescription(ParcelFileDescriptor newState) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
