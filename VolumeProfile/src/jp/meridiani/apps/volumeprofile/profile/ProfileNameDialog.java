package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.R;
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
	private static final String BUNDLE_DESCRIPTION        = "description";
	private static final String BUNDLE_PROFILE            = "profile";
	private static final String BUNDLE_POSITIVEBUTTONTEXT = "positiveButtonText";
	private static final String BUNDLE_NEGATIVEBUTTONTEXT = "negativeButtonText";

	public static ProfileNameDialog newInstance(String description, VolumeProfile profile, String positiveButtonText, String negativeButtonText) {
		ProfileNameDialog instance = new ProfileNameDialog();
		Bundle args = new Bundle();

		args.putString(BUNDLE_DESCRIPTION, description);
		args.putParcelable(BUNDLE_PROFILE, profile);
		args.putString(BUNDLE_POSITIVEBUTTONTEXT, positiveButtonText);
		args.putString(BUNDLE_NEGATIVEBUTTONTEXT, negativeButtonText);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View dialogView = inflater.inflate(R.layout.name_input_dialog, container, false);

		String description = getArguments().getString(BUNDLE_DESCRIPTION);
		if (description != null) {
    		TextView descriptionView = (TextView)dialogView.findViewById(R.id.input_dialog_description);
        	descriptionView.setText(description);
        }
		Button positiveButtonView = (Button)dialogView.findViewById(R.id.input_dialog_positive_button);
		Button negativeButtonView = (Button)dialogView.findViewById(R.id.input_dialog_negative_button);

		positiveButtonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				onPositiveClick();
			}
		});

		negativeButtonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				onNegativeClick();
			}
		});

		String positiveButtonText = getArguments().getString(BUNDLE_POSITIVEBUTTONTEXT);
		if (positiveButtonText != null) {
    		positiveButtonView.setText(positiveButtonText);
        }

		String negativeButtonText = getArguments().getString(BUNDLE_NEGATIVEBUTTONTEXT);
		if (negativeButtonText != null) {
    		negativeButtonView.setText(negativeButtonText);
        }

		return dialogView;
	}

	private void onPositiveClick() {
		Fragment parent = getParentFragment();
		if (parent instanceof ProfileEditCallback) {
			VolumeProfile profile = getArguments().getParcelable(BUNDLE_PROFILE);
			EditText edit = (EditText)getDialog().findViewById(R.id.input_dialog_edit);
			profile.setName(edit.getText().toString());
			((ProfileEditCallback)parent).onProfileEditPositive(profile);
		}
		dismiss();
	}

	private void onNegativeClick() {
		Fragment parent = getParentFragment();
		if (parent instanceof ProfileEditCallback) {
			((ProfileEditCallback)parent).onProfileEditNegative();
		}
		dismiss();
	}
}
