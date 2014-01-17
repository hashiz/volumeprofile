package jp.meridiani.apps.volumeprofile.profile;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.audio.AudioUtil;
import android.content.Context;

public class CurrentProfile {

	public static synchronized void setCurrentProfile(Context context, UUID profileId) {
		AudioUtil audio = new AudioUtil(context);
		ProfileStore store = ProfileStore.getInstance(context);
		VolumeProfile profile = store.loadProfile(profileId);

		audio.applyProfile(profile);
		store.setCurrentProfile(profile.getUuid());
	}
}
