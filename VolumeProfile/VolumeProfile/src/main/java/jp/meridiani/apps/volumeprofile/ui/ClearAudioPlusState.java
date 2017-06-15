package jp.meridiani.apps.volumeprofile.ui;

import jp.meridiani.apps.volumeprofile.R;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */
public enum ClearAudioPlusState {
    ON,
    OFF,
    TOGGLE;

    public int getResource() {
        int id = -1;
        switch (this) {
            case ON:
                id = R.string.clearaudioplus_state_on;
                break;
            case OFF:
                id = R.string.clearaudioplus_state_off;
                break;
            case TOGGLE:
                id = R.string.clearaudioplus_state_toggle;
                break;
        }
        return id;
    }
}
