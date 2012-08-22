Port of Kent Dickey's KEGS Apple IIgs Emulator to Android.

Work in progress!  Currently optimized for tablet.  Screen may be cropped on phones.

Config file and disk images in /mnt/sdcard/KEGS/
To attach disk images, use the F4 button to enter KEGS configuration.

The touch screen acts as a big trackpad for the mouse.  To click and drag,
for example to pull down GS/OS menus, you need to Long Press first.

What's not ready yet:
- Screen scaling to fit your device.
- Native UI configuration options.

Source code:
  https://github.com/jamessanford/kegs/

To build from source:
<pre>
  android update project -p . -s
  (cd jni && ndk-build)
  ant debug install
</pre>


Changes made to KEGS:
- Various ifdefs for __ANDROID__ in the kegs 'core'
- Addition of android_driver.c and android_sound_driver.c

Application structure:
- The UI is coordinated via KegsMain
- There is a KegsView$KegsThread class that gets its own thread.  This thread ends up calling mainLoop() in jni/android_driver.c and running KEGS in that thread.
- The native thread gets two things from Java:
-- a Bitmap
-- a ConcurrentLinkedQueue

All UI events are sent to the native thread by pushing events into the ConcurrentLinkedQueue (KegsEvent, MouseKegsEvent, KeyKegsEvent).

The native thread pulls events off the queue, writes updates into the bitmap object, then calls the Java "updateScreen" in the KegsView$KegsThread object.

The native thread also calls support functions in Java, such as checkForPause(), which deadlocks the thread while it should be paused.

Android NDK samples were used as a base for this:
- bitmap-plasma
- JetBoy
- native-audio
