package com.froop.app.kegs;

// Event objects are stuffed into a ConcurrentLinkedQueue
// and consumed by JNI android_driver.c:check_input_events()

class Event {
  static class KegsEvent {}

  static class KeyKegsEvent extends KegsEvent {
    public KeyKegsEvent(int key_id, boolean up) {
      this.key_id = key_id;
      this.up = up;
    }
    public int key_id;
    public boolean up;
  }

  static class MouseKegsEvent extends KegsEvent {
    public MouseKegsEvent(int x, int y, int buttons, int buttons_valid) {
      this.x = x;
      this.y = y;
      this.buttons = buttons;
      this.buttons_valid = buttons_valid;
    }
    public int x;  // change in X since last event
    public int y;  // change in Y since last event
    public int buttons;
    public int buttons_valid;
  }

  static class JoystickKegsEvent extends KegsEvent {
    public JoystickKegsEvent(int x, int y, int buttons) {
      this.x = x;
      this.y = y;
      this.buttons = buttons;
    }
    public int x;  // absolute X, -32767 to 32767
    public int y;  // absolute Y, -32767 to 32767
    public int buttons;
  }

  static class DiskImageEvent extends KegsEvent {
    public DiskImageEvent(String filename, int slot, int drive) {
      this.filename = filename;  // when null, eject only.
      this.slot = slot;
      this.drive = drive;
    }
    public String filename;
    public int slot;
    public int drive;
  }
}
