package jp.meridiani.apps.volumeprofile.prefs;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class BackupToGoogle extends BackupAgentHelper {
	private String mDefaultSharedPreferencesName =  getPackageName() + "_preferences";
	private static final String KEY_PREFS = "prefs";

	@Override
	public void onCreate() {
		addHelper(KEY_PREFS, new SharedPreferencesBackupHelper(this, mDefaultSharedPreferencesName));
	}
}
