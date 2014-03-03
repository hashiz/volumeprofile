package jp.meridiani.apps.volumeprofile.profile;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import android.content.Context;
import android.util.Log;

public class CurrentProfile {

	public static synchronized void setCurrentProfile(Context context, UUID profileId) {
		AudioUtil audio = new AudioUtil(context);
		ProfileStore store = ProfileStore.getInstance(context);
		VolumeProfile profile = store.loadProfile(profileId);

		Log.d("setCurrentProfile", "start" + profileId.toString() + profile.getName());
		store.setCurrentProfile(profile.getUuid());
		audio.applyProfile(profile);
		Log.d("setCurrentProfile", "end" + profileId.toString() + profile.getName());
	}
}
