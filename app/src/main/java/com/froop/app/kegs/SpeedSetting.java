package com.froop.app.kegs;

import android.util.Log;
import java.util.concurrent.ConcurrentLinkedQueue;

// Ugh.

class SpeedSetting {
  private ConcurrentLinkedQueue mEventQueue;
  private int mSpeedLimit;  // 0 = unlimited, 1=1mhz, 2=2.8mhz, 3=zipgs
  private int mSpeedZip;    // 0-15, zipgs, 0=16/16ths, 8=8/16th, 15=1/16th

  public SpeedSetting(ConcurrentLinkedQueue q,
                      int g_limit_speed, int g_zipgs_reg_c05a) {
    mEventQueue = q;
    mSpeedLimit = g_limit_speed;
    mSpeedZip = (g_zipgs_reg_c05a & 0xf0) >> 4;
  }

  public int getMenuItem() {
    // Map g_limit_speed to R.array.speed_choices
    int item = mSpeedLimit - 1;
    if (item < 0) {
      item = 5;
    }

    if (mSpeedLimit == 3 && mSpeedZip < 0x08) {
      item++;  // Bump from 8mhz item to 16mhz item
    }

    return item;
  }

  public void setFromMenuItem(int item) {
    // Map R.array.speed_choices to g_limit_speed
    if (item == 2) {
      mSpeedZip = 0x08;  // limit zip to 8/16ths (8mhz)
    } else {
      mSpeedZip = 0x00;  // default no zip limit
    }

    mSpeedLimit = item + 1;
    if (mSpeedLimit >= 5) {
      mSpeedLimit = 0;
    } else if (mSpeedLimit == 4) {
      mSpeedLimit = 3;  // turn 16mhz setting into "zipgs" speed
    }
    sendUpdateEvent();
  }

  private void sendUpdateEvent() {
    if (mEventQueue != null) {
      // Instead of a separate "control" event, key ids with bit 8 high are
      // special events.  See android_driver.c:x_key_special()
      Log.w("kegs", "updating limit to " + mSpeedLimit + " " + mSpeedZip);
      mEventQueue.add(new Event.KeyKegsEvent(0x80 + mSpeedLimit, true));
      mEventQueue.add(new Event.KeyKegsEvent(0x80 + 0x10 + mSpeedZip, true));
    }
  }
}
