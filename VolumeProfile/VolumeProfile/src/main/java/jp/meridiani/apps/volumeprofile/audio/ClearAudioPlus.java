package jp.meridiani.apps.volumeprofile.audio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;

public class ClearAudioPlus {

	// for Action Intent
	private static final String ACTION_CLEARAUDIO_PLUS_STATUS = "com.sonymobile.audioeffect.intent.action.CLEARAUDIO_PLUS_STATUS";
	private static final String ACTION_CLEARAUDIO_PLUS_REQUEST = "com.sonymobile.audioeffect.intent.action.CLEARAUDIO_PLUS_REQUEST";
    private static final String EXTRA_CLEARAUDIO_PLUS_STATUS = "com.sonymobile.audioeffect.intent.extra.CLEARAUDIO_PLUS_STATUS";

    // for reflection
    private static final String CLASS_CLEARAUDIOPLUS = "com.sonymobile.audioeffect.ClearAudioPlus";
    private static final String METHOD_ISGLOBALSETTING = "isGlobalSetting";
    private static final String METHOD_GETCURRENTSTATE = "getCurrentState";

    // old param
	private static final String AUDIO_PARAM_SUPPORTED_EFFECT = "Sony_effect;supported_effect";
	private static final String AUDIO_EFFECT_CLEARAUDIO_PLUS = "ClearAudio+";
	private static final String AUDIO_PARAM_CLEARAUDIO_PLUS_STATE = "Sony_effect;ca_plus_state";

	private Context mContext = null;
	private AudioManager mAmgr = null;
	private boolean mIsSupported = false;
	private boolean mIsNewMethod = false;

	private boolean methodInvoke(String className, String methodName) {
		try {
			Class<?> c = Class.forName(className);
			Object instance = c.getConstructor(new Class[] {Context.class}).newInstance(new Object[] {mContext});
			return ((Boolean)c.getMethod(methodName,new Class [0]).invoke(instance, new Object [0])).booleanValue();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void checkSupported() {
		mIsSupported = false;
		mIsNewMethod = false;
		if (methodInvoke(CLASS_CLEARAUDIOPLUS, METHOD_ISGLOBALSETTING)) {
			mIsSupported = true;
			mIsNewMethod = true;
			return;
		}

		String param = mAmgr.getParameters(AUDIO_PARAM_SUPPORTED_EFFECT);
		if (param != null && !param.isEmpty()) {
			String[] keyval = param.split("=");
			if (keyval.length >= 2) {
				for (String effect : keyval[1].split(":")) {
					if (effect.equals(AUDIO_EFFECT_CLEARAUDIO_PLUS)) {
						mIsSupported = true;
			    		mIsNewMethod = false;
			    		return;
					}
				}
			}
	    }
	}
	public ClearAudioPlus(Context context, AudioManager amgr) {
		mContext = context;
		mAmgr = amgr;
		checkSupported();
	}

	private boolean useNewMethod() {
		return mIsNewMethod;
	}

	public boolean isSupported() {
		return mIsSupported;
	}

	public boolean getState() {
	    if (!isSupported()) {
	    	return false;
	    }
	    if (useNewMethod()) {
			return methodInvoke(CLASS_CLEARAUDIOPLUS, METHOD_GETCURRENTSTATE);
	    }
	    else {
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
	}

	public void setState(boolean enable) {
	    if (!isSupported()) {
	    	return;
	    }

	    int state = enable ? 1 : 0;
		Intent intent = null;
		if (useNewMethod()) {
			intent = new Intent(ACTION_CLEARAUDIO_PLUS_REQUEST);
		}
		else {
			intent = new Intent(ACTION_CLEARAUDIO_PLUS_STATUS);
		}
		intent.putExtra(EXTRA_CLEARAUDIO_PLUS_STATUS, state);
		mContext.sendBroadcast(intent);
	}
}
