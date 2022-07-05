/************************************************************************/
/*			KEGS: Apple //gs Emulator			*/
/*			Copyright 2003 by Kent Dickey			*/
/*									*/
/*		This code is covered by the GNU GPL			*/
/*									*/
/*	The KEGS web page is kegs.sourceforge.net			*/
/*	You may contact the author at: kadickey@alumni.princeton.edu	*/
/************************************************************************/

const char rcsid_protos_macsnd_driver_h[] = "@(#)$KmKId: protos_macsnd_driver.h,v 1.2 2003-11-19 19:57:02-05 kentd Exp $";

/* END_HDR */

/* macsnd_driver.c */
void mac_snd_callback(SndChannelPtr snd_chan_ptr, SndCommand *in_sndcmd);
int mac_send_audio(byte *ptr, int in_size);
void child_sound_init_mac(void);
void macsnd_init(word32 *shmaddr);

