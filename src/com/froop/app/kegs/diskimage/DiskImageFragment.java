package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DiskImageFragment extends SherlockDialogFragment {
  private String[] mImages = {
    "System 6", "X-MAS Demo (FTA)", "Prince of Persia"};

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.diskimage_title);
    builder.setItems(mImages, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        DiskImage image = null;

        if (item == 0) {
          image = new DiskImage("System 6.hdv", "s7d1", 3, DiskImage.BOOT_SLOT_7, DiskImage.ASSET);
        } else if (item == 1) {
          image = new DiskImage("XMAS_DEMO.2MG", "s5d1", 2, DiskImage.BOOT_SLOT_5, DiskImage.ASSET);
        } else if (item == 2) {
          // TODO: There should be an adapter on the ListView instead.
          image = new DiskImage("prince.2mg", "s5d1", 2, DiskImage.BOOT_SLOT_5, DiskImage.DOWNLOAD);
        }

        if (image != null) {
          ((KegsMain)getActivity()).prepareDiskImage(image);
        }
      }
    });
    final AlertDialog dialog = builder.create();
    return dialog;
  }
}
