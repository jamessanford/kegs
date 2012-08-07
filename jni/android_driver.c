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

JNIEnv *g_env;
jobject g_thiz;
jobject g_bitmap;
AndroidBitmapInfo g_bitmap_info;
jobject g_eventqueue;

extern int Verbose;

extern int g_warp_pointer;
extern int g_screen_depth;
extern int g_force_depth;
int g_screen_mdepth = 0;

int g_android_mouse_x = 0;
int g_android_mouse_y = 0;

extern Kimage g_mainwin_kimage;

extern int g_send_sound_to_file;

extern int g_quit_sim_now;

int	g_has_focus = 0;
int	g_auto_repeat_on = -1;
int	g_x_shift_control_state = 0;

int	g_use_shmem = 0;

int	g_needs_cmap = 0;

extern word32 g_red_mask;
extern word32 g_green_mask;
extern word32 g_blue_mask;
extern int g_red_left_shift;
extern int g_green_left_shift;
extern int g_blue_left_shift;
extern int g_red_right_shift;
extern int g_green_right_shift;
extern int g_blue_right_shift;

extern int Max_color_size;

extern word32 g_palette_8to1624[256];
extern word32 g_a2palette_8to1624[256];

int	g_alt_left_up = 1;
int	g_alt_right_up = 1;

extern word32 g_full_refresh_needed;

extern int g_border_sides_refresh_needed;
extern int g_border_special_refresh_needed;
extern int g_status_refresh_needed;

extern int g_lores_colors[];
extern int g_cur_a2_stat;

extern int g_a2vid_palette;

extern int g_installed_full_superhires_colormap;

extern int g_screen_redraw_skip_amt;

extern word32 g_a2_screen_buffer_changed;

extern char *g_status_ptrs[MAX_STATUS_LINES];

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
        printf("xdriver_end\n");
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
                mac_printf("malloc for data fail, mdepth:%d\n", mdepth);
                exit(2);
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

#if 0
  static int pushed = 60;
  pushed++;
  if (pushed >= 60) {
    LOGE("XXXXXXXXXXXXXXXXXXXXXXX PUSH XXXXXXXXXXXXXXXXXXXXXXXXX");
    pushed=0;
  }
#endif

  if ((ret = AndroidBitmap_lockPixels(g_env, g_bitmap, &pixels)) < 0) {
    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    return;
  }

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
  pixels = ((char *)pixels + (g_bitmap_info.stride * desty)) + (destx * 4);

  for (y=0; y<height; y++) {
    uint32_t *line = (uint32_t*)pixels;
    inptr = indata;
    for (x=0; x<width; x++) {
      line++[0] = palptr[*inptr++];
    }
    pixels = (char *)pixels + g_bitmap_info.stride;
    indata += in_width;
  }

  AndroidBitmap_unlockPixels(g_env, g_bitmap);

#if 0
  if (pushed == 0) {
    LOGE("XXXXXXXXXXXXXXXXXXXXXXX UNLOCK XXXXXXXXXXXXXXXXXXXXXXXXX");
  }
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

#if 0
  if (pushed == 0) {
    LOGE("XXXXXXXXXXXXXXXXXXXXXXX AFTER afterUpdate XXXXXXXXXXXXXXXXXXXXXXXXX");
  }
#endif
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
        return;
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


// Instead of 'KegsView$KegsThread', the $ is encoded as _00024.
JNIEXPORT void JNICALL
Java_com_froop_app_kegs_KegsView_00024KegsThread_mainLoop( JNIEnv* env, jobject thiz, jobject bitmap, jobject eventqueue )
{
  int ret;

#if 0
  LOGE("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX enter mainLoop");
#endif

  g_env = env;
  g_thiz = thiz;
  g_bitmap = bitmap;
  g_eventqueue = eventqueue;

  if ((ret = AndroidBitmap_getInfo(env, bitmap, &g_bitmap_info)) < 0) {
    LOGE("AndroidBitmap_getInfo() failed");
    return;
  }

  if (g_bitmap_info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    LOGE("Bitmap format must be RGBA_8888");
    return;
  }

  kegsmain(0, NULL);
}

void
dev_video_init()
{

        int i;
        int     lores_col;
// We tell KEGS we have an 8bit display,
// but then when it asks us to push it, we transform the update area
// into ARGB_8888.  There may be palette update bugs?
        g_screen_mdepth = 8;
        g_screen_depth = g_screen_mdepth;

        g_red_left_shift = 0;
        g_green_left_shift = 8;
        g_blue_left_shift = 16;

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

#if 0
  LOGE("got key_id %d %d", key_id, key_up);
#endif

  int a2_code = key_id;
  if (a2_code != -1) {
    adb_physical_key_update(a2_code, key_up);
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
  jmethodID mid = (*g_env)->GetMethodID(g_env, cls, "checkForPause", "()V");
  (*g_env)->DeleteLocalRef(g_env, cls);
  if (mid == NULL) {
    return;
  }
  (*g_env)->CallVoidMethod(g_env, g_thiz, mid);

  do {
    cls = (*g_env)->GetObjectClass(g_env, g_eventqueue);
    mid = (*g_env)->GetMethodID(g_env, cls, "poll", "()Ljava/lang/Object;");
    (*g_env)->DeleteLocalRef(g_env, cls);
    if (mid == NULL) {
      return;
    }
    event_item = (*g_env)->CallObjectMethod(g_env, g_eventqueue, mid);

    if (event_item != NULL) {
      jclass mouse_class = (*g_env)->FindClass(g_env, "com/froop/app/kegs/KegsView$MouseKegsEvent");
      jclass key_class = (*g_env)->FindClass(g_env, "com/froop/app/kegs/KegsView$KeyKegsEvent");

      if (mouse_class != NULL && (*g_env)->IsInstanceOf(g_env, event_item, mouse_class)) {
        keep_going = x_mouse_update(mouse_class, event_item);
      } else if (key_class != NULL && (*g_env)->IsInstanceOf(g_env, event_item, key_class)) {
        keep_going = x_key_update(key_class, event_item);
      }
      (*g_env)->DeleteLocalRef(g_env, mouse_class);
      (*g_env)->DeleteLocalRef(g_env, key_class);
      (*g_env)->DeleteLocalRef(g_env, event_item);
    }
  } while(event_item != NULL && keep_going);
}
