package com.froop.app.kegs;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class KegsMain extends Activity implements KegsKeyboard.StickyReset, AssetImages.AssetsReady {
  private static final String FRAGMENT_ROM = "rom";
  private static final String FRAGMENT_DOWNLOAD = "download";
  private static final String FRAGMENT_ERROR = "error";
  private static final String FRAGMENT_SPEED = "speed";
  private static final String FRAGMENT_DISKIMAGE = "diskimage";
  private static final String FRAGMENT_ASSET = "asset";

  private ConfigFile mConfigFile;
  private KegsThread mKegsThread;

  // For the software renderer, use 'KegsView' here and in res/layout/main.xml
  //   Also consider undef ANDROID_GL in jni/android_driver.c
  protected KegsViewGL mKegsView;
  private KegsTouch mKegsTouch;
  private KegsKeyboard mKegsKeyboard;
  private TouchJoystick mJoystick;

  private boolean mAssetsReady = false;
  private boolean mModeMouse = true;

  private long mScreenSizeTime = 0;

  private View.OnClickListener mButtonClick = new View.OnClickListener() {
    public void onClick(View v) {
      final int click_id = v.getId();
      int key_id = -1;
      boolean sticky = false;
      if (click_id == R.id.key_escape) {
        key_id = KegsKeyboard.KEY_ESCAPE;
      } else if (click_id == R.id.key_return) {
        key_id = KegsKeyboard.KEY_RETURN;
      } else if (click_id == R.id.key_tab) {
        key_id = KegsKeyboard.KEY_TAB;
      } else if (click_id == R.id.key_control) {
        key_id = KegsKeyboard.KEY_CONTROL;
        sticky = true;
      } else if (click_id == R.id.key_open_apple) {
        key_id = KegsKeyboard.KEY_OPEN_APPLE;
        sticky = true;
      } else if (click_id == R.id.key_closed_apple) {
        key_id = KegsKeyboard.KEY_CLOSED_APPLE;
        sticky = true;
      } else if (click_id == R.id.key_left) {
        key_id = KegsKeyboard.KEY_LEFT;
      } else if (click_id == R.id.key_right) {
        key_id = KegsKeyboard.KEY_RIGHT;
      } else if (click_id == R.id.key_up) {
        key_id = KegsKeyboard.KEY_UP;
      } else if (click_id == R.id.key_down) {
        key_id = KegsKeyboard.KEY_DOWN;
      } else {
        Log.e("kegs", "UNKNOWN BUTTON " + click_id + " CLICKED");
      }
      if (key_id != -1) {
        if (sticky) {
          mKegsKeyboard.keyDownSticky(key_id, !((ToggleButton)v).isChecked());
        } else {
          mKegsKeyboard.keyDownUp(key_id);
        }
      }
    }
  };

  private boolean areControlsVisible() {
    return findViewById(R.id.b1).getVisibility() == View.VISIBLE;
  }

  public void onStickyReset() {
    ((ToggleButton)findViewById(R.id.key_control)).setChecked(false);
    ((ToggleButton)findViewById(R.id.key_open_apple)).setChecked(false);
    ((ToggleButton)findViewById(R.id.key_closed_apple)).setChecked(false);
  }

  public void onAssetsReady(boolean success) {
    mAssetsReady = success;
  }

  private void loadConfigWhenReady(final String configfile) {
    final AssetFragment frag = (AssetFragment)getFragmentManager().findFragmentByTag(FRAGMENT_ASSET);

    if (!getThread().nowWaitingForPowerOn() || !mAssetsReady) {
      if (frag == null && !mAssetsReady) {
        // Only the asset part will take time, so only show the dialog
        // when waiting for the asset.
        final DialogFragment assetProgress = new AssetFragment();
        assetProgress.show(getFragmentManager(), FRAGMENT_ASSET);
      }
      mKegsView.postDelayed(new Runnable() {
        public void run() { loadConfigWhenReady(configfile); }
      }, 100);
    } else {
      if (frag != null) {
        frag.dismiss();
      }
      mConfigFile.internalConfig(configfile);
      getThread().allowPowerOn();
    }
  }

  protected void loadConfig(String configfile) {
    getThread().doPowerOff();
    loadConfigWhenReady(configfile);
  }

  protected void getRomFile(String romfile) {
    final DialogFragment download = new DownloadDialogFragment();
    download.show(getFragmentManager(), FRAGMENT_DOWNLOAD);
    new DownloadRom().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, romfile);
  }

  class DownloadRom extends AsyncTask<String, Void, Boolean> {
    private String mRomfile;
    protected Boolean doInBackground(String ... raw_romfile) {
      mRomfile = raw_romfile[0];
      return new DownloadHelper().save(
          "http://jsan.co/KEGS/" + mRomfile,
          mConfigFile.getConfigPath() + "/" + mRomfile);
    }
    protected void onPostExecute(Boolean success) {
      final DialogFragment frag = (DialogFragment)getFragmentManager().findFragmentByTag(FRAGMENT_DOWNLOAD);
      if (frag != null) {
        frag.dismiss();
      }
      if (!success) {
        if (!isCancelled()) {
          final DialogFragment dialog = new ErrorDialogFragment();
          dialog.show(getFragmentManager(), FRAGMENT_ERROR);
        }
      } else {
        mConfigFile.defaultConfig();
        getThread().setReady(true);
        mKegsView.postDelayed(new Runnable() { public void run() {
          new DiskImageFragment().show(getFragmentManager(), FRAGMENT_DISKIMAGE);
        } }, 1000);
      }
    }
  }

  class DownloadDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      ProgressDialog dialog = new ProgressDialog(getActivity());
      // TODO: should probably use an XML layout for this.
      dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      dialog.setMessage(getResources().getText(R.string.rom_check));
      dialog.setProgressNumberFormat(null);
      if (android.os.Build.VERSION.SDK_INT >= 11) {
        dialog.setProgressPercentFormat(null);
      }
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
      dialog.setCanceledOnTouchOutside(false);
      return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
      super.onCancel(dialog);
      finish();
    }
  }

  class ErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage(getResources().getText(R.string.rom_error));
