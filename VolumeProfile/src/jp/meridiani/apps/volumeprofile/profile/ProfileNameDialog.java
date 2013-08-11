package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileNameDialog extends DialogFragment {
	private static final String BUNDLE_PROFILE            = "profile";
	private static final String BUNDLE_CALLBACKTYPE       = "callbackType";
	private static final String BUNDLE_DESCRIPTION        = "description";
	private static final String BUNDLE_POSITIVEBUTTONTEXT = "positiveButtonText";
	private static final String BUNDLE_NEGATIVEBUTTONTEXT = "negativeButtonText";

	private static enum CallbackType {
		NONE,
		FRAGMENT,
		ACTIVITY,
	}

	private VolumeProfile mProfile;
	private CallbackType  mCallbackType;
	private String        mDescription;
	private String        mPositiveButtonText;
	private String        mNegativeButtonText;

	public static ProfileNameDialog newInstance(VolumeProfile profile, ProfileEditCallback callback) {
		return newInstance(profile, callback, null, null, null);
	}

	public static ProfileNameDialog newInstance(VolumeProfile profile, ProfileEditCallback callback, String description, String positiveButtonText, String negativeButtonText) {
		ProfileNameDialog instance = new ProfileNameDialog();
		Bundle args = new Bundle();

		args.putParcelable(BUNDLE_PROFILE, profile);
		args.putString(BUNDLE_DESCRIPTION, description);
		args.putString(BUNDLE_POSITIVEBUTTONTEXT, positiveButtonText);
		args.putString(BUNDLE_NEGATIVEBUTTONTEXT, negativeButtonText);
		if (callback instanceof Fragment) {
			args.putString(BUNDLE_CALLBACKTYPE, CallbackType.FRAGMENT.name());
			instance.setTargetFragment((Fragment)callback, 0);
		}
		else if (callback instanceof Activity) {
			args.putString(BUNDLE_CALLBACKTYPE, CallbackType.ACTIVITY.name());
		}
		else {
			args.putString(BUNDLE_CALLBACKTYPE, CallbackType.NONE.name());
			throw new RuntimeException("callback is not Activity or Fragment");
		}
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mProfile = args.getParcelable(BUNDLE_PROFILE);
		mDescription = args.getString(BUNDLE_DESCRIPTION);
		mCallbackType = CallbackType.valueOf(args.getString(BUNDLE_CALLBACKTYPE));
		mPositiveButtonText = args.getString(BUNDLE_POSITIVEBUTTONTEXT);
		mNegativeButtonText = args.getString(BUNDLE_NEGATIVEBUTTONTEXT);

		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View dialogView = inflater.inflate(R.layout.name_input_dialog, container, false);

		EditText profileNameEdit = (EditText)dialogView.findViewById(R.id.input_dialog_edit);
		profileNameEdit.setText(mProfile.getName());

		if (mDescription != null) {
    		TextView descriptionView = (TextView)dialogView.findViewById(R.id.input_dialog_description);
        	descriptionView.setText(mDescription);
        }

		Button positiveButtonView = (Button)dialogView.findViewById(R.id.input_dialog_positive_button);

		positiveButtonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				onPositiveClick();
			}
		});

		if (mPositiveButtonText != null) {
    		positiveButtonView.setText(mPositiveButtonText);
        }

		Button negativeButtonView = (Button)dialogView.findViewById(R.id.input_dialog_negative_button);

		negativeButtonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				onNegativeClick();
			}
		});

		if (mNegativeButtonText != null) {
    		negativeButtonView.setText(mNegativeButtonText);
        }

		return dialogView;
	}

	private ProfileEditCallback getCallback() {
		ProfileEditCallback callback = null;
		switch (mCallbackType) {
		case ACTIVITY:
			callback = (ProfileEditCallback)getActivity();
			break;
		case FRAGMENT:
			callback = (ProfileEditCallback)getTargetFragment();
			break;
		default:
			break;
		}
		return callback;
	}

	private void onPositiveClick() {
		ProfileEditCallback callback = getCallback();
		if (callback != null) {
			EditText edit = (EditText)getDialog().findViewById(R.id.input_dialog_edit);
			mProfile.setName(edit.getText().toString());
			callback.onProfileEditPositive(mProfile);
		}
		dismiss();
	}

	private void onNegativeClick() {
		ProfileEditCallback callback = getCallback();
		if (callback != null) {
			callback.onProfileEditNegative();
		}
		dismiss();
	}
}
