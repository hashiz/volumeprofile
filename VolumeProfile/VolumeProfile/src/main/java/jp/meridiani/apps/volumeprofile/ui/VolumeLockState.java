package jp.meridiani.apps.volumeprofile.ui;

import jp.meridiani.apps.volumeprofile.R;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */

public enum VolumeLockState {
    LOCK,
    UNLOCK,
    TOGGLE;

    public int getResource() {
        int id = -1;
        switch (this) {
        case LOCK:
            id = R.string.volumelock_lock;
            break;
        case UNLOCK:
            id = R.string.volumelock_unlock;
            break;
        case TOGGLE:
            id = R.string.volumelock_toggle;
            break;
        }
        return id;
    }
}
