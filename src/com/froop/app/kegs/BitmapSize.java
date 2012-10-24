package com.froop.app.kegs;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

class BitmapSize {
  static class Const {
    public static final int A2Width = 640 + 32 + 32;   // kegs defcomm.h
    public static final int A2Height = 400 + 32 + 30;  // kegs defcomm.h
  }
  private static final float LARGE_SCREEN_INCHES = 9.0f;

  private int mWidth = 0;
  private int mHeight = 0;
  private boolean mLargeScreen = false;

  private boolean mScaled = false;
  private boolean mCropped = false;
  private float mScaleFactorX = 1.0f;
  private float mScaleFactorY = 1.0f;

  public BitmapSize(int width, int height, DisplayMetrics display) {
    mWidth = width;
    mHeight = height;
    if (display != null) {
      mLargeScreen = isLargeScreen(display);
    }
    calculateScale(width, height);
  }

  private boolean isLargeScreen(DisplayMetrics display) {
    float a_side = (display.widthPixels / display.xdpi);
    float b_side = (display.heightPixels / display.ydpi);

    float a_squared = a_side * a_side;
    float b_squared = b_side * b_side;
    float diagonal = LARGE_SCREEN_INCHES * LARGE_SCREEN_INCHES;

    return (a_squared + b_squared > diagonal);
  }

  public boolean showActionBar() {
    if (mWidth < mHeight) {
      // portrait mode
      return true;
    } else if (mLargeScreen) {
      // 9 inches or more
      return true;
    } else {
      return false;
    }
  }

  public int getViewWidth() {
    return (int)(Const.A2Width * mScaleFactorX);
  }

  public int getViewHeight() {
    return (int)(getSuggestedHeightUnscaled() * mScaleFactorY);
  }

  public int getSuggestedHeightUnscaled() {
    if (!doCropBorder()) {
      return Const.A2Height;
    }
    if (400.0f * mScaleFactorY >= mHeight - 1.0f) {  // -1 in case it is 'near'
      return 400;
    }
    // How much of the uncropped image would actually fit on the display.
    return Math.min(Const.A2Height, (int)(mHeight / mScaleFactorY));
  }

  public boolean doCropBorder() {
    return mCropped;
  }

  public boolean isScaled() {
    return (mScaleFactorX != 1.0f || mScaleFactorY != 1.0f);
  }

  public float getScaleX() {
    return mScaleFactorX;
  }

  public float getScaleY() {
    return mScaleFactorY;
  }

  public Rect getRectSrc() {
    if (doCropBorder()) {
      return new Rect(0, 32, Const.A2Width, Const.A2Height);
    } else {
      return new Rect(0, 0, Const.A2Width, Const.A2Height);
    }
  }

  public Rect getRectDst() {
    if (doCropBorder()) {
      return new Rect(0, 0, Const.A2Width, Const.A2Height - 32);
    } else {
      return new Rect(0, 0, Const.A2Width, Const.A2Height);
    }
  }

  private void calculateScale(int width, int height) {
    float scaleX;
    float scaleY;
    boolean crop = false;

    // Force integer scaling on X axis.
    scaleX = (float)Math.round((width * 0.9) / 640);
    scaleX = Math.max(1, scaleX);
    scaleY = scaleX;

    if (height < Const.A2Height * scaleY) {
      // Scale it so that only the 400 pixels that matter take up the view.
      scaleY = Math.min(scaleX, height / 400.0f);

      // User should only use the 400 pixels that matter.
      crop = true;
    }

    // If Y would be compressed in a weird way, reduce the scale and use 1:1.
    if ((scaleX - scaleY) > 0.5) {
      scaleX = Math.max(1, scaleX - 1);
      scaleY = scaleX;

      // See whether we should crop it at that height.
      if (height < Const.A2Height * scaleY) {
        crop = true;
      } else {
        crop = false;
      }
    }

    mCropped = crop;
    mScaleFactorX = scaleX;
    mScaleFactorY = scaleY;
    Log.i("kegs", "using scale " + scaleX + ":" + scaleY + " crop=" + crop + " from screen " + width + "x" + height);
  }
}
