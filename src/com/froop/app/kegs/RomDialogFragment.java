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
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.rom_title);
    builder.setSingleChoiceItems(R.array.rom_choices, -1,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        if (item == 0) {
          ((KegsMain)getActivity()).finish();
        } else if (item == 1) {
          ((KegsMain)getActivity()).getRomFile(ConfigFile.ROM01);
        } else if (item == 2) {
          ((KegsMain)getActivity()).getRomFile(ConfigFile.ROM03);
        }
      }
    });
    final AlertDialog dialog = builder.create();
    dialog.setCanceledOnTouchOutside(false);  // lame
    return dialog;
  }
}
