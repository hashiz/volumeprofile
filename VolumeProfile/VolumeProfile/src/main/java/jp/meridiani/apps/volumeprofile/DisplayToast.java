package jp.meridiani.apps.volumeprofile;

import android.content.Context;
import android.widget.Toast;

public class DisplayToast {

	private static Toast sToast = null;

	private DisplayToast() {
	}

	public static void show(Context context, int resId, int duration) {
		show(context, context.getString(resId), duration);
	}

	public static synchronized void show(Context context, CharSequence text, int duration) {
		if (sToast != null) {
			sToast.cancel();
		}
		sToast = Toast.makeText(context, text, duration);
		sToast.show();
	}
}
