import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;



public class Analyser extends Application
{

	public static boolean isHexa(String str)
	{
		if(str.length() % 2 == 1)	// longeur impair => str n'est pas un representation d'un hexa, qui a une longeur paire
			return false;
		for(int i = 0; i < str.length(); i++)
		{
			if(  !((Character.toUpperCase(str.charAt(i)) >= 'A' && Character.toUpperCase(str.charAt(i)) <= 'F') || Character.isDigit(str.charAt(i)) ) )
				return false;	//return false si on a un caractere qui n'est pas un symbol valide en hexa (A, B, C, D, E, F) ou si on n'a pas un caractere qui est un chiffre
		}
		return true;
	}
	
	
	public static void decodeFrame(List<String> frame)
	{
		int i;
		List<String> aux = new ArrayList<>();
		
		//on n'a pas de preambule ou FCS dans la trame ethernet, donc l'entete ethernet a 14 octets
		for(i = 0; i < 14; i++)
			aux.add(frame.get(i));
	
		EthernetHeader myEthernetHeader = new EthernetHeader(aux);
		String protocolType = myEthernetHeader.typeToProtocol();
		aux.clear();	//vider l'ArrayList auxiliare
		
		switch(protocolType)
		{
			case "ARP": 
			{
				for(i = 14; i < 42; i++)	//un message ARP a 28 bytes
					aux.add(frame.get(i));
				ARPMessage myARPMessage = new ARPMessage(aux);
				//System.out.println(myEthernetHeader);
				//System.out.println(myARPMessage);
				GUI.println(myEthernetHeader.toString());
				GUI.println(myARPMessage.toString());
				break;
			}
			case "IPv4":
			{
				for (i = 14; i < 34; i++)	//un message IPv4 a 20 bytes
					aux.add(frame.get(i));
				IPv4 myIPv4 = new IPv4(aux);
				String ProtocolType2 = myIPv4.get_protocol();
				aux.clear();
				
				//System.out.println(myEthernetHeader);
				//System.out.println(myIPv4);
				GUI.println(myEthernetHeader.toString());
				GUI.println(myIPv4.toString());
				
				int header_ipv4 = myIPv4.get_header_length();
				int total_length = myIPv4.get_total_length();
				if (header_ipv4 != 20) {
					// expect options && prelucrare options
				}
				switch (ProtocolType2) {
					case "ICMP": {
						break;
					}
					case "TCP": {
						for (i = 34; i < 34 + total_length - header_ipv4; i++)	//un header TCP a 20 bytes
							aux.add(frame.get(i));
						TCP myTCP = new TCP(aux);
						//System.out.println(myTCP);
						GUI.println(myTCP.toString());
						break;
					}
					case "UDP": {

					}
					default: {
						System.out.println("another");
					}


				}

				break;
				
			}
			default: break;
			//TODO restul de cazuri
		}
		
		//System.out.println("_____________________________________________\n");
		GUI.println("___________________________________________________________________\n");
		
	}
	
	
	/**
	 * Methode qui permet de lire le traces d'un fichier texte, qui contient es octets bruts
	 * @param fileName	le nom du fichier ou se trouve la trace
	 * @throws Exception 
	 */
	public static void readFrame(String fileName) throws Exception
	{

		int ok = 1;
		int current_line = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		
		String line, words[] = null;	//line pour lire les lignes, words[] pour separer les octets d'une ligne
		int current_offset = 0, offset;			//pour compter l'offset courant
		Byte offsetByte = new Byte();		//pour lire l'offset au debut de la ligne
		List<String> list = new ArrayList<>();	//pour stocker les octets(comme Strings) d'une trame
		int trame_nr = 1;
		
		while ((line = br.readLine()) != null)
		{	
			current_line++;
			if(line.isEmpty())
				continue;	//continue si on a un ligne vide
			else
				words = line.split(" ");	//split the line in bytes
			
			if(!isHexa(words[0]))
				continue;	//continue si la ligne ne commence pas par un offset valide
			else
			{
				if(words[0].equals("0000"))	//new frame
				{
					
					if(!list.isEmpty())
					{
						//System.out.println("Trame numero " + trame_nr++ + ":");
						if(ok == 1)
						{
							GUI.println("\nTrame numero " + trame_nr++ + ":");
							decodeFrame(list);		//decode the frame 
						}
						ok = 1;
						list.clear();
					}
						
					current_offset = 0;	//nouvelle trame => current offset = 0
						
					for(int i = 1; i < words.length; i++)
						if(isHexa(words[i]))
						{
							list.add(words[i]);
							current_offset++;
						}
				}
				else 	//the same frame
				{

					if(current_offset < 256)	//offset est seulement un byte
					{
						offsetByte = new Byte(words[0]);		//offset byte
						offset = offsetByte.getValue();			//convert to int
						
						//System.out.println(offset);
						
						if(offset != current_offset)
						{
							//System.out.println("Error, offset not right at line: " + current_line);	 //throw ceva exceptie custom
							GUI.println("Error, offset not correct at line: " + current_line);
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error dialog");
							alert.setHeaderText("Corrupt trace");
							alert.setContentText("Ooops, the offset on line " + current_line + " is not correct. The file might be corrupted.");
							alert.show();
							ok = 0; 	//can't print this trace
							continue;
							//br.close();
							//throw new Exception("Error, offset not correct at line: " + current_line);
						}
						else
							for(int i = 1; i < words.length; i++)
								if(isHexa(words[i]))
								{
									list.add(words[i]);
									current_offset++;
								}
					}
					else //la trame a >=256 et <1500(0x5DC) octets, donc offset est sur 2 octets
					{
						offsetByte = new Byte(words[0]);		//offset first byte
						offset = offsetByte.getValue()  * 16;	
						offsetByte = new Byte(words[1]);		//offset second byte, 
						offset += offsetByte.getValue();
						
						if(offset != current_offset)
						{
							//System.out.println("Error, offset not right");	 //throw ceva exceptie custom
							GUI.println("Error, offset not correct at line: " + current_line);
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error dialog");
							alert.setHeaderText("Corrupt trace");
							alert.setContentText("Ooops, the offset on line " + current_line + " is not correct. The file might be corrupted.");
							alert.show();
							ok = 0; 	//can't print this trace
							continue;
							//br.close();
							///throw new Exception("Error, offset not correct at line + " + current_line);
						}
						else
							for(int i = 2; i < words.length; i++)
								if(isHexa(words[i]))
								{
									list.add(words[i]);
									current_offset++;
								}
					}
				}//end else
			}//end if(isHexa(words[0]);
			
		} //end while 
		
		//System.out.println("Trame numero " + trame_nr++ + ":");
		
		//last frame
		if(!list.isEmpty())	//check if the file actually had bytes in it
		{
			if(ok == 1)	//the frame is printable
			{
				GUI.println("\nTrame numero " + trame_nr++ + ":");
				decodeFrame(list);
			}
		}
		else	//the file had no bytes in it
		{
			GUI.println("The file you selected doesn't contain any valid trace");
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning dialog");
			alert.setHeaderText("No trace detected");
			alert.setContentText("Ooops, the file you selected doesn't contain any valid trace. Please verify that you selected the right file");
			alert.show();
		}
			
		br.close();
		
	}
	
	
	public void start(Stage stage) throws Exception
	{
		new GUI(stage);
	}
	
	
	public static void main(String[] args) 
	{
		Application.launch(args);
	}



}
