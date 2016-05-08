package kolmogorov;

import java.io.*;

class UTM_AG {
/*
 *	NewTape=UTM_AG(TM,TRS,EWT,TT)
 *			 
		TM         	-Description of Turing's Machine in ASCII binary
		TRS		-Maximum number of TRS
		EWT		-Empty working tape (Initially a string of 0s)
		TT		-Target Tape (desired output)
					   	  
		Maximum 64 states (000000 - 111111)
			State 111111 is HALT

	ON OUTPUT:
		1) The processed tape if HALT
		2) Idem if Transtions is exceeded
		3) Null Tape if over/under flow occurs
  */
  public static String OutTape(String TM,int TRS,String EWT,String TT) throws Exception{
  String WT,LT,RT,sO;
//
  WT=EWT;										// Original Working Tape is empty
  int    WTL=WT.length();							// Size of working tape
  int    HP=WT.length()/2;							// Head is positioned in the middle
//
  int PtrTM;									// Pointer to TM
  int I,M,LLT,LRT;								// Input and Movement
  LT=WT.substring(0,HP);							// Left tape minus last bit
  LLT=LT.length();								// Length of LeftTape
  RT=WT.substring(HP+1);	 						// Right tape minus first bit
  LRT=RT.length();								// Length of RighTape
  sO="";
  int Q=0;										// Start in state Q=0
  for (int i=1;i<=TRS;i++){
  	 I=Integer.parseInt(WT.substring(HP,HP+1));			// Input symbol in Tape
  	 PtrTM=Q*16+I*8;								// Position in the TM
  	 sO=TM.substring(PtrTM,PtrTM+1);
  	 M=Integer.parseInt(TM.substring(PtrTM+1,PtrTM+2));		// Movement
  	 if (M==0){
  	 	HP++;									// Move RIGHT
  	 	if (HP==WTL){
//	 		System.out.println("\nRight limit of tape exceeded");
  	 		return "";
  	 	}//endif
		LT=LT+sO;
		LLT++;
		WT=LT+RT;
		LRT--;
		RT=RT.substring(1);}
  	 else{	// M==1
  	 	HP--;									// Move LEFT
	  	if (HP<0){
// 			System.out.println("\nLeft limit of tape exceeded");
  	 		return "";
  	 	}//endif
  	 	RT=sO+RT;
  	 	LRT++;
		WT=LT+RT;
		LLT--;
  	 	LT=LT.substring(0,LLT);
  	 }//endif
	 Q=0;										// Next State
	 for (int j=PtrTM+2;j<PtrTM+8;j++){
	 	Q=Q*2;
	 	if (TM.substring(j,j+1).equals("1")) Q++;
	 }//endFor
  	 if (Q==63)
// 	 	System.out.println("\n\nHALT state was reached");
  	 	return WT;								// *** Processed Tape
  	 //endif
//  System.out.println("\nMaximum number of TRS was reached");
    }//endFor
//    System.out.println(WT);
//    BufferedReader Kbr	= new BufferedReader(new InputStreamReader(System.in));
//	String Resp=Kbr.readLine();
  return WT;
 }//endOutTape

  public static String Num2asciiBin(int estado_i,int bits){
  	String asciiBin="";
	for (int i=0;i<bits;i++){
		if (estado_i%2==1)
			asciiBin="1"+asciiBin;
		else
			asciiBin="0"+asciiBin;
		estado_i=estado_i/2;
	}//endFor
	return asciiBin;
  }//endNum2asciiBin

  public static int asciiBin2Num (String estado_i,int bits){
	int Q=0;
	if (estado_i.substring(0,1).equals("1")) Q=1;
	for (int i=1;i<bits;i++){
	 	Q=Q*2;
	 	if (estado_i.substring(i,i+1).equals("1")) Q++;
	 }//endFor
	return Q;
  }//end_asciiBin2Num
  

