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
    "System 6", "X-MAS Demo (FTA)"};

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.diskimage_title);
    builder.setItems(mImages, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        if (item == 0) {
          ((KegsMain)getActivity()).loadConfig("boot_slot_7");
        } else if (item == 1) {
          ((KegsMain)getActivity()).loadConfig("boot_slot_5");
        }
      }
    });
    final AlertDialog dialog = builder.create();
    return dialog;
  }
}
