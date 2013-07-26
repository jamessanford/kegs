/************************************************************************/
/*			KEGS: Apple //gs Emulator			*/
/*			Copyright 2002 by Kent Dickey			*/
/*									*/
/*		This code is covered by the GNU GPL			*/
/*									*/
/*	The KEGS web page is kegs.sourceforge.net			*/
/*	You may contact the author at: kadickey@alumni.princeton.edu	*/
/************************************************************************/

#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <time.h>
#include <stdlib.h>
#include <signal.h>

#define  LOG_TAG    "libkegs"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include "defc.h"

// KegsViewGL, when defined lock pixels only once per mainLoop.
#define ANDROID_GL

JNIEnv *g_env;
jobject g_thiz;
jobject g_bitmap;
AndroidBitmapInfo g_bitmap_info;
jobject g_eventqueue;

void *g_android_pixels;

int g_use_shmem = 0;

extern int g_screen_depth;
int g_screen_mdepth = 0;

int g_android_mouse_x = 0;
int g_android_mouse_y = 0;

extern int g_joystick_type;
extern int g_paddle_buttons;
extern int g_paddle_val[];

extern Kimage g_mainwin_kimage;

extern int g_config_kegs_update_needed;
extern int g_limit_speed;

extern word32 g_red_mask;
extern word32 g_green_mask;
extern word32 g_blue_mask;
extern int g_red_left_shift;
extern int g_green_left_shift;
extern int g_blue_left_shift;
extern int g_red_right_shift;
extern int g_green_right_shift;
extern int g_blue_right_shift;

extern word32 g_palette_8to1624[256];
extern word32 g_a2palette_8to1624[256];

extern int g_lores_colors[];

extern int g_installed_full_superhires_colormap;

void
x_dialog_create_kegs_conf(const char *str)
{
        /* do nothing -- not implemented yet */
        return;
}

int
x_show_alert(int is_fatal, const char *str)
{
        /* Not implemented yet */
        adb_all_keys_up();

        clear_fatal_logs();
        return 0;
}

void
xdriver_end()
{
}

void
x_get_kimage(Kimage *kimage_ptr) {
        byte    *ptr;
        int     width;
        int     height;
        int     depth, mdepth;
        int     size;

        width = kimage_ptr->width_req;
        height = kimage_ptr->height;
        depth = kimage_ptr->depth;
        mdepth = kimage_ptr->mdepth;

        size = (width*height*mdepth) >> 3;
        ptr = (byte *)malloc(size);

        if(ptr == 0) {
                LOGE("malloc for data fail, mdepth:%d", mdepth);
                my_exit(2);
        }

        kimage_ptr->data_ptr = ptr;

        kimage_ptr->dev_handle = (void *)-1;
}


extern Kimage g_kimage_superhires;

void
x_push_kimage(Kimage *kimage_ptr, int destx, int desty, int srcx, int srcy,
        int width, int height)
{
  void *pixels;
  int ret;

#ifndef ANDROID_GL
  if ((ret = AndroidBitmap_lockPixels(g_env, g_bitmap, &pixels)) < 0) {
    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    return;
  }
#else
  pixels = g_android_pixels;
#endif

// FILL PIXELS

  word32 *palptr;

  if(kimage_ptr == &g_kimage_superhires) {
    palptr = &(g_palette_8to1624[0]);
  } else {
    palptr = &(g_a2palette_8to1624[0]);
  }
  if(kimage_ptr->depth != 8) {
    LOGE("convert_kimage_depth from non-8 bit depth");
    return;
  }

  byte *indata, *inptr;
  indata = (byte *)kimage_ptr->data_ptr;
  int in_width = kimage_ptr->width_act;
  int x, y;

  indata += (srcy * in_width) + srcx;
#ifdef ANDROID_ARGB_8888
  pixels = ((char *)pixels + (g_bitmap_info.stride * desty)) + (destx * 4);
  for (y=0; y<height; y++) {
    uint32_t *line = (uint32_t*)pixels;
    inptr = indata;
    for (x=0; x<width; x++) {
      line++[0] = (uint32_t)(palptr[*inptr++]);
    }
    pixels = (char *)pixels + g_bitmap_info.stride;
    indata += in_width;
  }
#else
// RGB_565
  pixels = ((char *)pixels + (g_bitmap_info.stride * desty)) + (destx * 2);
  for (y=0; y<height; y++) {
    uint16_t *line = (uint16_t*)pixels;
    inptr = indata;
    for (x=0; x<width; x++) {
      line++[0] = (uint16_t)(palptr[*inptr++]);
    }
    pixels = (char *)pixels + g_bitmap_info.stride;
    indata += in_width;
  }
#endif

#ifndef ANDROID_GL
  AndroidBitmap_unlockPixels(g_env, g_bitmap);
#endif

// TODO: these can be global refs instead...
// and right now, can be just one call instead of two
// alternatively, could use a Rect when locking the canvas
  jclass cls = (*g_env)->GetObjectClass(g_env, g_thiz);
  jmethodID mid = (*g_env)->GetMethodID(g_env, cls, "updateScreen", "()V");
  if (mid == NULL) {
    return;
  }
  (*g_env)->CallVoidMethod(g_env, g_thiz, mid);
  (*g_env)->DeleteLocalRef(g_env, cls);
}

