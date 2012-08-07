package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class RomDialogFragment extends DialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final CharSequence[] items = {"I don't own a GS",
                                  "I have a ROM 01",
                                  "I have a ROM 03"};

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Checking for ROM");
    builder.setSingleChoiceItems(items, -1,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        if (item == 0) {
          ((KegsMain)getActivity()).finish();
        } else if (item == 1) {
          ((KegsMain)getActivity()).getRomFile(Config.mROM01);
        } else if (item == 2) {
          ((KegsMain)getActivity()).getRomFile(Config.mROM03);
        }
      }
    });
    final AlertDialog dialog = builder.create();
    dialog.setCanceledOnTouchOutside(false);  // lame
    return dialog;
  }
}
