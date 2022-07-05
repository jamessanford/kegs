package com.froop.app.kegs;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

class TouchSpecialZone {
  // Clicks within this rect have a special handler.
  private Rect mSpecialRect = null;

  public TouchSpecialZone(Rect zone) {
    mSpecialRect = zone;  // may be null
  }

  // Override this.
  public void activate() {}

  public boolean click(MotionEvent point, int index) {
    if (mSpecialRect != null) {
      final int x = (int)point.getX(index);
      final int y = (int)point.getY(index);
      if (mSpecialRect.contains(x, y)) {
        activate();
        return true;
      }
    }
    return false;
  }
}
