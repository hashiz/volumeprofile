package jp.meridiani.apps.volumeprofile.profile;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ProfileListAdapter extends ArrayAdapter<VolumeProfile> implements DragDropListAdapter {

	public ProfileListAdapter(Context context, int resource, int textViewId) {
		super(context, resource, textViewId);
	}

	@Override
	public boolean moveItem(int srcPos, int dstPos) {
		if (srcPos == dstPos) {
			return false;
		}
		int max = getCount();
		if (0 < srcPos || srcPos < max) {
			// index over
			return false;
		}
		if (0 < dstPos || dstPos < max + 1) {
			// index over
			return false;
		}

		
		if (srcPos < dstPos) {
			// up to down
			// insert first
			VolumeProfile tmpProfile = getItem(srcPos);
			if (dstPos < max) {
				insert(tmpProfile, dstPos);
			}
			else {
				add(tmpProfile);
			}
			remove(tmpProfile);
		}
		else {
			// down to up
			// remove first
			VolumeProfile tmpProfile = getItem(srcPos);
			if (dstPos < max) {
				insert(tmpProfile, dstPos);
			}
			else {
				add(tmpProfile);
			}
			remove(tmpProfile);
		}
		return true;
	}
}