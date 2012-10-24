Port of Kent Dickey's KEGS Apple IIgs Emulator to Android.

You can find a release version in Google Play: https://play.google.com/store/apps/details?id=com.froop.app.kegs

Work in progress!  Screen may be cropped in portrait mode on phones.
Flip it over to landscape orientation.

Look on your sdcard in /Android/data/com.froop.app.kegs/files/
There's a 'default' config there that gets copied to 'config.kegs' on startup.

The touch screen acts as a big trackpad for the mouse.

To click and drag, either Long Press then drag or
use one finger for movement and one finger for the mouse button.

Source code:
  https://github.com/jamessanford/kegs/

To build from source:
<pre>
  (git submodule init && git submodule update)
  android update project -p ActionBarSherlock/library -s
  android update project -p . -s
  ndk-build
  ant debug install
</pre>


Changes made to KEGS:
- Various ifdefs for __ANDROID__ in the kegs 'core'
- Addition of android_driver.c and android_sound_driver.c

Application structure:
- The UI is coordinated via KegsMain
- There is a KegsThread class that gets its own thread.  This thread ends up calling mainLoop() in jni/android_driver.c and running KEGS in that thread.
- The native thread gets two things from Java:
-- a Bitmap
-- a ConcurrentLinkedQueue

All UI events are sent to the native thread by pushing events into the ConcurrentLinkedQueue (KegsEvent, MouseKegsEvent, KeyKegsEvent).

The native thread pulls events off the queue, writes updates into the bitmap object, then calls the Java "updateScreen" in the KegsThread object.

The native thread also calls support functions in Java, such as checkForPause(), which deadlocks the thread while it should be paused.

Android NDK samples were used as a base for this:
- bitmap-plasma
- JetBoy
- native-audio
- FireflyRenderer
