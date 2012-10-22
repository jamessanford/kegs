package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.util.Log;
import android.content.Context;
import android.os.AsyncTask;

class AssetImages extends AsyncTask<Void, Void, Boolean> {
  private ConfigFile mConfigFile;

  AssetImages(ConfigFile config) {
    mConfigFile = config;
  }

  protected void onPreExecute() {
  }

  protected Boolean doInBackground(Void... params) {
    mConfigFile.ensureAssetCopied("XMAS_DEMO.2MG");
    mConfigFile.ensureAssetCopied("System 6 Shareware.zip",
                                  "System 6 and Free Games.hdv");
    // TODO: could check to make sure they actually exist now.
    return true;
  }

  protected void onCancelled(Boolean success) {
// post message, runnable, ...tell it about failure
  }

  protected void onPostExecute(Boolean success) {
// post message, runnable, ...tell it to continue (or fail)
  }
}
