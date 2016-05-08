package kolmogorov;

/*	ESTE PROGRAMA TRATA DE ENCONTRAR LA MÁQUINA DE TURING QUE ESCRIBA EN UNA
 *	CINTA ORIGINALMENTE LLENA DE CEROS UNA CADENA IGUAL A LA QUE SE LE PROPORCIONA
 *	EN UN ARCHIVO BINARIO-ASCII
 */
import java.io.*;
import java.util.Random;

class Kolmogorov {
static String temp;
/*************************************
 *
 *		PARAMETERS
 */
  static int LG=1024;					// Longitud del genoma
  static int EliteSize=10;				// Tamaño de la Elite
//
// ************************************
  static String EWT;					// Empty Working Tape
  static int P,N,TRS,N_2,L_2,FN=1,G,B2M,Nx2,iTmp;
  static String	Resp,TT;
  static int    WTL;					// Working Tape Length
  static double	Pc,	Pm;
  static double	fTmp,W;				// WW --> Ponderación para Solos,Pares,Tríadas,Cuartetas
  static double	BestSingleMatches=-1;
  static double BestFitness=-1;
  static double TTLen;
  static String BestTapeMatch="";
  static int root;
  static Random RandN;
  static String EliteHC;
//
  static int maxN=250,minN=1;			// Número de individuos
  static int maxT=5000000,minT=1;		// Número de transiciones
  static int maxL=50000,  minL=1;		// Longitud de la cinta
  static double	maxPc=1f, minPc=.01f;	// Probabilidad de cruza
  static double	maxPm=1f, minPm=.001f;	// Probabilidad de mutación
  static int 	maxG=10000,  minG=1;	// Número de generaciones
  static double	maxW=1, minW=0;		// Valores de W
//
  public static String genomaHC;
  public static	String genoma [];
  public static double fitnessHC;
  public static	double fitness[];
  public static	BufferedReader Fbr,Kbr,Tbr;
  
/*
 *	LEE UNA CADENA HASTA EL PRIMER <TAB>
 *		A la entrada:	Buffered Reader
 *		A la salida:	La cadena hasta (excluyendo) el 1er <TAB>
 */
   public static String	LHT(BufferedReader BR) throws Exception {
   	String Dato=BR.readLine();
   	for	(int i=0;i<Dato.length();i++){
   		if (Dato.substring(i,i+1).equals("\t"))
   			return Dato.substring(0,i);
   		//endIf
   	}//endFor
   	System.out.println("No se encontro el tabulador");
   	return "";
   }//endLHT

