package com.froop.app.kegs;

import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

public class CopyHelper {
  private byte[] mPreface;
  private InputStream mInput;
  private boolean mClose;
  private String mDir;
  private String mFile;
  private static final String mTmp = "tmp_";

  CopyHelper(InputStream input, String dir, String filename) {
    mPreface = null;
    mInput = input;
    mClose = true;
    mDir = dir;
    mFile = filename;
  }

  public CopyHelper withPreface(byte[] preface) {
    mPreface = preface;
    return this;
  }

  public CopyHelper withClose(boolean close) {
    mClose = close;
    return this;
  }

  // This leaves a partial temporary file on error and doesn't let you know
  // whether it was successful.  If your disk is full you will be unhappy.
  //
  // Caller could check for final file name.
  public void copy() throws java.io.IOException {
    Log.i("kegs", "CopyHelper to " + mDir + "/" + mFile);
    final File dir = new File(mDir);
    dir.mkdirs();

    final File output_file = new File(mDir, mTmp + mFile);
    final File final_file = new File(mDir, mFile);
    if (output_file == null || final_file == null) {
      throw new java.io.IOException("null File in " + mDir);
    }

    output_file.delete();
    output_file.createNewFile();

    byte buf[] = new byte[4096];
    FileOutputStream out = new FileOutputStream(output_file);
    if (mPreface != null) {
      out.write(mPreface, 0, mPreface.length);
    }
    int totalread = 0;
    do {
      int numread = mInput.read(buf);
      if (numread <= 0) {
        break;
      } else {
        out.write(buf, 0, numread);
      }
      totalread += numread;
    } while (true);
    out.close();
    if (totalread > 400 * 1024) {
      nativeSync();
    }
    output_file.renameTo(final_file);
    if (mClose) {
      mInput.close();
    }
  }

  // See jni/android_driver.c:nativeSync()
  private native void nativeSync();
}
