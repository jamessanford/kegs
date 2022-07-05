/************************************************************************/
/*			KEGS: Apple //gs Emulator			*/
/*			Copyright 2002 by Kent Dickey			*/
/*									*/
/*		This code is covered by the GNU GPL			*/
/*									*/
/*	The KEGS web page is kegs.sourceforge.net			*/
/*	You may contact the author at: kadickey@alumni.princeton.edu	*/
/************************************************************************/

const char rcsid_joystick_driver_c[] = "@(#)$KmKId: joystick_driver.c,v 1.12 2004-10-17 21:28:48-04 kentd Exp $";

#include "defc.h"
#ifndef _MSC_VER	// OG Unknown MSFT header
#include <sys/time.h>
#endif

extern int g_joystick_native_type1;		/* in paddles.c */
extern int g_joystick_native_type2;		/* in paddles.c */
extern int g_joystick_native_type;		/* in paddles.c */
extern int g_paddle_buttons;
extern int g_paddle_val[];

void
joystick_init()
{
	g_joystick_native_type1 = -1;
	g_joystick_native_type2 = -1;
	g_joystick_native_type = -1;
}

void
joystick_update(double dcycs)
{
	int	i;

	for(i = 0; i < 4; i++) {
		g_paddle_val[i] = 32767;
	}
	g_paddle_buttons = 0xc;
}

void
joystick_update_buttons()
{
}

// OG
void joystick_shut()
{
}

