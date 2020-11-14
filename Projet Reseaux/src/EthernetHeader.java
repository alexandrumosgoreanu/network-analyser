import java.util.List;

public class EthernetHeader 
{
		/**
		 * adr_source est un atribut (de type Byte Array) qui stocke l'adresse destination de la frame Ethernet
		 * adr_dest est un atribut (de type Byte Array) qui stocke l'adresse source de la frame Ethernet
		 * type est un atribut (de type Byte Array) qui indique quel protocol est encapsule dans la frame Ethernet
		 */
		private Byte[] adr_source = new Byte[6]; 
		private Byte[] adr_dest = new Byte[6];
		private Byte[] type = new Byte[2];
		
		public EthernetHeader(List<String> list)			//constructor
		{
			int i, j = 0;
			
			for(i = 0; i < 6; i++)
				adr_dest[j++] = new Byte(list.get(i));		//destination adresse
			
			j = 0;
			for(i = 6; i < 12; i++)
				adr_source[j++] = new Byte(list.get(i));	//source adresse
			
			j = 0;
			for(i = 12; i < 14; i++)
				type[j++] = new Byte(list.get(i));			//protocol type
			
		}
		
		/**
		 * Methode qui verifie si l'adresse destination est l'adresse de broadcast
		 * @return true si l'adresse destination est l'adresse de broadcast(FF:FF:FF:FF:FF:FF), false sinon
		 */
		public boolean isBroadcast()
		{
			for(Byte b : adr_dest)
				if(b.getValue() != 255)
					return false;
			return true;
		}
		
		
		/**
		 * Surcharge de methode toString pour afficher les infos de l'entete Ethernet
		 */
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			int i;
			
			sb.append("Ethernet II:\n\t");
			sb.append("Destination: ");
			
			if(isBroadcast())
			{
				sb.append("Broadcast (");
				for(i = 0; i < 6; i++)
				{	
					sb.append(adr_dest[i].getHexValue());
					if(i != 5)
						sb.append(":");
				}
				sb.append(")");
			}
			else
				for(i = 0; i < 6; i++)
				{	
					sb.append(adr_dest[i].getHexValue());
					if(i != 5)
						sb.append(":");
				}
			
			sb.append("\n\t").append("Source: ");
			for(i = 0; i < 6; i++)
			{	
				sb.append(adr_source[i].getHexValue());
				if(i != 5 )
					sb.append(":");
			}
				
		
			
			sb.append("\n\t").append("Type: 0x");
			sb.append(type[0].getHexValue()).append(type[1].getHexValue()).append(" (").append(typeToProtocol()).append(")");
			
			return sb.toString();
		}
		
		
		
		/**
		 * Conversion de type en Protocol
		 * @return le nom du protocol comme un String
		 */
		public String typeToProtocol()
		{
			String str = type[0].getHexValue() + type[1].getHexValue();
			switch(str)
			{
				case "0800": return "IPv4";
				case "0806": return "ARP";
				case "0842": return "Wake-on-LAN";
				case "22F0": return "AVTP";
				case "22F3": return "IETF TRILL Protocol";
				//TODO de adaugat restul de protocoale, o sa il intreb pe profu daca trebuie toate din tabelul asta https://en.wikipedia.org/wiki/EtherType
			}
			
			return null;	
		}
		
}
