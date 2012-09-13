package com.froop.app.kegs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentLinkedQueue;

class KegsView extends SurfaceView implements SurfaceHolder.Callback {
  private static final int mA2Width = 640 + 32 + 32;   // kegs defcomm.h
  private static final int mA2Height = 400 + 32 + 30;  // kegs defcomm.h

  // Look also at mPauseLock.
  private boolean mHaveSurface = false;
  private boolean mPaused = false;
  private boolean mReady = false;

  protected ConcurrentLinkedQueue<Event.KegsEvent> mEventQueue = new ConcurrentLinkedQueue<Event.KegsEvent>();

  class KegsThread extends Thread {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private final ReentrantLock mSurfaceLock = new ReentrantLock();
    private final ReentrantLock mPauseLock = new ReentrantLock();
    private final Rect mRect = new Rect(0, 0, mA2Width, mA2Height);

    public KegsThread(SurfaceHolder surfaceHolder, Context context) {
      mSurfaceHolder = surfaceHolder;
      mContext = context;

      mBitmap = Bitmap.createBitmap(mA2Width, mA2Height,
                                    Bitmap.Config.ARGB_8888);
    }

    // Typically called by the native thread, but this can also be
    // called on the UI thread via setHaveSurface.
    protected void updateScreen() {
      if (!mHaveSurface) {
        return;
      }
      mSurfaceLock.lock();
      try {
        mCanvas = mSurfaceHolder.lockCanvas();  // Use mRect ?
        if(mCanvas != null) {
// Scaling tests: save/scale/restore, or drawBitmap into a destination rect.
//          mCanvas.save();
          mCanvas.drawARGB(255, 0, 0, 0);
//          mCanvas.scale(1.8f, 1.8f);
          mCanvas.drawBitmap(mBitmap, 0, 0, null);
//          mCanvas.restore();
// Doesn't work well, but consider eliminating the border instead, for phones.
          mSurfaceHolder.unlockCanvasAndPost(mCanvas);
          mCanvas = null;
        }
      } finally {
        mSurfaceLock.unlock();
      }
    }

    private void checkForPause() {
      if (mPaused) {
        mPauseLock.lock();
        // deadlock here until onResume.  Maybe not efficient.
        mPauseLock.unlock();
      }
    }

    private native void mainLoop(Bitmap b, ConcurrentLinkedQueue q);

    @Override
    public void run() {
      mainLoop(mBitmap, mEventQueue);
// For TESTING:
//      while (true) {
//        try {
//          Thread.sleep(500);
//          checkForPause();
//        } catch (InterruptedException e) {}
//      }
    }

    public void onPause() {
      if (!mReady) {
        return;  // bail out, we haven't started doing anything yet
      }
      if (!mPaused) {
        mPaused = true;
        mPauseLock.lock();
      }
    }

    public void onResume() {
      if (!mReady) {
        return;  // bail out, we haven't started doing anything yet
      }
      thread.updateScreen();
      if (mPaused) {
        mPaused = false;
        mPauseLock.unlock();
      } else if (!thread.isAlive()) {
        thread.start();
      }
    }

    // Has the thread itself actually paused waiting to acquire the lock?
    public boolean nowPaused() {
      return mPauseLock.hasQueuedThreads();
    }

    public void setHaveSurface(boolean haveSurface) {
      mSurfaceLock.lock();
      mHaveSurface = haveSurface;
      mSurfaceLock.unlock();

      if (haveSurface) {
        // Refresh the canvas when we obtain a surface.
        updateScreen();
      }
    }

    public void setSurfaceSize(int width, int height) {
// consider..     mSurfaceLock.lock(); update sizes and bitmap; unlock
    }

  }

  private Context mContext;
  private KegsThread thread;

  public KegsView(Context context, AttributeSet attrs) {
    super(context, attrs);

    SurfaceHolder holder = getHolder();
    holder.addCallback(this);

    thread = new KegsThread(holder, context);

    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
  }

  public void setEmulationSpeed(int speed) {
    // Speed matches g_limit_speed inside KEGS.
    // Instead of a separate "control" event, key ids with bit 8 high are
    // special events.  See android_driver.c:x_key_special()
    mEventQueue.add(new Event.KeyKegsEvent(speed + 0x80, true));
  }

  public KegsThread getThread() {
    return thread;
  }

  public ConcurrentLinkedQueue getEventQueue() {
    return mEventQueue;
  }

  @Override
  public void onWindowFocusChanged(boolean hasWindowFocus) {
// TODO: check to see if this is necessary, for example during alarms or phone calls.
//    if (!hasWindowFocus) {
//     thread.onPause();
//    } else {
//     thread.onResume();
//    }
  }

  public void surfaceChanged(SurfaceHolder holder,
                             int format, int width, int height) {
    thread.setSurfaceSize(width, height);
  }

  public native String stringFromJNI();

  public void setReady(boolean ready) {
    final boolean wasReady = mReady;
    mReady = ready;

    if (ready && !wasReady) {
      // Will start the thread if not already started.
      thread.onResume();
    }
  }

  // The surface callbacks are occasionally called in between pause and resume.

  public void surfaceCreated(SurfaceHolder holder) {
    thread.setHaveSurface(true);
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    thread.setHaveSurface(false);
  }
}
