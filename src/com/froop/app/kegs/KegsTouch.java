package com.froop.app.kegs;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import java.util.concurrent.ConcurrentLinkedQueue;

// Translate onTouchEvent calls into mouse pointer movement.

class KegsTouch {
  private ConcurrentLinkedQueue mEventQueue;
  private int mButton1 = 0;
  private int mTouchSlopSquare;

  private int mPrimaryId = -1;    // mouse movement
  private int mSecondaryId = -1;  // button presses

  private boolean mPrimaryPastSlop = false;
  private boolean mPrimaryLongPress = false;
  private int mPrimaryX = -1;  // original X
  private int mPrimaryY = -1;  // original Y
  private int mPrimaryLastX = -1;  // last seen X
  private int mPrimaryLastY = -1;  // last seen Y

  private int mSecondaryX = -1;  // in case of promotion to primary
  private int mSecondaryY = -1;  // in case of promotion to primary

  private static final int LONG_PRESS = 1;
  private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

  private TouchSpecialZone mSpecialZone = null;

  public KegsTouch(Context context, ConcurrentLinkedQueue q) {
    mEventQueue = q;

    final ViewConfiguration configuration = ViewConfiguration.get(context);
    int touchSlop = configuration.getScaledTouchSlop();
    mTouchSlopSquare = touchSlop * touchSlop;
  }

  public void setSpecialZone(TouchSpecialZone zone) {
    mSpecialZone = zone;
  }

  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      if (msg.what == LONG_PRESS) {
        if (mPrimaryId != -1) {
          mPrimaryLongPress = true;
          mButton1 = 1;
          mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
        }
      }
    }
  };

  // For promotion of secondary to primary.  Crazy stuff.
  private boolean checkSecondarySlop(MotionEvent newPoint, int index) {
    final int deltaX = (int)newPoint.getX(index) - mSecondaryX;
    final int deltaY = (int)newPoint.getY(index) - mSecondaryY;
    final int distance = (deltaX * deltaX) + (deltaY * deltaY);
    if (distance > mTouchSlopSquare) {
      return true;
    } else {
      return false;
    }
  }

  private boolean checkPrimarySlop(MotionEvent newPoint, int index) {
    if (mPrimaryPastSlop) {
      return true;
    }

    final int deltaX = (int)newPoint.getX(index) - mPrimaryX;
    final int deltaY = (int)newPoint.getY(index) - mPrimaryY;
    final int distance = (deltaX * deltaX) + (deltaY * deltaY);
    if (distance > mTouchSlopSquare) {
      mPrimaryPastSlop = true;
      return true;
    } else {
      return false;
    }
  }

  public boolean onTouchEvent(MotionEvent event) {
    final int action = event.getActionMasked();
    final int pointerIndex = event.getActionIndex();
    final int pointerId = event.getPointerId(pointerIndex);

    if (action == MotionEvent.ACTION_DOWN ||
        action == MotionEvent.ACTION_POINTER_DOWN) {
      if (mPrimaryId == -1 && mSecondaryId != pointerId) {
        // First new finger down becomes movement.
        mPrimaryId = pointerId;
        mPrimaryX = (int)event.getX(pointerIndex);
        mPrimaryY = (int)event.getY(pointerIndex);
        mPrimaryLastX = mPrimaryX;
        mPrimaryLastY = mPrimaryY;
        mPrimaryPastSlop = false;
        mPrimaryLongPress = false;

        // Track for long presses.
        mHandler.removeMessages(LONG_PRESS);
        mHandler.sendEmptyMessageDelayed(LONG_PRESS, LONG_PRESS_TIMEOUT);
      } else {
        // Any subequent fingers become the mouse button.
        mSecondaryId = pointerId;
        mSecondaryX = (int)event.getX(pointerIndex);
        mSecondaryY = (int)event.getY(pointerIndex);
        mButton1 = 1;
        mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
      }
      return true;
    } else if (action == MotionEvent.ACTION_UP ||
               action == MotionEvent.ACTION_POINTER_UP) {
      if (mPrimaryId == pointerId) {
        mHandler.removeMessages(LONG_PRESS);
        if (mPrimaryLongPress) {
          if (mSecondaryId == -1) {
            // If the other finger is down, let it take over the mouse button.
            mButton1 = 0;
            mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
          }
        } else if (!checkPrimarySlop(event, pointerIndex)) {
          // It didn't move while it was down, so send a click event.
          if (mSpecialZone != null &&
              !mSpecialZone.click(event, pointerIndex)) {
            mButton1 = 1;
            mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
            mButton1 = 0;
            mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
          }
        }
        mPrimaryId = -1;
        mPrimaryPastSlop = false;
        mPrimaryLongPress = false;
        return true;
      } else if (mSecondaryId == pointerId) {
        // Release mouse button.
        mButton1 = 0;
        mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
        mSecondaryId = -1;
        mSecondaryX = -1;
        mSecondaryY = -1;
        return true;
      }
    } else if (action == MotionEvent.ACTION_CANCEL) {
      if (pointerId == mPrimaryId) {
        mHandler.removeMessages(LONG_PRESS);
        if (mPrimaryLongPress && mSecondaryId == -1 && (mButton1 & 1) == 1) {
          mButton1 = 0;
          mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
        }
        mPrimaryId = -1;
        mPrimaryPastSlop = false;
        mPrimaryLongPress = false;
        return true;
      } else if (pointerId == mSecondaryId) {
        mSecondaryId = -1;
        mSecondaryX = -1;
        mSecondaryY = -1;
        if ((mButton1 & 1) == 1) {
          mButton1 = 0;
          mEventQueue.add(new Event.MouseKegsEvent(0, 0, mButton1, 1));
        }
        return true;
      }
    } else if (action == MotionEvent.ACTION_MOVE) {
      // This doesn't use getActionIndex(), we need to check the entire event.
      int moveIndex;
      int moveId;
      for(moveIndex = 0; moveIndex < event.getPointerCount(); moveIndex++) {
        moveId = event.getPointerId(moveIndex);

        if (moveId == mSecondaryId && mPrimaryId != -1 && !mPrimaryPastSlop && mSecondaryX != -1 && mSecondaryY != -1) {
          // If the secondary goes past slop now, swap primary and secondary...
          // This allows you to click with one finger, and then drag the next
          // and that drag becomes the primary finger.
          if (checkSecondarySlop(event, moveIndex)) {
            final int oldPrimary = mPrimaryId;
            mPrimaryId = mSecondaryId;
            mSecondaryId = oldPrimary;
            mPrimaryPastSlop = true;
            mPrimaryX = mSecondaryX;
            mPrimaryY = mSecondaryY;
            mPrimaryLastX = mSecondaryX;
            mPrimaryLastY = mSecondaryY;
            mSecondaryX = -1;
            mSecondaryY = -1;
          }
          // Let this fall through to process the event.
        }
        if (moveId == mPrimaryId) {
          if (checkPrimarySlop(event, moveIndex)) {
            mHandler.removeMessages(LONG_PRESS);
            final int currentX = (int)event.getX(moveIndex);
            final int currentY = (int)event.getY(moveIndex);
            final int changeX = currentX - mPrimaryLastX;
            final int changeY = currentY - mPrimaryLastY;
            mPrimaryLastX = currentX;
            mPrimaryLastY = currentY;
            mEventQueue.add(new Event.MouseKegsEvent(changeX, changeY, mButton1, 1));
            // Once we have had an active primary, don't allow promotions.
            mSecondaryX = -1;
            mSecondaryY = -1;
          }
        }
      }
      return true;  // ACTION_MOVE
    }
    return false;
  }
}
