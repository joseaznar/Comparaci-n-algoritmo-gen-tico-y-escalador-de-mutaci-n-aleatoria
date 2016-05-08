package kolmogorov;

/*
 *	Alimenta una Máquina de Turing
 */
import java.io.*;

class LeeDatos{
	static RandomAccessFile Datos;
	static BufferedReader Kbr;

    public static void Cinta() throws Exception {
	  Kbr=new BufferedReader(new InputStreamReader(System.in));
	  String sResp;
   	  while (true){
		System.out.println("Deme el nombre del archivo de datos que quiere procesar:");
		String FName=Kbr.readLine().toUpperCase();
	  	try {
	  		Datos=new RandomAccessFile(new File(FName),"r");
	  	}//endTry
  		catch (Exception e1){
  			System.out.println("No se encontro \""+FName+"\"");
			continue;
  		}//endCatch
		String Cinta="",Car;
		int BytesEnDatos=0;
		byte X;
		/*
		 *	Averigua el tamaño del archivo en bytes
		 */
		while (true){
			Datos.seek(BytesEnDatos);
			try{X=Datos.readByte();}
			catch (Exception e){break;}
			BytesEnDatos++;
		}//endWhile
		Datos.close();
		//
		/*
		 *	Convierte en binario-ASCII
		 */
		int Y;
		Datos=new RandomAccessFile(new File(FName),"r");
		for (int i=0;i<BytesEnDatos;i++){
			Datos.seek(i);					// Busca el i-ésimo byte
			Y=Datos.readByte();				// Léelo
/*
 *		CONVIERTE EL BYTE A BINARIO-ASCII
 */
			Car="";
			for (int j=0;j<8;j++){
				if (Y%2==0) Car="0"+Car; else Car="1"+Car;
				Y=Y/2;
			} //endFor
			Cinta=Cinta+Car;					// Anexa el binario-ascii a la cinta
		}//endFor
	    PrintStream Tape=new PrintStream(new FileOutputStream(new File("TT.txt")));
		Tape.println(Cinta);					// La imagen binaria esta en "Tape.txt"
		Datos.close();
		break;
	 }//endWhile
   }//endMain
}//endClass