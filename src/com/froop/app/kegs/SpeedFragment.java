package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class SpeedFragment extends DialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    // TODO: Show the current speed and use setSingleChoiceItems
    builder.setTitle(R.string.speed_title);
    builder.setItems(R.array.speed_choices,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        // Adjust item number to match g_limit_speed.
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
