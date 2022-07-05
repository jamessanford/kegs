package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import android.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {
  private int mMessage;
  private Runnable mRunnable;

  public ErrorDialogFragment(int textId, Runnable runnable) {
    super();
    mMessage = textId;
    mRunnable = runnable;  // ok if null
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.error_title);
    builder.setIcon(R.drawable.ic_dialog_alert_holo_dark);
    builder.setMessage(mMessage);
    builder.setPositiveButton(R.string.dialog_ok,
                              new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int button) {
        dismiss();
      }
    });
    final AlertDialog dialog = builder.create();
    dialog.setCanceledOnTouchOutside(false);  // prevent accidental dismissal
    return dialog;
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    if (mRunnable != null) {
      mRunnable.run();
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mRunnable != null) {
      mRunnable.run();
    }
  }
}
