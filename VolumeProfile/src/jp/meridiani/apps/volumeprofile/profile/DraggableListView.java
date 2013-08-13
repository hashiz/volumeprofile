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
	public int onItemDragStart(View dragItemView) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void onItemDrop(View dropItemView, int dragItemPosition, DropAt pos) {
		ProfileListAdapter adapter = (ProfileListAdapter)getAdapter();
		int dropItemPosition = getPositionForView(dropItemView);
		adapter.getItem(dragItemPosition);
	}
}
