package com.froop.app.kegs;

import android.util.Log;
import java.lang.Integer;
import java.io.File;

class DiskImage implements Comparable {
  // NOTE: "this class has a natural ordering that is inconsistent with equals"
  //       (sorted on the filename excluding path, case insensitive)

  // template
  public static final String BOOT_SLOT_5 = "boot_slot_5";
  public static final String BOOT_SLOT_6 = "boot_slot_6";
  public static final String BOOT_SLOT_7 = "boot_slot_7";

  // origin
  public static final int ASSET = 0;
  public static final int DOWNLOAD = 1;
  public static final int LOCALFILE = 2;

  // action
  public static final int BOOT = 0;
  public static final int SWAP = 1;
  public static final int ASK = 2;
  public static final int CANCEL = 3;

  public String filename;
  public String drive;
  public int speed;
  public String template;
  public int origin;
  public int action;
  public String extract_filename = null;  // for extracting from zipfiles

  // Example:
  //   DiskImage("XMAS_DEMO.2MG", "s5d1", 2, "boot_slot_5", LOCALFILE);
  public DiskImage(final String filename, final String drive,
                   final int speed, final String template, final int origin) {
    this.filename = filename;
    this.drive = drive;
    this.speed = speed;
    this.template = template;
    this.origin = origin;
    this.action = BOOT;
  }

  public static DiskImage fromPath(String path) {
    final File file = new File(path);
    final long length = file.length();
    if (!file.exists()) {
      return null;
    } else if (length >= 1024 * 1024) {
      // TODO: should insert the disk and use the System 6 template
      return new DiskImage(path, "s7d1", 3, BOOT_SLOT_7, LOCALFILE);
    } else if (length >= 400 * 1024) {
      return new DiskImage(path, "s5d1", 2, BOOT_SLOT_5, LOCALFILE);
    } else if (length > 0) {
      return new DiskImage(path, "s6d1", 1, BOOT_SLOT_6, LOCALFILE);
    } else {
      return null;
    }
  }

  public static boolean isDiskImageFilename(String filename) {
    if (filename.endsWith(".2mg") ||
        filename.endsWith(".dsk") ||
        filename.endsWith(".nib") ||
        filename.endsWith(".hdv") ||
        filename.endsWith(".po") ||
        filename.endsWith(".do") ||
        filename.endsWith(".bin") ||
        filename.endsWith(".2MG") ||
        filename.endsWith(".DSK") ||
        filename.endsWith(".NIB") ||
        filename.endsWith(".HDV") ||
        filename.endsWith(".PO") ||
        filename.endsWith(".DO") ||
        filename.endsWith(".BIN")) {
      return true;
    } else {
      return false;
    }
  }

  public String getBaseFilename() {
    int pos = this.filename.lastIndexOf("/");
    return this.filename.substring(pos + 1);
  }

  public String getTitle() {
    // Use better names for included assets.
    return AssetImages.translateTitle(getBaseFilename());
  }

  public int getIconId() {
    if (this.template.equals(BOOT_SLOT_7)) {
      return (R.drawable.ic_menu_save);  // FIXME should be hard disk icon
    } else if (this.template.equals(BOOT_SLOT_5)) {
      return (R.drawable.ic_menu_save);
    } else if (this.template.equals(BOOT_SLOT_6)) {
      return (R.drawable.ic_menu_save);  // FIXME should be 5.25 disk icon
    } else {
      return (R.drawable.ic_menu_save);  // FIXME should be question mark icon
    }
  }

  public Event.DiskImageEvent getDiskImageEvent() {
    if (this.drive.substring(0, 1).equals("s") &&
        this.drive.substring(2, 3).equals("d")) {
      int slot = Integer.parseInt(this.drive.substring(1, 2));
      int drive = Integer.parseInt(this.drive.substring(3));
      return new Event.DiskImageEvent(this.filename, slot, drive);
    } else {
      Log.e("kegs", "disk image " + this.filename + " has bad drive " + this.drive);
      return null;
    }
  }

  public int compareTo(Object other) {
    if (other instanceof DiskImage) {
      return this.getTitle().compareToIgnoreCase(((DiskImage)other).getTitle());
    } else {
      throw new ClassCastException();
    }
  }
}
