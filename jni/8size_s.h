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

	.word	inst00_8	/*1*/ 	/* brk */
	.word	inst01_8	/*1*/ 	/* ORA (Dloc,X) */
	.word	inst02_8	/*1*/ 	/* COP */
	.word	inst03_8	/*1*/ 	/* ORA Disp8,S */
	.word	inst04_8	/*1*/ 	/* TSB Dloc */
	.word	inst05_8	/*1*/ 	/* ORA Dloc */
	.word	inst06_8	/*1*/ 	/* ASL Dloc */
	.word	inst07_8	/*1*/ 	/* ORA [Dloc] */
	.word	inst08_8	/*0*/ 	/* PHP */
	.word	inst09_8	/*4*/ 	/* ORA #imm */
	.word	inst0a_8	/*0*/ 	/* ASL a */
	.word	inst0b_8	/*0*/ 	/* PHD */
	.word	inst0c_8	/*2*/ 	/* TSB abs */
	.word	inst0d_8	/*2*/ 	/* ORA abs */
	.word	inst0e_8	/*2*/ 	/* ASL abs */
	.word	inst0f_8	/*3*/ 	/* ORA long */
	.word	inst10_8	/*1*/ 	/* BPL disp8 */
	.word	inst11_8	/*1*/ 	/* ORA (),y */
	.word	inst12_8	/*1*/ 	/* ORA () */
	.word	inst13_8	/*1*/ 	/* ORA (disp8,s),y */
	.word	inst14_8	/*1*/ 	/* TRB Dloc */
	.word	inst15_8	/*1*/ 	/* ORA Dloc,x */
	.word	inst16_8	/*1*/ 	/* ASL Dloc,x */
	.word	inst17_8	/*1*/ 	/* ORA [],y */
	.word	inst18_8	/*0*/ 	/* clc */
	.word	inst19_8	/*2*/ 	/* ORA abs,y */
	.word	inst1a_8	/*0*/ 	/* INC a */
	.word	inst1b_8	/*0*/ 	/* TCS */
	.word	inst1c_8	/*2*/ 	/* TRB Abs */
	.word	inst1d_8	/*2*/ 	/* ORA Abs,X */
	.word	inst1e_8	/*2*/ 	/* ASL abs,x */
	.word	inst1f_8	/*3*/ 	/* ORA Long,x */
	.word	inst20_8	/*2*/ 	/* JSR abs */
	.word	inst21_8	/*1*/ 	/* AND (Dloc,X) */
	.word	inst22_8	/*3*/ 	/* JSL Abslong */
	.word	inst23_8	/*1*/ 	/* AND Disp8,S */
	.word	inst24_8	/*1*/ 	/* BIT Dloc */
	.word	inst25_8	/*1*/ 	/* AND Dloc */
	.word	inst26_8	/*1*/ 	/* ROL Dloc */
	.word	inst27_8	/*1*/ 	/* AND [Dloc] */
	.word	inst28_8	/*0*/ 	/* PLP */
	.word	inst29_8	/*4*/ 	/* AND #imm */
	.word	inst2a_8	/*0*/ 	/* ROL a */
	.word	inst2b_8	/*0*/ 	/* PLD */
	.word	inst2c_8	/*2*/ 	/* BIT abs */
	.word	inst2d_8	/*2*/ 	/* AND abs */
	.word	inst2e_8	/*2*/ 	/* ROL abs */
	.word	inst2f_8	/*3*/ 	/* AND long */
	.word	inst30_8	/*1*/ 	/* BMI disp8 */
	.word	inst31_8	/*1*/ 	/* AND (),y */
	.word	inst32_8	/*1*/ 	/* AND () */
	.word	inst33_8	/*1*/ 	/* AND (disp8,s),y */
	.word	inst34_8	/*1*/ 	/* BIT Dloc,X */
	.word	inst35_8	/*1*/ 	/* AND Dloc,x */
	.word	inst36_8	/*1*/ 	/* ROL Dloc,x */
	.word	inst37_8	/*1*/ 	/* AND [],y */
	.word	inst38_8	/*0*/ 	/* SEC */
	.word	inst39_8	/*2*/ 	/* AND abs,y */
	.word	inst3a_8	/*0*/ 	/* DEC a */
	.word	inst3b_8	/*0*/ 	/* TSC */
	.word	inst3c_8	/*2*/ 	/* BIT Abs,X */
	.word	inst3d_8	/*2*/ 	/* AND Abs,X */
	.word	inst3e_8	/*2*/ 	/* ROL abs,x */
	.word	inst3f_8	/*3*/ 	/* AND Long,x */
	.word	inst40_8	/*0*/ 	/* RTI */
	.word	inst41_8	/*1*/ 	/* EOR (Dloc,X) */
	.word	inst42_8	/*1*/ 	/* WDM */
	.word	inst43_8	/*1*/ 	/* EOR Disp8,S */
	.word	inst44_8	/*2*/ 	/* MVP I,J */
	.word	inst45_8	/*1*/ 	/* EOR Dloc */
	.word	inst46_8	/*1*/ 	/* LSR Dloc */
	.word	inst47_8	/*1*/ 	/* EOR [Dloc] */
	.word	inst48_8	/*0*/ 	/* PHA */
	.word	inst49_8	/*4*/ 	/* EOR #imm */
	.word	inst4a_8	/*0*/ 	/* LSR a */
	.word	inst4b_8	/*0*/ 	/* PHK */
	.word	inst4c_8	/*2*/ 	/* JMP abs */
	.word	inst4d_8	/*2*/ 	/* EOR abs */
	.word	inst4e_8	/*2*/ 	/* LSR abs */
	.word	inst4f_8	/*3*/ 	/* EOR long */
	.word	inst50_8	/*1*/ 	/* BVC disp8 */
	.word	inst51_8	/*1*/ 	/* EOR (),y */
	.word	inst52_8	/*1*/ 	/* EOR () */
	.word	inst53_8	/*1*/ 	/* EOR (disp8,s),y */
	.word	inst54_8	/*2*/ 	/* MVN I,J */
	.word	inst55_8	/*1*/ 	/* EOR Dloc,x */
	.word	inst56_8	/*1*/ 	/* LSR Dloc,x */
	.word	inst57_8	/*1*/ 	/* EOR [],y */
	.word	inst58_8	/*0*/ 	/* CLI */
	.word	inst59_8	/*2*/ 	/* EOR abs,y */
	.word	inst5a_8	/*0*/ 	/* PHY */
	.word	inst5b_8	/*0*/ 	/* TCD */
	.word	inst5c_8	/*3*/ 	/* JMP Long */
	.word	inst5d_8	/*2*/ 	/* EOR Abs,X */
	.word	inst5e_8	/*2*/ 	/* LSR abs,x */
	.word	inst5f_8	/*3*/ 	/* EOR Long,x */
	.word	inst60_8	/*0*/ 	/* RTS */
	.word	inst61_8	/*1*/ 	/* ADC (Dloc,X) */
	.word	inst62_8	/*2*/ 	/* PER DISP16 */
	.word	inst63_8	/*1*/ 	/* ADC Disp8,S */
	.word	inst64_8	/*1*/ 	/* STZ Dloc */
	.word	inst65_8	/*1*/ 	/* ADC Dloc */
	.word	inst66_8	/*1*/ 	/* ROR Dloc */
	.word	inst67_8	/*1*/ 	/* ADC [Dloc] */
	.word	inst68_8	/*0*/ 	/* PLA */
	.word	inst69_8	/*4*/ 	/* ADC #imm */
	.word	inst6a_8	/*0*/ 	/* ROR a */
	.word	inst6b_8	/*0*/ 	/* RTL */
	.word	inst6c_8	/*2*/ 	/* JMP (abs) */
	.word	inst6d_8	/*2*/ 	/* ADC abs */
	.word	inst6e_8	/*2*/ 	/* ROR abs */
	.word	inst6f_8	/*3*/ 	/* ADC long */
	.word	inst70_8	/*1*/ 	/* BVS disp8 */
	.word	inst71_8	/*1*/ 	/* ADC (),y */
	.word	inst72_8	/*1*/ 	/* ADC () */
	.word	inst73_8	/*1*/ 	/* ADC (disp8,s),y */
	.word	inst74_8	/*1*/ 	/* STZ Dloc,X */
	.word	inst75_8	/*1*/ 	/* ADC Dloc,x */
	.word	inst76_8	/*1*/ 	/* ROR Dloc,x */
	.word	inst77_8	/*1*/ 	/* ADC [],y */
	.word	inst78_8	/*0*/ 	/* SEI */
	.word	inst79_8	/*2*/ 	/* ADC abs,y */
	.word	inst7a_8	/*0*/ 	/* PLY */
	.word	inst7b_8	/*0*/ 	/* TDC */
	.word	inst7c_8	/*2*/ 	/* JMP (abs,x) */
	.word	inst7d_8	/*2*/ 	/* ADC Abs,X */
	.word	inst7e_8	/*2*/ 	/* ROR abs,x */
	.word	inst7f_8	/*3*/ 	/* ADC Long,x */
	.word	inst80_8	/*1*/ 	/* BRA Disp8 */
	.word	inst81_8	/*1*/ 	/* STA (Dloc,X) */
	.word	inst82_8	/*2*/ 	/* BRL DISP16 */
	.word	inst83_8	/*1*/ 	/* STA Disp8,S */
	.word	inst84_8	/*1*/ 	/* STY Dloc */
	.word	inst85_8	/*1*/ 	/* STA Dloc */
	.word	inst86_8	/*1*/ 	/* STX Dloc */
	.word	inst87_8	/*1*/ 	/* STA [Dloc] */
	.word	inst88_8	/*0*/ 	/* DEY */
	.word	inst89_8	/*4*/ 	/* BIT #imm */
	.word	inst8a_8	/*0*/ 	/* TXA */
	.word	inst8b_8	/*0*/ 	/* PHB */
	.word	inst8c_8	/*2*/ 	/* STY abs */
	.word	inst8d_8	/*2*/ 	/* STA abs */
	.word	inst8e_8	/*2*/ 	/* STX abs */
	.word	inst8f_8	/*3*/ 	/* STA long */
	.word	inst90_8	/*1*/ 	/* BCC disp8 */
	.word	inst91_8	/*1*/ 	/* STA (),y */
	.word	inst92_8	/*1*/ 	/* STA () */
	.word	inst93_8	/*1*/ 	/* STA (disp8,s),y */
	.word	inst94_8	/*1*/ 	/* STY Dloc,X */
	.word	inst95_8	/*1*/ 	/* STA Dloc,x */
	.word	inst96_8	/*1*/ 	/* STX Dloc,y */
	.word	inst97_8	/*1*/ 	/* STA [],y */
	.word	inst98_8	/*0*/ 	/* TYA */
	.word	inst99_8	/*2*/ 	/* STA abs,y */
	.word	inst9a_8	/*0*/ 	/* TXS */
	.word	inst9b_8	/*0*/ 	/* TXY */
	.word	inst9c_8	/*2*/ 	/* STX abs */
	.word	inst9d_8	/*2*/ 	/* STA Abs,X */
	.word	inst9e_8	/*2*/ 	/* STZ abs,x */
	.word	inst9f_8	/*3*/ 	/* STA Long,x */
	.word	insta0_8	/*5*/ 	/* LDY #imm */
	.word	insta1_8	/*1*/ 	/* LDA (Dloc,X) */
	.word	insta2_8	/*5*/ 	/* LDX #imm */
	.word	insta3_8	/*1*/ 	/* LDA Disp8,S */
	.word	insta4_8	/*1*/ 	/* LDY Dloc */
	.word	insta5_8	/*1*/ 	/* LDA Dloc */
	.word	insta6_8	/*1*/ 	/* LDX Dloc */
	.word	insta7_8	/*1*/ 	/* LDA [Dloc] */
	.word	insta8_8	/*0*/ 	/* TAY */
	.word	insta9_8	/*4*/ 	/* LDA #imm */
	.word	instaa_8	/*0*/ 	/* TAX */
	.word	instab_8	/*0*/ 	/* PLB */
	.word	instac_8	/*2*/ 	/* LDY abs */
	.word	instad_8	/*2*/ 	/* LDA abs */
	.word	instae_8	/*2*/ 	/* LDX abs */
	.word	instaf_8	/*3*/ 	/* LDA long */
	.word	instb0_8	/*1*/ 	/* BCS disp8 */
	.word	instb1_8	/*1*/ 	/* LDA (),y */
	.word	instb2_8	/*1*/ 	/* LDA () */
	.word	instb3_8	/*1*/ 	/* LDA (disp8,s),y */
	.word	instb4_8	/*1*/ 	/* LDY Dloc,X */
	.word	instb5_8	/*1*/ 	/* LDA Dloc,x */
	.word	instb6_8	/*1*/ 	/* LDX Dloc,y */
	.word	instb7_8	/*1*/ 	/* LDA [],y */
	.word	instb8_8	/*0*/ 	/* CLV */
	.word	instb9_8	/*2*/ 	/* LDA abs,y */
	.word	instba_8	/*0*/ 	/* TSX */
	.word	instbb_8	/*0*/ 	/* TYX */
	.word	instbc_8	/*2*/ 	/* LDY abs,x */
	.word	instbd_8	/*2*/ 	/* LDA Abs,X */
	.word	instbe_8	/*2*/ 	/* LDX abs,y */
	.word	instbf_8	/*3*/ 	/* LDA Long,x */
	.word	instc0_8	/*5*/ 	/* CPY #Imm */
	.word	instc1_8	/*1*/ 	/* CMP (Dloc,X) */
	.word	instc2_8	/*1*/ 	/* REP #8bit */
	.word	instc3_8	/*1*/ 	/* CMP Disp8,S */
	.word	instc4_8	/*1*/ 	/* CPY Dloc */
	.word	instc5_8	/*1*/ 	/* CMP Dloc */
	.word	instc6_8	/*1*/ 	/* DEC Dloc */
	.word	instc7_8	/*1*/ 	/* CMP [Dloc] */
	.word	instc8_8	/*0*/ 	/* INY */
	.word	instc9_8	/*4*/ 	/* CMP #imm */
	.word	instca_8	/*0*/ 	/* DEX */
	.word	instcb_8	/*0*/ 	/* WAI */
	.word	instcc_8	/*2*/ 	/* CPY abs */
	.word	instcd_8	/*2*/ 	/* CMP abs */
	.word	instce_8	/*2*/ 	/* DEC abs */
	.word	instcf_8	/*3*/ 	/* CMP long */
	.word	instd0_8	/*1*/ 	/* BNE disp8 */
	.word	instd1_8	/*1*/ 	/* CMP (),y */
	.word	instd2_8	/*1*/ 	/* CMP () */
	.word	instd3_8	/*1*/ 	/* CMP (disp8,s),y */
	.word	instd4_8	/*1*/ 	/* PEI Dloc */
	.word	instd5_8	/*1*/ 	/* CMP Dloc,x */
	.word	instd6_8	/*1*/ 	/* DEC Dloc,x */
	.word	instd7_8	/*1*/ 	/* CMP [],y */
	.word	instd8_8	/*0*/ 	/* CLD */
	.word	instd9_8	/*2*/ 	/* CMP abs,y */
	.word	instda_8	/*0*/ 	/* PHX */
	.word	instdb_8	/*0*/ 	/* STP */
	.word	instdc_8	/*2*/ 	/* JML (Abs) */
	.word	instdd_8	/*2*/ 	/* CMP Abs,X */
	.word	instde_8	/*2*/ 	/* DEC abs,x */
	.word	instdf_8	/*3*/ 	/* CMP Long,x */
	.word	inste0_8	/*5*/ 	/* CPX #Imm */
	.word	inste1_8	/*1*/ 	/* SBC (Dloc,X) */
	.word	inste2_8	/*1*/ 	/* SEP #8bit */
	.word	inste3_8	/*1*/ 	/* SBC Disp8,S */
	.word	inste4_8	/*1*/ 	/* CPX Dloc */
	.word	inste5_8	/*1*/ 	/* SBC Dloc */
	.word	inste6_8	/*1*/ 	/* INC Dloc */
	.word	inste7_8	/*1*/ 	/* SBC [Dloc] */
	.word	inste8_8	/*0*/ 	/* INX */
	.word	inste9_8	/*4*/ 	/* SBC #imm */
	.word	instea_8	/*0*/ 	/* NOP */
	.word	insteb_8	/*0*/ 	/* XBA */
	.word	instec_8	/*2*/ 	/* CPX abs */
	.word	insted_8	/*2*/ 	/* SBC abs */
	.word	instee_8	/*2*/ 	/* INC abs */
	.word	instef_8	/*3*/ 	/* SBC long */
	.word	instf0_8	/*1*/ 	/* BEQ disp8 */
	.word	instf1_8	/*1*/ 	/* SBC (),y */
	.word	instf2_8	/*1*/ 	/* SBC () */
	.word	instf3_8	/*1*/ 	/* SBC (disp8,s),y */
	.word	instf4_8	/*2*/ 	/* PEA Imm */
	.word	instf5_8	/*1*/ 	/* SBC Dloc,x */
	.word	instf6_8	/*1*/ 	/* INC Dloc,x */
	.word	instf7_8	/*1*/ 	/* SBC [],y */
	.word	instf8_8	/*0*/ 	/* SED */
	.word	instf9_8	/*2*/ 	/* SBC abs,y */
	.word	instfa_8	/*0*/ 	/* PLX */
	.word	instfb_8	/*0*/ 	/* XCE */
	.word	instfc_8	/*2*/ 	/* JSR (Abs,x) */
	.word	instfd_8	/*2*/ 	/* SBC Abs,X */
	.word	instfe_8	/*2*/ 	/* INC abs,x */
	.word	instff_8	/*3*/ 	/* SBC Long,x */

	.block	4*16

#endif
