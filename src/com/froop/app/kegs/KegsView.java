package com.froop.app.kegs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

class KegsView extends SurfaceView implements KegsThread.UpdateScreen, SurfaceHolder.Callback {
  // Reported area of this view, see updateScreenSize()
  private int mWidth = 0;
  private int mHeight = 0;

  private Bitmap mBitmap;
  private final BitmapThread mBitmapThread = new BitmapThread();
  private Handler mHandler;

  public KegsView(Context context, AttributeSet attrs) {
    super(context, attrs);

    SurfaceHolder holder = getHolder();
    holder.addCallback(this);

    Bitmap bitmap = Bitmap.createBitmap(BitmapSize.Const.A2Width,
                                        BitmapSize.Const.A2Height,
                                        Bitmap.Config.RGB_565);
    mBitmap = bitmap;

    mBitmapThread.setBitmap(holder, bitmap);
    mHandler = mBitmapThread.getHandler();
    mBitmapThread.start();

    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(mWidth, mHeight);
  }

  private FpsCounter fpsCount = new FpsCounter("kegs", "native");

  public Bitmap getBitmap() {
    return mBitmap;
  }

  // Typically updateScreen is called by the native thread,
  // but it may also be run on the UI thread.
  //
  // We use a Handler to tell the bitmap thread to actually draw
  // on the canvas.  No locking is involved, so it is possible for
  // the canvas to get a bitmap that is in the process of being updated
  // by the native thread.  This should be relatively uncommon.
  //
  // If you wish to draw to the canvas in the native thread, it should
  // be safe to bypass the Handler and call mBitmapThread.updateScreen()
  // here instead.
  public void updateScreen() {
    // Empty the queue first in case bitmap thread is lagging behind.
    mHandler.removeMessages(0);
    mHandler.sendEmptyMessage(0);
    fpsCount.fps();
  }

  public void updateScreenSize(BitmapSize bitmapSize) {
    mWidth = bitmapSize.getViewWidth();
    mHeight = bitmapSize.getViewHeight();
    requestLayout();
    mBitmapThread.updateScreenSize(bitmapSize);
  }

  public void surfaceChanged(SurfaceHolder holder,
                             int format, int width, int height) {
  }

  // The surface callbacks are occasionally called in between pause and resume.

  public void surfaceCreated(SurfaceHolder holder) {
    mBitmapThread.setHaveSurface(true);
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    mBitmapThread.setHaveSurface(false);
  }
}
