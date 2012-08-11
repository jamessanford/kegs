/************************************************************************/
/*			KEGS: Apple //gs Emulator			*/
/*			Copyright 2002 by Kent Dickey			*/
/*									*/
/*		This code is covered by the GNU GPL			*/
/*									*/
/*	The KEGS web page is kegs.sourceforge.net			*/
/*	You may contact the author at: kadickey@alumni.princeton.edu	*/
/************************************************************************/

#ifdef INCLUDE_RCSID_S
	.stringz "@(#)$KmKId: size_tab.h,v 1.13 2002-11-19 03:10:38-05 kadickey Exp $"
#else

	.byte 0x1,	/* 00 */ 	/* brk */
	.byte 0x1,	/* 01 */ 	/* ORA (Dloc,X) */
	.byte 0x1,	/* 02 */ 	/* COP */
	.byte 0x1,	/* 03 */ 	/* ORA Disp8,S */
	.byte 0x1,	/* 04 */ 	/* TSB Dloc */
	.byte 0x1,	/* 05 */ 	/* ORA Dloc */
	.byte 0x1,	/* 06 */ 	/* ASL Dloc */
	.byte 0x1,	/* 07 */ 	/* ORA [Dloc] */
	.byte 0x0,	/* 08 */ 	/* PHP */
	.byte 0x4,	/* 09 */ 	/* ORA #imm */
	.byte 0x0,	/* 0a */ 	/* ASL a */
	.byte 0x0,	/* 0b */ 	/* PHD */
	.byte 0x2,	/* 0c */ 	/* TSB abs */
	.byte 0x2,	/* 0d */ 	/* ORA abs */
	.byte 0x2,	/* 0e */ 	/* ASL abs */
	.byte 0x3,	/* 0f */ 	/* ORA long */
	.byte 0x1,	/* 10 */ 	/* BPL disp8 */
	.byte 0x1,	/* 11 */ 	/* ORA (),y */
	.byte 0x1,	/* 12 */ 	/* ORA () */
	.byte 0x1,	/* 13 */ 	/* ORA (disp8,s),y */
	.byte 0x1,	/* 14 */ 	/* TRB Dloc */
	.byte 0x1,	/* 15 */ 	/* ORA Dloc,x */
	.byte 0x1,	/* 16 */ 	/* ASL Dloc,x */
	.byte 0x1,	/* 17 */ 	/* ORA [],y */
	.byte 0x0,	/* 18 */ 	/* clc */
	.byte 0x2,	/* 19 */ 	/* ORA abs,y */
	.byte 0x0,	/* 1a */ 	/* INC a */
	.byte 0x0,	/* 1b */ 	/* TCS */
	.byte 0x2,	/* 1c */ 	/* TRB Abs */
	.byte 0x2,	/* 1d */ 	/* ORA Abs,X */
	.byte 0x2,	/* 1e */ 	/* ASL abs,x */
	.byte 0x3,	/* 1f */ 	/* ORA Long,x */
	.byte 0x2,	/* 20 */ 	/* JSR abs */
	.byte 0x1,	/* 21 */ 	/* AND (Dloc,X) */
	.byte 0x3,	/* 22 */ 	/* JSL Abslong */
	.byte 0x1,	/* 23 */ 	/* AND Disp8,S */
	.byte 0x1,	/* 24 */ 	/* BIT Dloc */
	.byte 0x1,	/* 25 */ 	/* AND Dloc */
	.byte 0x1,	/* 26 */ 	/* ROL Dloc */
	.byte 0x1,	/* 27 */ 	/* AND [Dloc] */
	.byte 0x0,	/* 28 */ 	/* PLP */
	.byte 0x4,	/* 29 */ 	/* AND #imm */
	.byte 0x0,	/* 2a */ 	/* ROL a */
	.byte 0x0,	/* 2b */ 	/* PLD */
	.byte 0x2,	/* 2c */ 	/* BIT abs */
	.byte 0x2,	/* 2d */ 	/* AND abs */
	.byte 0x2,	/* 2e */ 	/* ROL abs */
	.byte 0x3,	/* 2f */ 	/* AND long */
	.byte 0x1,	/* 30 */ 	/* BMI disp8 */
	.byte 0x1,	/* 31 */ 	/* AND (),y */
	.byte 0x1,	/* 32 */ 	/* AND () */
	.byte 0x1,	/* 33 */ 	/* AND (disp8,s),y */
	.byte 0x1,	/* 34 */ 	/* BIT Dloc,X */
	.byte 0x1,	/* 35 */ 	/* AND Dloc,x */
	.byte 0x1,	/* 36 */ 	/* ROL Dloc,x */
	.byte 0x1,	/* 37 */ 	/* AND [],y */
	.byte 0x0,	/* 38 */ 	/* SEC */
	.byte 0x2,	/* 39 */ 	/* AND abs,y */
	.byte 0x0,	/* 3a */ 	/* DEC a */
	.byte 0x0,	/* 3b */ 	/* TSC */
	.byte 0x2,	/* 3c */ 	/* BIT Abs,X */
	.byte 0x2,	/* 3d */ 	/* AND Abs,X */
	.byte 0x2,	/* 3e */ 	/* ROL abs,x */
	.byte 0x3,	/* 3f */ 	/* AND Long,x */
	.byte 0x0,	/* 40 */ 	/* RTI */
	.byte 0x1,	/* 41 */ 	/* EOR (Dloc,X) */
	.byte 0x1,	/* 42 */ 	/* WDM */
	.byte 0x1,	/* 43 */ 	/* EOR Disp8,S */
	.byte 0x2,	/* 44 */ 	/* MVP I,J */
	.byte 0x1,	/* 45 */ 	/* EOR Dloc */
	.byte 0x1,	/* 46 */ 	/* LSR Dloc */
	.byte 0x1,	/* 47 */ 	/* EOR [Dloc] */
	.byte 0x0,	/* 48 */ 	/* PHA */
	.byte 0x4,	/* 49 */ 	/* EOR #imm */
	.byte 0x0,	/* 4a */ 	/* LSR a */
	.byte 0x0,	/* 4b */ 	/* PHK */
	.byte 0x2,	/* 4c */ 	/* JMP abs */
	.byte 0x2,	/* 4d */ 	/* EOR abs */
	.byte 0x2,	/* 4e */ 	/* LSR abs */
	.byte 0x3,	/* 4f */ 	/* EOR long */
	.byte 0x1,	/* 50 */ 	/* BVC disp8 */
	.byte 0x1,	/* 51 */ 	/* EOR (),y */
	.byte 0x1,	/* 52 */ 	/* EOR () */
	.byte 0x1,	/* 53 */ 	/* EOR (disp8,s),y */
	.byte 0x2,	/* 54 */ 	/* MVN I,J */
	.byte 0x1,	/* 55 */ 	/* EOR Dloc,x */
	.byte 0x1,	/* 56 */ 	/* LSR Dloc,x */
	.byte 0x1,	/* 57 */ 	/* EOR [],y */
	.byte 0x0,	/* 58 */ 	/* CLI */
	.byte 0x2,	/* 59 */ 	/* EOR abs,y */
	.byte 0x0,	/* 5a */ 	/* PHY */
	.byte 0x0,	/* 5b */ 	/* TCD */
	.byte 0x3,	/* 5c */ 	/* JMP Long */
	.byte 0x2,	/* 5d */ 	/* EOR Abs,X */
	.byte 0x2,	/* 5e */ 	/* LSR abs,x */
	.byte 0x3,	/* 5f */ 	/* EOR Long,x */
	.byte 0x0,	/* 60 */ 	/* RTS */
	.byte 0x1,	/* 61 */ 	/* ADC (Dloc,X) */
	.byte 0x2,	/* 62 */ 	/* PER DISP16 */
	.byte 0x1,	/* 63 */ 	/* ADC Disp8,S */
	.byte 0x1,	/* 64 */ 	/* STZ Dloc */
	.byte 0x1,	/* 65 */ 	/* ADC Dloc */
	.byte 0x1,	/* 66 */ 	/* ROR Dloc */
	.byte 0x1,	/* 67 */ 	/* ADC [Dloc] */
	.byte 0x0,	/* 68 */ 	/* PLA */
	.byte 0x4,	/* 69 */ 	/* ADC #imm */
	.byte 0x0,	/* 6a */ 	/* ROR a */
	.byte 0x0,	/* 6b */ 	/* RTL */
	.byte 0x2,	/* 6c */ 	/* JMP (abs) */
	.byte 0x2,	/* 6d */ 	/* ADC abs */
	.byte 0x2,	/* 6e */ 	/* ROR abs */
	.byte 0x3,	/* 6f */ 	/* ADC long */
	.byte 0x1,	/* 70 */ 	/* BVS disp8 */
	.byte 0x1,	/* 71 */ 	/* ADC (),y */
	.byte 0x1,	/* 72 */ 	/* ADC () */
	.byte 0x1,	/* 73 */ 	/* ADC (disp8,s),y */
	.byte 0x1,	/* 74 */ 	/* STZ Dloc,X */
	.byte 0x1,	/* 75 */ 	/* ADC Dloc,x */
	.byte 0x1,	/* 76 */ 	/* ROR Dloc,x */
	.byte 0x1,	/* 77 */ 	/* ADC [],y */
	.byte 0x0,	/* 78 */ 	/* SEI */
	.byte 0x2,	/* 79 */ 	/* ADC abs,y */
	.byte 0x0,	/* 7a */ 	/* PLY */
	.byte 0x0,	/* 7b */ 	/* TDC */
	.byte 0x2,	/* 7c */ 	/* JMP (abs,x) */
	.byte 0x2,	/* 7d */ 	/* ADC Abs,X */
	.byte 0x2,	/* 7e */ 	/* ROR abs,x */
	.byte 0x3,	/* 7f */ 	/* ADC Long,x */
	.byte 0x1,	/* 80 */ 	/* BRA Disp8 */
	.byte 0x1,	/* 81 */ 	/* STA (Dloc,X) */
	.byte 0x2,	/* 82 */ 	/* BRL DISP16 */
	.byte 0x1,	/* 83 */ 	/* STA Disp8,S */
	.byte 0x1,	/* 84 */ 	/* STY Dloc */
	.byte 0x1,	/* 85 */ 	/* STA Dloc */
	.byte 0x1,	/* 86 */ 	/* STX Dloc */
	.byte 0x1,	/* 87 */ 	/* STA [Dloc] */
	.byte 0x0,	/* 88 */ 	/* DEY */
	.byte 0x4,	/* 89 */ 	/* BIT #imm */
	.byte 0x0,	/* 8a */ 	/* TXA */
	.byte 0x0,	/* 8b */ 	/* PHB */
	.byte 0x2,	/* 8c */ 	/* STY abs */
	.byte 0x2,	/* 8d */ 	/* STA abs */
	.byte 0x2,	/* 8e */ 	/* STX abs */
	.byte 0x3,	/* 8f */ 	/* STA long */
	.byte 0x1,	/* 90 */ 	/* BCC disp8 */
	.byte 0x1,	/* 91 */ 	/* STA (),y */
	.byte 0x1,	/* 92 */ 	/* STA () */
	.byte 0x1,	/* 93 */ 	/* STA (disp8,s),y */
	.byte 0x1,	/* 94 */ 	/* STY Dloc,X */
	.byte 0x1,	/* 95 */ 	/* STA Dloc,x */
	.byte 0x1,	/* 96 */ 	/* STX Dloc,y */
	.byte 0x1,	/* 97 */ 	/* STA [],y */
	.byte 0x0,	/* 98 */ 	/* TYA */
	.byte 0x2,	/* 99 */ 	/* STA abs,y */
	.byte 0x0,	/* 9a */ 	/* TXS */
	.byte 0x0,	/* 9b */ 	/* TXY */
	.byte 0x2,	/* 9c */ 	/* STX abs */
	.byte 0x2,	/* 9d */ 	/* STA Abs,X */
	.byte 0x2,	/* 9e */ 	/* STZ abs,x */
	.byte 0x3,	/* 9f */ 	/* STA Long,x */
	.byte 0x5,	/* a0 */ 	/* LDY #imm */
	.byte 0x1,	/* a1 */ 	/* LDA (Dloc,X) */
	.byte 0x5,	/* a2 */ 	/* LDX #imm */
	.byte 0x1,	/* a3 */ 	/* LDA Disp8,S */
	.byte 0x1,	/* a4 */ 	/* LDY Dloc */
	.byte 0x1,	/* a5 */ 	/* LDA Dloc */
	.byte 0x1,	/* a6 */ 	/* LDX Dloc */
	.byte 0x1,	/* a7 */ 	/* LDA [Dloc] */
	.byte 0x0,	/* a8 */ 	/* TAY */
	.byte 0x4,	/* a9 */ 	/* LDA #imm */
	.byte 0x0,	/* aa */ 	/* TAX */
	.byte 0x0,	/* ab */ 	/* PLB */
	.byte 0x2,	/* ac */ 	/* LDY abs */
	.byte 0x2,	/* ad */ 	/* LDA abs */
	.byte 0x2,	/* ae */ 	/* LDX abs */
	.byte 0x3,	/* af */ 	/* LDA long */
	.byte 0x1,	/* b0 */ 	/* BCS disp8 */
	.byte 0x1,	/* b1 */ 	/* LDA (),y */
	.byte 0x1,	/* b2 */ 	/* LDA () */
	.byte 0x1,	/* b3 */ 	/* LDA (disp8,s),y */
	.byte 0x1,	/* b4 */ 	/* LDY Dloc,X */
	.byte 0x1,	/* b5 */ 	/* LDA Dloc,x */
	.byte 0x1,	/* b6 */ 	/* LDX Dloc,y */
	.byte 0x1,	/* b7 */ 	/* LDA [],y */
	.byte 0x0,	/* b8 */ 	/* CLV */
	.byte 0x2,	/* b9 */ 	/* LDA abs,y */
	.byte 0x0,	/* ba */ 	/* TSX */
	.byte 0x0,	/* bb */ 	/* TYX */
	.byte 0x2,	/* bc */ 	/* LDY abs,x */
	.byte 0x2,	/* bd */ 	/* LDA Abs,X */
	.byte 0x2,	/* be */ 	/* LDX abs,y */
	.byte 0x3,	/* bf */ 	/* LDA Long,x */
	.byte 0x5,	/* c0 */ 	/* CPY #Imm */
	.byte 0x1,	/* c1 */ 	/* CMP (Dloc,X) */
	.byte 0x1,	/* c2 */ 	/* REP #8bit */
	.byte 0x1,	/* c3 */ 	/* CMP Disp8,S */
	.byte 0x1,	/* c4 */ 	/* CPY Dloc */
	.byte 0x1,	/* c5 */ 	/* CMP Dloc */
	.byte 0x1,	/* c6 */ 	/* DEC Dloc */
	.byte 0x1,	/* c7 */ 	/* CMP [Dloc] */
	.byte 0x0,	/* c8 */ 	/* INY */
	.byte 0x4,	/* c9 */ 	/* CMP #imm */
	.byte 0x0,	/* ca */ 	/* DEX */
	.byte 0x0,	/* cb */ 	/* WAI */
	.byte 0x2,	/* cc */ 	/* CPY abs */
	.byte 0x2,	/* cd */ 	/* CMP abs */
	.byte 0x2,	/* ce */ 	/* DEC abs */
	.byte 0x3,	/* cf */ 	/* CMP long */
	.byte 0x1,	/* d0 */ 	/* BNE disp8 */
	.byte 0x1,	/* d1 */ 	/* CMP (),y */
	.byte 0x1,	/* d2 */ 	/* CMP () */
	.byte 0x1,	/* d3 */ 	/* CMP (disp8,s),y */
	.byte 0x1,	/* d4 */ 	/* PEI Dloc */
	.byte 0x1,	/* d5 */ 	/* CMP Dloc,x */
	.byte 0x1,	/* d6 */ 	/* DEC Dloc,x */
	.byte 0x1,	/* d7 */ 	/* CMP [],y */
	.byte 0x0,	/* d8 */ 	/* CLD */
	.byte 0x2,	/* d9 */ 	/* CMP abs,y */
	.byte 0x0,	/* da */ 	/* PHX */
	.byte 0x0,	/* db */ 	/* STP */
	.byte 0x2,	/* dc */ 	/* JML (Abs) */
	.byte 0x2,	/* dd */ 	/* CMP Abs,X */
	.byte 0x2,	/* de */ 	/* DEC abs,x */
	.byte 0x3,	/* df */ 	/* CMP Long,x */
	.byte 0x5,	/* e0 */ 	/* CPX #Imm */
	.byte 0x1,	/* e1 */ 	/* SBC (Dloc,X) */
	.byte 0x1,	/* e2 */ 	/* SEP #8bit */
	.byte 0x1,	/* e3 */ 	/* SBC Disp8,S */
	.byte 0x1,	/* e4 */ 	/* CPX Dloc */
	.byte 0x1,	/* e5 */ 	/* SBC Dloc */
	.byte 0x1,	/* e6 */ 	/* INC Dloc */
	.byte 0x1,	/* e7 */ 	/* SBC [Dloc] */
	.byte 0x0,	/* e8 */ 	/* INX */
	.byte 0x4,	/* e9 */ 	/* SBC #imm */
	.byte 0x0,	/* ea */ 	/* NOP */
	.byte 0x0,	/* eb */ 	/* XBA */
	.byte 0x2,	/* ec */ 	/* CPX abs */
	.byte 0x2,	/* ed */ 	/* SBC abs */
	.byte 0x2,	/* ee */ 	/* INC abs */
	.byte 0x3,	/* ef */ 	/* SBC long */
	.byte 0x1,	/* f0 */ 	/* BEQ disp8 */
	.byte 0x1,	/* f1 */ 	/* SBC (),y */
	.byte 0x1,	/* f2 */ 	/* SBC () */
	.byte 0x1,	/* f3 */ 	/* SBC (disp8,s),y */
	.byte 0x2,	/* f4 */ 	/* PEA Imm */
	.byte 0x1,	/* f5 */ 	/* SBC Dloc,x */
	.byte 0x1,	/* f6 */ 	/* INC Dloc,x */
	.byte 0x1,	/* f7 */ 	/* SBC [],y */
	.byte 0x0,	/* f8 */ 	/* SED */
	.byte 0x2,	/* f9 */ 	/* SBC abs,y */
	.byte 0x0,	/* fa */ 	/* PLX */
	.byte 0x0,	/* fb */ 	/* XCE */
	.byte 0x2,	/* fc */ 	/* JSR (Abs,x) */
	.byte 0x2,	/* fd */ 	/* SBC Abs,X */
	.byte 0x2,	/* fe */ 	/* INC abs,x */
	.byte 0x3,	/* ff */ 	/* SBC Long,x */



#endif
