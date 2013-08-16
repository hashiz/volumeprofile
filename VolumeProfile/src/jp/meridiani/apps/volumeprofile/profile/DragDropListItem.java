package jp.meridiani.apps.volumeprofile.profile;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class DragDropListItem extends LinearLayout implements Checkable, OnTouchListener {
	
	private static final String NAMESPACE = "http://schemas.meridiani.jp/apk/res/meridiani";
	private static final String ATTR_CHECKABLEITEM = "checkableItem";
	private static final String ATTR_DRAGHANDLEITEM = "dragHandleItem";

	public interface DragDropListener {
		public enum DropAt {
			ABOVE,
			BLOW,
		}

		// @return item's position
		// @param dragItemView dragging item's View 
		public int getItemPosition(View dragItemView);

		// @param dragItemPosition dragging item's position
		// @param dropItemPosition dropped item's position
		// @param dropPos          drop on above/blow
		public void onItemDrop(int dragItemPosition, int dropItemPosition, DropAt dropPos);

		public void onDragLocation(int position, int y, int vCenter);

	}

	private int mAttrCheckableItemId = -1;
	private int mAttrDragHandleItemId = -1;
	private Checkable mCheckableChild = null;
	private View mDragHandleChild = null;
	private boolean mChecked = false;
	private DragDropListener mDragDropListener = null;

	public DragDropListItem(Context context) {
		super(context);
	}

	public DragDropListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAttrCheckableItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_CHECKABLEITEM, -1);
		mAttrDragHandleItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_DRAGHANDLEITEM, -1);
	}

	public DragDropListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mAttrCheckableItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_CHECKABLEITEM, -1);
		mAttrDragHandleItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_DRAGHANDLEITEM, -1);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mCheckableChild = (Checkable)findViewById(mAttrCheckableItemId);
		mDragHandleChild = findViewById(mAttrDragHandleItemId);
		if (mDragHandleChild != null) {
			mDragHandleChild.setOnTouchListener(this);
		}
	}

	public void setDragDropListener(DragDropListener listener) {
		mDragDropListener = listener;
	}

	// Checkable interface
	@Override
	public void setChecked(boolean checked) {
		if (mCheckableChild != null) {
			mCheckableChild.setChecked(checked);
		}
		mChecked = checked;
	}

	@Override
	public boolean isChecked() {
		if (mCheckableChild != null) {
			return mCheckableChild.isChecked();
		}
		return mChecked;
	}

	@Override
	public void toggle() {
		if (mCheckableChild != null) {
			mCheckableChild.toggle();
		}
		mChecked = ! mChecked;
	}

	// Drag and Drop
	@Override
	public boolean onDragEvent(DragEvent event) {
		int action = event.getAction();
		int vCenter = getHeight() / 2;
		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			return true;
		case DragEvent.ACTION_DRAG_ENTERED:
			return true;
		case DragEvent.ACTION_DRAG_LOCATION:
			if (event.getY() < vCenter) {
				// TODO: Highlight top borderline
				setBackgroundResource(android.R.drawable.divider_horizontal_dark);
			}
			else {
				// TODO: Highlight bottom borderline
				setBackgroundResource(android.R.drawable.divider_horizontal_dark);
			}
			invalidate();
			if (mDragDropListener != null) {
				int pos = mDragDropListener.getItemPosition(this);
				mDragDropListener.onDragLocation(pos, (int)event.getY(), vCenter);
			}
			return true;
		case DragEvent.ACTION_DRAG_EXITED:
			// Clear borderline highlight
			setBackgroundResource(0);
			invalidate();
			return true;
		case DragEvent.ACTION_DROP:
			setBackgroundResource(0);
			invalidate();
			if (mDragDropListener != null) {
				int dragItemPosition = ((Integer)event.getLocalState());
				int dropItemPosition = mDragDropListener.getItemPosition(this);
				DragDropListener.DropAt dropPos;
				if (event.getY() < vCenter) {
					// Insert Item above
					dropPos = DragDropListener.DropAt.ABOVE;
				}
				else {
					// Insert Item blow
					dropPos = DragDropListener.DropAt.BLOW;
				}
				mDragDropListener.onItemDrop(dragItemPosition, dropItemPosition, dropPos);
			}
			return true;
		}
		return false;
	}

	private class Shadow extends DragShadowBuilder {

		int mItemTouchX;
		int mItemTouchY;

		public Shadow(View itemView, View handleView, float touchX, float touchY) {
			super(itemView);

			int offsetX = handleView.getLeft();
			int offsetY = handleView.getTop();

			mItemTouchX = (int) (offsetX + touchX);
			mItemTouchY = (int) (offsetY + touchY);
		}

		@Override
		public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
			super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
			// adjust to handle view position
			shadowTouchPoint.x = mItemTouchX;
			shadowTouchPoint.y = mItemTouchY;
		}
	}

	// OnTouchListener interface
	@Override
	public boolean onTouch(View handleView, MotionEvent motionEvent) {
		if (mDragDropListener == null) {
			return false;
		}
		int action = motionEvent.getAction();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			DragShadowBuilder shadow = new Shadow(this, handleView, motionEvent.getX(), motionEvent.getY()) ;
			startDrag(null, shadow, mDragDropListener.getItemPosition(this), 0);
			return true;
		}
		return true;
	}

	
}
