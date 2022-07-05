package com.froop.app.kegs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import android.app.DialogFragment;

// A predefined progress dialog that calls a Runner when it's cancelled.

public class SpecialProgressDialog extends DialogFragment {
  private Runnable mCancelRunnable;

  public SpecialProgressDialog(Runnable runnable) {
    super();
    mCancelRunnable = runnable;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    ProgressDialog dialog = new ProgressDialog(getActivity());
    // TODO: should probably use an XML layout for this.
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setMessage(getResources().getText(R.string.progress_message));
    if (android.os.Build.VERSION.SDK_INT <= 10 || android.os.Build.VERSION.SDK_INT >= 14) {
      // This seems to crash on some vendors Honeycomb.
      dialog.setProgressNumberFormat(null);
    }
    if (android.os.Build.VERSION.SDK_INT >= 14) {
      dialog.setProgressPercentFormat(null);
    }
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);
    return dialog;
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    mCancelRunnable.run();
  }
}
