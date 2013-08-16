package jp.meridiani.apps.volumeprofile.prefs;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

public class BackupToGoogle extends BackupAgent {

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {

		Prefs prefs = Prefs.getInstance(getApplicationContext());

		prefs.
		data.writeEntityHeader(key, dataSize);
	}

	@Override
	public void onRestore(BackupDataInput arg0, int arg1,
			ParcelFileDescriptor arg2) throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

}
