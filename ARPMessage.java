import java.util.List;

public class ARPMessage 
{
	/**
	 * les atributs sont tous les attributs necessaire pour decoder un message ARP
	 */
	private final Byte[] HardwareType = new Byte[2];
	private final Byte[] ProtocolType = new Byte[2];
	private final int HLEN;	//ethernet = 48
	private final int PLEN;	//IPv4 = 32
	private final Byte[] opcode = new Byte[2];
	private final Byte[] source_hw_adr = new Byte[6];			//source MAC adr
	private final Byte[] dest_hw_adr = new Byte[6];			//dest MAC adr
	private final Byte[] source_protocol_adr = new Byte[4];	//source ip adresse
	private final Byte[] dest_protocol_adr = new Byte[4];		//dest ip adresse
	
	
	/**
	 * Constructeur
	 * @param list la liste d'octets captures
	 */
	public ARPMessage(List<String> list)
	{
		int i, j;
		Byte aux;	
		
		HardwareType[0] = new Byte(list.get(0));//Hardware type - premiers 2 bytes
		HardwareType[1] = new Byte(list.get(1));
		
		
		ProtocolType[0] = new Byte(list.get(2));
		ProtocolType[1] = new Byte(list.get(3));
		
		aux = new Byte(list.get(4));
		HLEN = aux.getValue();
		aux = new Byte(list.get(5));
		PLEN = aux.getValue();
		
		opcode[0] = new Byte(list.get(6));
		opcode[1] = new Byte(list.get(7));
		
		j = 0;
		for(i = 8; i < 8 + HLEN; i++)
			source_hw_adr[j++] = new Byte(list.get(i));
		
		j = 0;
		for(i = 14; i < 14 + PLEN; i++)
			source_protocol_adr[j++] = new Byte(list.get(i));
		
		j = 0;
		for(i = 18; i < 18 + HLEN; i++)
			dest_hw_adr[j++] = new Byte(list.get(i));
		
		j = 0;
		for(i = 24; i < 24 + PLEN; i++)
			dest_protocol_adr[j++] = new Byte(list.get(i));
	}
	
	
	/**
	 * Conversion de type en Protocol
	 * @return le nom du protocol comme un String
	 */
	public String convertToProtocol()
	{
		String str = ProtocolType[0].getHexValue() + ProtocolType[1].getHexValue();
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
	
	/**
	 * Opcode conversion
	 * @return opcode comme un string
	 */
	public String convertOpcode()
	{
		String str = opcode[0].getHexValue() + opcode[1].getHexValue();
		switch(str)
		{
		case "0001": return "request";
		case "0002": return "reply";
		//TODO restul de operatii cu opcode
		}
		return null;
	}
	
	/**
	 * Hw type conversion
	 * @return Hardware type comme un String
	 */
	public String convertHwType()
	{
		String str = HardwareType[0].getHexValue() + HardwareType[1].getHexValue();
		switch(str)
		{
			case "0001": return "Ethernet (1)";
			case "0017": return "HDLC (17)";
		}
		return null;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\tAddress Resolution Protocol (").append(convertOpcode()).append(")");
		sb.append("\n\t\tHardware type: ").append(convertHwType());
		
		sb.append("\n\t\tProtocol type: ").append(convertToProtocol()).append(" (0x").append(ProtocolType[0].getHexValue()).append(ProtocolType[1].getHexValue()).append(")");
		sb.append("\n\t\tHardware size: ").append(HLEN);
		sb.append("\n\t\tProtocol size: ").append(PLEN);
		sb.append("\n\t\tOpcode: ").append(convertOpcode()).append(" (").append(opcode[1].getValue()).append(")");
		
		sb.append("\n\t\tSender MAC address: " );
		for(int i = 0; i < 6; i++)
		{
			sb.append(source_hw_adr[i].getHexValue());
			if(i != 5)
				sb.append(":");
		}
		
		sb.append("\n\t\tSender IP address: ");
		for(int i = 0; i < 4; i++)
		{
			sb.append(source_protocol_adr[i]);
			if(i != 3)
				sb.append(".");
		}
		
		sb.append("\n\t\tTarget MAC address: " );
		for(int i = 0; i < 6; i++)
		{
			sb.append(dest_hw_adr[i].getHexValue());
			if(i != 5)
				sb.append(":");
		}
		
		sb.append("\n\t\tTarget IP address: ");
		for(int i = 0; i < 4; i++)
		{
			sb.append(dest_protocol_adr[i]);
			if(i != 3)
				sb.append(".");
		}
		
		return sb.toString();
	}
}
