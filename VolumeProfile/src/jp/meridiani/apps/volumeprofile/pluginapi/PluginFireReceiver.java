package jp.meridiani.apps.volumeprofile.pluginapi;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.R;
import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import jp.meridiani.apps.volumeprofile.profile.ProfileStore;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PluginFireReceiver extends BroadcastReceiver {

	public PluginFireReceiver() {
		Log.d(this.getClass().getName(), "PluginFireReceiver");
    }

    @Override
	public void onReceive(Context context, Intent intent) {
		Log.d(this.getClass().getName(), "onReceive");
		if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
        	return;
        }
        BundleUtil bundle;
		try {
			bundle = new BundleUtil(intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE));
		} catch (InvalidBundleException e) {
			return;
		}
        UUID profileId = bundle.getProfileId();

        VolumeProfile profile = ProfileStore.getInstance(context).loadProfile(profileId);
        if ( profile == null ) {
            String profileName = bundle.getProfileName();
       		Toast.makeText(context, context.getString(R.string.msg_profile_notfound, profileName), Toast.LENGTH_LONG).show();
       		return;
        }

        new AudioUtil(context).applyProfile(profile);
   		Toast.makeText(context, context.getString(R.string.msg_profile_applied, profile.getName()), Toast.LENGTH_LONG).show();
    }
}
