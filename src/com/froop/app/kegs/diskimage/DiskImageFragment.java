package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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


public class DiskImageFragment extends DialogFragment {
  private String[] mImages = {
    "System 6", "X-MAS Demo (FTA)"};

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    DiskImageAdapter items = new DiskImageAdapter(getActivity());
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.diskimage_title);
    builder.setSingleChoiceItems(items, -1,
        new DialogInterface.OnClickListener() {
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

  public class DiskImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Bitmap mBitmap;
    private Bitmap mBitmap2;

    public DiskImageAdapter(Context context) {
      mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
      mBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.jog_tab_target_green);
    }

    public Object getItem(int pos) {
      return pos;
    }

    public long getItemId(int pos) {
      return pos;
    }

    public int getCount() {
      return mImages.length;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = mInflater.inflate(
          R.layout.disk_image_list_item, parent, false);
      }

      int item = pos;  // BUG: this should be getItem(pos)
//      ((ImageView)convertView.findViewById(R.id.icon)).setImageBitmap(mBitmap);
      ((TextView)convertView.findViewById(android.R.id.text1)).setText(mImages[item]);
      // If cached, this puts a nice green dot next to the name:
//      ((ImageView)convertView.findViewById(R.id.cached)).setImageBitmap(mBitmap2);
      return convertView;
    }
  }
}
