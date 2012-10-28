package com.froop.app.kegs;

import android.util.Log;
import android.os.AsyncTask;

import java.io.File;

class DownloadImage extends AsyncTask<Void, Void, Boolean> {
  interface DownloadReady {
    void onDownloadReady(boolean result);
  }

  private DownloadReady mNotify;
  private DiskImage mImage;
  private File mDest;

  DownloadImage(DownloadReady notify, String imagePath, DiskImage image) {
    mNotify = notify;
    mImage = image;
    mDest = new File(imagePath + "/" + mImage.filename);

    if (mDest.exists()) {
      // Assume whatever is there will work.
      // So the caller will immediately see that it's already done.
      mNotify.onDownloadReady(true);
    }
  }

  protected void onPreExecute() {
  }

  protected Boolean doInBackground(Void... params) {
    if (mDest.exists()) {
      // Assume whatever is there will work.
      return true;
    }

    return new DownloadHelper().save(
      "http://jsan.co/KEGS/images/" + mImage.filename, mDest.getPath());
  }

  protected void onCancelled(final Boolean result) {
    mNotify.onDownloadReady(false);
  }

  protected void onPostExecute(final Boolean result) {
    mNotify.onDownloadReady(result);
  }
}