   public static void CreaParams() throws Exception {
	  try {
		Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("AGParams.txt"))));
	  }//endTry
	  catch (Exception e){
	    PrintStream Fps=new PrintStream(new FileOutputStream(new File("AGParams.txt")));
		Fps.println("200\t\t1) Individuos");
		Fps.println("50000\t\t2) Numero de transiciones");
		Fps.println("1000\t\t3) Longitud de la cinta");
		Fps.println("0.900\t\t4) Pc");
		Fps.println("0.010\t\t5) Pm");
		Fps.println("1000\t\t6) Generaciones");
		Fps.println("0.5\t\t7) Ponderación");
	  }//endCatch
  }//endCreaParams

  public static	void GetParams() throws	Exception {
	  Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("AGParams.txt"))));
	  N =Integer.parseInt(LHT(Fbr));			// 1) Individuos
	  TRS=Integer.parseInt(LHT(Fbr));			// 2) Transiciones
	  WTL=Integer.parseInt(LHT(Fbr));			// 3) Long. de la cinta
	  Tbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("TT.txt"))));
	  TT=Tbr.readLine();						// Cinta destino (Target Tape)
	  TTLen=TT.length();						// Longitud de la cinta deseada
	  Pc=Double.valueOf(LHT(Fbr)).floatValue();	// 4) Pc
	  Pm=Double.valueOf(LHT(Fbr)).floatValue();	// 5) Pm
	  G =Integer.parseInt(LHT(Fbr));			// 6) Generaciones
	  W=Double.valueOf(LHT(Fbr)).floatValue();	// 7) Ponderación
  }//endGetParams

  public static	void DispParams() throws Exception {
	System.out.println();
	System.out.println("1) Numero de individuos:    "+ N);
	System.out.println("2) Numero de transiciones:  "+ TRS);
	System.out.println("3) Long. de la cinta	    "+ WTL);
	System.out.printf ("4) Prob. de cruzamiento:    %8.6f\n",Pc);
	System.out.printf ("5) Prob. de mutacion:       %8.6f\n",Pm);
	System.out.println("6) Numero de generaciones:  "+ G);
	System.out.printf ("7) Factor de Ponderacion:   %8.6f\n",W);
  }//endDispParams

  public static	void ModiParams() throws Exception {
	Kbr	= new BufferedReader(new InputStreamReader(System.in));
  	String Resp;
	while (true){
		CalcParams();
		DispParams();
		System.out.print("\nModificar (S/N)? ");
		Resp=Kbr.readLine().toUpperCase();
		if (!Resp.equals("S")&!Resp.equals("N")) continue;
		if (Resp.equals("N")) return;
		if (Resp.equals("S")){
			while (true){
				System.out.print("Opcion No:       ");
				int Opt;
				try{Opt=Integer.parseInt(Kbr.readLine());}
				catch (Exception e){continue;}
				if (Opt<1|Opt>7) continue;
				System.out.print("Nuevo valor:     ");
				iTmp=1; fTmp=1;
				try{if (Opt==4|Opt==5|Opt==7) fTmp=Double.valueOf(Kbr.readLine()).floatValue();
					else	   				  iTmp=Integer.parseInt(Kbr.readLine());}
				catch (Exception e){continue;}
				boolean OK=true;
				switch(Opt) {
					case 1: {N =iTmp; if (N<minN|N>maxN) 	OK=false; break;}
					case 2: {TRS=iTmp;if (TRS<minT|TRS>maxT)OK=false; break;}
					case 3: {WTL=iTmp;if (WTL<minL|WTL>maxL)OK=false; break;}
					case 4: {Pc=fTmp; if (Pc<minPc|Pc>maxPc)OK=false; break;}
					case 5: {Pm=fTmp; if (Pm<minPm|Pm>maxPm)OK=false; break;}
					case 6: {G =iTmp; if (G<minG|G>maxG)    OK=false; break;}
					case 7:	{W =fTmp; if (W<minW|W>maxW)	OK=false; break;}
				}//endSwitch
				if (OK) break;
				System.out.println("Error en la opcion # "+Opt);
			}//endWhile
		}//endIf
	}//endWhile
  }//endModiParams

  public static	void CalcParams() {
	N_2=N/2;
	Nx2=N*2;
	genoma = new String [Nx2];
	fitness= new double [Nx2];
	L_2=LG/2;
	B2M=(int)((double)N*(double)LG*Pm);		//Bits to Mutate
  }//endCalcParams

 public static	void UpdateParams()	throws Exception {
	PrintStream Fps=new PrintStream(new FileOutputStream(new File("AGParams.txt")));
	Fps.println(N+"\t\t1) Individuos");
	Fps.println(TRS+"\t\t2) Número de transiciones");
	Fps.println(WTL+"\t\t3) Longitud de la Cinta");
	Fps.printf("%8.6f\t\t4)Pc",Pc);
		Fps.println();
	Fps.printf("%8.6f\t\t5)Pm",Pm);
		Fps.println();
	Fps.println(G+"\t\t6) Generaciones");
	Fps.printf("%8.6f\t\t7) W",W);
		Fps.println();
  }//endUpdateParams
  
  public static	void PoblacionInicial(double fitness[],	String genoma[]) throws	Exception{
	/*
	 *Genera Nx2 individuos aleatoriamente
	 */
  	for (int i=0;i<N;i++){
  		genoma[i]="";
		for (int j=0;j<LG;j++){
			if (RandN.nextFloat()<0.5)
				genoma[i]=genoma[i].concat("0");
			else
		  		genoma[i]=genoma[i].concat("1");
		  	//endIf
		}//endFor
  	}//endFor
        
  }//endPoblacionInicial
  
  public static	void PoblacionInicialHillClimber(double fitness) throws	Exception{
	/*
	 *Genera EL individuo inicial
	 */  	
        genomaHC="";
        for (int j=0;j<LG;j++){
                if (RandN.nextFloat()<0.5)
                        genomaHC=genomaHC.concat("0");
                else
                        genomaHC=genomaHC.concat("1");
                //endIf
        }//endFor
  }//endPoblacionInicialHillClimber

  public static	void Evalua(double fitness[],String	genoma[]) throws Exception{
	String OutTape;
	double Solos,Pares,Triadas,Cuartetas;
	boolean FF2,FF3,FF4;
	double WF=1+W;
	int HP,TTLen;
	for (int i=0;i<N;i++){
/*
 *		La cinta inicial está Llena de 0s
 */
		OutTape=UTM_AG.OutTape(genoma[i],TRS,EWT,TT);
		Solos=0;Pares=0;Triadas=0;Cuartetas=0;
		FF2=true;FF3=true;FF4=true;
		HP=WTL/2;
		TTLen=TT.length();
		if (OutTape.length()!=0){
			int k=0;
			for (int j=HP;j<HP+TTLen;j++){
				if (OutTape.substring(j,j+1).equals(TT.substring(k,k+1))){
					Solos++;
					if (Solos>BestSingleMatches) BestSingleMatches=Solos;
				}//endIf
				if (FF2){
					try{if (OutTape.substring(j,j+2).equals(TT.substring(k,k+2))) Pares++;}
					catch (Exception e) {FF2=false;}
				}//endif
				if (FF3){
					try{if (OutTape.substring(j,j+3).equals(TT.substring(k,k+3))) Triadas++;}
					catch (Exception e) {FF3=false;}
				}//endIf
				if (FF4){
					try{if (OutTape.substring(j,j+4).equals(TT.substring(k,k+4))) Cuartetas++;}
					catch (Exception e) {FF4=false;}
				}//endIf
				k++;
			}//endFor
			fitness[i]=Solos+WF*(Pares+WF*(Triadas+WF*Cuartetas));
//			System.out.println("Fitness="+fitness[i]);
			if (fitness[i]>BestFitness){
				BestFitness=fitness[i];
				BestTapeMatch=OutTape.substring(HP,HP+TTLen);
			}//endIf
		}else{
			fitness[i]=0d;
		}//endIf
	}//endFor
	return;
  }//endEvalua
  
  public static	void EvaluaHillClimber() throws Exception{
	String OutTape;
	double Solos,Pares,Triadas,Cuartetas;
	boolean FF2,FF3,FF4;
	double WF=1+W;
	int HP,TTLen;
	
/*
 *		La cinta inicial está Llena de 0s
 */
        OutTape=UTM_AG.OutTape(genomaHC,TRS,EWT,TT);
        Solos=0;Pares=0;Triadas=0;Cuartetas=0;
        FF2=true;FF3=true;FF4=true;
        HP=WTL/2;
        TTLen=TT.length();
        if (OutTape.length()!=0){
                int k=0;
                for (int j=HP;j<HP+TTLen;j++){
                        if (OutTape.substring(j,j+1).equals(TT.substring(k,k+1))){
                                Solos++;
                                if (Solos>BestSingleMatches) BestSingleMatches=Solos;
                        }//endIf
                        if (FF2){
                                try{if (OutTape.substring(j,j+2).equals(TT.substring(k,k+2))) Pares++;}
                                catch (Exception e) {FF2=false;}
                        }//endif
                        if (FF3){
                                try{if (OutTape.substring(j,j+3).equals(TT.substring(k,k+3))) Triadas++;}
                                catch (Exception e) {FF3=false;}
                        }//endIf
                        if (FF4){
                                try{if (OutTape.substring(j,j+4).equals(TT.substring(k,k+4))) Cuartetas++;}
                                catch (Exception e) {FF4=false;}
                        }//endIf
                        k++;
                }//endFor
                fitnessHC=Solos+WF*(Pares+WF*(Triadas+WF*Cuartetas));
//			System.out.println("Fitness="+fitness[i]);
                if (fitnessHC>BestFitness){
                        BestFitness=fitnessHC;
                        BestTapeMatch=OutTape.substring(HP,HP+TTLen);
                }//endIf
        }else{
                fitnessHC=0d;
        }//endIf
	return;
  }//endEvaluaHillClimber

  public static	void Duplica(double	fitness[],String genoma[]){
	for (int i=0;i<N;i++){
		genoma [N+i]=genoma [i];
		fitness[N+i]=fitness[i];
	}//endFor
  }//endCopia
  
  public static	void Cruza(String genoma[]){
  	int N_i,P;
	String LI,MI,RI,LN,MN,RN;
	for (int i=0;i<N_2;i++){
		if (RandN.nextFloat()>Pc) continue;
		N_i=N-i-1;
		P=-1;while(P<0|P>=L_2) P=(int)(RandN.nextFloat()*L_2);
		LI=genoma[i  ].substring(0,P);
		MI=genoma[i  ].substring(P,P+L_2);
		RI=genoma[i  ].substring(P+L_2);
		LN=genoma[N_i].substring(0,P);
		MN=genoma[N_i].substring(P,P+L_2);
		RN=genoma[N_i].substring(P+L_2);
		genoma[i  ]=LI.concat(MN).concat(RI);
		genoma[N_i]=LN.concat(MI).concat(RN);
	}//endFor
  }//endCruza

  public static	void Muta(String genoma[]) throws Exception {
	int nInd, nBit;
	for (int i=1;i<=B2M;i++){
		nInd=-1; while (nInd<0|nInd>=N)  nInd=(int)(RandN.nextFloat()*N);
		nBit=-1; while (nBit<0|nBit>=LG) nBit=(int)(RandN.nextFloat()*LG);
/*
 *		** Mutation **
 */
		String mBit="0";
		String G=genoma[nInd];
		if (nBit!=0&nBit!=LG-1){
		 if (G.substring(nBit,nBit+1).equals("0")) mBit="1";
		 genoma[nInd]=G.substring(0,nBit).concat(mBit).concat(G.substring(nBit+1));
		 continue;
		}//endif
		if (nBit==0){
			if (G.substring(0,1).equals("0")) mBit="1";
			genoma[nInd]=mBit.concat(G.substring(1));
			continue;
		}//endif
		//if (nBit==LG-1){
			if (G.substring(LG-1).equals("0")) mBit="1";
			genoma[nInd]=G.substring(0,LG-1).concat(mBit);
		//}//endIf
	}//endFor
  }//endMuta
  
    public static void MutaHillClimber() throws Exception {
	int nBit;
	
        nBit=-1; while (nBit<0|nBit>=LG) nBit=(int)(RandN.nextFloat()*LG);
/*
 *		** Mutation **
 */
        char mBit='0';
        String G=genomaHC;
                
        if (G.substring(nBit,nBit+1).equals("0")) mBit='1';
        
        char [] F=G.toCharArray();
        F[nBit]=mBit;
        genomaHC="";
        for (int i=0;i<G.length();i++)
            genomaHC=genomaHC.concat(F[i]+"");
        
  }//endMutaHillClimber
		 
