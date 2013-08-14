package jp.meridiani.apps.volumeprofile.profile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import jp.meridiani.apps.volumeprofile.profile.DragDropListItem.DragDropListener;

public class ProfileListAdapter extends ArrayAdapter<VolumeProfile> implements DragDropListAdapter {

	public ProfileListAdapter(Context context, int resource, int textViewId) {
		super(context, resource, textViewId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = super.getView(position, convertView, parent);
		if (itemView instanceof DragDropListItem) {
			if (parent instanceof DragDropListener) {
				((DragDropListItem)itemView).setDragDropListener((DragDropListener)parent);
			}
		}
		return itemView;
	};

	@Override
	public boolean moveItem(int srcPos, int dstPos) {
		if (srcPos == dstPos) {
			return false;
		}
		int max = getCount();
		if (srcPos < 0 || max < srcPos) {
			// index over
			return false;
		}
		if (dstPos < 0 || max + 1 < dstPos) {
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
		// TODO: write database here?
		// TODO: renumbering sort order
		return true;
	}
}