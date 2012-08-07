package com.froop.app.kegs;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;

// Ick.  Look elsewhere.

public class DownloadHelper {
  private URL mURL;
  private HttpURLConnection mConnection;
  private InputStream mStream;

  private boolean openInputStream() {
    try {
      mConnection = (HttpURLConnection)mURL.openConnection();
      mConnection.connect();
      if (mConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        mConnection.disconnect();
        Log.e("kegs", "HTTP ERROR " + mConnection.getResponseCode());
        return false;
      }
    } catch (IOException e) {
      Log.e("kegs", "HTTP I/O EXCEPTION");
      return false;
    }
    try {
      mStream = new BufferedInputStream(mConnection.getInputStream());
    } catch (IOException e) {
      mConnection.disconnect();
      Log.e("kegs", "INPUTSTREAM I/O EXCEPTION");
      return false;
    }
    return true;
  }

  private boolean setURL(String url_string) {
    try {
      mURL = new URL(url_string);
    } catch (MalformedURLException e) {
      Log.e("kegs", "MALFORMED URL");
      return false;
    }
    return true;
  }

  // Should be valid to call if open() works.
  public void close() {
    mConnection.disconnect();
    mStream = null;
    mConnection = null;
    mURL = null;
  }

  public boolean open(String url_string) {
    if (!setURL(url_string)) {
      return false;
    }
    if (!openInputStream()) {
      return false;
    }
    return true;
  }

  public boolean save(String url_string, String local_file) {
    final File final_file = new File(local_file);
    final File dir = new File(final_file.getParent());
    dir.mkdirs();

    final File output_file = new File(dir, "tmp");
    output_file.delete();  // in case an earlier attempt failed
    FileOutputStream out;
    try {
      output_file.createNewFile();
      out = new FileOutputStream(output_file);
    } catch (java.io.IOException e) {
      Log.e("kegs", "unable to create " + local_file);
      return false;
    }

    if (!open(url_string)) {
      return false;
    }

    byte buf[] = new byte[4096];
    try {
      do {
        int numread = mStream.read(buf);
        if (numread <= 0) {
          break;
        } else {
          out.write(buf, 0, numread);
        }
      } while (true);
      out.close();
      close();
    } catch (java.io.IOException e) {
      Log.e("kegs", "error while downloading " + url_string);
      return false;
    }

    return output_file.renameTo(final_file);
  }
}
