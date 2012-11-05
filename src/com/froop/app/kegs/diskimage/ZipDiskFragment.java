package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ZipDiskFragment extends SherlockDialogFragment {
  private ArrayList<String> mFiles = new ArrayList<String>();
  private ArrayList<String> mTitles = new ArrayList<String>();
  private DiskImage mImage;

  public ZipDiskFragment(DiskImage image) {
    super();
    mImage = image;

    // NOTE: We extract the zipfile table of contents in the UI thread.
    getTableOfContents();
  }

  private void getTableOfContents() {
    try {
      final ZipFile zip = new ZipFile(mImage.filename);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        final String filename = entry.getName();
        if (DiskImage.isDiskImageFilename(filename)) {
          mFiles.add(filename);
          int pos = filename.lastIndexOf("/");
          mTitles.add(filename.substring(pos + 1));
        }
      }
      zip.close();
    } catch (java.io.IOException e) {
      Log.e("kegs", Log.getStackTraceString(e));
      // Do not show any results.
      mFiles.clear();
      mTitles.clear();
    }
  }

  // Caller may use getFirstImage when there is only one item
  public boolean needsDialog() {
    return mTitles.size() != 1;
  }

  public DiskImage getFirstImage() {
    mImage.extract_filename = mFiles.get(0);
    return mImage;
  }

  private void forceDiskLoaderError() {
    // TODO: Ick.  All this just so that whoever opened this fragment
    //             will get a DiskLoader callback.
    mImage.filename = "";
    mImage.origin = DiskImage.ERROR;
    ((KegsMain)getActivity()).runDiskLoader(mImage);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    // Force 3.5 inch disk icon instead of .getIconId()
    // TODO: Could also show a special zipfile icon...?
    builder.setIcon(R.drawable.ic_menu_save);
    builder.setTitle(mImage.getTitle());

    if (mTitles.size() == 0) {
      builder.setMessage(R.string.zipfile_nomatches);
      builder.setPositiveButton(R.string.dialog_ok,
                                new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int button) {
          dismiss();
          forceDiskLoaderError();
        }
      });
    } else {
      builder.setItems(mTitles.toArray(new String[0]), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
          dismiss();
          mImage.extract_filename = mFiles.get(item);
          ((KegsMain)getActivity()).runDiskLoader(mImage);
        }
      });
    }
    return builder.create();
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
    forceDiskLoaderError();
  }
}
