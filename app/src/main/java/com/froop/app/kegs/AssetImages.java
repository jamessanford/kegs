package com.froop.app.kegs;

import android.os.AsyncTask;

import java.io.File;

class AssetImages extends AsyncTask<Void, Void, Boolean> {
  interface AssetsReady {
    void onAssetsReady(boolean result);
  }

  private AssetsReady mNotify;
  private ConfigFile mConfigFile;

  AssetImages(AssetsReady notify, ConfigFile config) {
    mNotify = notify;
    mConfigFile = config;

    // An optimization to let the UI thread know as soon as possible.
    if (mNotify != null &&
        new File(mConfigFile.getImagePath(), "XMAS_DEMO.2MG").exists() &&
        new File(mConfigFile.getImagePath(), "System 6.hdv").exists()) {
      mNotify.onAssetsReady(true);
    }
  }

  private void checkOldImagePath(String filename) {
    final File oldPath = new File(mConfigFile.getConfigPath(), filename);
    final File newPath = new File(mConfigFile.getImagePath(), filename);
    if (oldPath != null && oldPath.exists()) {
      new File(mConfigFile.getImagePath()).mkdirs();
      oldPath.renameTo(newPath);
    }
  }

  protected void onPreExecute() {
    // We used to drop images directly into the config dir.
    // Make sure any images in the config dir get moved to the images dir.
    checkOldImagePath("XMAS_DEMO.2MG");
    checkOldImagePath("System 6.hdv");
  }

  public static boolean isAssetFilename(final String filename) {
    // We have to ignore any temporary files that we are working on, too.
    if (filename.equals("System 6.hdv") ||
        filename.equals("XMAS_DEMO.2MG") ||
        filename.equals("tmp_System 6.hdv") ||
        filename.equals("tmp_XMAS_DEMO.2MG")) {
      return true;
    }
    return false;
  }

  public static String translateTitle(final String title) {
    if (title.equals("System 6.hdv")) {
      return "System 6";
    } else if (title.equals("XMAS_DEMO.2MG")) {
      return "X-MAS Demo";
    } else {
      return title;
    }
  }

  protected Boolean doInBackground(Void... params) {
    mConfigFile.ensureAssetCopied(mConfigFile.getImagePath(), "XMAS_DEMO.2MG");
    mConfigFile.ensureAssetCopied(mConfigFile.getImagePath(),
                                  "System 6 Shareware.zip", "System 6.hdv");
    // TODO: could check to make sure they actually exist now.
    return true;
  }

  protected void onCancelled(final Boolean result) {
    mNotify.onAssetsReady(false);
  }

  protected void onPostExecute(final Boolean result) {
    mNotify.onAssetsReady(result);
  }
}
