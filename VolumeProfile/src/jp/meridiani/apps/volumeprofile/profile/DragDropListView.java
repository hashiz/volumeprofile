package jp.meridiani.apps.volumeprofile.profile;

import java.util.List;

import jp.meridiani.apps.volumeprofile.profile.DragDropListItem.DragDropListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class DragDropListView extends ListView implements DragDropListener {

	private OnSortedListener mOnSortedListener = null;

	public interface OnSortedListener {
		public void onSorted(List<VolumeProfile> list);
	}

	public DragDropListView(Context context) {
		super(context);
	}

	public DragDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DragDropListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnSortedListener(OnSortedListener listener) {
		mOnSortedListener = listener;
	}

	@Override
	public int getItemPosition(View dragItemView) {
		return getPositionForView(dragItemView);
	}

	@Override
	public void setSelection(int position) {
	};

	@Override
	public void onItemDrop(int dragItemPosition, int dropItemPosition, DropAt pos) {
		ProfileListAdapter adapter = (ProfileListAdapter)getAdapter();
		if (pos == DropAt.BLOW) {
			dropItemPosition++;
		}
		if (adapter.moveItem(dragItemPosition, dropItemPosition)) {
			int selPos = getCheckedItemPosition();
			if (selPos == dragItemPosition) {
				setItemChecked(dropItemPosition, true);
			}
			else {
				int posMin = Math.min(dragItemPosition, dropItemPosition);
				int posMax = Math.max(dragItemPosition, dropItemPosition);
				if (posMin <= selPos && selPos <= posMax) {
					if (dragItemPosition < dropItemPosition) {
						// move up
						setItemChecked(selPos - 1, true);
					}
					else {
						// move down
						setItemChecked(selPos + 1, true);
					}
				}
			}
			if (mOnSortedListener != null) {
				mOnSortedListener.onSorted(adapter.getProfileList());
			}
		}
	}
}
