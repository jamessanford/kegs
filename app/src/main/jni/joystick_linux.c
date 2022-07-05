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
#include <sys/time.h>
# include <linux/joystick.h>

extern int g_joystick_native_type1;		/* in paddles.c */
extern int g_joystick_native_type2;		/* in paddles.c */
extern int g_joystick_native_type;		/* in paddles.c */
extern int g_paddle_buttons;
extern int g_paddle_val[];


const char *g_joystick_dev = "/dev/js0";	/* default joystick dev file */
#define MAX_JOY_NAME	128

int	g_joystick_native_fd = -1;
int	g_joystick_num_axes = 0;
int	g_joystick_num_buttons = 0;


void
joystick_init()
{
	char	joy_name[MAX_JOY_NAME];
	int	version;
	int	fd;
	int	i;

	fd = open(g_joystick_dev, O_RDONLY | O_NONBLOCK);
	if(fd < 0) {
		printf("Unable to open joystick dev file: %s, errno: %d\n",
			g_joystick_dev, errno);
		printf("Defaulting to mouse joystick\n");
		return;
	}

	strcpy(&joy_name[0], "Unknown Joystick");
	version = 0x800;

	ioctl(fd, JSIOCGNAME(MAX_JOY_NAME), &joy_name[0]);
	ioctl(fd, JSIOCGAXES, &g_joystick_num_axes);
	ioctl(fd, JSIOCGBUTTONS, &g_joystick_num_buttons);
	ioctl(fd, JSIOCGVERSION, &version);

	printf("Detected joystick: %s [%d axes, %d buttons vers: %08x]\n",
		joy_name, g_joystick_num_axes, g_joystick_num_buttons,
		version);

	g_joystick_native_type1 = 1;
	g_joystick_native_type2 = -1;
	g_joystick_native_fd = fd;
	for(i = 0; i < 4; i++) {
		g_paddle_val[i] = 32767;
	}
	g_paddle_buttons = 0xc;

	joystick_update(0.0);
}

/* joystick_update_linux() called from paddles.c.  Update g_paddle_val[] */
/*  and g_paddle_buttons with current information */
void
joystick_update(double dcycs)
{
	struct js_event js;	/* the linux joystick event record */
	int	mask;
	int	val;
	int	num;
	int	type;
	int	ret;
	int	len;
	int	i;

	/* suck up to 20 events, then give up */
	len = sizeof(struct js_event);
	for(i = 0; i < 20; i++) {
		ret = read(g_joystick_native_fd, &js, len);
		if(ret != len) {
			/* just get out */
			break;
		}
		type = js.type & ~JS_EVENT_INIT;
		val = js.value;
		num = js.number & 3;		/* clamp to 0-3 */
		switch(type) {
		case JS_EVENT_BUTTON:
			mask = 1 << num;
			if(val) {
				val = mask;
			}
			g_paddle_buttons = (g_paddle_buttons & ~mask) | val;
			break;
		case JS_EVENT_AXIS:
			/* val is -32767 to +32767 */
			g_paddle_val[num] = val;
			break;
		}
	}

	if(i > 0) {
		paddle_update_trigger_dcycs(dcycs);
	}
}

void
joystick_update_buttons()
{
}

// OG
void joystick_shut()
{
}

