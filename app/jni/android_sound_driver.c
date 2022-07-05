// Lots of boilerplate OpenSLES code from the Android NDK 'native-audio' sample.
// Hooked up to a copy of the KEGS Mac sound driver.

// FIXME: Consider switching to https://github.com/google/oboe

#include <assert.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "libkegs"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include "defc.h"
#include "sound.h"

extern JNIEnv *g_env;
extern jobject g_thiz;

extern int g_audio_rate;
extern int g_audio_socket;
extern int g_audio_enable;

static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;
static SLObjectItf outputMixObject = NULL;

static SLObjectItf bqPlayerObject = NULL;
static SLPlayItf bqPlayerPlay;
static SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
static SLVolumeItf bqPlayerVolume;

#define MACSND_REBUF_SIZE       (64*1024)
word32  g_macsnd_rebuf[MACSND_REBUF_SIZE];
volatile word32 *g_macsnd_rebuf_ptr;
volatile word32 *g_macsnd_rebuf_cur;
volatile int g_macsnd_playing = 0;

void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
{
  assert(bq == bqPlayerBufferQueue);
  assert(NULL == context);

  // Read volatile items once.
  word32 *buf_ptr = (word32 *)g_macsnd_rebuf_ptr;  // where new data is being written
  word32 *buf_cur = (word32 *)g_macsnd_rebuf_cur;  // where we play from

  // Bail early so g_macsnd_playing can be reset.
  if (buf_ptr == buf_cur) {
    g_macsnd_playing = 0;
    return;
  }
  g_macsnd_playing = 1;

// Play g_macsnd_rebuf_cur to EITHER
//    g_macsnd_rebuf_ptr
//      OR
//    &g_macsnd_rebuf[MACSND_REBUF_SIZE]
  word32 *last;
  if (buf_ptr < buf_cur) {
    // new data has wrapped around, only play to the end of the buffer.
    last = &g_macsnd_rebuf[MACSND_REBUF_SIZE];
  } else {
    // play to buf_ptr
    last = buf_ptr;
  }

  int samps;
  samps = last - buf_cur;
  samps = MIN(samps, 768);

  // Where we'll start playing from next time.
  if (&buf_cur[samps] == &g_macsnd_rebuf[MACSND_REBUF_SIZE]) {
    g_macsnd_rebuf_cur = &g_macsnd_rebuf[0];
  } else {
    g_macsnd_rebuf_cur = &buf_cur[samps];
  }

  SLresult result;
  result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, (char *)buf_cur, samps * 4);
  assert(SL_RESULT_SUCCESS == result);
}

int android_send_audio(byte *ptr, int in_size) {
  word32  *wptr, *macptr;
  word32  *eptr;
  int     samps;
  int     i;

  samps = in_size / 4;
  wptr = (word32 *)ptr;
  macptr = (word32 *)g_macsnd_rebuf_ptr;
  eptr = &g_macsnd_rebuf[MACSND_REBUF_SIZE];
  for(i = 0; i < samps; i++) {
    *macptr++ = *wptr++;
    if(macptr >= eptr) {
      macptr = &g_macsnd_rebuf[0];
    }
  }

  g_macsnd_rebuf_ptr = macptr;

  if(!g_macsnd_playing) {
    bqPlayerCallback(bqPlayerBufferQueue, NULL);
  }

  return in_size;
}

void child_sound_init_android() {
  g_audio_rate = 44100;
  set_audio_rate(g_audio_rate);

  if(engineObject != NULL) {
    // Already active.
    return;
  }

  SLresult result;
  result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
  assert(SL_RESULT_SUCCESS == result);
  result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);
  result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
  assert(SL_RESULT_SUCCESS == result);

  result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, NULL, NULL);
  assert(SL_RESULT_SUCCESS == result);
  result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);

  SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
  SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1,
    SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
    SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT, SL_BYTEORDER_LITTLEENDIAN};
  SLDataSource audioSrc = {&loc_bufq, &format_pcm};

  SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
  SLDataSink audioSnk = {&loc_outmix, NULL};

  const SLInterfaceID ids[2] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME};
  const SLboolean req[2] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
  result = (*engineEngine)->CreateAudioPlayer(engineEngine, &bqPlayerObject, &audioSrc, &audioSnk, 2, ids, req);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
            &bqPlayerBufferQueue);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, NULL);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_VOLUME, &bqPlayerVolume);
  assert(SL_RESULT_SUCCESS == result);

// -1000 is listenable, -2000 is quiet, ...
#if 0
  result = (*bqPlayerVolume)->SetVolumeLevel(bqPlayerVolume, -2000);
  assert(SL_RESULT_SUCCESS == result);
#endif

  result = (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
  assert(SL_RESULT_SUCCESS == result);
}

void android_snd_init(word32 *shmaddr) {
  g_macsnd_rebuf_cur = &g_macsnd_rebuf[0];
  g_macsnd_rebuf_ptr = &g_macsnd_rebuf[0];

  /* just initialization, results in child_sound_init_android() */
  child_sound_loop(-1, -1, shmaddr);
}
