package com.froop.app.kegs;

import android.util.Log;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

class DiskLoader extends AsyncTask<Void, Void, Boolean> {
  interface ImageReady {
    void onImageReady(boolean result, DiskImage image);
  }

  private ImageReady mNotify;
  private DiskImage mImage;

  private boolean mSlow = false;
  private String mDestPath;

  DiskLoader(final ImageReady notify, final ConfigFile config,
             final DiskImage image) {
    mNotify = notify;
    mImage = image;

    if (image.filename.endsWith(".gz") ||
        image.filename.endsWith(".zip") ||
        image.filename.endsWith(".GZ") ||
        image.filename.endsWith(".ZIP") ||
        image.origin == DiskImage.ASSET ||
        image.origin == DiskImage.DOWNLOAD) {
      mSlow = true;
    }

    if (image.origin == DiskImage.ASSET ||
        image.origin == DiskImage.DOWNLOAD) {
      mDestPath = config.getImagePath();

      final File local_copy = new File(mDestPath, image.filename);
      if (local_copy != null && local_copy.exists()) {
        // Assume whatever is there will work.
        mSlow = false;
      }
    }

    if (image.origin == DiskImage.LOCALFILE) {
      // If we need to extract it, it will go here.
      mDestPath = config.getCachePath();
    }
  }

  public boolean willBeSlow() {
    return mSlow;
  }

  protected void onPreExecute() {
  }

  private Boolean extractImage() {
    if (mImage.origin == DiskImage.ERROR) {
      return false;
    }

    if (mImage.origin == DiskImage.ASSET) {
      // Just keep polling waiting for it, AssetImages is working on it.
      final File local_copy = new File(mDestPath, mImage.filename);
      while (!local_copy.exists()) {
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        if (isCancelled()) {
          return false;
        }
      }
      return true;
    }

    if (mImage.origin == DiskImage.DOWNLOAD) {
      final File local_copy = new File(mDestPath, mImage.filename);
      if (local_copy != null && local_copy.exists()) {
        // Assume whatever is there will work.
        return true;
      } else {
        return new DownloadHelper().save(
            "http://jsan.co/KEGS/images/" + mImage.filename,
            local_copy.getPath());
      }
    }

    if (mImage.filename.endsWith(".gz") || mImage.filename.endsWith(".GZ")) {
      final String old_filename = mImage.getBaseFilename();
      final String filename = old_filename.substring(0, old_filename.lastIndexOf("."));
      try {
        GZIPInputStream zipStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(new File(mImage.filename))));
        new CopyHelper(zipStream, mDestPath, filename).copy();
      } catch (java.io.IOException e) {
        Log.e("kegs", Log.getStackTraceString(e));
        return false;
      }
      mImage.filename = new File(mDestPath, filename).getPath();
      return true;
    }

    if (mImage.filename.endsWith(".zip") || mImage.filename.endsWith(".ZIP")) {
      final int pos = mImage.extract_filename.lastIndexOf("/");
      final String filename = mImage.extract_filename.substring(pos + 1);
      try {
        final ZipFile zip = new ZipFile(mImage.filename);
        final ZipEntry extract = zip.getEntry(mImage.extract_filename);
        if (extract == null) {
          zip.close();
          Log.e("kegs", mImage.filename + " has no " + mImage.extract_filename);
          return false;
        }
        new CopyHelper(zip.getInputStream(extract), mDestPath, filename).copy();
        zip.close();
      } catch (java.io.IOException e) {
        Log.e("kegs", Log.getStackTraceString(e));
        return false;
      }
      mImage.filename = new File(mDestPath, filename).getPath();
      return true;
    }

    // No special handling required, finish up and run onImageReady.
    return true;
  }

  protected Boolean doInBackground(Void... params) {
    Boolean result = extractImage();
    nativeSync();  // Flush new disk images before claiming they are ready.
    return result;
  }

  protected void onCancelled(final Boolean result) {
    mNotify.onImageReady(result, mImage);
  }

  protected void onPostExecute(final Boolean result) {
    mNotify.onImageReady(result, mImage);
  }

  // See jni/android_driver.c:nativeSync()
  private native void nativeSync();
}
