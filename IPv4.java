import java.util.Arrays;
import java.util.List;

public class IPv4 {

    private final int Version;
    private final Byte Header_length;
    private final Byte DSF;
    private final Byte[] Total_Length = new Byte[2];
    private final Byte[] Identification = new Byte[2];
    private final Byte[] Flags = new Byte[2];
    private final Byte TTL;
    private final Byte Protocol;
    private final Byte[] Checksum = new Byte[2];
    private final Byte[] source_protocol_adr = new Byte[4];	//source ip adresse
    private final Byte[] dest_protocol_adr = new Byte[4];		//dest ip adresse

    public IPv4(List<String> list) {


        int i, j;
        Byte aux;

        Version = Integer.parseInt(String.valueOf(list.get(0).charAt(0)));
        Header_length = new Byte(list.get(0).substring(1));
        DSF = new Byte((list.get(1)));
        Total_Length[0] = new Byte(list.get(2));
        Total_Length[1] = new Byte(list.get(3));

        Identification[0] = new Byte(list.get(4));
        Identification[1] = new Byte(list.get(5));

        Flags[0] = new Byte(list.get(6));
        Flags[1] = new Byte(list.get(7));

        TTL = new Byte(list.get(8));

        Protocol = new Byte(list.get(9));

        Checksum[0] = new Byte(list.get(10));
        Checksum[1] = new Byte(list.get(11));

        j = 0;
        for(i = 12; i < 16; i++)
            source_protocol_adr[j++] = new Byte(list.get(i));

        j = 0;
        for(i = 16; i < 20; i++)
            dest_protocol_adr[j++] = new Byte(list.get(i));
    }
    public String is_set(int i) {
        if ((i & 1) == 1) {
            return "Set";
        } else {
            return "Not set";
        }
    }

    public String get_protocol() {
        int i = Protocol.getValue();
        switch (i) {
            case 1: return "ICMP";
            case 2: return "IGMP";
            case 6: return "TCP";
            case 8: return "EGP";
            case 17: return "UDP";
            case 36: return "XTP";
            default: return "aia e";
        }
    }

    public int get_header_length() {
        return Header_length.getValue() * 4;
    }
    public int get_total_length() {
        return Total_Length[0].getValue() * 16 * 16 + Total_Length[1].getValue();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tInternet Protocol Version 4");
        sb.append("\n\t\tVersion: ").append(Version);
        sb.append("\n\t\tHeader Length: " + Header_length.getValue() * 4 + " bytes (" + Header_length + ")");
        sb.append("\n\t\tDifferentiated Services Field: 0x" + DSF.getHexValue());
        sb.append("\n\t\t\t\tDifferentiated Services Codepoint: " + DSF.getValue() / 4);
        sb.append("\n\t\t\t\tExplicit Congestion Notification: " + DSF.getValue() % 4);
        sb.append("\n\t\tTotal Length: " + get_total_length());
        int x = Identification[0].getValue() * 16 * 16;
        x += Identification[1].getValue();
        sb.append("\n\t\tIdentification: 0x" + Identification[0].getHexValue() + Identification[1].getHexValue() +
                " (" + x + ")");
        sb.append("\n\t\tFlags: 0x" + Flags[0].getHexValue() + Flags[1].getHexValue());
        sb.append("\n\t\t\t\tReserved bit: " +  is_set(Flags[0].getValue() / 128));
        sb.append("\n\t\t\t\tDon't Fragment: " +  is_set(Flags[0].getValue() / 64));
        sb.append("\n\t\t\t\tMore Fragments: " +  is_set(Flags[0].getValue() / 32));
        sb.append("\n\t\t\t\tFragment Offset: " +  ((Flags[0].getValue() % 2) * 128 + Flags[1].getValue()));
        sb.append("\n\t\tTime to live: " + TTL.getValue());
        sb.append("\n\t\tProtocol: " + get_protocol() + " (" + Protocol.getValue() + ")");
        sb.append("\n\t\tHeader checksum: 0x" + Checksum[0].getHexValue() + Checksum[1].getHexValue());
        sb.append("\n\t\tSource: " + source_protocol_adr[0].getValue() + "." + source_protocol_adr[1].getValue() + "."
                + source_protocol_adr[2].getValue() + "." + source_protocol_adr[3].getValue());
        sb.append("\n\t\tDestination: " + dest_protocol_adr[0].getValue() + "." + dest_protocol_adr[1].getValue() + "."
                + dest_protocol_adr[2].getValue() + "." + dest_protocol_adr[3].getValue() + "\n");
        // mai trebuie schema cu optiuni daca are mai mult de 20 bytes
        return sb.toString();
    }
}
