package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.util.Log;
import android.os.AsyncTask;

class AssetImages extends AsyncTask<Void, Void, Boolean> {
  interface AssetsReady {
    void onAssetsReady(boolean success);
  }

  private KegsMain mContext;
  private ConfigFile mConfigFile;

  AssetImages(KegsMain context, ConfigFile config) {
    mContext = context;
    mConfigFile = config;
  }

  protected void onPreExecute() {
  }

  protected Boolean doInBackground(Void... params) {
    mConfigFile.ensureAssetCopied("XMAS_DEMO.2MG");
    mConfigFile.ensureAssetCopied("System 6 Shareware.zip",
                                  "System 6 and Free Games.hdv");
    // TODO: could check to make sure they actually exist now.

// For testing:
//    try { Thread.sleep(20000); } catch (InterruptedException e) {}

    return true;
  }

  protected void onCancelled(final Boolean success) {
    mContext.runOnUiThread(new Runnable() {
      public void run() { mContext.onAssetsReady(false); }
    });
  }

  protected void onPostExecute(final Boolean success) {
    mContext.runOnUiThread(new Runnable() {
      public void run() { mContext.onAssetsReady(success); }
    });
  }
}
