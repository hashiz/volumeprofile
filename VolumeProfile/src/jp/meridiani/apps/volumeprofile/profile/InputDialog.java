package jp.meridiani.apps.volumeprofile.profile;

import jp.meridiani.apps.volumeprofile.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InputDialog extends DialogFragment {
	private static final String BUNDLE_DESCRIPTION        = "description";
	private static final String BUNDLE_POSITIVEBUTTONTEXT = "positiveButtonText";
	private static final String BUNDLE_NEGATIVEBUTTONTEXT = "negativeButtonText";

	public interface InputDialogListener {
		public void onInputDialogPositive(String inputText);
		public void onInputDialogNegative();
	}

	public static InputDialog newInstance(String description, String positiveButtonText, String negativeButtonText) {
		InputDialog instance = new InputDialog();
		Bundle args = new Bundle();

		args.putString(BUNDLE_DESCRIPTION, description);
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

		String positiveButtonText = getArguments().getString(BUNDLE_POSITIVEBUTTONTEXT);
        if (positiveButtonText != null) {
    		Button positiveButtonView = (Button)dialogView.findViewById(R.id.input_dialog_positive_button);
    		positiveButtonView.setText(positiveButtonText);
        }

        String negativeButtonText = getArguments().getString(BUNDLE_NEGATIVEBUTTONTEXT);
        if (negativeButtonText != null) {
    		Button negativeButtonView = (Button)dialogView.findViewById(R.id.input_dialog_negative_button);
    		negativeButtonView.setText(negativeButtonText);
        }

		return dialogView;
	}

}
