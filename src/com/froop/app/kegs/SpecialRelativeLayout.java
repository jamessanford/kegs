package com.froop.app.kegs;

import android.widget.RelativeLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

// A RelativeLayout that sends notifications when its screen size changes.

class SpecialRelativeLayout extends RelativeLayout {
  public SpecialRelativeLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  interface NotifySizeChanged {
    void onSizeChanged(int w, int h, int oldw, int oldh);
  }

  private NotifySizeChanged mNotify;
  private int mWidth = 0;
  private int mHeight = 0;

  public void setNotifySizeChanged(NotifySizeChanged notify) {
    mNotify = notify;

    // In case the call had come in earlier.
    if (notify != null && mWidth != 0 && mHeight != 0) {
      mNotify.onSizeChanged(mWidth, mHeight, 0, 0);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;
    if (mNotify != null) {
      mNotify.onSizeChanged(w, h, oldw, oldh);
    }
  }
}
