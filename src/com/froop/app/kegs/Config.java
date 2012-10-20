package com.froop.app.kegs;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;

// Ick.

class Config {
  // FIXME: use local directory here, not sdcard
  public static final File mPath = Environment.getExternalStorageDirectory();
  public static final String mConfigFile = "KEGS/config.kegs";
  public static final String mROM03 = "KEGS/ROM.03";
  public static final String mROM01 = "KEGS/ROM.01";

  static public String whichRomFile() {
    File rom = new File(mPath, mROM03);
    if (rom != null && rom.exists()) {
      return rom.getPath();
    }

    rom = new File(mPath, mROM01);
    if (rom != null && rom.exists()) {
      return rom.getPath();
    }

    return null;
  }

  public static void defaultConfig(String rom_path) {
    // FIXME: copy 'default' over to 'config.kegs'
    File config = new File(mPath, mConfigFile);
    if (config == null || !config.exists()) {
      createConfig(rom_path);
    }
  }

  public static void createConfig(String rom_path) {
    try {
      final byte[] data_bytes = String.format(
          "g_cfg_rom_path = %s\ng_limit_speed = 3\n", rom_path).getBytes();
      File config = new File(mPath, mConfigFile);
      config.createNewFile();
      FileOutputStream out = new FileOutputStream(config);
      out.write(data_bytes, 0, data_bytes.length);
      out.close();
    } catch (java.io.IOException e) {
      // kegs will fail and exit with no config
      return;
    }
  }
}
