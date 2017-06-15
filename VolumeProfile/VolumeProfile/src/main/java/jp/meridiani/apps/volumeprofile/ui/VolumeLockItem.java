package jp.meridiani.apps.volumeprofile.ui;

import android.app.Activity;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */
public class VolumeLockItem {
    private Activity mEditActivity;
    private VolumeLockState mValue;

    public VolumeLockItem(Activity editActivity, VolumeLockState value) {
        mEditActivity = editActivity;
        mValue = value;
    }

    public VolumeLockState getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return mEditActivity.getString(mValue.getResource());
    }
}
