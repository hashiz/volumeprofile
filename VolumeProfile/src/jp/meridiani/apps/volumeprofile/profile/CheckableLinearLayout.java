package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.profile.CheckableLinearLayout.OnItemDropListener.DropPos;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable, OnTouchListener {
	
	private static final String NAMESPACE = "http://schemas.meridiani.jp/apk/res/meridiani";
	private static final String ATTR_CHECKABLEITEM = "checkableItem";
	private static final String ATTR_DRAGHANDLEITEM = "dragHandleItem";

	public interface OnItemDropListener {
		public enum DropPos {
			ABOVE,
			BLOW,
		}
		public void onItemDrop(View view, DropPos pos);
	}

	private int mAttrCheckableItemId = -1;
	private int mAttrDragHandleItemId = -1;
	private Checkable mCheckableChild = null;
	private View mDragHandleChild = null;
	private boolean mChecked = false;
	private OnItemDropListener mOnItemDropListener = null;

	public CheckableLinearLayout(Context context) {
		super(context);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAttrCheckableItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_CHECKABLEITEM, -1);
		mAttrDragHandleItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_DRAGHANDLEITEM, -1);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
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

	public void setOnItemDropListener(OnItemDropListener listener) {
		mOnItemDropListener = listener;
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
			shadowTouchPoint.x = mItemTouchX;
			shadowTouchPoint.y = mItemTouchY;
		}
	}

	@Override
	public boolean onDragEvent(DragEvent event) {
		int action = event.getAction();
		int vCenter = getHeight() / 2;
		switch (action) {
		case DragEvent.ACTION_DRAG_ENTERED:
			return true;
		case DragEvent.ACTION_DRAG_LOCATION:
			if (event.getY() < vCenter) {
				// TODO: Highlight top borderline
			}
			else {
				// TODO: Highlight bottom borderline
			}
			return true;
		case DragEvent.ACTION_DRAG_EXITED:
			// TODO: Clear borderline highlight
			return true;
		case DragEvent.ACTION_DROP:
			if (mOnItemDropListener == null) {
				return false;
			}
			if (event.getY() < vCenter) {
				// Insert Item above
				mOnItemDropListener.onItemDrop(this, DropPos.ABOVE);
			}
			else {
				// Insert Item blow
				mOnItemDropListener.onItemDrop(this, DropPos.BLOW);
			}
			return true;
		}
		return false;
	}

	// OnTouchListener interface
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		int action = motionEvent.getAction();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			motionEvent.getX();
			motionEvent.getY();
			ClipData data = ClipData.newPlainText("profile", "profile");
			DragShadowBuilder shadow = new Shadow(this, view, motionEvent.getX(), motionEvent.getY()) ;
			startDrag(data, shadow, null, 0);
			return true;
		}
		return true;
	}

	
}