void
x_update_physical_colormap()
{
}

void
x_redraw_status_lines()
{
}


void
x_hide_pointer(int do_hide)
{
}

void
x_full_screen(int do_full)
{
}

void
x_auto_repeat_on(int must)
{
}

void
x_auto_repeat_off(int must)
{
}

void
show_xcolor_array()
{
}

void
x_push_done()
{
}

void
x_update_color(int col_num, int red, int green, int blue, word32 rgb)
{
}

// This typically sets g_config_kegs_name to the full path of 'config.kegs'.
void android_config_init(char *output, int maxlen) {
  output[0] = 0;

  jclass cls = (*g_env)->GetObjectClass(g_env, g_thiz);
  jmethodID mid = (*g_env)->GetMethodID(g_env, cls, "getConfigFile", "()Ljava/lang/String;");
  (*g_env)->DeleteLocalRef(g_env, cls);
  if (mid == NULL) {
    return;
  }
  jstring config_path = (*g_env)->CallObjectMethod(g_env, g_thiz, mid);
  if (config_path == NULL) {
    return;
  }
  const char *nativeString = (*g_env)->GetStringUTFChars(g_env, config_path, 0);
  strncpy(output, nativeString, maxlen - 1);
  output[maxlen - 1] = 0;
  (*g_env)->ReleaseStringUTFChars(g_env, config_path, nativeString);
  (*g_env)->DeleteLocalRef(g_env, config_path);
}

JNIEXPORT void JNICALL
Java_com_froop_app_kegs_CopyHelper_nativeSync( JNIEnv* env, jobject thiz) {
  sync();
  sync();
}

// Instead of 'KegsView$KegsThread', the $ is encoded as _00024.
// (not any more, but it was KegsView_00024KegsThread_mainLoop)
JNIEXPORT void JNICALL
Java_com_froop_app_kegs_KegsThread_mainLoop( JNIEnv* env, jobject thiz, jobject bitmap, jobject eventqueue )
{
  int ret;

  g_env = env;
  g_thiz = thiz;
  g_bitmap = bitmap;
  g_eventqueue = eventqueue;

  if ((ret = AndroidBitmap_getInfo(env, bitmap, &g_bitmap_info)) < 0) {
    LOGE("AndroidBitmap_getInfo() failed");
    return;
  }

#ifdef ANDROID_ARGB_8888
  if (g_bitmap_info.format != ANDROID_BITMAP_FORMAT_ARGB_8888) {
    LOGE("Bitmap format must be ARGB_8888");
    return;
  }
#else
  if (g_bitmap_info.format != ANDROID_BITMAP_FORMAT_RGB_565) {
    LOGE("Bitmap format must be RGB_565");
    return;
  }
#endif

#ifdef ANDROID_GL
  if ((ret = AndroidBitmap_lockPixels(g_env, g_bitmap, &g_android_pixels)) < 0) {
    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    return;
  }
#endif

  kegsmain(0, NULL);

#ifdef ANDROID_GL
  AndroidBitmap_unlockPixels(g_env, g_bitmap);
  g_android_pixels = NULL;
#endif
}

void
dev_video_init()
{

        int i;
        int     lores_col;
// We tell KEGS we have an 8bit display,
// but in x_push_kimage we transform the update area into RGB_565 or ARGB_8888.
        g_screen_mdepth = 8;
        g_screen_depth = g_screen_mdepth;

#ifdef ANDROID_ARGB_8888
        g_red_left_shift = 0;
        g_green_left_shift = 8;
        g_blue_left_shift = 16;
#else
// RGB_565
        g_red_right_shift = 3;
        g_green_right_shift = 2;
        g_blue_right_shift = 3;
        g_red_left_shift = 11;
        g_green_left_shift = 5;
        g_blue_left_shift = 0;
#endif

        video_get_kimages();

        if(g_screen_depth != 8) {
                //      Allocate g_mainwin_kimage
                video_get_kimage(&g_mainwin_kimage, 0, g_screen_depth,
                                                g_screen_mdepth);
        }

        for(i = 0; i < 256; i++) {
                lores_col = g_lores_colors[i & 0xf];
                video_update_color_raw(i, lores_col);
                g_a2palette_8to1624[i] = g_palette_8to1624[i];
        }

        x_update_physical_colormap();

        g_installed_full_superhires_colormap = 1;
}

