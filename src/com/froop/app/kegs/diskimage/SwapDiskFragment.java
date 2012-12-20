package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class SwapDiskFragment extends SherlockDialogFragment {
  private DiskImage mImage;

  public SwapDiskFragment(DiskImage image) {
    super();
    mImage = image;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(mImage.getTitle());
    builder.setIcon(mImage.getIconId());
    final int choices = mImage.isHardDrive() ? R.array.swapdisk_hd_choices : R.array.swapdisk_choices;
    builder.setItems(choices, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        if (item == 0) {
          // ignore this image
        } else if (item == 1) {
          mImage.action = DiskImage.SWAP;  // just a disk insert
          ((KegsMain)getActivity()).loadDiskImage(mImage);
        } else if (item == 2) {
          mImage.action = DiskImage.BOOT;  // was ASK
          ((KegsMain)getActivity()).loadDiskImage(mImage);
        }
      }
    });
    return builder.create();
  }
}
