import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Analyser 
{

	public static boolean isHexa(String str)
	{
		if (str.length() % 2 == 1)	// longeur impair => str n'est pas un representation d'un hexa, qui a une longeur paire
			return false;
		for (int i = 0; i < str.length(); i++)
		{
			if (!((Character.toUpperCase(str.charAt(i)) >= 'A' && Character.toUpperCase(str.charAt(i)) <= 'F') || Character.isDigit(str.charAt(i))))
				return false;	//return false si on a un caractere qui n'est pas un symbol valide en hexa (A, B, C, D, E, F) ou si on n'a pas un caractere qui est un chiffre
		}
		return true;
	}
	
	
	public static void decodeFrame(List<String> frame)
	{
		int i;
		List<String> aux = new ArrayList<>();
		
		//on n'a pas de preambule ou FCS dans la trame ethernet, donc l'entete ethernet a 14 octets
		for (i = 0; i < 14; i++)
			aux.add(frame.get(i));
	
		EthernetHeader myEthernetHeader = new EthernetHeader(aux);
		String protocolType = myEthernetHeader.typeToProtocol();
		aux.clear();	//vider l'ArrayList auxiliare
		
		switch(protocolType)
		{
			case "ARP": 
			{
				for (i = 14; i < 42; i++)	//un message ARP a 28 bytes
					aux.add(frame.get(i));
				ARPMessage myARPMessage = new ARPMessage(aux);
				System.out.println(myEthernetHeader);
				System.out.println(myARPMessage);
				break;
			}
			case "IPv4":
			{
				for (i = 14; i < 34; i++)	//un message IPv4 a 20 bytes
					aux.add(frame.get(i));
				IPv4 myIPv4 = new IPv4(aux);
				String ProtocolType2 = myIPv4.get_protocol();
				aux.clear();
				System.out.println(myEthernetHeader);
				System.out.println(myIPv4);
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
						System.out.println(myTCP);
						break;
					}
					case "UDP": {

					}
					default: {
						System.out.println("aia e");
					}


				}

				break;
			}
			default: break;
			//TODO restul de cazuri
		}
		
		System.out.println("_____________________________________________\n");
		
	}
	
	
	/**
	 * Methode qui permet de lire le traces d'un fichier texte, qui contient des octets bruts
	 * @param fileName	le nom du fichier ou se trouve la trace
	 * @throws IOException	s'il y a des problemes avec le fichier
	 */
	public static void readFrame(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		
		String line;    //line pour lire les lignes, words[] pour separer les octets d'une ligne
		String[] words = null;
		int current_offset = 0, offset;			//pour compter l'offset courant
		Byte offsetByte = new Byte();		//pour lire l'offset au debut de la ligne
		List<String> list = new ArrayList<>();	//pour stocker les octets(comme Strings) d'une trame
		int trame_nr = 1;
		
		while ((line = br.readLine()) != null)
		{	
			if(line.isEmpty())
				continue;	//continue si on a un ligne vide
			else
				words = line.split(" ");	//split the line in bytes
			
			if(!isHexa(words[0]))
				continue;	//continue si la ligne ne commence pas par un offset valide
			else
			{
				if(words[0].equals("00"))	//new frame
				{
					if(!list.isEmpty())
					{
						System.out.println("Trame numero: " + trame_nr++);
						decodeFrame(list);		//decode the frame 
						list.clear();
					}
						
					current_offset = 0;	//nouvelle trame => current offset = 0
						
					for (int i = 1; i < words.length; i++)
						if (isHexa(words[0]))
							list.add(words[i]);
				}
				else 	//the same frame
				{
					offsetByte = new Byte(words[0]);		//offset byte
//offset first byte
					if (current_offset < 256)	//offset est seulement un byte
					{
						offset = offsetByte.getValue();			//convert to int
						if (offset != current_offset)
							System.out.println("Error, offset not right");	 //throw ceva exceptie custom
						else
							for (int i = 1; i < words.length; i++)
								if (isHexa(words[0]))
									list.add(words[i]);
					}
					else //la trame a >=256 et <1500(0x5DC) octets, donc offset est sur 2 octets
					{
						offset = offsetByte.getValue()  * 16;
						offsetByte = new Byte(words[1]);		//offset second byte, 
						offset += offsetByte.getValue();
						
						if (offset != current_offset)
							System.out.println("Error, offset not right");	 //throw ceva exceptie custom
						else
							for (int i = 2; i < words.length; i++)
								if (isHexa(words[0]))
									list.add(words[i]);
					}
				}//end else
			}//end if(isHexa(words[0]);
				current_offset += words.length - 1;
			
		} //end while 
		//System.out.println(list);
		System.out.println("Trame numero: " + trame_nr++);
		decodeFrame(list);
		
		br.close();
		
	}
	
	
	public static void main(String[] args) throws IOException
	{
		
		String fileName = "data/input.txt";
		readFrame(fileName);
	}
}
