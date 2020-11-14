import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Analyser 
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
			}
		}
		
		System.out.println(myEthernetHeader);
		
		
		
		
	}
	
	
	/**
	 * Methode qui permet de lire le traces d'un fichier texte, qui contient es octets bruts
	 * @param fileName	le nom du fichier ou se trouve la trace
	 * @throws IOException	s'il y a des problemes avec le fichier
	 */
	public static void readFrame(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		
		String line, words[] = null;	//line pour lire les lignes, words[] pour separer les octets d'une ligne
		int current_offset = 0, offset;			//pour compter l'offset courant
		Byte offsetByte = new Byte();		//pour lire l'offset au debut de la ligne
		List<String> list = new ArrayList<>();	//pour stocker les octets(comme Strings) d'une trame

		
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
						decodeFrame(list);		//decode the frame 
						list.clear();
					}
						
					current_offset = 0;	//nouvelle trame => current offset = 0
						
					for(int i = 1; i < words.length; i++)
						if(isHexa(words[0]))
							list.add(words[i]);
				}
				else 	//the same frame
				{
					if(current_offset < 256)	//offset est seulement un byte
					{
						offsetByte = new Byte(words[0]);		//offset byte
						offset = offsetByte.getValue();			//convert to int
						if(offset != current_offset)
							System.out.println("Error, offset not right");	 //throw ceva exceptie custom
						else
							for(int i = 1; i < words.length; i++)
								if(isHexa(words[0]))
									list.add(words[i]);
					}
					else //la trame a >=256 et <1500(0x5DC) octets, donc offset est sur 2 octets
					{
						offsetByte = new Byte(words[0]);		//offset first byte
						offset = offsetByte.getValue()  * 16;	
						offsetByte = new Byte(words[1]);		//offset second byte, 
						offset += offsetByte.getValue();
						
						if(offset != current_offset)
							System.out.println("Error, offset not right");	 //throw ceva exceptie custom
						else
							for(int i = 2; i < words.length; i++)
								if(isHexa(words[0]))
									list.add(words[i]);
					}
				}//end else
			}//end if(isHexa(words[0]);
				current_offset += words.length - 1;
			
		} //end while 
		//System.out.println(list);
		decodeFrame(list);
		
		br.close();
		
	}
	
	
	public static void main(String[] args) throws IOException
	{
		
		String fileName = "data/input.txt";
		readFrame(fileName);
	}
}
