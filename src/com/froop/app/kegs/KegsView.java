package com.froop.app.kegs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentLinkedQueue;

// This is the primary interface into the native KEGS thread, and also
// where the native KEGS thread calls back into Java.

class KegsView extends SurfaceView implements SurfaceHolder.Callback {
  // Reported area of this view, see updateScreenSize()
  private int mWidth = 0;
  private int mHeight = 0;

  // Look also at mPauseLock.
  private boolean mPaused = false;
  private boolean mReady = false;     // 'true' will begin the native thread.

  protected ConcurrentLinkedQueue<Event.KegsEvent> mEventQueue = new ConcurrentLinkedQueue<Event.KegsEvent>();

  private final BitmapThread mBitmapThread = new BitmapThread();

  class KegsThread extends Thread {
    private Handler mHandler;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private final ReentrantLock mPauseLock = new ReentrantLock();

    public KegsThread(SurfaceHolder surfaceHolder, Context context) {
      mSurfaceHolder = surfaceHolder;
      mContext = context;

      mBitmap = Bitmap.createBitmap(BitmapSize.Const.A2Width,
                                    BitmapSize.Const.A2Height,
                                    Bitmap.Config.RGB_565);
      mBitmap.setHasAlpha(false);

      mBitmapThread.setBitmap(surfaceHolder, mBitmap);
      mHandler = mBitmapThread.getHandler();
    }

    private FpsCounter fpsCount = new FpsCounter("kegs", "native");

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
    protected void updateScreen() {
      // Empty the queue first in case bitmap thread is lagging behind.
      mHandler.removeMessages(0);
      mHandler.sendEmptyMessage(0);
      fpsCount.fps();
    }

    private void checkForPause() {
      if (mPaused) {
        mPauseLock.lock();
        // deadlock here until onResume.  Maybe not efficient.
        mPauseLock.unlock();
      }
    }

    // See jni/android_driver.c:mainLoop()
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
      updateScreen();
      if (mPaused) {
        mPaused = false;
        mPauseLock.unlock();
      } else if (!thread.isAlive()) {
        thread.start();
        mBitmapThread.start();
      }
    }

    // Has the thread itself actually paused waiting to acquire the lock?
    public boolean nowPaused() {
      return mPauseLock.hasQueuedThreads();
    }
  }

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

  public void doWarmReset() {
    // Press keys down.
    mEventQueue.add(new Event.KeyKegsEvent(KegsKeyboard.KEY_OPEN_APPLE, false));
    mEventQueue.add(new Event.KeyKegsEvent(KegsKeyboard.KEY_CONTROL, false));
    mEventQueue.add(new Event.KeyKegsEvent(KegsKeyboard.KEY_RESET, false));
    // Release reset key first, then the others.
    mEventQueue.add(new Event.KeyKegsEvent(KegsKeyboard.KEY_RESET, true));
    mEventQueue.add(new Event.KeyKegsEvent(KegsKeyboard.KEY_CONTROL, true));
    mEventQueue.add(new Event.KeyKegsEvent(KegsKeyboard.KEY_OPEN_APPLE, true));
  }

  public KegsThread getThread() {
    return thread;
  }

  public ConcurrentLinkedQueue getEventQueue() {
    return mEventQueue;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(mWidth, mHeight);
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
