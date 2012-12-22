package com.froop.app.kegs;

import android.util.Log;
import android.view.MotionEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

// Change touchscreen events into an absolute joystick position and
// a joystick button. Handles up to two fingers down.  The first one that moves
// becomes a joystick, the second acts as a button.  You can also fire a button
// by tapping a single finger.

// Quick and dirty.

class TouchJoystick {
  private ConcurrentLinkedQueue mEventQueue;
  private TouchSpecialZone mSpecialZone = null;
  private int mTouchSlop;

  private int mMotionPointer = -1;  // active pointer Id
  private int mButton1 = 0;        // buttons pressed?  lower two bits.
  private MotionEvent trackA;      // original A down event
  private MotionEvent trackB;      // original B down event
  private int trackAIndex = -1;    // index into trackA that pointerA is in
  private int trackBIndex = -1;    // index into trackB that pointerB is in
  private int pointerA = -1;       // pointer Id in 'A' slot
  private int pointerB = -1;       // pointer Id in 'B' slot

  public TouchJoystick(ConcurrentLinkedQueue q) {
    mEventQueue = q;

    mTouchSlop = android.view.ViewConfiguration.getTouchSlop();
// TODO: Avoid deprecated interface, read docs on replacement:
// final ViewConfiguration configuration = ViewConfiguration.get(context);
// mTouchSlop = configuration.getScaledTouchSlop();
  }

  public void setSpecialZone(TouchSpecialZone zone) {
    mSpecialZone = zone;
  }

  private void reset_tracks(boolean resetA, boolean resetB) {
    if (resetA && trackA != null) {
      trackA.recycle();
      trackA = null;
      trackAIndex = -1;
      pointerA = -1;
    }
    if (resetB && trackB != null) {
      trackB.recycle();
      trackB = null;
      trackBIndex = -1;
      pointerB = -1;
    }
  }

  private boolean isPastSlop(MotionEvent e1, MotionEvent e2,
                             int e1index, int e2index) {
//    Log.w("kegs", "SLOP " + " " + e1 + " " + e2);
    if (Math.abs(e2.getX(e2index) - e1.getX(e1index)) >= mTouchSlop) {
      return true;
    } else if (Math.abs(e2.getY(e2index) - e1.getY(e1index)) >= mTouchSlop) {
      return true;
    } else {
      return false;
    }
  }

  public boolean onTouchEvent(MotionEvent e) {
    final int action = e.getActionMasked();
    final int pointerIndex = e.getActionIndex();
    final int pointerId = e.getPointerId(pointerIndex);

//    Log.w("kegs", "touch " + action + " " + pointerId + " ##" + pointerA + "## " + trackA + " ##" + pointerB + "## " + trackB);

    if (action == MotionEvent.ACTION_DOWN ||
        action == MotionEvent.ACTION_POINTER_DOWN) {
      if (trackA != null && trackB != null) {
        // We are already tracking two fingers.
        return false;
      }
      if (action == MotionEvent.ACTION_POINTER_DOWN ||
          e.getPointerCount() > 1) {
        // we have two fingers down now, so might as well press the button.
        mButton1 = 1;
        mEventQueue.add(new Event.JoystickKegsEvent(0xFFFF, 0xFFFF, mButton1));
      }
      // start tracking it so that we can test it for SLOP
      if (trackA == null) {
        trackA = MotionEvent.obtain(e);
        trackAIndex = pointerIndex;
        pointerA = pointerId;
      } else {
        if (trackB != null) {
          trackB.recycle();
        }
        trackB = MotionEvent.obtain(e);
        trackBIndex = pointerIndex;
        pointerB = pointerId;
      }
    } else if (action == MotionEvent.ACTION_MOVE) {
      if (mMotionPointer == -1) {
        // No primary pointer is set, see if one has moved enough to be primary.
        if (pointerId == pointerA && isPastSlop(trackA, e, trackAIndex, pointerIndex)) {
          mMotionPointer = pointerId;
          if (trackB == null) {
            // In case the secondary pointer is now primary.
            mButton1 = 0;
            mEventQueue.add(new Event.JoystickKegsEvent(0xFFFF, 0xFFFF, mButton1));
          }
        } else if (pointerId == pointerB && isPastSlop(trackB, e, trackBIndex, pointerIndex)) {
          mMotionPointer = pointerId;
          if (trackA == null) {
            // In case the secondary pointer is now primary.
            mButton1 = 0;
            mEventQueue.add(new Event.JoystickKegsEvent(0xFFFF, 0xFFFF, mButton1));
          }
        }
      }
      if (pointerId == mMotionPointer) {
        // SEND JOYSTICK MOVEMENT based on track{A,B}
        if (pointerId == pointerA) {
          send_position(trackA, e, trackAIndex, pointerIndex);
        } else if (pointerId == pointerB) {
          send_position(trackB, e, trackBIndex, pointerIndex);
        } else {
          // probably an additional finger was down that we had latched onto, ignore it.
          //   Log.e("kegs", "movement for pointerId " + pointerId + " is unknown!");
        }
      }
    } else if (action == MotionEvent.ACTION_POINTER_UP
               || action == MotionEvent.ACTION_UP
               || action == MotionEvent.ACTION_CANCEL) {
      if (pointerId == mMotionPointer) {
        // recenter joystick
        // 0, 0 would be center, but it's "too perfect"
        mEventQueue.add(new Event.JoystickKegsEvent(61, -33, mButton1));
        mMotionPointer = -1;
      } else {
        if (mMotionPointer == -1) {
          // No active movement, assume this click/release should be a button press/release.
          // TODO it probably shouldn't be sent if they had their finger down for more than 500ms or so.
          if (mSpecialZone != null && !mSpecialZone.click(e, pointerId)) {
            mButton1 = 1;
            mEventQueue.add(new Event.JoystickKegsEvent(0xFFFF, 0xFFFF, mButton1));
          }
        }
        // SEND JOYSTICK BUTTON UP
        mButton1 = 0;
        mEventQueue.add(new Event.JoystickKegsEvent(0xFFFF, 0xFFFF, mButton1));
      }
      reset_tracks(pointerId == pointerA, pointerId == pointerB);
    }
    return true;
  }

  private void send_position(MotionEvent e1, MotionEvent e2,
                             int e1index, int e2index) {
    // Send absolute position, -32767 to 32767

    // TODO Still trying to find a good translation from distance to axis value.
    // Consider looking at DPI and at pressure changes.
    //   (Would like to capture just 'rolling the thumb')

    // Examples of things that didn't work so well...
    // (log8 - 1.3) * 42000 ; (log10 - 1.0) * 45000 ; (log5 - 1.7) * 50000
    //   final float x1 = e2.getX(e2index) - e1.getX(e1index);
    //   final int x2 = (int)(((Math.log(Math.abs(x1))/logVal) - 1.7) * 50000);
    //   final int x3 = Math.max(0, Math.min(32767, x2)) * (x1 < 0 ? -1 : 1);

    // Currently using linear over 18 pixels.  (32768/18)
    final float x1 = e2.getX(e2index) - e1.getX(e1index);
    final int x3 = (int)(x1 * 1820);

    final float y1 = e2.getY(e2index) - e1.getY(e1index);
    final int y3 = (int)(y1 * 1820);

//    Log.w("kegs", "joystick " + x3 + " " + y3 + " " + mButton1);
    mEventQueue.add(new Event.JoystickKegsEvent(x3, y3, mButton1));
  }
}
