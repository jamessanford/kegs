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
    public int x;
    public int y;
    public int buttons;
    public int buttons_valid;
  }

  protected ConcurrentLinkedQueue<KegsEvent> mEventQueue = new ConcurrentLinkedQueue<KegsEvent>();
  
  class KegsThread extends Thread {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private final ReentrantLock mSurfaceLock = new ReentrantLock();
    private final ReentrantLock mPauseLock = new ReentrantLock();
    private final Rect mRect = new Rect(0, 0, mA2Width, mA2Height);
//    private final Rect mDestRect = new Rect(0, 0, 800, 600);

    public KegsThread(SurfaceHolder surfaceHolder, Context context) {
      mSurfaceHolder = surfaceHolder;
      mContext = context;

      mBitmap = Bitmap.createBitmap(mA2Width, mA2Height,
                                    Bitmap.Config.ARGB_8888);
    }

    // Called by surfaceCreated also!
    public void updateScreen() {
      mSurfaceLock.lock();
      mCanvas = mSurfaceHolder.lockCanvas();  // Use mRect ?
      try {
        if(mCanvas != null) {
          mCanvas.drawARGB(255, 0, 0, 0);
          mCanvas.drawBitmap(mBitmap, 0, 0, null);
// scaling tests:
//          mCanvas.drawBitmap(mBitmap, mRect, mDestRect, null);
// Doesn't work well, but consider eliminating the border instead, for phones.
          mSurfaceHolder.unlockCanvasAndPost(mCanvas);
          mCanvas = null;
        }
      } finally {
        mSurfaceLock.unlock();
      }
    }

    void checkForPause() {
      if (mPaused) {
        mPauseLock.lock();
        // deadlock here until onResume.  Maybe not efficient.
        mPauseLock.unlock();
      }
    }

    public native void mainLoop(Bitmap b, ConcurrentLinkedQueue q);

    @Override
    public void run() {
      mainLoop(mBitmap, mEventQueue);
// For TESTING:
//      while (true) {
//        try {
//          Thread.sleep(100);
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
      if (mHaveSurface && mPaused) {
        mPaused = false;
        mPauseLock.unlock();
      }
      // otherwise, wait for the surface...
    }

    // Has the thread itself actually paused waiting to acquire the lock?
    public boolean nowPaused() {
      return mPauseLock.hasQueuedThreads();
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
    if (ready && !mReady && mHaveSurface) {
      thread.start();
    }
    mReady = ready;
  }

  public void surfaceCreated(SurfaceHolder holder) {
    mHaveSurface = true;
    if (!mReady) {
      return;  // bail out, start the thread later
    }
    thread.updateScreen();
    if (mPaused) {
      thread.onResume();
    } else {
      thread.start();
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    Log.w("kegs", "surfaceDestroyed");
    mHaveSurface = false;
    if (!mReady) {
      return;  // bail out, we never actually started
    }
    if (!mPaused) {
      thread.onPause();
    }
    while (!thread.nowPaused()) {
      // We are waiting for the thread to actually pause itself,
      // so that it won't be updating the canvas.
      try {
        Thread.sleep(18);  // 18ms == just over 1/60th of a second
      } catch (InterruptedException e) {}
    }
  }
}
