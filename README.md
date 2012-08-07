Port of Kent Dickey's KEGS Apple IIgs Emulator to Android.

Work in progress!  Currently optimized for tablet.  Screen may be cropped on phones.

Config file and disk images in /mnt/sdcard/KEGS/
To attach disk images, use the F4 button to enter KEGS configuration.

The touch screen acts as a big trackpad for the mouse.  To click and drag,
for example to pull down GS/OS menus, you need to Long Press first.

What's not ready yet:
- No audio.
- No joystick.
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
