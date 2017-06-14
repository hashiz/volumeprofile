package jp.meridiani.apps.volumeprofile.ui;

import android.app.Activity;

import jp.meridiani.apps.volumeprofile.pluginapi.PluginEditActivity;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */
public class ClearAudioPlusStateItem {
    private Activity mEditActivity;
    private ClearAudioPlusStateValue mValue;

    public ClearAudioPlusStateItem(Activity editActivity, ClearAudioPlusStateValue value) {
        mEditActivity = editActivity;
        mValue = value;
    }

    public ClearAudioPlusStateValue getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return mEditActivity.getString(mValue.getResource());
    }
}
