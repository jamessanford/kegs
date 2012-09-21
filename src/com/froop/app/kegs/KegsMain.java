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
import android.view.GestureDetector;
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

  protected KegsView mKegsView;
  private KegsTouch mKegsTouch;
  private KegsKeyboard mKegsKeyboard;
  private TouchJoystick mJoystick;

  private PopupMenu mSettingsMenu;
  private boolean mModeMouse = true;

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
      if (item_id == R.id.input_mouse) {
        mModeMouse = !mModeMouse;
        return true;
      } else if (item_id == R.id.input_keyboard) {
        // There doesn't seem to be a reliable way to determine the current state, so we have to just toggle it.
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
          imm.toggleSoftInput(0, 0);
        }
        return true;
      } else if (item_id == R.id.input_controls) {
        final int vis = areControlsVisible() ? View.GONE : View.VISIBLE;
        findViewById(R.id.b1).setVisibility(vis);
        findViewById(R.id.b2).setVisibility(vis);
        return true;
      } else if (item_id == R.id.emulation_speed) {
        new SpeedFragment().show(getFragmentManager(), FRAGMENT_SPEED);
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
    item = m.findItem(R.id.input_mouse);
    item.setTitle(mModeMouse ? R.string.input_joystick : R.string.input_mouse);

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
        mKegsView.setReady(true);
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
      dialog.setProgressPercentFormat(null);
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
      return builder.create();
    }
  }

  private void setScreenSize() {
    final int width = getResources().getDisplayMetrics().widthPixels;
    final int height = getResources().getDisplayMetrics().heightPixels;

// If we can fit at least 90% of a scaled screen into the display area, do it.
// If we hit less than 100% height, turn off system action bar and title.
// If ((400 + 32) * scale) > height, then crop border.

    float scaleX = 1.0f;
    float scaleY = 1.0f;
    boolean crop = false;

    // Force integer scaling on X axis.
    scaleX = (float)Math.round((width * 0.9) / 640);
    // TODO: Fix '48' hack being used for system buttons or soft buttons.
    scaleY = Math.min(scaleX, (height - 48) / 400.0f);

    // If Y would be compressed in a weird way, reduce the scale and use 1:1.
    if ((scaleX - scaleY) > 0.5) {
      scaleX = Math.max(1, scaleX - 1);
      scaleY = scaleX;
    }

    if (height < ((400 + 64) * scaleY)) {
      ActionBar actionBar = getActionBar();
      if (actionBar != null && actionBar.isShowing()) {
        actionBar.hide();
      }
    } else {
      ActionBar actionBar = getActionBar();
      if (actionBar != null && !actionBar.isShowing()) {
        actionBar.show();
      }
    }

    // TODO: Fix '32' and '64' for software buttons and window decorations.
    if (height < ((400 + 32 + 64) * scaleY)) {
      crop = true;
    }

    Log.w("kegs", "using scale " + scaleX + ":" + scaleY + " " + crop);

    mKegsView.setScale(scaleX, scaleY, crop);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    setScreenSize();
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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mKegsView = (KegsView)findViewById(R.id.kegsview);
    setScreenSize();  // This causes an unnecessary requestLayout of KegsView.

    mKegsTouch = new KegsTouch(mKegsView.getEventQueue());
    final GestureDetector inputDetect = new GestureDetector(this, mKegsTouch);

    mJoystick = new TouchJoystick(mKegsView.getEventQueue());

    final View mainView = findViewById(R.id.mainview);
    mainView.setClickable(true);
    mainView.setLongClickable(true);
    mainView.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        // TODO: consider using two listeners and setOnTouchListener them
        if (mModeMouse) {
          return inputDetect.onTouchEvent(event);
        } else {
          return mJoystick.onTouchEvent(event);
        }
      }
    });

    mKegsKeyboard = new KegsKeyboard(mKegsView.getEventQueue());
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
      mKegsView.setReady(true);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    mKegsView.getThread().onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mKegsView.getThread().onResume();
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
