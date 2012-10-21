package com.froop.app.kegs;

import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

public class CopyHelper {
  private InputStream mInput;
  private String mDir;
  private String mFile;
  private static final String mTmp = "tmp_";

  CopyHelper(InputStream input, String dir, String filename) {
    mInput = input;
    mDir = dir;
    mFile = filename;
  }

  // This leaves a partial temporary file on error and doesn't let you know
  // whether it was successful.  If your disk is full you will be unhappy.
  //
  // Caller could check for final file name.
  public void copy() throws java.io.IOException {
    Log.e("kegs", "CopyHelper to " + mDir + "/" + mFile);
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
    do {
      int numread = mInput.read(buf);
      if (numread <= 0) {
        break;
      } else {
        out.write(buf, 0, numread);
      }
    } while (true);
    out.close();
    output_file.renameTo(final_file);
    mInput.close();
  }
}
