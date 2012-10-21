package com.froop.app.kegs;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class ConfigFile {
  public static final String ROM03 = "ROM.03";
  public static final String ROM01 = "ROM.01";

  private static final String mConfigFile = "config.kegs";
  private static final String mConfigDefault = "default";

  private Context mContext;
  private String mConfigPath;

  ConfigFile(Context context) {
    mContext = context;
    mConfigPath = mContext.getExternalFilesDir(null).getPath();
  }

  public String getConfigFile() {
    return mConfigPath + "/" + mConfigFile;
  }

  public String getConfigPath() {
    return mConfigPath;
  }

  public void ensureAssetCopied(String assetName) {
    // Make sure there's a user-readable copy of whatever from assets.
    final File local_copy = new File(mConfigPath, assetName);
    if (local_copy != null && local_copy.exists()) {
      // Assume that whatever is there will work.
      return;
    }

    try {
      new CopyHelper(mContext.getAssets().open(assetName),
                     mConfigPath, assetName).copy();
    } catch (java.io.IOException e) {
     // KEGS will just fail.
     return;
    }
  }

  public void defaultConfig() {
    ensureAssetCopied(mConfigDefault);
    // Then, copy whatever is there over to the actual 'config.kegs' file.
    try {
      new CopyHelper(new FileInputStream(new File(mConfigPath, mConfigDefault)),
                     mConfigPath, mConfigFile).copy();
    } catch (java.io.IOException e) {
     // KEGS will just fail.
     return;
    }
  }

  public void internalConfig(String configName) {
    // Overwrite 'config.kegs' with a config from the assets directory.
    try {
      new CopyHelper(mContext.getAssets().open(configName),
                     mConfigPath, mConfigFile).copy();
    } catch (java.io.IOException e) {
     // KEGS will just fail.
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
