package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.profile.DragDropListItem.DragDropListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class DraggableListView extends ListView implements DragDropListener {

	public DraggableListView(Context context) {
		super(context);
	}

	public DraggableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DraggableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public int getItemPosition(View dragItemView) {
		return getPositionForView(dragItemView);
	}

	@Override
	public void onItemDrop(int dragItemPosition, int dropItemPosition, DropAt pos) {
		ProfileListAdapter adapter = (ProfileListAdapter)getAdapter();
		if (pos == DropAt.BLOW) {
			dropItemPosition++;
		}
		adapter.moveItem(dragItemPosition, dropItemPosition);
	}
}
