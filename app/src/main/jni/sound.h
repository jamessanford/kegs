/************************************************************************/
/*			KEGS: Apple //gs Emulator			*/
/*			Copyright 2002 by Kent Dickey			*/
/*									*/
/*		This code is covered by the GNU GPL			*/
/*									*/
/*	The KEGS web page is kegs.sourceforge.net			*/
/*	You may contact the author at: kadickey@alumni.princeton.edu	*/
/************************************************************************/

#ifdef INCLUDE_RCSID_C
const char rcsid_sound_h[] = "@(#)$KmKId: sound.h,v 1.17 2003-11-21 15:15:57-05 kentd Exp $";
#endif

#if !defined(_WIN32) && !defined(__CYGWIN__) && !defined(__ANDROID__)
# include <sys/ipc.h>
# include <sys/shm.h>
#endif

#define SOUND_SHM_SAMP_SIZE		(32*1024)

#define SAMPLE_SIZE		2
#define NUM_CHANNELS		2
#define SAMPLE_CHAN_SIZE	(SAMPLE_SIZE * NUM_CHANNELS)

STRUCT(Doc_reg) {
	double	dsamp_ev;
	double	dsamp_ev2;
	double	complete_dsamp;
	int	samps_left;
	word32	cur_acc;
	word32	cur_inc;
	word32	cur_start;
	word32	cur_end;
	word32	cur_mask;
	int	size_bytes;
	int	event;
	int	running;
	int	has_irq_pending;
	word32	freq;
	word32	vol;
	word32	waveptr;
	word32	ctl;
	word32	wavesize;
	word32	last_samp_val;
};

/* prototypes for win32snd_driver.c functions */
void win32snd_init(word32 *);
void win32snd_shutdown();
void win32snd_shutdown();
void child_sound_init_win32();
int win32_send_audio(byte *ptr, int size);


/* Prototypes for macsnd_driver.c functions */
int mac_send_audio(byte *ptr, int in_size);
void child_sound_init_mac();
void macsnd_init(word32 *shmaddr);

/* Prototypes for android_sound_driver.c functions */
int android_send_audio(byte *ptr, int in_size);
void child_sound_init_android();
void android_snd_init(word32 *shmaddr);