/*		Selecciona los mejores N individuos
 *
 */
  public static	void Selecciona(double fitness[],String	genoma[]) {
  	double fitnessOfBest,fTmp;
  	String sTmp;
	int indexOfBest;
  	for (int i=0;i<N;i++){
	  	fitnessOfBest=fitness[i];
		indexOfBest  =i;
  		for (int j=i+1;j<Nx2;j++){
			if (fitness[j]>fitnessOfBest){
				fitnessOfBest=fitness[j];
				indexOfBest  =j;
			}//endIf
  		}//endFor
  		if (indexOfBest!=i){
  			sTmp=genoma[i];
  			genoma[i]=genoma[indexOfBest];
  			genoma[indexOfBest]=sTmp;
 			fTmp=fitness[i];
 			fitness[i]=fitness[indexOfBest];
 			fitness[indexOfBest]=fTmp;
  		}//endIf
  	}//endFor
	return;
  }//endSelecciona

   public static void ResultadosDeLaCorrida(String Elite[]) throws Exception {
/*
 *		EL MEJOR AJUSTE
 */
		System.out.printf("\n\nAjuste maximo: %15.7f\n",fitness[0]);
/*
 *		COMPLEJIDAD
 */
		String Tape=EWT;
	 	int EstadosTM=10000,EstadosTM_i,BestTMNdx=0;
	 	for (int i=0;i<EliteSize;i++){
			Tape=EWT;
	 		EstadosTM_i=UTM_AG.Complejidad(Elite[i],TRS,EWT,false);
	 		if (EstadosTM_i<EstadosTM){
	 			EstadosTM=EstadosTM_i;
	 			BestTMNdx=i;
	 		}//endIf
	 	}//endFor
/*
 *		LA MEJOR MÁQUINA
 */
		PrintStream TgtTMps=new PrintStream(new FileOutputStream(new File("TargetTM.txt")));
		TgtTMps.println(Elite[BestTMNdx]);
		System.out.println("La mejor MT encontrada esta en \"TargetTM.txt\"\n");
	 	EstadosTM=UTM_AG.Complejidad(Elite[BestTMNdx],TRS,EWT,true);
/*
 *		LA MEJOR CINTA DESTINO
 */
		System.out.println("La mejor cinta encontrada esta en \"TargetTape.txt\"\n");
/*
 *		COINCIDENCIAS
 */
		System.out.println("\na) Numero de coincidencias: "+BestSingleMatches);
		double Ratio=(double)BestSingleMatches/TTLen;
		System.out.println("b) Longitud de la cinta de datos: "+TTLen+"\n");
		System.out.printf("\t===> Tasa de coincidencias: %6.4f\n\n\n",Ratio);
	 	System.out.println("Estados en la Maquina de Turing: "+EstadosTM);
	 	System.out.println("\n\t******************************************");
	 	System.out.printf("\t*  La complejidad de Kolmogorov: %7.0f *\n",(float)(EstadosTM*16));
	 	System.out.println("\t******************************************\n");
		return;
	}//endMethod
   public static void ResultadosDeLaCorridaHillClimber(String Elite) throws Exception {
/*
 *		EL MEJOR AJUSTE
 */
		System.out.printf("\n\nAjuste maximo: %15.7f\n",BestFitness);
/*
 *		COMPLEJIDAD
 */
		String Tape=EWT;
	 	int EstadosTM=10000;
	 	
                Tape=EWT;
                EstadosTM=UTM_AG.Complejidad(EliteHC,TRS,EWT,false);
/*
 *		LA MEJOR MÁQUINA
 */
		PrintStream TgtTMps=new PrintStream(new FileOutputStream(new File("TargetTM.txt")));
		TgtTMps.println(EliteHC);
		System.out.println("La mejor MT encontrada esta en \"TargetTM.txt\"\n");
	 	EstadosTM=UTM_AG.Complejidad(EliteHC,TRS,EWT,true);
/*
 *		LA MEJOR CINTA DESTINO
 */
		System.out.println("La mejor cinta encontrada esta en \"TargetTape.txt\"\n");
/*
 *		COINCIDENCIAS
 */
		System.out.println("\na) Numero de coincidencias: "+BestSingleMatches);
		double Ratio=(double)BestSingleMatches/TTLen;
		System.out.println("b) Longitud de la cinta de datos: "+TTLen+"\n");
		System.out.printf("\t===> Tasa de coincidencias: %6.4f\n\n\n",Ratio);
	 	System.out.println("Estados en la Maquina de Turing: "+EstadosTM);
	 	System.out.println("\n\t******************************************");
	 	System.out.printf("\t*  La complejidad de Kolmogorov: %7.0f *\n",(float)(EstadosTM*16));
	 	System.out.println("\t******************************************\n");
		return;
	}//endMmethod
   public static void main(String[] args) throws Exception {
	BufferedReader Fbr,Kbr;
	while (true){
	 Kbr	= new BufferedReader(new InputStreamReader(System.in));
	 while (true){
		System.out.println("Deme la raiz del generador de numeros aleatorios");
		try{root=Integer.parseInt(Kbr.readLine());break;}
		catch (Exception e) {System.out.println("Debe ser entero!\n");}
	 }//endWhile
	 RandN = new Random(root);
	 LeeDatos.Cinta();						// Rutina externa
	 CreaParams();							//Crea archivo si no existe
	 GetParams();							//Lee parametros de archivo
	 ModiParams();							//Modifica valores
	 CalcParams();							//Calcula parametros
	 UpdateParams();						//Graba en archivo
         System.out.println("\n\nDesea ocupar un AG o un HC (A/H)");
	 String Resp=Kbr.readLine().toUpperCase();
         boolean algG=false;
	 if (Resp.equals("A")){ algG=true;}
/*
 *		EMPIEZA EL ALGORITMO GENETICO
 */
   	 if(algG){
            EWT="0";for (int i=1;i<WTL;i++) EWT=EWT+"0";
            PoblacionInicial(fitness, genoma);			//Genera la poblacion inicial
            Evalua(fitness,genoma);
            int First=1, Last=G;						//Evalua los primeros N
            int Optimo=0;boolean BestFound=false; 
            String [] Elite = new String [EliteSize];
            while (true){
                   BestSingleMatches=-1;
                   BestFitness=-1;
                   BestTapeMatch="";
                   for (int i=First;i<Last;i++){
                           Duplica(fitness,genoma);			//Duplica los primeros N
                           Cruza(genoma);					//Cruza los primeros N
                           Muta(genoma);					//Muta los primeros N
                           Evalua(fitness,genoma);				//Evalua los primeros N
                           if (BestSingleMatches==TTLen){
                                   BestFound=true;
                                   Elite[Optimo]=genoma[0];
                                   Optimo++;
                                   if (Optimo==EliteSize) break;		//Termina si hay <EliteSize> ajustes perfectos
                           }//endIf
                           Selecciona(fitness,genoma);			//Selecciona los mejores N
                           System.out.printf("GEN  %8.0f\tMatches %8.0f\n",(float)i,(float)BestSingleMatches);
                    }//endFor
                    if (!BestFound){
                           for (int i=0;i<EliteSize;i++)
                                   Elite[i]=genoma[i];
                           //endFor
                    }//Endif
                    ResultadosDeLaCorrida(Elite);
                    System.out.println("DESEA CONTINUAR LA BUSQUEDA? (S/*)");
                    if (!Kbr.readLine().toUpperCase().equals("S")) break;
                    Optimo=0;
                    First=First+G;
                    Last=Last+G;
            }//endWhile
         }//endIif
/*
 *		EMPIEZA EL HILL CLIMBER
 */
         else{
            EWT="0";for (int i=1;i<WTL;i++) EWT=EWT+"0";
            PoblacionInicialHillClimber(fitnessHC);			//Genera la poblacion inicial
            EvaluaHillClimber();						//Evalua los primeros N
            EliteHC=genomaHC;
            boolean BestFound=false;
            while (true){
                   BestSingleMatches=-1;
                   BestFitness=-1;
                   int Optimo=0;
                   String [] Elite=new String [EliteSize];
                   BestTapeMatch="";
                   for (int i=0;i<G*N;i++){
                           MutaHillClimber();			//Muta un elemento del genoma 
                           EvaluaHillClimber();				//Evalua los primeros N
                           if(fitnessHC>BestFitness){
                               EliteHC=genomaHC;
                               BestFitness=fitnessHC;
                           }
                           if (BestSingleMatches==TTLen){
                                   BestFound=true;
                                   Elite[Optimo]=genomaHC;
                                   Optimo++;
                                   if (Optimo==EliteSize) break;		//Termina si hay <EliteSize> ajustes perfectos
                           }//endIf
                           System.out.printf("Individual %8.0f\tMatches %8.0f\n",(float)i,(float)BestSingleMatches);
                    }//endFor
                    if (!BestFound){		 	
                           ResultadosDeLaCorridaHillClimber(EliteHC);
                    }
                    else{
                        ResultadosDeLaCorrida(Elite);
                    }//Endif
                    
                    System.out.println("DESEA CONTINUAR LA BUSQUEDA? (S/*)");
                    if (!Kbr.readLine().toUpperCase().equals("S")) break;
                    Optimo=0;
            }//endWhile
         }
	 System.out.println("\n\nOtra corrida? (S/*)");
	 Resp=Kbr.readLine().toUpperCase();
	 if (!Resp.equals("S")) break;
   }//endLoop
   System.out.println("\n\n*****\t\t\tFIN DE PROGRAMA\t\t\t*****\n\n\n");
  }//endMain
} //endClass

