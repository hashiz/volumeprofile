package jp.meridiani.apps.volumeprofile.ui;

import android.app.Activity;

import jp.meridiani.apps.volumeprofile.pluginapi.PluginEditActivity;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */
public class VolumeLockItem {
    private Activity mEditActivity;
    private VolumeLockValue mValue;

    public VolumeLockItem(Activity editActivity, VolumeLockValue value) {
        mEditActivity = editActivity;
        mValue = value;
    }

    public VolumeLockValue getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return mEditActivity.getString(mValue.getResource());
    }
}
