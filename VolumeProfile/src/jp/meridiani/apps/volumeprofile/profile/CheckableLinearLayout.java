package jp.meridiani.apps.volumeprofile.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
	
	private static final String NAMESPACE = "http://schemas.meridiani.jp/apk/res/meridiani";
	private static final String ATTR_CHECKABLEITEM = "checkableItem";

	private int mAttrCheckableItemId = -1;
	private Checkable mCheckableChild = null;
	private boolean mChecked = false;

	public CheckableLinearLayout(Context context) {
		super(context);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAttrCheckableItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_CHECKABLEITEM, 0);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mAttrCheckableItemId = attrs.getAttributeResourceValue(NAMESPACE, ATTR_CHECKABLEITEM, 0);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mCheckableChild = findCheckableChild();
	}

	private Checkable findCheckableChild() {
		View child = null;
		if (mAttrCheckableItemId < 0) {
			for ( int i = 0; i < getChildCount(); i++) {
				child = getChildAt(i);
				if (child instanceof Checkable) {
					break;
				}
			}
		}
		else {
			child = findViewById(mAttrCheckableItemId);
		}

		if ( child != null && child instanceof Checkable) {
			return (Checkable)child;
		}
		return null;
	}

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

	
}