// TODO setCanceledOnTouchOutside(false) ?  otherwise can accidentally dismiss the error.
      return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
      super.onCancel(dialog);
      finish();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
      super.onDismiss(dialog);
      finish();
    }
  }

  class AssetFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      ProgressDialog dialog = new ProgressDialog(getActivity());
      // TODO: should probably use an XML layout for this.
      dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      dialog.setMessage(getResources().getText(R.string.asset_loading));
      dialog.setProgressNumberFormat(null);
      if (android.os.Build.VERSION.SDK_INT >= 11) {
        dialog.setProgressPercentFormat(null);
      }
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
      dialog.setCanceledOnTouchOutside(false);
      return dialog;
    }
  }

  public KegsThread getThread() {
    return mKegsThread;
  }

  private void updateActionBar(boolean showActionBar) {
    final ActionBar actionBar = getActionBar();
    if (showActionBar) {
      if (actionBar != null && !actionBar.isShowing()) {
        actionBar.show();
      }
    } else {
      if (actionBar != null && actionBar.isShowing()) {
        actionBar.hide();
      }
    }
  }

  private void updateScreenSize(int width, int height, long sent) {
    if (mScreenSizeTime != sent) {
      // There's a newer event coming soon, wait for it.
      //
      // This is a bug workaround.  We need to wait for the size to settle
      // before acting on it.   If we update *immediately* it seems to
      // confuse Android.  If we update multiple times, Android creates
      // completely screwed up animations during the rotation.
      //
      // So, we mark mScreenSize with a message time and tell it to update
      // 250ms later.  We only want to act on the last update that is sent,
      // but we can't easily cancel the fact that the message is being sent,
      // so bail out here if the time doesn't match the last message.
      return;
    }
    final BitmapSize bitmapSize = new BitmapSize(width, height);

    updateActionBar(bitmapSize.showActionBar());

    mKegsView.updateScreenSize(bitmapSize);

    // Force another redraw of the bitmap into the canvas.  Bug workaround.
    getThread().updateScreen();
  }

  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    // Fire off a guess at a new size during this request,
    // it makes the animation transition look better.
    int width = getResources().getDisplayMetrics().widthPixels;
    int height = getResources().getDisplayMetrics().heightPixels;
    if (android.os.Build.VERSION.SDK_INT >= 11) {
      // NOTE: 48 is a guess at the System Bar obstruction.
      // These are 'visible insets' into the display from the window manager.
      height -= 48;
    }
    final BitmapSize bitmapSize = new BitmapSize(width, height);
    updateActionBar(bitmapSize.showActionBar());
    mKegsView.updateScreenSize(bitmapSize);
    getThread().updateScreen();
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    return mKegsKeyboard.keyEvent(event) || super.dispatchKeyEvent(event);
  }

