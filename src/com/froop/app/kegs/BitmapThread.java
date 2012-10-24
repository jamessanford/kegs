package com.froop.app.kegs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import java.util.concurrent.locks.ReentrantLock;

class BitmapThread extends Thread {
  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      updateScreen();
    }
  };

  private SurfaceHolder mSurfaceHolder;
  private final ReentrantLock mSurfaceLock = new ReentrantLock();
  private Bitmap mBitmap;
  private Canvas mCanvas;
  private boolean mHaveSurface = false;
  private boolean mScaled = false;
  private float mScaleFactorX = 1.0f;
  private float mScaleFactorY = 1.0f;
  private Rect mRectSrc = new Rect(0, 0, 0, 0);
  private Rect mRectDst = new Rect(0, 0, 0, 0);

  private FpsCounter fpsCount = new FpsCounter("kegs", "thread");

  public void setBitmap(SurfaceHolder surfaceHolder, Bitmap bitmap) {
    mSurfaceHolder = surfaceHolder;
    mBitmap = bitmap;
  }

  public void run() {
    Looper.prepare();
    Looper.loop();
  }

  public Handler getHandler() {
    return mHandler;
  }

  public void updateScreen() {
    mSurfaceLock.lock();
    try {
      if (!mHaveSurface) {
        return;  // unlock with 'finally' clause
      }
      mCanvas = mSurfaceHolder.lockCanvas();  // Use Rect ?
      if(mCanvas != null) {
        if (!mScaled) {
          mCanvas.drawBitmap(mBitmap, mRectSrc, mRectDst, null);
        } else {
          mCanvas.save();
          mCanvas.scale(mScaleFactorX, mScaleFactorY);
          mCanvas.drawBitmap(mBitmap, mRectSrc, mRectDst, null);
          mCanvas.restore();
        }
        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        mCanvas = null;
      }
    } finally {
      mSurfaceLock.unlock();
// for testing
//      fpsCount.fps();
    }
  }

  public void updateScreenSize(BitmapSize bitmapSize) {
    // Keep our own copy of the size data, to give atomicity and
    // possibly help performance with fewer indirections.
    mSurfaceLock.lock();
    mScaled = bitmapSize.isScaled();
    mScaleFactorX = bitmapSize.getScaleX();
    mScaleFactorY = bitmapSize.getScaleY();
    mRectSrc = new Rect(bitmapSize.getRectSrc());
    mRectDst = new Rect(bitmapSize.getRectDst());
    mSurfaceLock.unlock();
    updateScreen();  // NOTE: UI thread.
  }

  public void setHaveSurface(boolean haveSurface) {
    mSurfaceLock.lock();
    mHaveSurface = haveSurface;
    mSurfaceLock.unlock();

    if (haveSurface) {
      // Refresh the canvas when we obtain a surface.
      updateScreen();  // NOTE: UI thread.
    }
  }
}
