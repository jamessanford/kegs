package com.froop.app.kegs;

import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

class KegsKeyboard {
  // These are ADB keycodes.
  public final static int KEY_ESCAPE = 0x35;
  public final static int KEY_RETURN = 0x24;
  public final static int KEY_BACKSPACE = 0x33;  // 0x75 == delete
  public final static int KEY_TAB = 0x30;
  public final static int KEY_SPACE = 0x31;
  public final static int KEY_F4 = 0x76;
  public final static int KEY_LEFT = 0x3b;
  public final static int KEY_RIGHT = 0x3c;
  public final static int KEY_UP = 0x3e;
  public final static int KEY_DOWN = 0x3d;
  public final static int KEY_SHIFT = 0x38;
  public final static int KEY_RESET = 0x7f;

  // These keys can be set to stick down by calling keyDownSticky()
  // They will release on a normal keypress.
  // To indicate release on the GUI, get notified via setOnStickyReset()
  public final static int KEY_CONTROL = 0x36;
  public final static int KEY_OPEN_APPLE = 0x37;
  public final static int KEY_CLOSED_APPLE = 0x3a;

  private int mSticky = 0;  // Bitmask for sticky keys.
  private final static int STICKY_CONTROL = 1;
  private final static int STICKY_OPEN_APPLE = 2;
  private final static int STICKY_CLOSED_APPLE = 4;

  interface StickyReset {
    void onStickyReset();
  }
  private StickyReset mNotify;

  // NOTE: Using event.getDeviceId() at runtime isn't working.
  protected KeyCharacterMap keymap = KeyCharacterMap.load(KeyCharacterMap.BUILT_IN_KEYBOARD);

  private ConcurrentLinkedQueue mEventQueue;

  public KegsKeyboard(ConcurrentLinkedQueue q) {
    mEventQueue = q;
  }

  public void setOnStickyReset(StickyReset notify) {
    mNotify = notify;
  }

  // Direct interface to modifiers, not sticky keys.
  public boolean keyModifiers(final int meta, boolean key_up) {
    boolean handled = false;
    if ((meta & KeyEvent.META_ALT_ON) != 0) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_OPEN_APPLE, key_up));
      handled = true;
    }
    if ((meta & KeyEvent.META_META_ON) != 0) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_CLOSED_APPLE, key_up));
      handled = true;
    }
    if ((meta & KeyEvent.META_CTRL_ON) != 0) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_CONTROL, key_up));
      handled = true;
    }
    return handled;
  }

  // Handle 'space' through to ~.
  public boolean handleAsciiKey(int key_id) {
    if (key_id < 0x20 || key_id > 0x7e) {
      return false;
    }
    key_id -= 0x20;
    final KeyTable.A2Key a2key = KeyTable.ascii.get(key_id);
    if (a2key.use_shift) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_SHIFT, false));
      keyDownUp(a2key.keycode);
      mEventQueue.add(new Event.KeyKegsEvent(KEY_SHIFT, true));
    } else {
      keyDownUp(a2key.keycode);
    }
    return true;
  }

  public boolean handleOtherKey(int keyCode) {
    boolean handled = false;
    // Yes, this should be a lookup table.
    if (keyCode == KeyEvent.KEYCODE_ENTER) {
      keyDownUp(KEY_RETURN);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
      keyDownUp(KEY_SPACE);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_TAB) {
      keyDownUp(KEY_TAB);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
      keyDownUp(KEY_ESCAPE);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_DEL) {
      keyDownUp(KEY_BACKSPACE);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
      keyDownUp(KEY_LEFT);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
      keyDownUp(KEY_RIGHT);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
      keyDownUp(KEY_UP);
      handled = true;
    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
      keyDownUp(KEY_DOWN);
      handled = true;
    }
    return handled;
  }

  public boolean keyEvent(KeyEvent event) {
    boolean handled = false;
    if (event.getAction() == KeyEvent.ACTION_MULTIPLE && event.getKeyCode() == KeyEvent.KEYCODE_UNKNOWN) {
      // TODO: support event.getCharacters(), but we may need to use InputConnection instead of key events.
      // Log.w("kegs", "key CHARACTERS " + event.getCharacters());
      // return true;
    }
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      final int meta = event.getMetaState();
      handled = keyModifiers(meta, false);
      final int keyCode = event.getKeyCode();
      if (keymap != null && keymap.isPrintingKey(keyCode)) {
        // Remove meta states that we handle to get a meaningful ASCII result.
        int reduced_meta = meta;
        reduced_meta &= ~(KeyEvent.META_ALT_LEFT_ON | KeyEvent.META_ALT_RIGHT_ON | KeyEvent.META_ALT_ON);
        reduced_meta &= ~(KeyEvent.META_META_LEFT_ON | KeyEvent.META_META_RIGHT_ON | KeyEvent.META_META_ON);
        reduced_meta &= ~(KeyEvent.META_CTRL_LEFT_ON | KeyEvent.META_CTRL_RIGHT_ON | KeyEvent.META_CTRL_ON);
        int key_id = keymap.get(keyCode, reduced_meta);
        handled = handleAsciiKey(key_id) | handled;
      } else {
        handled = handleOtherKey(keyCode) | handled;
      }
      // Release any modifiers that may have been pressed.
      // BUG: ACTION_UP for this is not working, so just toggle them here.
      handled = keyModifiers(meta, true) | handled;
    }
    return handled;
  }

  private void resetStickyKeys() {
    if (mSticky != 0) {
      if ((mSticky & STICKY_CONTROL) != 0) {
        mEventQueue.add(new Event.KeyKegsEvent(KEY_CONTROL, true));
      }
      if ((mSticky & STICKY_OPEN_APPLE) != 0) {
        mEventQueue.add(new Event.KeyKegsEvent(KEY_OPEN_APPLE, true));
      }
      if ((mSticky & STICKY_CLOSED_APPLE) != 0) {
        mEventQueue.add(new Event.KeyKegsEvent(KEY_CLOSED_APPLE, true));
      }
      mSticky = 0;
      if (mNotify != null) {
        mNotify.onStickyReset();
      }
    }
  }

  public void keyDownSticky(final int key_id, final boolean key_up) {
    int mask = 0;
    if (key_id == KEY_CONTROL) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_CONTROL, key_up));
      mask = STICKY_CONTROL;
    } else if (key_id == KEY_OPEN_APPLE) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_OPEN_APPLE, key_up));
      mask = STICKY_OPEN_APPLE;
    } else if (key_id == KEY_CLOSED_APPLE) {
      mEventQueue.add(new Event.KeyKegsEvent(KEY_CLOSED_APPLE, key_up));
      mask = STICKY_CLOSED_APPLE;
    }
    if (key_up) {
      mSticky &= ~mask;
    } else {
      mSticky |= mask;
    }
  }

  public void keyDownUp(final int key_id) {
    mEventQueue.add(new Event.KeyKegsEvent(key_id, false));  // key down
    mEventQueue.add(new Event.KeyKegsEvent(key_id, true));   // key up
    resetStickyKeys();
  }
}
