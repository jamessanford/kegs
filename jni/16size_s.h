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

	.word	inst00_16	/*1*/ 	/* brk */
	.word	inst01_16	/*1*/ 	/* ORA (Dloc,X) */
	.word	inst02_16	/*1*/ 	/* COP */
	.word	inst03_16	/*1*/ 	/* ORA Disp8,S */
	.word	inst04_16	/*1*/ 	/* TSB Dloc */
	.word	inst05_16	/*1*/ 	/* ORA Dloc */
	.word	inst06_16	/*1*/ 	/* ASL Dloc */
	.word	inst07_16	/*1*/ 	/* ORA [Dloc] */
	.word	inst08_16	/*0*/ 	/* PHP */
	.word	inst09_16	/*4*/ 	/* ORA #imm */
	.word	inst0a_16	/*0*/ 	/* ASL a */
	.word	inst0b_16	/*0*/ 	/* PHD */
	.word	inst0c_16	/*2*/ 	/* TSB abs */
	.word	inst0d_16	/*2*/ 	/* ORA abs */
	.word	inst0e_16	/*2*/ 	/* ASL abs */
	.word	inst0f_16	/*3*/ 	/* ORA long */
	.word	inst10_16	/*1*/ 	/* BPL disp8 */
	.word	inst11_16	/*1*/ 	/* ORA (),y */
	.word	inst12_16	/*1*/ 	/* ORA () */
	.word	inst13_16	/*1*/ 	/* ORA (disp8,s),y */
	.word	inst14_16	/*1*/ 	/* TRB Dloc */
	.word	inst15_16	/*1*/ 	/* ORA Dloc,x */
	.word	inst16_16	/*1*/ 	/* ASL Dloc,x */
	.word	inst17_16	/*1*/ 	/* ORA [],y */
	.word	inst18_16	/*0*/ 	/* clc */
	.word	inst19_16	/*2*/ 	/* ORA abs,y */
	.word	inst1a_16	/*0*/ 	/* INC a */
	.word	inst1b_16	/*0*/ 	/* TCS */
	.word	inst1c_16	/*2*/ 	/* TRB Abs */
	.word	inst1d_16	/*2*/ 	/* ORA Abs,X */
	.word	inst1e_16	/*2*/ 	/* ASL abs,x */
	.word	inst1f_16	/*3*/ 	/* ORA Long,x */
	.word	inst20_16	/*2*/ 	/* JSR abs */
	.word	inst21_16	/*1*/ 	/* AND (Dloc,X) */
	.word	inst22_16	/*3*/ 	/* JSL Abslong */
	.word	inst23_16	/*1*/ 	/* AND Disp8,S */
	.word	inst24_16	/*1*/ 	/* BIT Dloc */
	.word	inst25_16	/*1*/ 	/* AND Dloc */
	.word	inst26_16	/*1*/ 	/* ROL Dloc */
	.word	inst27_16	/*1*/ 	/* AND [Dloc] */
	.word	inst28_16	/*0*/ 	/* PLP */
	.word	inst29_16	/*4*/ 	/* AND #imm */
	.word	inst2a_16	/*0*/ 	/* ROL a */
	.word	inst2b_16	/*0*/ 	/* PLD */
	.word	inst2c_16	/*2*/ 	/* BIT abs */
	.word	inst2d_16	/*2*/ 	/* AND abs */
	.word	inst2e_16	/*2*/ 	/* ROL abs */
	.word	inst2f_16	/*3*/ 	/* AND long */
	.word	inst30_16	/*1*/ 	/* BMI disp8 */
	.word	inst31_16	/*1*/ 	/* AND (),y */
	.word	inst32_16	/*1*/ 	/* AND () */
	.word	inst33_16	/*1*/ 	/* AND (disp8,s),y */
	.word	inst34_16	/*1*/ 	/* BIT Dloc,X */
	.word	inst35_16	/*1*/ 	/* AND Dloc,x */
	.word	inst36_16	/*1*/ 	/* ROL Dloc,x */
	.word	inst37_16	/*1*/ 	/* AND [],y */
	.word	inst38_16	/*0*/ 	/* SEC */
	.word	inst39_16	/*2*/ 	/* AND abs,y */
	.word	inst3a_16	/*0*/ 	/* DEC a */
	.word	inst3b_16	/*0*/ 	/* TSC */
	.word	inst3c_16	/*2*/ 	/* BIT Abs,X */
	.word	inst3d_16	/*2*/ 	/* AND Abs,X */
	.word	inst3e_16	/*2*/ 	/* ROL abs,x */
	.word	inst3f_16	/*3*/ 	/* AND Long,x */
	.word	inst40_16	/*0*/ 	/* RTI */
	.word	inst41_16	/*1*/ 	/* EOR (Dloc,X) */
	.word	inst42_16	/*1*/ 	/* WDM */
	.word	inst43_16	/*1*/ 	/* EOR Disp8,S */
	.word	inst44_16	/*2*/ 	/* MVP I,J */
	.word	inst45_16	/*1*/ 	/* EOR Dloc */
	.word	inst46_16	/*1*/ 	/* LSR Dloc */
	.word	inst47_16	/*1*/ 	/* EOR [Dloc] */
	.word	inst48_16	/*0*/ 	/* PHA */
	.word	inst49_16	/*4*/ 	/* EOR #imm */
	.word	inst4a_16	/*0*/ 	/* LSR a */
	.word	inst4b_16	/*0*/ 	/* PHK */
	.word	inst4c_16	/*2*/ 	/* JMP abs */
	.word	inst4d_16	/*2*/ 	/* EOR abs */
	.word	inst4e_16	/*2*/ 	/* LSR abs */
	.word	inst4f_16	/*3*/ 	/* EOR long */
	.word	inst50_16	/*1*/ 	/* BVC disp8 */
	.word	inst51_16	/*1*/ 	/* EOR (),y */
	.word	inst52_16	/*1*/ 	/* EOR () */
	.word	inst53_16	/*1*/ 	/* EOR (disp8,s),y */
	.word	inst54_16	/*2*/ 	/* MVN I,J */
	.word	inst55_16	/*1*/ 	/* EOR Dloc,x */
	.word	inst56_16	/*1*/ 	/* LSR Dloc,x */
	.word	inst57_16	/*1*/ 	/* EOR [],y */
	.word	inst58_16	/*0*/ 	/* CLI */
	.word	inst59_16	/*2*/ 	/* EOR abs,y */
	.word	inst5a_16	/*0*/ 	/* PHY */
	.word	inst5b_16	/*0*/ 	/* TCD */
	.word	inst5c_16	/*3*/ 	/* JMP Long */
	.word	inst5d_16	/*2*/ 	/* EOR Abs,X */
	.word	inst5e_16	/*2*/ 	/* LSR abs,x */
	.word	inst5f_16	/*3*/ 	/* EOR Long,x */
	.word	inst60_16	/*0*/ 	/* RTS */
	.word	inst61_16	/*1*/ 	/* ADC (Dloc,X) */
	.word	inst62_16	/*2*/ 	/* PER DISP16 */
	.word	inst63_16	/*1*/ 	/* ADC Disp8,S */
	.word	inst64_16	/*1*/ 	/* STZ Dloc */
	.word	inst65_16	/*1*/ 	/* ADC Dloc */
	.word	inst66_16	/*1*/ 	/* ROR Dloc */
	.word	inst67_16	/*1*/ 	/* ADC [Dloc] */
	.word	inst68_16	/*0*/ 	/* PLA */
	.word	inst69_16	/*4*/ 	/* ADC #imm */
	.word	inst6a_16	/*0*/ 	/* ROR a */
	.word	inst6b_16	/*0*/ 	/* RTL */
	.word	inst6c_16	/*2*/ 	/* JMP (abs) */
	.word	inst6d_16	/*2*/ 	/* ADC abs */
	.word	inst6e_16	/*2*/ 	/* ROR abs */
	.word	inst6f_16	/*3*/ 	/* ADC long */
	.word	inst70_16	/*1*/ 	/* BVS disp8 */
	.word	inst71_16	/*1*/ 	/* ADC (),y */
	.word	inst72_16	/*1*/ 	/* ADC () */
	.word	inst73_16	/*1*/ 	/* ADC (disp8,s),y */
	.word	inst74_16	/*1*/ 	/* STZ Dloc,X */
	.word	inst75_16	/*1*/ 	/* ADC Dloc,x */
	.word	inst76_16	/*1*/ 	/* ROR Dloc,x */
	.word	inst77_16	/*1*/ 	/* ADC [],y */
	.word	inst78_16	/*0*/ 	/* SEI */
	.word	inst79_16	/*2*/ 	/* ADC abs,y */
	.word	inst7a_16	/*0*/ 	/* PLY */
	.word	inst7b_16	/*0*/ 	/* TDC */
	.word	inst7c_16	/*2*/ 	/* JMP (abs,x) */
	.word	inst7d_16	/*2*/ 	/* ADC Abs,X */
	.word	inst7e_16	/*2*/ 	/* ROR abs,x */
	.word	inst7f_16	/*3*/ 	/* ADC Long,x */
	.word	inst80_16	/*1*/ 	/* BRA Disp8 */
	.word	inst81_16	/*1*/ 	/* STA (Dloc,X) */
	.word	inst82_16	/*2*/ 	/* BRL DISP16 */
	.word	inst83_16	/*1*/ 	/* STA Disp8,S */
	.word	inst84_16	/*1*/ 	/* STY Dloc */
	.word	inst85_16	/*1*/ 	/* STA Dloc */
	.word	inst86_16	/*1*/ 	/* STX Dloc */
	.word	inst87_16	/*1*/ 	/* STA [Dloc] */
	.word	inst88_16	/*0*/ 	/* DEY */
	.word	inst89_16	/*4*/ 	/* BIT #imm */
	.word	inst8a_16	/*0*/ 	/* TXA */
	.word	inst8b_16	/*0*/ 	/* PHB */
	.word	inst8c_16	/*2*/ 	/* STY abs */
	.word	inst8d_16	/*2*/ 	/* STA abs */
	.word	inst8e_16	/*2*/ 	/* STX abs */
	.word	inst8f_16	/*3*/ 	/* STA long */
	.word	inst90_16	/*1*/ 	/* BCC disp8 */
	.word	inst91_16	/*1*/ 	/* STA (),y */
	.word	inst92_16	/*1*/ 	/* STA () */
	.word	inst93_16	/*1*/ 	/* STA (disp8,s),y */
	.word	inst94_16	/*1*/ 	/* STY Dloc,X */
	.word	inst95_16	/*1*/ 	/* STA Dloc,x */
	.word	inst96_16	/*1*/ 	/* STX Dloc,y */
	.word	inst97_16	/*1*/ 	/* STA [],y */
	.word	inst98_16	/*0*/ 	/* TYA */
	.word	inst99_16	/*2*/ 	/* STA abs,y */
	.word	inst9a_16	/*0*/ 	/* TXS */
	.word	inst9b_16	/*0*/ 	/* TXY */
	.word	inst9c_16	/*2*/ 	/* STX abs */
	.word	inst9d_16	/*2*/ 	/* STA Abs,X */
	.word	inst9e_16	/*2*/ 	/* STZ abs,x */
	.word	inst9f_16	/*3*/ 	/* STA Long,x */
	.word	insta0_16	/*5*/ 	/* LDY #imm */
	.word	insta1_16	/*1*/ 	/* LDA (Dloc,X) */
	.word	insta2_16	/*5*/ 	/* LDX #imm */
	.word	insta3_16	/*1*/ 	/* LDA Disp8,S */
	.word	insta4_16	/*1*/ 	/* LDY Dloc */
	.word	insta5_16	/*1*/ 	/* LDA Dloc */
	.word	insta6_16	/*1*/ 	/* LDX Dloc */
	.word	insta7_16	/*1*/ 	/* LDA [Dloc] */
	.word	insta8_16	/*0*/ 	/* TAY */
	.word	insta9_16	/*4*/ 	/* LDA #imm */
	.word	instaa_16	/*0*/ 	/* TAX */
	.word	instab_16	/*0*/ 	/* PLB */
	.word	instac_16	/*2*/ 	/* LDY abs */
	.word	instad_16	/*2*/ 	/* LDA abs */
	.word	instae_16	/*2*/ 	/* LDX abs */
	.word	instaf_16	/*3*/ 	/* LDA long */
	.word	instb0_16	/*1*/ 	/* BCS disp8 */
	.word	instb1_16	/*1*/ 	/* LDA (),y */
	.word	instb2_16	/*1*/ 	/* LDA () */
	.word	instb3_16	/*1*/ 	/* LDA (disp8,s),y */
	.word	instb4_16	/*1*/ 	/* LDY Dloc,X */
	.word	instb5_16	/*1*/ 	/* LDA Dloc,x */
	.word	instb6_16	/*1*/ 	/* LDX Dloc,y */
	.word	instb7_16	/*1*/ 	/* LDA [],y */
	.word	instb8_16	/*0*/ 	/* CLV */
	.word	instb9_16	/*2*/ 	/* LDA abs,y */
	.word	instba_16	/*0*/ 	/* TSX */
	.word	instbb_16	/*0*/ 	/* TYX */
	.word	instbc_16	/*2*/ 	/* LDY abs,x */
	.word	instbd_16	/*2*/ 	/* LDA Abs,X */
	.word	instbe_16	/*2*/ 	/* LDX abs,y */
	.word	instbf_16	/*3*/ 	/* LDA Long,x */
	.word	instc0_16	/*5*/ 	/* CPY #Imm */
	.word	instc1_16	/*1*/ 	/* CMP (Dloc,X) */
	.word	instc2_16	/*1*/ 	/* REP #8bit */
	.word	instc3_16	/*1*/ 	/* CMP Disp8,S */
	.word	instc4_16	/*1*/ 	/* CPY Dloc */
	.word	instc5_16	/*1*/ 	/* CMP Dloc */
	.word	instc6_16	/*1*/ 	/* DEC Dloc */
	.word	instc7_16	/*1*/ 	/* CMP [Dloc] */
	.word	instc8_16	/*0*/ 	/* INY */
	.word	instc9_16	/*4*/ 	/* CMP #imm */
	.word	instca_16	/*0*/ 	/* DEX */
	.word	instcb_16	/*0*/ 	/* WAI */
	.word	instcc_16	/*2*/ 	/* CPY abs */
	.word	instcd_16	/*2*/ 	/* CMP abs */
	.word	instce_16	/*2*/ 	/* DEC abs */
	.word	instcf_16	/*3*/ 	/* CMP long */
	.word	instd0_16	/*1*/ 	/* BNE disp8 */
	.word	instd1_16	/*1*/ 	/* CMP (),y */
	.word	instd2_16	/*1*/ 	/* CMP () */
	.word	instd3_16	/*1*/ 	/* CMP (disp8,s),y */
	.word	instd4_16	/*1*/ 	/* PEI Dloc */
	.word	instd5_16	/*1*/ 	/* CMP Dloc,x */
	.word	instd6_16	/*1*/ 	/* DEC Dloc,x */
	.word	instd7_16	/*1*/ 	/* CMP [],y */
	.word	instd8_16	/*0*/ 	/* CLD */
	.word	instd9_16	/*2*/ 	/* CMP abs,y */
	.word	instda_16	/*0*/ 	/* PHX */
	.word	instdb_16	/*0*/ 	/* STP */
	.word	instdc_16	/*2*/ 	/* JML (Abs) */
	.word	instdd_16	/*2*/ 	/* CMP Abs,X */
	.word	instde_16	/*2*/ 	/* DEC abs,x */
	.word	instdf_16	/*3*/ 	/* CMP Long,x */
	.word	inste0_16	/*5*/ 	/* CPX #Imm */
	.word	inste1_16	/*1*/ 	/* SBC (Dloc,X) */
	.word	inste2_16	/*1*/ 	/* SEP #8bit */
	.word	inste3_16	/*1*/ 	/* SBC Disp8,S */
	.word	inste4_16	/*1*/ 	/* CPX Dloc */
	.word	inste5_16	/*1*/ 	/* SBC Dloc */
	.word	inste6_16	/*1*/ 	/* INC Dloc */
	.word	inste7_16	/*1*/ 	/* SBC [Dloc] */
	.word	inste8_16	/*0*/ 	/* INX */
	.word	inste9_16	/*4*/ 	/* SBC #imm */
	.word	instea_16	/*0*/ 	/* NOP */
	.word	insteb_16	/*0*/ 	/* XBA */
	.word	instec_16	/*2*/ 	/* CPX abs */
	.word	insted_16	/*2*/ 	/* SBC abs */
	.word	instee_16	/*2*/ 	/* INC abs */
	.word	instef_16	/*3*/ 	/* SBC long */
	.word	instf0_16	/*1*/ 	/* BEQ disp8 */
	.word	instf1_16	/*1*/ 	/* SBC (),y */
	.word	instf2_16	/*1*/ 	/* SBC () */
	.word	instf3_16	/*1*/ 	/* SBC (disp8,s),y */
	.word	instf4_16	/*2*/ 	/* PEA Imm */
	.word	instf5_16	/*1*/ 	/* SBC Dloc,x */
	.word	instf6_16	/*1*/ 	/* INC Dloc,x */
	.word	instf7_16	/*1*/ 	/* SBC [],y */
	.word	instf8_16	/*0*/ 	/* SED */
	.word	instf9_16	/*2*/ 	/* SBC abs,y */
	.word	instfa_16	/*0*/ 	/* PLX */
	.word	instfb_16	/*0*/ 	/* XCE */
	.word	instfc_16	/*2*/ 	/* JSR (Abs,x) */
	.word	instfd_16	/*2*/ 	/* SBC Abs,X */
	.word	instfe_16	/*2*/ 	/* INC abs,x */
	.word	instff_16	/*3*/ 	/* SBC Long,x */

	.block	4*16

#endif
