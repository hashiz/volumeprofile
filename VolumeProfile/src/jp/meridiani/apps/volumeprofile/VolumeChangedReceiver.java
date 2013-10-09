package jp.meridiani.apps.volumeprofile;

import android.content.BroadcastReceiver;

public abstract class VolumeChangedReceiver extends BroadcastReceiver {
	public static final String VOLUME_CHANGED_ACTION     = "android.media.VOLUME_CHANGED_ACTION";
	public static final String EXTRA_VOLUME_STREAM_TYPE  = "android.media.EXTRA_VOLUME_STREAM_TYPE";
	public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
	public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";

}
