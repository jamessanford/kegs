package com.froop.app.kegs;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

class ConfigFile {
  public static final String ROM03 = "ROM.03";
  public static final String ROM01 = "ROM.01";

  private static final String mConfigFile = "config.kegs";
  private static final String mConfigDefault = "default";

  private Context mContext;
  private String mConfigPath;

  ConfigFile(Context context) {
    mContext = context;

    File externalDir = mContext.getExternalFilesDir(null);
    if (externalDir == null) {
      // Use internal storage if external directory is not available.
      externalDir = mContext.getFilesDir();
    }

    mConfigPath = externalDir.getPath();
  }

  public String getConfigFile() {
    return mConfigPath + "/" + mConfigFile;
  }

  public String getConfigPath() {
    return mConfigPath;
  }

  public String getImagePath() {
    return mConfigPath + "/images";
  }

  public String getCachePath() {
    File cacheDir = mContext.getExternalCacheDir();
    if (cacheDir == null) {
      // Use internal storage if external directory is not available.
      cacheDir = mContext.getCacheDir();
    }
    return cacheDir.getPath();
  }

  public String[] getAllImageDirs() {
    // Return an array of pathnames that may contain disk images.
    // The pathnames may not actually exist, that is OK.
    String externalStorage;

    File externalDir = Environment.getExternalStorageDirectory();
    if (externalDir == null) {
      // Try harder.  It is OK if this does not exist.
      externalStorage = "/mnt/sdcard";
    } else {
      externalStorage = externalDir.getPath();
    }

    String[] dirs = {
      getImagePath(),
      externalStorage,
      externalStorage + "/kegs",
      externalStorage + "/Download"
    };
    return dirs;
  }

  public void ensureAssetCopied(String destPath, String zipfile, String exampleFile) {
    // We only check for a local copy of a single file before unzipping...
    final File local_copy = new File(destPath, exampleFile);
    if (local_copy != null && local_copy.exists()) {
      // Assume that whatever is there will work.
      return;
    }

    // NOTE: There's no sanity checking here, so it's best for builtin assets.
    try {
      ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(mContext.getAssets().open(zipfile)));
      ZipEntry zipEntry;
      while ((zipEntry = zipStream.getNextEntry()) != null) {
        new CopyHelper(zipStream, destPath,
                       zipEntry.getName()).withClose(false).copy();
      }
    } catch (java.io.IOException e) {
     // KEGS will just fail.
     Log.e("kegs", Log.getStackTraceString(e));
     return;
    }
  }

  public void ensureAssetCopied(String destPath, String assetName) {
    // Make sure there's a user-readable copy of whatever from assets.
    final File local_copy = new File(destPath, assetName);
    if (local_copy != null && local_copy.exists()) {
      // Assume that whatever is there will work.
      return;
    }

    try {
      new CopyHelper(mContext.getAssets().open(assetName),
                     destPath, assetName).copy();
    } catch (java.io.IOException e) {
     // KEGS will just fail.
     Log.e("kegs", Log.getStackTraceString(e));
     return;
    }
  }

  public void defaultConfig() {
    ensureAssetCopied(mConfigPath, mConfigDefault);
    // Then, copy whatever is there over to the actual 'config.kegs' file.
    try {
      new CopyHelper(new FileInputStream(new File(mConfigPath, mConfigDefault)),
                     mConfigPath, mConfigFile).copy();
    } catch (java.io.IOException e) {
     // KEGS will just fail.
     Log.e("kegs", Log.getStackTraceString(e));
     return;
    }
  }

  public String fullPath(final String filename) {
    if (filename.startsWith("/")) {
      return filename;
    } else {
      return getImagePath() + "/" + filename;
    }
  }

  public byte[] getConfigPreface(final DiskImage image) {
    return String.format("g_limit_speed = %d\n%s = %s\n\n",
                         image.speed, image.drive,
                         fullPath(image.filename)).getBytes();
  }

  public void setConfig(DiskImage image) {
    // Overwrite 'config.kegs' with a config based on a template.
    try {
      new CopyHelper(mContext.getAssets().open(image.template), mConfigPath,
                     mConfigFile).withPreface(getConfigPreface(image)).copy();
    } catch (java.io.IOException e) {
     // KEGS will just fail.
     Log.e("kegs", Log.getStackTraceString(e));
     return;
    }
  }

  public String whichRomFile() {
    File rom = new File(mConfigPath, ROM03);
    if (rom != null && rom.exists()) {
      return rom.getPath();
    }

    rom = new File(mConfigPath, ROM01);
    if (rom != null && rom.exists()) {
      return rom.getPath();
    }

    return null;
  }
}
