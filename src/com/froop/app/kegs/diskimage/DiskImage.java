package com.froop.app.kegs;

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
}
