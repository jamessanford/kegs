package com.froop.app.kegs;

import java.io.File;

class DiskImage {
  public static final String BOOT_SLOT_5 = "boot_slot_5";
  public static final String BOOT_SLOT_6 = "boot_slot_6";
  public static final String BOOT_SLOT_7 = "boot_slot_7";

  public boolean primary = true;
  public String filename;
  public String drive;
  public int speed;
  public String template;

  // Example:
  //   DiskImage("XMAS_DEMO.2MG", "s5d1", 2, "boot_slot_5");
  public DiskImage(final String filename, final String drive,
                   final int speed, final String template) {
    this.filename = filename;
    this.drive = drive;
    this.speed = speed;
    this.template = template;
  }

  public static DiskImage fromPath(String path) {
    final File file = new File(path);
    final long length = file.length();
    if (!file.exists()) {
      return null;
    } else if (length >= 1024 * 1024) {
      // TODO: should insert the disk and use the System 6 template
      return new DiskImage(path, "s7d1", 3, BOOT_SLOT_7);
    } else if (length >= 400 * 1024) {
      return new DiskImage(path, "s5d1", 2, BOOT_SLOT_5);
    } else if (length > 0) {
      return new DiskImage(path, "s6d1", 1, BOOT_SLOT_6);
    } else {
      return null;
    }
  }
}
