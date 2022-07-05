package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class SpeedFragment extends SherlockDialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    final SpeedSetting currentSpeed = ((KegsMain)getActivity()).getThread().getEmulationSpeed();

    builder.setTitle(R.string.speed_title);
    builder.setSingleChoiceItems(R.array.speed_choices,
                                 currentSpeed.getMenuItem(),
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        currentSpeed.setFromMenuItem(item);
      }
    });
    return builder.create();
  }
}
