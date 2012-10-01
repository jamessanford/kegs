package com.froop.app.kegs;

import android.util.Log;

class FpsCounter {
  private String mName;
  private String mIdent;
  private long fpsLast = System.currentTimeMillis() + 1000;
  private int fpsCount = 0;

  FpsCounter(String logName, String ident) {
    mName = logName;
    mIdent = "fps " + ident + " ";
  }

  public void fps() {
    fpsCount += 1;
    long fpsNow = System.currentTimeMillis();
    if (fpsNow > fpsLast) {
      Log.w(mName, mIdent + fpsCount);
      fpsLast = fpsNow + 1000;
      fpsCount = 0;
    }
  }
}
