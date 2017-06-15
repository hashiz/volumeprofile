package jp.meridiani.apps.volumeprofile.event;

import android.content.Context;
import android.os.Parcel;

import java.util.UUID;

import jp.meridiani.apps.volumeprofile.DataHolder;
import jp.meridiani.apps.volumeprofile.profile.VolumeProfile;
import jp.meridiani.apps.volumeprofile.ui.ClearAudioPlusState;
import jp.meridiani.apps.volumeprofile.ui.VolumeLockState;

/**
 * Created by kjcs_hashi on 2017/06/14.
 */

public class EventAction extends DataHolder {
    private static final String TAG_START = "<event_action>";
    private static final String TAG_END   = "</event_action>";
    private static final String CHANGEVOLUMEPROFILE = "CHANGEVOLUMEPROFILE";
    private static final String VOLUMEPROFILEID = "VOLUMEPROFILEID";
    private static final String CHANGEVOLUMELOCKSTATE = "CHANGEVOLUMELOCKSTATE";
    private static final String VOLUMELOCKSTATE = "VOLUMELOCKSTATE";
    private static final String CHANGECLEARAUDIOPLUSSTATE = "CHANGECLEARAUDIOPLUSSTATE";
    private static final String CLEARAUDIOPLUSSTATE = "CLEARAUDIOPLUSSTATE";
    private static final String[] KEYS = {
            CHANGEVOLUMEPROFILE,
            CHANGEVOLUMELOCKSTATE,
            VOLUMEPROFILEID,
            VOLUMELOCKSTATE,
            CHANGECLEARAUDIOPLUSSTATE,
            CLEARAUDIOPLUSSTATE
    };

    private Context mContext;

    private boolean mChangeVolumeProfile = false;
    private UUID mVolumeProfileId = null;
    private boolean mChangeVolumeLockState = false;
    private VolumeLockState mVolumeLockState = VolumeLockState.LOCK;
    private boolean mChangeClearAudioPlusState = false;
    private ClearAudioPlusState mClearAudioPlusState = ClearAudioPlusState.ON;

    EventAction() {
    }

    public EventAction(Parcel in) {
        super(in);
    }

    public boolean getChangeVolumeProfile() {
        return mChangeVolumeProfile;
    }
    public void setChangeVolumeProfile(boolean value) {
        mChangeVolumeProfile = value;
    }

    public boolean getChangeVolumeLockState() {
        return mChangeVolumeLockState;
    }
    public void setChangeVolumeLockState(boolean value) {
        mChangeVolumeLockState = value;
    }

    public boolean getChangeClearAudioPlusState() {
        return mChangeClearAudioPlusState;
    }
    public void setChangeClearAudioPlusState(boolean value) {
        mChangeClearAudioPlusState = value;
    }

    public UUID getVolumeProfileId() {
        return mVolumeProfileId;
    }
    public void setVolumeProfileId(UUID value) {
        mVolumeProfileId = value;
    }

    public VolumeLockState getVolumeLockState() {
        return mVolumeLockState;
    }
    public void setVolumeLockState(VolumeLockState value) {
        mVolumeLockState = value;
    }

    public ClearAudioPlusState getClearAudioPlusState() {
        return mClearAudioPlusState;
    }
    public void setClearAudioPlusState(ClearAudioPlusState value) {
        mClearAudioPlusState = value;
    }

    protected String TAG_START() {
        return TAG_START;
    }

    protected String TAG_END() {
        return TAG_END;
    }

    protected String[] getKeys() {
        return KEYS;
    }

    protected void setValue(String key, String value) {
        switch (key) {
            case CHANGEVOLUMEPROFILE:
                mChangeVolumeProfile = Boolean.parseBoolean(value);
                break;
            case VOLUMEPROFILEID:
                mVolumeProfileId = UUID.fromString(value);
                break;
            case CHANGEVOLUMELOCKSTATE:
                mChangeVolumeLockState = Boolean.parseBoolean(value);
                break;
            case VOLUMELOCKSTATE:
                mVolumeLockState = VolumeLockState.valueOf(value);
                break;
            case CHANGECLEARAUDIOPLUSSTATE:
                mChangeClearAudioPlusState = Boolean.parseBoolean(value);
                break;
            case CLEARAUDIOPLUSSTATE:
                mClearAudioPlusState = ClearAudioPlusState.valueOf(value);
                break;
        }
    }

    protected String getValue(String key) {
        switch (key) {
            case CHANGEVOLUMEPROFILE:
                return Boolean.toString(mChangeVolumeProfile);
            case VOLUMEPROFILEID:
                return mVolumeProfileId.toString();
            case CHANGEVOLUMELOCKSTATE:
                return Boolean.toString(mChangeVolumeLockState);
            case VOLUMELOCKSTATE:
                return mVolumeLockState.toString();
            case CHANGECLEARAUDIOPLUSSTATE:
                return Boolean.toString(mChangeClearAudioPlusState);
            case CLEARAUDIOPLUSSTATE:
                return mClearAudioPlusState.toString();
        }
        return null;
    }

    public static final Creator<EventAction> CREATOR = new Creator<EventAction>() {
        public EventAction createFromParcel(Parcel in) {
            return new EventAction(in);
        }

        public EventAction[] newArray(int size) {
            return new EventAction[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }
}
