package com.froop.app.kegs;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

class KegsTouch extends GestureDetector.SimpleOnGestureListener {
  private ConcurrentLinkedQueue mEventQueue;
  private int mButton1 = 0;

  public KegsTouch(ConcurrentLinkedQueue q) {
    mEventQueue = q;
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2,
                         float velocityX, float velocityY) {
// move mouse cursor
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2,
                          float distanceX, float distanceY) {
    int changeX = (int)distanceX * -1;
    int changeY = (int)distanceY * -1;
    mEventQueue.add(
        new KegsView.MouseKegsEvent(changeX, changeY, mButton1, 1));
//    if (mButton1 == 1 && e2.getAction() == MotionEvent.ACTION_UP) {
//      mButton1 = 0;
//      mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
//    }
    return true;
  }

  @Override
  public void onLongPress(MotionEvent e) {
// press mouse button down
//    Log.e("kegs", "onlongpress");
    mButton1 = 1;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
// press mouse button down, then up
    mButton1 = 1;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
    mButton1 = 0;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
    return true;
  }

  @Override
  public boolean onDoubleTap(MotionEvent e) {
    mButton1 = 1;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
    mButton1 = 0;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
    mButton1 = 1;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
    mButton1 = 0;
    mEventQueue.add(new KegsView.MouseKegsEvent(0, 0, mButton1, 1));
    return true;
  }
}
