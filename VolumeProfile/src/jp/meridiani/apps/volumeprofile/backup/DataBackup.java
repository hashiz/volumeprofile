package jp.meridiani.apps.volumeprofile.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class DataBackup extends BackupAgentHelper {
	private static final String KEY_PREFS = "prefs";
	private static final String KEY_PROFS = "profiles";

	@Override
	public void onCreate() {
		String defaultSharedPreferencesName =  getPackageName() + "_preferences";
		addHelper(KEY_PREFS, new SharedPreferencesBackupHelper(this, defaultSharedPreferencesName));
		addHelper(KEY_PROFS, new ProfileStoreBackupHelper(this));
	}
}
