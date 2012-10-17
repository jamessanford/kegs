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
import android.graphics.Rect;
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
import android.widget.PopupMenu;
import android.widget.ToggleButton;

public class KegsMain extends Activity implements KegsKeyboard.StickyReset {
  private static final String FRAGMENT_ROM = "rom";
  private static final String FRAGMENT_DOWNLOAD = "download";
  private static final String FRAGMENT_ERROR = "error";
  private static final String FRAGMENT_SPEED = "speed";
  private static final String FRAGMENT_DISKIMAGE = "diskimage";

  private KegsThread mKegsThread;

  // For the software renderer, use 'KegsView' here and in res/layout/main.xml
  //   Also consider undef ANDROID_GL in jni/android_driver.c
  protected KegsViewGL mKegsView;
  private KegsTouch mKegsTouch;
  private KegsKeyboard mKegsKeyboard;
  private TouchJoystick mJoystick;

  private PopupMenu mSettingsMenu;
  private boolean mModeMouse = true;
  private int mLastActionBar = 0;  // window height at last ActionBar change.

  private View.OnClickListener mButtonClick = new View.OnClickListener() {
    public void onClick(View v) {
//      Log.e("kegs", "button clicked");
      final int click_id = v.getId();
      int key_id = -1;
      boolean sticky = false;
      if (click_id == R.id.key_escape) {
        key_id = KegsKeyboard.KEY_ESCAPE;
      } else if (click_id == R.id.key_return) {
        key_id = KegsKeyboard.KEY_RETURN;
      } else if (click_id == R.id.key_f4) {
        key_id = KegsKeyboard.KEY_F4;
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

  private PopupMenu.OnMenuItemClickListener mSettingsClick = new PopupMenu.OnMenuItemClickListener() {
    public boolean onMenuItemClick(MenuItem item) {
      final int item_id = item.getItemId();
      if (item_id == R.id.input_controls) {
        final int vis = areControlsVisible() ? View.GONE : View.VISIBLE;
        findViewById(R.id.b1).setVisibility(vis);
        findViewById(R.id.b2).setVisibility(vis);
        return true;
      } else if (item_id == R.id.warm_reset) {
        getThread().doWarmReset();
        return true;
      } else if (item_id == R.id.power_cycle) {
        getThread().doPowerOff();
        getThread().allowPowerOn();
        return true;
      }
      return false;
    }
  };

  private boolean areControlsVisible() {
    return findViewById(R.id.b1).getVisibility() == View.VISIBLE;
  }

  // Adjust items to say "Use Joystick" vs "Use Mouse", etc.
  private void updateSettingsMenu() {
    final Menu m = mSettingsMenu.getMenu();
    MenuItem item;
    item = m.findItem(R.id.input_controls);
    item.setTitle(areControlsVisible() ? R.string.input_controls_hide : R.string.input_controls_show);
  }

  public void showPopup(View v) {
    updateSettingsMenu();
    mSettingsMenu.show();
  }

  public void onStickyReset() {
    ((ToggleButton)findViewById(R.id.key_control)).setChecked(false);
    ((ToggleButton)findViewById(R.id.key_open_apple)).setChecked(false);
    ((ToggleButton)findViewById(R.id.key_closed_apple)).setChecked(false);
  }

  protected void getRomFile(String romfile) {
    final DialogFragment download = new DownloadDialogFragment();
    download.show(getFragmentManager(), FRAGMENT_DOWNLOAD);
    new DownloadRom().execute(romfile);
  }

  class DownloadRom extends AsyncTask<String, Void, Boolean> {
    private String mRomfile;
    protected Boolean doInBackground(String ... raw_romfile) {
      mRomfile = raw_romfile[0];
      return new DownloadHelper().save(
          "http://jsan.co/" + mRomfile, Config.mPath.getPath() + "/" + mRomfile);
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
        Config.checkConfig(mRomfile);
        getThread().setReady(true);
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
      dialog.setCanceledOnTouchOutside(false);  // lame
      return dialog;
    }
  }

  class ErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage(getResources().getText(R.string.rom_error));
// TODO do getActivity().finish() on button clicks
// TODO setCanceledOnTouchOutside(false) ?  otherwise can accidentally dismiss the error.
      return builder.create();
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

  private void setScreenSize(boolean quick) {
    int width;
    int height;

    if (quick) {
      width = getResources().getDisplayMetrics().widthPixels;
      height = getResources().getDisplayMetrics().heightPixels;
      if (android.os.Build.VERSION.SDK_INT >= 11) {
        // NOTE: 48 is a guess at the System Bar obstruction.
        // These are 'visible insets' into the display from the window manager.
        height -= 48;
      }
    } else {
      Rect displaySize = new Rect();
      // We use the mKegsView object here, but we could ask any view.
      mKegsView.getWindowVisibleDisplayFrame(displaySize);
      width = displaySize.width();
      height = displaySize.height();
    }
    final BitmapSize bitmapSize = new BitmapSize(width, height);

    mKegsView.updateScreenSize(bitmapSize);

    // Only change action bar if the window height is significantly
    // different from the last time we changed the action bar.
    if (height < (mLastActionBar * 0.85) || height > (mLastActionBar * 1.15)) {
      mLastActionBar = height;
      updateActionBar(bitmapSize.showActionBar());
    }

    // Force another redraw of the bitmap into the canvas.  Bug workaround.
    getThread().updateScreen();
  }

  private void workaroundScreenSize() {
    // First use displayMetrics.
    setScreenSize(true);

    // Then update with getWindowVisibleDisplayFrame in 250ms.
    //
    // We want to use getWindowVisibleDisplayFrame, but it's not
    // always immediately available.  Bug workaround.
    //
    // BUG: Sometimes if the device rotates as the soft keyboard
    //      is becoming visible for the first time, the reported
    //      window size is reduced and we don't scale to full screen.
    //      The user can fix this by rotating the screen again.
    //      We may want to trap IME visible/hidden events and update the size.
    mKegsView.postDelayed(new Runnable() {
                           public void run() { setScreenSize(false); }
                         }, 250);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    workaroundScreenSize();
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
    // TODO: add code to adjust anything that might change
    return super.onPrepareOptionsMenu(menu);
  }

  // TODO: FIXME: Seriously in progress.
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
      // TOAST...'now using...joystick or mouse'
      return true;
    } else if (item_id == R.id.action_function) {
// drop down menu for special keys...?
      return true;
    } else if (item_id == R.id.action_diskimage) {
// FIXME
//      new DiskImageFragment().show(getFragmentManager(), FRAGMENT_DISKIMAGE);
      return true;
    }
    return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mKegsView = (KegsViewGL)findViewById(R.id.kegsview);

    mKegsThread = new KegsThread(mKegsView.getBitmap());
    mKegsThread.registerUpdateScreenInterface(mKegsView);

    workaroundScreenSize();

    mKegsTouch = new KegsTouch(this, getThread().getEventQueue());
    mJoystick = new TouchJoystick(getThread().getEventQueue());

    final View mainView = findViewById(R.id.mainview);
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

    mKegsKeyboard = new KegsKeyboard(getThread().getEventQueue());
    mKegsKeyboard.setOnStickyReset(this);

    mSettingsMenu = new PopupMenu(this, findViewById(R.id.key_settings));
    mSettingsMenu.inflate(R.menu.options);
    mSettingsMenu.setOnMenuItemClickListener(mSettingsClick);

    findViewById(R.id.key_escape).setOnClickListener(mButtonClick);
    findViewById(R.id.key_return).setOnClickListener(mButtonClick);
    findViewById(R.id.key_f4).setOnClickListener(mButtonClick);
    findViewById(R.id.key_control).setOnClickListener(mButtonClick);
    findViewById(R.id.key_open_apple).setOnClickListener(mButtonClick);
    findViewById(R.id.key_closed_apple).setOnClickListener(mButtonClick);
    findViewById(R.id.key_left).setOnClickListener(mButtonClick);
    findViewById(R.id.key_right).setOnClickListener(mButtonClick);
    findViewById(R.id.key_up).setOnClickListener(mButtonClick);
    findViewById(R.id.key_down).setOnClickListener(mButtonClick);

    final String romfile = Config.whichRomFile();
    if (romfile == null) {
      final DialogFragment chooseRom = new RomDialogFragment();
      chooseRom.show(getFragmentManager(), FRAGMENT_ROM);
    } else {
      Config.checkConfig(romfile);
      getThread().setReady(true);
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
