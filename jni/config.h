/************************************************************************/
/*			KEGS: Apple //gs Emulator			*/
/*			Copyright 2003-2004 by Kent Dickey		*/
/*									*/
/*		This code is covered by the GNU GPL			*/
/*									*/
/*	The KEGS web page is kegs.sourceforge.net			*/
/*	You may contact the author at: kadickey@alumni.princeton.edu	*/
/************************************************************************/

#ifdef INCLUDE_RCSID_C
const char rcsid_config_h[] = "@(#)$KmKId: config.h,v 1.9 2004-11-12 23:10:28-05 kentd Exp $";
#endif

#define CONF_BUF_LEN		1024
#define COPY_BUF_SIZE		4096
#define CFG_PRINTF_BUFSIZE	2048

#define CFG_PATH_MAX		1024

#define CFG_NUM_SHOWENTS	16

#define CFGTYPE_MENU		1
#define CFGTYPE_INT		2
#define CFGTYPE_DISK		3
#define CFGTYPE_FUNC		4
#define CFGTYPE_FILE		5
/* CFGTYPE limited to just 4 bits: 0-15 */

/* Cfg_menu, Cfg_dirent and Cfg_listhdr are defined in defc.h */

STRUCT(Cfg_defval) {
	Cfg_menu *menuptr;
	int	intval;
	char	*strval;
};
