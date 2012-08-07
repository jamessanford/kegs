package com.froop.app.kegs;

import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

class KegsKeyboard {
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

  // Handle 'space' through to ~.
  public boolean handleAsciiKey(int key_id) {
    if (key_id < 0x20 || key_id > 0x7e) {
      return false;
    }
    key_id -= 0x20;
    final KeyTable.A2Key a2key = KeyTable.ascii.get(key_id);
    if (a2key.use_shift) {
      mEventQueue.add(new KegsView.KeyKegsEvent(KEY_SHIFT, false));
      keyDownUp(a2key.keycode);
      mEventQueue.add(new KegsView.KeyKegsEvent(KEY_SHIFT, true));
    } else {
      keyDownUp(a2key.keycode);
    }
    return true;
  }

  public boolean keyEvent(KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      int keyCode = event.getKeyCode();
      if (keyCode == KeyEvent.KEYCODE_ENTER) {
        keyDownUp(KEY_RETURN);
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
        keyDownUp(KEY_SPACE);
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_TAB) {
        keyDownUp(KEY_TAB);
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_DEL) {
        keyDownUp(KEY_BACKSPACE);
        return true;
      } else if (keymap == null) {
        return false;
      } else if (keymap.isPrintingKey(keyCode)) {
        int key_id = keymap.get(keyCode, event.getMetaState());
        return handleAsciiKey(key_id);
      }
    }
    return false;
  }

  private void resetStickyKeys() {
    if (mSticky != 0) {
      if ((mSticky & STICKY_CONTROL) != 0) {
        mEventQueue.add(new KegsView.KeyKegsEvent(KEY_CONTROL, true));
      }
      if ((mSticky & STICKY_OPEN_APPLE) != 0) {
        mEventQueue.add(new KegsView.KeyKegsEvent(KEY_OPEN_APPLE, true));
      }
      if ((mSticky & STICKY_CLOSED_APPLE) != 0) {
        mEventQueue.add(new KegsView.KeyKegsEvent(KEY_CLOSED_APPLE, true));
      }
      mSticky = 0;
      if (mNotify != null) {
        mNotify.onStickyReset();
      }
    }
  }

  public void keyDownSticky(int key_id, boolean key_up) {
    int mask = 0;
    if (key_id == KEY_CONTROL) {
      mEventQueue.add(new KegsView.KeyKegsEvent(KEY_CONTROL, key_up));
      mask = STICKY_CONTROL;
    } else if (key_id == KEY_OPEN_APPLE) {
      mEventQueue.add(new KegsView.KeyKegsEvent(KEY_OPEN_APPLE, key_up));
      mask = STICKY_OPEN_APPLE;
    } else if (key_id == KEY_CLOSED_APPLE) {
      mEventQueue.add(new KegsView.KeyKegsEvent(KEY_CLOSED_APPLE, key_up));
      mask = STICKY_CLOSED_APPLE;
    }
    if (key_up) {
      mSticky &= ~mask;
    } else {
      mSticky |= mask;
    }
  }

  public void keyDownUp(int key_id) {
    mEventQueue.add(new KegsView.KeyKegsEvent(key_id, false));  // key down
    mEventQueue.add(new KegsView.KeyKegsEvent(key_id, true));   // key up
    resetStickyKeys();
  }
}
