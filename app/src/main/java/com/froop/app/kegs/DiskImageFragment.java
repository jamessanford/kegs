package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
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

import android.app.DialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class DiskImageFragment extends DialogFragment {
  private ArrayList<DiskImage> mFoundImages = new ArrayList<DiskImage>();

  // TODO: This should be the title name, and then we should index to it.
  private static int mLastSelected = -1;

  // DiskImage.BOOT, DiskImage.ASK, etc.
  private final int mDiskImageAction;

  public DiskImageFragment(final ConfigFile config, int image_action) {
    super();

    mDiskImageAction = image_action;

    // NOTE: We scan the directories in the UI thread before displaying
    // the dialog.  The user is waiting, but considering this is just
    // peeking at the directory listing on local disk it should be quick.
    // (less than 500ms)

    // Also, we scan each time the dialog is displayed, in case the
    // user adds files in the mean time.  If we wanted to be fancy,
    // we could use a special adapter, rescan every second, invalidate
    // the list, etc.
    updateFoundImages(config);
  }

  private void updateFoundImages(final ConfigFile config) {
    String[] dirs = config.getAllImageDirs();
    for (String dir : dirs) {
      String[] files = new File(dir).list();
      if (files != null) {
        for (String filename : files) {
          // NOTE: Checking each filename against the known asset image names
          //       is a bit silly.
          if (!filename.startsWith(".") &&
              DiskImage.isDiskImageFilename(filename) &&
              !AssetImages.isAssetFilename(filename)) {
            final DiskImage image = DiskImage.fromPath(dir + "/" + filename);
            if (image != null) {
              mFoundImages.add(image);
            }
          }
        }
      }
    }
    // NOTE HACK.  These files may not exist yet, so we cannot use fromPath.
    mFoundImages.add(new DiskImage("System 6.hdv", "s7d1", 3, DiskImage.BOOT_SLOT_7, DiskImage.ASSET));
    mFoundImages.add(new DiskImage("XMAS_DEMO.2MG", "s5d1", 2, DiskImage.BOOT_SLOT_5, DiskImage.ASSET));
    Collections.sort(mFoundImages);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.diskimage_title);
    final DiskImageAdapter items = new DiskImageAdapter(getActivity());
    builder.setSingleChoiceItems(items, mLastSelected, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        mLastSelected = item;
        DiskImage image = mFoundImages.get(item);
        if (image != null) {
          image.action = mDiskImageAction;
          ((KegsMain)getActivity()).prepareDiskImage(image);
        }
      }
    });
/* Seems strange to have a Cancel button.  Disable.
    builder.setNegativeButton(R.string.dialog_cancel,
                              new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int button) {
        dismiss();
      }
    });
*/
    final AlertDialog dialog = builder.create();
    return dialog;
  }


  // This whole adapter is just so that we can try using custom views,
  // or use isEnabled()==false to create a separator line in the ListView.
  public class DiskImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    public DiskImageAdapter(Context context) {
      mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Object getItem(int pos) {
      return pos;
    }

    public long getItemId(int pos) {
      return pos;
    }

    public int getCount() {
      return mFoundImages.size();
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
      int layoutid;
      if (android.os.Build.VERSION.SDK_INT >= 11) {
        layoutid = android.R.layout.simple_list_item_1;
      } else {
        layoutid = android.R.layout.select_dialog_item;
      }

      if (convertView == null) {
        convertView = mInflater.inflate(layoutid, parent, false);
      }
      int item = pos;  // BUG: should be getItem(pos)
      String title = mFoundImages.get(item).getTitle();
      ((TextView)(convertView.findViewById(android.R.id.text1))).setText(title);
      return convertView;
    }
  }
}
