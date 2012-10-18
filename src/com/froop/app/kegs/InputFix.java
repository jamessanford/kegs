package com.froop.app.kegs;

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

// This is a bug workaround so that we see KEYCODE_DEL on API 16 JELLY_BEAN
// and later.  Let me know what the "right" way of doing this is!

class InputFix {
  public static InputConnection getInputConnection(View view, EditorInfo attrs) {
    attrs.inputType = EditorInfo.TYPE_NULL;
    attrs.imeOptions |= (EditorInfo.IME_FLAG_NO_EXTRACT_UI |
                         EditorInfo.IME_FLAG_NO_ENTER_ACTION |
                         EditorInfo.IME_ACTION_NONE);

    // Change defaults so that the IME makes the API calls.
    attrs.initialSelStart = 0;
    attrs.initialSelEnd = 0;

    InputConnection ic = new BaseInputConnection(view, false) {
      @Override
      public boolean deleteSurroundingText(int before, int after) {
        if (before == 1 && after == 0) {
          // In API 16 JELLY_BEAN, the IME no longer sends KEYCODE_DEL
          // for 'fake' editors like us.  Force it to send the code
          // instead of this "delete one character" request.
          //
          // There must be a better way to handle input rather than
          // providing an entire InputConnection or hacking it like this.
          final long eventTime = SystemClock.uptimeMillis();
          final int keyCode = KeyEvent.KEYCODE_DEL;

          sendKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN,
            keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
            KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE));
          sendKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP,
            keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
            KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE));
          return true;
        } else {
          return super.deleteSurroundingText(before, after);
        }
      }
    };
    return ic;
  }
}
