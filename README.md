Port of Kent Dickey's KEGS Apple IIgs Emulator to Android.  Also includes OG ActiveGS patches as of kegs_3_0_242.

You can find a release version in Google Play: https://play.google.com/store/apps/details?id=com.froop.app.kegs

Usability notes:
  The touch screen acts as a big trackpad for the mouse.

  To click and drag, either Long Press then drag or
  use one finger for movement and one finger for the mouse button.

  It looks for disk images on the main sdcard or 'legacy' storage directory,
  and a few subdirectories under that: Downloads, KEGS, and
  Android/data/com.froop.app.kegs/files/

  If you want to edit the KEGS 'config.kegs' directly, edit 'default' in
  the last directory mentioned above.

  Input: Physical keyboards should work, though on some devices you may need
  to reset the input method to something like Hacker's Keyboard.

  Sound: If you hear no sound, do something where sound should be playing
  (perhaps the X-MAS demo main menu) and then adjust the sound volume.
  If KEGS is not generating sound, the volume buttons adjust a different
  sound level (for example your ringer)


Source code:
  https://github.com/jamessanford/kegs/

To build from source, open this project in Android Studio.

Changes made to KEGS:
- Various ifdefs for \_\_ANDROID\_\_ in the kegs 'core'
- Addition of android_driver.c and android_sound_driver.c

Application structure:
- The UI is coordinated via KegsMain
- There is a KegsThread class that gets its own thread.  This thread ends up calling mainLoop() in jni/android_driver.c and running KEGS in that thread.
- The native thread gets two things from Java: a Bitmap, and a ConcurrentLinkedQueue.

All UI events are sent to the native thread by pushing events into the ConcurrentLinkedQueue (KegsEvent, MouseKegsEvent, KeyKegsEvent).

The native thread pulls events off the queue, writes updates into the bitmap object, then calls the Java "updateScreen" in the KegsThread object.

The native thread also calls support functions in Java, such as checkForPause(), which blocks the native thread while it should be paused.  See comments in KegsThread.java for more details.

Android NDK samples were helpful when creating this port:
- bitmap-plasma
- JetBoy
- native-audio
- FireflyRenderer