//  @Override
//  public boolean dispatchGenericMotionEvent(MotionEvent event) {
//    // Joystick!  if ((event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK) != 0 && event.getAction() == MotionEvent.ACTION_MOVE) {}
//    // See also GameControllerInput.java from ApiDemos
//    return super.dispatchGenericMotionEvent(event);
//  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // BUG: no overflow menu on devices with menu button
    // BUG: when action bar is hidden, menu bar only shows overflow items
    getMenuInflater().inflate(R.menu.actions, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    // remember to call invalidateOptionsMenu() for this to be run
    MenuItem item;
    item = menu.findItem(R.id.action_joystick);
    if (item != null) {
      item.setIcon(mModeMouse ? R.drawable.ic_bt_misc_hid : R.drawable.ic_bt_pointing_hid);
      item.setTitle(mModeMouse ? R.string.input_joystick : R.string.input_mouse);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Action bar was clicked.
    final int item_id = item.getItemId();
    if (item_id == R.id.action_keyboard) {
      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      if (imm != null) {
        imm.toggleSoftInput(0, 0);
      }
      return true;
    } else if (item_id == R.id.action_speed) {
      new SpeedFragment().show(getFragmentManager(), FRAGMENT_SPEED);
      return true;
    } else if (item_id == R.id.action_joystick) {
      mModeMouse = !mModeMouse;
      invalidateOptionsMenu();  // update icon
      return true;
    } else if (item_id == R.id.action_diskimage) {
      new DiskImageFragment().show(getFragmentManager(), FRAGMENT_DISKIMAGE);
      return true;
    } else if (item_id == R.id.action_more_keys) {
      final int vis = areControlsVisible() ? View.GONE : View.VISIBLE;
      findViewById(R.id.b1).setVisibility(vis);
      findViewById(R.id.b2).setVisibility(vis);
      findViewById(R.id.b3).setVisibility(vis);
      return true;
    } else if (item_id == R.id.action_power_off) {
      getThread().doPowerOff();
      finish();
    }
    return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mKegsView = (KegsViewGL)findViewById(R.id.kegsview);

    mConfigFile = new ConfigFile(this);

    mKegsThread = new KegsThread(mConfigFile.getConfigFile(),
                                 mKegsView.getBitmap());
    mKegsThread.registerUpdateScreenInterface(mKegsView);

    mKegsTouch = new KegsTouch(this, getThread().getEventQueue());
    mJoystick = new TouchJoystick(getThread().getEventQueue());

    final SpecialRelativeLayout mainView = (SpecialRelativeLayout)findViewById(R.id.mainview);
    mainView.setClickable(true);
    mainView.setLongClickable(true);
    mainView.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        // TODO: consider using two listeners and setOnTouchListener them
        if (mModeMouse) {
          return mKegsTouch.onTouchEvent(event);
        } else {
          return mJoystick.onTouchEvent(event);
        }
      }
    });
    mainView.setNotifySizeChanged(
      new SpecialRelativeLayout.NotifySizeChanged() {
        public void onSizeChanged(final int w, final int h, int oldw, int oldh) {
          final long now = System.currentTimeMillis();
          mScreenSizeTime = now;
          mKegsView.postDelayed(new Runnable() { public void run() {
            updateScreenSize(w, h, now);
          } }, 250);
        }
      }
    );

    mKegsKeyboard = new KegsKeyboard(getThread().getEventQueue());
    mKegsKeyboard.setOnStickyReset(this);

    findViewById(R.id.key_escape).setOnClickListener(mButtonClick);
    findViewById(R.id.key_return).setOnClickListener(mButtonClick);
    findViewById(R.id.key_control).setOnClickListener(mButtonClick);
    findViewById(R.id.key_open_apple).setOnClickListener(mButtonClick);
    findViewById(R.id.key_closed_apple).setOnClickListener(mButtonClick);
    findViewById(R.id.key_left).setOnClickListener(mButtonClick);
    findViewById(R.id.key_right).setOnClickListener(mButtonClick);
    findViewById(R.id.key_up).setOnClickListener(mButtonClick);
    findViewById(R.id.key_down).setOnClickListener(mButtonClick);

    // Make sure local copy of internal disk images exist.
    new AssetImages(this, mConfigFile).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    final String romfile = mConfigFile.whichRomFile();
    if (romfile == null) {
      final DialogFragment chooseRom = new RomDialogFragment();
      chooseRom.show(getFragmentManager(), FRAGMENT_ROM);
    } else {
      mConfigFile.defaultConfig();
      getThread().setReady(true);
      mKegsView.postDelayed(new Runnable() { public void run() {
        new DiskImageFragment().show(getFragmentManager(), FRAGMENT_DISKIMAGE);
      } }, 1000);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    getThread().onPause();
    if (mKegsView instanceof KegsViewGL) {
      mKegsView.onPause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    getThread().onResume();
    if (mKegsView instanceof KegsViewGL) {
      mKegsView.onResume();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.w("kegs", "onDestroy called, halting");
    // Force process to exit.  We cannot handle another onCreate
    // once a KegsView has been active.  (JNI kegsmain has already run)
    java.lang.Runtime.getRuntime().halt(0);
  }

  static {
    System.loadLibrary("kegs");
  }
}
