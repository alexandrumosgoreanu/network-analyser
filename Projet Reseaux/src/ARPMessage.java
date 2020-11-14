import java.util.List;

public class ARPMessage 
{
	private String HardwareType;
	private int HLEN;	//ethernet = 48
	private int PLEN;	//IPv4 = 32
	private Byte[] source_hw_adr = new Byte[6];			//source MAC adr
	private Byte[] dest_hw_adr = new Byte[6];			//dest MAC adr
	private Byte[] source_protocol_adr = new Byte[4];	//source ip adresse
	private Byte[] dest_protocol_adr = new Byte[4];		//dest ip adresse
	
	public ARPMessage(List<String> list)
	{
		int i = 0;
		Byte aux = new Byte(list.get(0));	//Hardware type - premiers 2 bytes
		HardwareType =  aux.getHexValue();
		aux = new Byte(list.get(1));
		HardwareType += aux.getHexValue();
		
		if(HardwareType.equals("0001"))
			HardwareType = "Ethernet (1)";
		else if(HardwareType.equals("0017"))
			HardwareType = "HDLC (17)";
		
		
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
