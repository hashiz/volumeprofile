package jp.meridiani.apps.volumeprofile.ui;

import android.app.Activity;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */
public class ClearAudioPlusStateItem {
    private Activity mEditActivity;
    private ClearAudioPlusState mValue;

    public ClearAudioPlusStateItem(Activity editActivity, ClearAudioPlusState value) {
        mEditActivity = editActivity;
        mValue = value;
    }

    public ClearAudioPlusState getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return mEditActivity.getString(mValue.getResource());
    }
}