void joystick_init() {
  g_paddle_val[0] = 32767;  // x
  g_paddle_val[1] = 32767;  // y
  g_paddle_val[2] = 32767;  // x #2
  g_paddle_val[3] = 32767;  // y #2
  g_paddle_buttons = 0x0C;
  if (g_joystick_type != JOYSTICK_TYPE_NATIVE_1) {
    g_joystick_type = JOYSTICK_TYPE_NATIVE_1;
    g_config_kegs_update_needed = 1;
  }
}

void joystick_update(double dcycs) {
  paddle_update_trigger_dcycs(dcycs);
}

void joystick_update_buttons() {
}

void joystick_shut() {
}

int x_joystick_update(jclass joystick_class, jobject joystick_event) {
  static int button_last = 0;

  jfieldID fid = (*g_env)->GetFieldID(g_env, joystick_class, "x", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint x = (*g_env)->GetIntField(g_env, joystick_event, fid);

  fid = (*g_env)->GetFieldID(g_env, joystick_class, "y", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint y = (*g_env)->GetIntField(g_env, joystick_event, fid);

  fid = (*g_env)->GetFieldID(g_env, joystick_class, "buttons", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint buttons = (*g_env)->GetIntField(g_env, joystick_event, fid);

  if (x != 0xFFFF) {
    g_paddle_val[0] = MIN(32767, MAX(-32767, x));
  }
  if (y != 0xFFFF) {
    g_paddle_val[1] = MIN(32767, MAX(-32767, y));
  }
  g_paddle_buttons = (g_paddle_buttons & ~3) + (buttons & 3);

  if (buttons != button_last) {
    button_last = buttons;
    return 0;  // wait until the next cycle to process more events
  }
  return 1;
}

int x_mouse_update(jclass mouse_class, jobject mouse_event) {
  static int button_last = 0;

  jfieldID fid = (*g_env)->GetFieldID(g_env, mouse_class, "x", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint x = (*g_env)->GetIntField(g_env, mouse_event, fid);

  fid = (*g_env)->GetFieldID(g_env, mouse_class, "y", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint y = (*g_env)->GetIntField(g_env, mouse_event, fid);

  fid = (*g_env)->GetFieldID(g_env, mouse_class, "buttons", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint buttons = (*g_env)->GetIntField(g_env, mouse_event, fid);

  fid = (*g_env)->GetFieldID(g_env, mouse_class, "buttons_valid", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint buttons_valid = (*g_env)->GetIntField(g_env, mouse_event, fid);

  g_android_mouse_x += x;
  g_android_mouse_y += y;

  g_android_mouse_x = MIN(639, g_android_mouse_x);
  g_android_mouse_x = MAX(0, g_android_mouse_x);
  g_android_mouse_y = MIN(399, g_android_mouse_y);
  g_android_mouse_y = MAX(0, g_android_mouse_y);

  update_mouse(g_android_mouse_x, g_android_mouse_y, buttons, buttons_valid);
  if (buttons != button_last) {
    button_last = buttons;
    return 0;  // wait until the next cycle to process more events
  }
  return 1;
}

int x_diskimage(jclass diskimage_class, jobject diskimage_event) {
  jfieldID fid = (*g_env)->GetFieldID(g_env, diskimage_class, "slot", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint slot = (*g_env)->GetIntField(g_env, diskimage_event, fid);

  fid = (*g_env)->GetFieldID(g_env, diskimage_class, "drive", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint drive = (*g_env)->GetIntField(g_env, diskimage_event, fid);

  // s6d1 is 'slot 6 drive 0' according to KEGS.
  drive--;

  fid = (*g_env)->GetFieldID(g_env, diskimage_class, "filename", "Ljava/lang/String;");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jstring name = (*g_env)->GetObjectField(g_env, diskimage_event, fid);
  char *nativeString = NULL;
  if (name != NULL) {
    nativeString = (char *)(*g_env)->GetStringUTFChars(g_env, name, 0);
  }

  // KEGS makes its own copy of the string.
  int eject = nativeString == NULL ? 1 : 0;
  insert_disk(slot, drive, nativeString, eject, 0, 0, -1);

  (*g_env)->ReleaseStringUTFChars(g_env, name, nativeString);
  (*g_env)->DeleteLocalRef(g_env, name);

  return 1;
}

void x_key_special(int key_id) {
  key_id = key_id & 0x7f;  // only use lower 7 bits
  switch(key_id) {
    case 0:
    case 1:
    case 2:
    case 3:
      g_limit_speed = key_id;
      g_config_kegs_update_needed = 1;
      break;
    case 120:
      set_halt(HALT_WANTTOQUIT);  // request kegsmain to exit
      break;
  }
}

int x_key_update(jclass key_class, jobject key_event) {
  jfieldID fid = (*g_env)->GetFieldID(g_env, key_class, "key_id", "I");
  if (fid == NULL) {
    LOGE("NO FID");
    return 0;
  }
  jint key_id = (*g_env)->GetIntField(g_env, key_event, fid);

  fid = (*g_env)->GetFieldID(g_env, key_class, "up", "Z");
  if (fid == NULL) {
    LOGE("NO FID2");
    return 0;
  }
  jboolean key_up = (*g_env)->GetBooleanField(g_env, key_event, fid);

  if (key_id >= 0 && key_id < 0x80) {
    adb_physical_key_update(key_id, key_up);
  } else if (key_id >= 0x80) {
    x_key_special(key_id);
  }
  if (!key_up) {
    return 0;  // only process one key down event per loop
    // (if we did key a key down and key up event for the same key on the same loop, it would be lost)
  }
  return 1;
}

// If we do both the "down" and "up" of a click in a single pass
// of check_input_events(), it loses the click.
//
// So, only process mouse events until the mouse button status changes.
//
// This allows us to stuff the event queue with both DOWN and UP,
// the first pass will see "DOWN", and 1/60th of a second later the "UP"
// will go through.
//
// Same thing with keyboard -- we can process both any number of
// "key up" events, but only one "key down" event per loop.
// (actually we could keep doing keys as long as the key is different)
void
check_input_events()
{
  int keep_going = 1;
  jobject event_item;

  // check if paused, first
  jclass cls = (*g_env)->GetObjectClass(g_env, g_thiz);
  jmethodID mid = (*g_env)->GetMethodID(g_env, cls, "checkForPause", "(I)V");
  (*g_env)->DeleteLocalRef(g_env, cls);
  if (mid == NULL) {
    return;
  }
  (*g_env)->CallVoidMethod(g_env, g_thiz, mid, g_limit_speed);

  do {
    cls = (*g_env)->GetObjectClass(g_env, g_eventqueue);
    mid = (*g_env)->GetMethodID(g_env, cls, "poll", "()Ljava/lang/Object;");
    (*g_env)->DeleteLocalRef(g_env, cls);
    if (mid == NULL) {
      return;
    }
    event_item = (*g_env)->CallObjectMethod(g_env, g_eventqueue, mid);

    if (event_item != NULL) {
      jclass mouse_class = (*g_env)->FindClass(g_env, "com/froop/app/kegs/Event$MouseKegsEvent");
      jclass joystick_class = (*g_env)->FindClass(g_env, "com/froop/app/kegs/Event$JoystickKegsEvent");
      jclass key_class = (*g_env)->FindClass(g_env, "com/froop/app/kegs/Event$KeyKegsEvent");
      jclass diskimage_class = (*g_env)->FindClass(g_env, "com/froop/app/kegs/Event$DiskImageEvent");

      if (mouse_class != NULL && (*g_env)->IsInstanceOf(g_env, event_item, mouse_class)) {
        keep_going = x_mouse_update(mouse_class, event_item);
      } else if (joystick_class != NULL && (*g_env)->IsInstanceOf(g_env, event_item, joystick_class)) {
        keep_going = x_joystick_update(joystick_class, event_item);
      } else if (key_class != NULL && (*g_env)->IsInstanceOf(g_env, event_item, key_class)) {
        keep_going = x_key_update(key_class, event_item);
      } else if (diskimage_class != NULL && (*g_env)->IsInstanceOf(g_env, event_item, diskimage_class)) {
        keep_going = x_diskimage(diskimage_class, event_item);
      }
      (*g_env)->DeleteLocalRef(g_env, mouse_class);
      (*g_env)->DeleteLocalRef(g_env, joystick_class);
      (*g_env)->DeleteLocalRef(g_env, key_class);
      (*g_env)->DeleteLocalRef(g_env, diskimage_class);
      (*g_env)->DeleteLocalRef(g_env, event_item);
    }
  } while(event_item != NULL && keep_going);
}

// OG
void x_release_kimage(Kimage* kimage_ptr)
{
        if (kimage_ptr->dev_handle == (void*)-1)
        {
                free(kimage_ptr->data_ptr);
                kimage_ptr->data_ptr = NULL;
        }
}

// OG Addding ratio
int x_calc_ratio(float x,float y)
{
        // NOTE: This is a little bit slower, but is consistent time with each frame update.
        //       Using this looks better as sleep() successfully syncs up with 1/60sec updates.
        return 1;
}