  public static int Complejidad(String TM,int TRS,String EWT, boolean CT) throws Exception {
  //	CT --> Crea TM
  String WT=EWT;
  int    WTL=WT.length();							// Size of tape
  int    HP=WT.length()/2;
//  PrintStream TracePS=new PrintStream(new FileOutputStream(new File("Trace.txt")));
  boolean Estado[] = new boolean[64];
  Estado[0]=true;
  int PtrTM;									// Pointer to TM
  int I,M;										// Input and Movement
  String LT=WT.substring(0,HP);						// Left tape minus last bit
  int LLT=LT.length();								// Length of LeftTape
  String RT=WT.substring(HP+1);	 					// Right tape minus first bit
  int LRT=RT.length();								// Length of RighTape
  String sO="";
  int Q=0;										// Start in state Q=0
//  if (CT) {TracePS.println(WT.substring(HP,HP+80));
//           TracePS.println("SE "+Q);}
  for (int i=1;i<=TRS;i++){
  	 I=Integer.parseInt(WT.substring(HP,HP+1));			// Input symbol in Tape
  	 PtrTM=Q*16+I*8;								// Position in the TM
  	 sO=TM.substring(PtrTM,PtrTM+1);
  	 M=Integer.parseInt(TM.substring(PtrTM+1,PtrTM+2));		// Movement
  	 if (M==0){
  	 	HP++;									// Move RIGHT
  	 	if (HP==WTL){
  	 		break;							// From outermost FOR
  	 	}//endif
		LT=LT+sO;
		LLT++;
		WT=LT+RT;
		LRT--;
		RT=RT.substring(1);}
  	 else{	// M==1
  	 	HP--;									// Move LEFT
	  	if (HP<0){
  	 		break;							// From outermost FOR
  	 	}//endif
  	 	RT=sO+RT;
  	 	LRT++;
		WT=LT+RT;
		LLT--;
  	 	LT=LT.substring(0,LLT);
  	 }//endif
	 Q=0;										// Next State
	 for (int j=PtrTM+2;j<PtrTM+8;j++){
	 	Q=Q*2;
	 	if (TM.substring(j,j+1).equals("1")) Q++;
	 }//endFor
//     if (CT) {TracePS.println(WT.substring(HP,HP+80));TracePS.println("SE "+Q);}
	 Estado[Q]=true;
  	 if (Q==63){
  	 	break;
  	 }//endif
  }//endfor
  int NumEstados=0;
  for (int i=0;i<63;i++)
  	if (Estado[i])
		NumEstados++;
  	//endIf
  //endFor
  if (!CT)
  	return NumEstados;							// Fin si no pide TM
  //endif
/**********************************************************************
 *
 *	Este segmento de código solo se ejecuta para la MT más corta
 *
 **********************************************************************/
  PrintStream TgtTAPEps=new PrintStream(new FileOutputStream(new File("TargetTape.txt")));
  TgtTAPEps.println(WT);
  int [] Equ=new int [NumEstados];
  int j=0;
  for (int i=0;i<NumEstados;i++){
  	if (Estado[i]){
  		Equ[i]=j;								// Nuevo índice
  		j++;
  	}//EndIf
  }//endFor
  String PTM="";									//Packed Turing Machine
  int Ei_0=0,Ei_1=0;
  for (int i=0;i<64;i++){
/*
 *	SI EL ESTADO EXISTE EN LA MT REDUCIDA,
 *  ENCUENTRA EQUIVALENCIA A NUEVOS ESTADOS
 */
  	if (Estado[i]){
		int ix16=i*16,ix16p2=ix16+2,ix16p8=ix16+8,ix16p10=ix16+10,ix16p16=ix16+16;
  		PTM=PTM+TM.substring(ix16,ix16p2);				// Los bits de I-O para X=0
	  	Ei_0=asciiBin2Num(TM.substring(ix16p2,ix16p8),6);	// El estado destino PARA X=0
		boolean found=false;
	  	for (j=0;j<64;j++){
	  		if (Estado[Ei_0]){					// Si existe el estado destino
	  			PTM=PTM+Num2asciiBin(Equ[j],6);		// pon el nuevo índice
	  			found=true;
	  			break;
	  		}//endIf
	  	}//endFor
	  	if (!found)								// Si no existe el estado destino
	  		PTM=PTM+"111111";						// pon estado default "HALT"
	  	//endIf
  		PTM=PTM+TM.substring(ix16p8,ix16p10);			// Los bits de I_O para X=1
	  	Ei_1=asciiBin2Num(TM.substring(ix16p10,ix16p16),6);	// El estado destino para X=1
		found=false;
	  	for (j=0;j<64;j++){
	  		if (Estado[Ei_1]){					// Si existe el estado destino
	  			PTM=PTM+Num2asciiBin(Equ[j],6);		// pon el nuevo índice
	  			found=true;
	  			break;
	  		}//endIf
	  	}//endFor
	  	if (!found)								// Si no existe el estado destino
	  		PTM=PTM+"111111";						// pon estado default "HALT"
	  	//endIf
	}//endIf
  }//endFor
  PrintStream PkTMps=new PrintStream(new FileOutputStream(new File("PackedTM.txt")));
  PkTMps.println(PTM);
/*
 *		TMs INTERPRETADAS
 */
  PkTMps=new PrintStream(new FileOutputStream(new File("TargetViewTM.xls")));
  int NS=0;
  BufferedReader Fbr;
  Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("TargetTM.txt"))));
  TM=Fbr.readLine();
  String sM;
  PtrTM=0;
  for (int i=0;i<64;i++){
  	I =Integer.parseInt(TM.substring(PtrTM,PtrTM+1));
	sM="R";
  	if (TM.substring(PtrTM+1,PtrTM+2).equals("1")) sM="L";
  	NS=asciiBin2Num(TM.substring(PtrTM+2,PtrTM+8),6);
  	PkTMps.printf(I+"\t"+sM+"\t%2.0f",(float)NS);
	PtrTM=PtrTM+8;  	
  	I =Integer.parseInt(TM.substring(PtrTM,PtrTM+1));
	sM="R";
  	if (TM.substring(PtrTM+1,PtrTM+2).equals("1")) sM="L";
  	NS=asciiBin2Num(TM.substring(PtrTM+2,PtrTM+8),6);
  	PkTMps.printf("\t"+I+"\t"+sM+"\t%2.0f",(float)NS);
  	PkTMps.println();
  }//endFor
//
  PkTMps=new PrintStream(new FileOutputStream(new File("PackedViewTM.xls")));
  Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("PackedTM.txt"))));
  TM=Fbr.readLine();
  PtrTM=0;
  for (int i=0;i<TM.length()/16;i++){
  	I =Integer.parseInt(TM.substring(PtrTM,PtrTM+1));
  	M =Integer.parseInt(TM.substring(PtrTM+1,PtrTM+2));
  	NS=asciiBin2Num(TM.substring(PtrTM+2,PtrTM+8),6);
  	PkTMps.printf(I+"\t"+M+"\t%2.0f",(float)NS);
	PtrTM=PtrTM+8;  	
  	I =Integer.parseInt(TM.substring(PtrTM,PtrTM+1));
  	M =Integer.parseInt(TM.substring(PtrTM+1,PtrTM+2));
  	NS=asciiBin2Num(TM.substring(PtrTM+2,PtrTM+8),6);
  	PkTMps.printf("\t"+I+"\t"+M+"\t%2.0f",(float)NS);
  	PkTMps.println();
  }//endFor
  return NumEstados;
  }//endMethod
} //endClass
