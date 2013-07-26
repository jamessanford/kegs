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

    int currentSpeed = ((KegsMain)getActivity()).getThread().getEmulationSpeed();
    // Adjust g_limit_speed to match default item number.
    currentSpeed--;
    if (currentSpeed < 0) {
      currentSpeed = 3;
    }

    builder.setTitle(R.string.speed_title);
    builder.setSingleChoiceItems(R.array.speed_choices, currentSpeed,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        // Adjust selected item number to match g_limit_speed.
        item++;
        if (item > 3) {
          item = 0;
        }
        ((KegsMain)getActivity()).getThread().setEmulationSpeed(item);
      }
    });
    return builder.create();
  }
}
