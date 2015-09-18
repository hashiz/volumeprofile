package jp.meridiani.apps.volumeprofile.audio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class ClearAudioPlus {

	// for reflection
	private static final String ClearAudioPlus   = "com.sonymobile.audioeffect.ClearAudioPlus";
	private static final String isGlobalSetting  = "isGlobalSetting";
	private static final String getCurrentState = "getCurrentState";

	private static final String ACTION_CLEARAUDIO_PLUS_STATUS = "com.sonymobile.audioeffect.intent.action.CLEARAUDIO_PLUS_STATUS";
	private static final String ACTION_CLEARAUDIO_PLUS_REQUEST = "com.sonymobile.audioeffect.intent.action.CLEARAUDIO_PLUS_REQUEST";
    private static final String EXTRA_CLEARAUDIO_PLUS_STATUS = "com.sonymobile.audioeffect.intent.extra.CLEARAUDIO_PLUS_STATUS";

    // old param
	private static final String AUDIO_PARAM_SUPPORTED_EFFECT = "Sony_effect;supported_effect";
	private static final String AUDIO_EFFECT_CLEARAUDIO_PLUS = "ClearAudio+";
	private static final String AUDIO_PARAM_CLEARAUDIO_PLUS_STATE = "Sony_effect;ca_plus_state";

	// new param
	private static final String CLEAR_AUDIO_PLUS_PREFS = "SE-CLEAR_AUDIO_PLUS";
	private static final String KEY_CA_PLUS_PREF_STATE = "C_A_PLUS";

	private Context mContext = null;
	private AudioManager mAmgr = null;
	private Object mClearAudioPlus = null;
	private boolean mIsSupported = false;

	public ClearAudioPlus(Context context, AudioManager amgr) {
		mContext = context;
		mAmgr = amgr;
		mIsSupported = false;
	    if (classExists(ClearAudioPlus)) {
			Object mClearAudioPlus = newInstance(ClearAudioPlus);
			if (mClearAudioPlus!=null) {
				mIsSupported = getResultBoolean(mClearAudioPlus,isGlobalSetting);
			}
	    }
	    else {
			String param = mAmgr.getParameters(AUDIO_PARAM_SUPPORTED_EFFECT);
			if (param != null && !param.isEmpty()) {
				String[] keyval = param.split("=");
				if (keyval.length >= 2) {
					for (String effect : keyval[1].split(":")) {
						if (effect.equals(AUDIO_EFFECT_CLEARAUDIO_PLUS)) {
							mIsSupported = true;
						}
					}
				}
			}
			else {
				param = mAmgr.getParameters(CLEAR_AUDIO_PLUS_PREFS);
				if (param != null && !param.isEmpty()) {
					String[] keyval = param.split("=");
					if (keyval.length >= 2) {
						for (String effect : keyval[1].split(":")) {
							if (effect.equals(AUDIO_EFFECT_CLEARAUDIO_PLUS)) {
								mIsSupported = true;
							}
						}
					}
				}
			}
	    }
	}

	private boolean classExists(String className) {
		try {
	      Class.forName(className);
	      return true;
	    }
	    catch (ClassNotFoundException localClassNotFoundException) {}
	    return false;
	}

	private Object newInstance(String className) {
		Object instance = null;
	    try {
		      instance = Class.forName(className).getConstructor(new Class[] { Context.class }).newInstance(new Object[] { mContext });
	    }
	    catch (Exception e) {
	    	return null;
	    }
	    return instance;
	}

	private boolean getResultBoolean(Object instance, String method) {
		boolean result = false;
		try {
	        result = ((Boolean)instance.getClass().getMethod(method, new Class[0]).invoke(instance, new Object[0])).booleanValue();
		}
	    catch (Exception e) {
	    	return false;
	    }
	    return result;
	}

	private boolean useOldMethod() {
		if (mClearAudioPlus != null) {
			return false;
		}
		String param = mAmgr.getParameters(AUDIO_PARAM_SUPPORTED_EFFECT);
		if (param != null) {
			return true;
		}
		return false;
	}

	public boolean isSupported() {
		return mIsSupported;
	}

	public boolean getState() {
	    if (!isSupported()) {
	    	return false;
	    }
	    if (useOldMethod()) {
			String param = mAmgr.getParameters(AUDIO_PARAM_CLEARAUDIO_PLUS_STATE);
			if (param == null) {
				return false;
			}
			String[] keyval = param.split("=");
			if (keyval.length < 2) {
				return false;
			}
			boolean enabled = false;
			try {
				enabled = Integer.parseInt(keyval[1]) == 1 ? true : false;
			}
			catch (NumberFormatException e) {
				enabled = false;
			}
			return enabled;
	    }
	    else {
	    	return getResultBoolean(mClearAudioPlus, getCurrentState);
	    }
	}

	public void setState(boolean enable) {
	    if (!isSupported()) {
	    	return;
	    }

	    int state = enable ? 1 : 0;
		Intent intent = null;
		if (useOldMethod()) {
			intent = new Intent(ACTION_CLEARAUDIO_PLUS_STATUS);
		}
		else {
			intent = new Intent(ACTION_CLEARAUDIO_PLUS_REQUEST);
		}
		intent.putExtra(EXTRA_CLEARAUDIO_PLUS_STATUS, state);
		mContext.sendBroadcast(intent);
	}
}
