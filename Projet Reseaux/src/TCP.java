import java.util.List;

public class TCP {

    private final Byte[] Source_Port = new Byte[2];
    private final Byte[] Destination_Port = new Byte[2];
    private final Byte[] seq = new Byte[4];
    private final Byte[] ack = new Byte[4];
    private final int header_length;
    private final Byte[] Flags = new Byte[2];
    private final Byte[] Window_size_value = new Byte[2];
    private final Byte[] Checksum = new Byte[2];
    private final Byte[] Urgent_pointer = new Byte[2];

    public TCP(List<String> list) {
        Source_Port[0] = new Byte(list.get(0));
        Source_Port[1] = new Byte(list.get(1));
        Destination_Port[0] = new Byte(list.get(2));
        Destination_Port[1] = new Byte(list.get(3));
        int j = 0;
        for (int i = 4; i < 8; i++)
            seq[j++] = new Byte(list.get(i));
        j = 0;
        for (int i = 8; i < 12; i++)
            ack[j++] = new Byte(list.get(i));
        Byte x = new Byte(list.get(12));
        header_length = x.getValue() / 16;
        Flags[0] = new Byte(list.get(12));
        Flags[1] = new Byte(list.get(13));
        Window_size_value[0] = new Byte(list.get(14));
        Window_size_value[1] = new Byte(list.get(15));
        Checksum[0] = new Byte(list.get(16));
        Checksum[1] = new Byte(list.get(17));
        Urgent_pointer[0] = new Byte(list.get(18));
        Urgent_pointer[1] = new Byte(list.get(19));
    }
    public String is_set(int i) {
        if ((i & 1) == 1) {
            return "Set";
        } else {
            return "Not set";
        }
    }
    public int get_header_length() {
        return header_length;
    }
    public String decode_options_TCP(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\tOptions: (" + (4 * get_header_length() - 20) + " bytes)");
        for (int i = 0; i < list.size(); i++) {

            switch (new Byte(list.get(i)).getValue()) {
                case(0): {
                    sb.append("\n\t\t\tTCP Option - ");
                    sb.append("End of options list");
                    break;
                }
                case(1): {
                    sb.append("\n\t\t\tTCP Option - ");
                    sb.append("No-Operation (NOP)");
                    sb.append("\n\t\t\t\tKind: No-Operation (1)");
                    break;
                }
                case(2): {
                    sb.append("\n\t\t\tTCP Option - ");
                    sb.append("Maximum Segment Size: ");
                    int x = 0;
                    x += new Byte("" + list.get(i + 2) + list.get(i + 3)).getValue();
                    sb.append(x + " bytes" + "\n\t\t\t\tKind: Maximum Segment Size (2)\n\t\t\t\tLength: 4\n\t\t\t\tMSS Value: " + x);
                    i += 3;
                    break;
                }
                case(3): {
                    sb.append("\n\t\t\tTCP Option - ");
                    sb.append("Window scale: " + new Byte(list.get(i + 2)).getValue() + " (multiply by 256)");
                    sb.append("\n\t\t\t\tKind: Window scale (3)\n\t\t\t\tLength: 3\n\t\t\t\tShift count: 8");
                    i += 2;
                    break;
                }
                case(4): {
                    sb.append("\n\t\t\tTCP Option - ");
                    sb.append("SACK permitted");
                    sb.append("\n\t\t\t\tKind: SACK permitted (4)\n\t\t\t\tLength: 2");
                    i += 1;
                    break;
                }
                case(8): {
                    sb.append("\n\t\t\tTCP Option - ");
                    sb.append("Timestamps: TSval ");
                    long x = 0;
                    x += new Byte("" + list.get(i + 2) + list.get(i + 3) + list.get(i + 4) + list.get(i + 5)).getValue();
                    sb.append(x + ", TSecr ");

                    long y = 0;
                    y += new Byte("" + list.get(i + 6) + list.get(i + 7) + list.get(i + 8) + list.get(i + 9)).getValue();
                    sb.append(y);
                    sb.append("\n\t\t\t\tKind: Time Stamp Option (8)\n\t\t\t\tLength: 10\n\t\t\t\tTimestamp value: " + x +
                            "\n\t\t\t\tTimestamp echo reply: " + y);
                    i += 9;
                    break;
                }
                default: {
                    break;
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tTransmission Control Protocol:");
        sb.append("\n\t\tSource Port: " + (Source_Port[0].getValue() * 16 * 16 + Source_Port[1].getValue()));
        sb.append("\n\t\tDestination Port: " + (Destination_Port[0].getValue() * 16 * 16 + Destination_Port[1].getValue()));
        sb.append("\n\t\tSequence number: " + ((long)seq[0].getValue() * 16777216 + (long)seq[1].getValue() * 65536 +
                (long)seq[2].getValue() * 256 + (long)seq[3].getValue()));
        sb.append("\n\t\tAcknowledgement number: " + ((long)ack[0].getValue() * 16777216 + (long)ack[1].getValue() * 65536 +
                (long)ack[2].getValue() * 256 + (long)ack[3].getValue()));
        sb.append("\n\t\tHeader Length: " + header_length * 4 + " bytes (" + header_length + ")");
        sb.append("\n\t\tFlags: 0x" + new Byte(String.valueOf(Flags[0].getValue() % 16)).getHexValue().substring(1) + Flags[1].getHexValue());
        int y = new Byte (new Byte(String.valueOf(Flags[0].getValue() % 16)).getHexValue().substring(1)).getValue();
        sb.append("\n\t\t\t\tReserved: " + is_set(y / 2));
        sb.append("\n\t\t\t\tNonce: " + is_set(y % 2));
        sb.append("\n\t\t\t\tCongestion Window Reduced (CWR): " + is_set(Flags[1].getValue() / 128));
        sb.append("\n\t\t\t\tECN-Echo: " + is_set(Flags[1].getValue() / 64));
        sb.append("\n\t\t\t\tUrgent: " + is_set(Flags[1].getValue() / 32));
        sb.append("\n\t\t\t\tAcknowledgement: " + is_set(Flags[1].getValue() / 16));
        sb.append("\n\t\t\t\tPush: " + is_set(Flags[1].getValue() / 8));
        sb.append("\n\t\t\t\tReset: " + is_set(Flags[1].getValue() / 4));
        sb.append("\n\t\t\t\tSyn: " + is_set(Flags[1].getValue() / 2));
        sb.append("\n\t\t\t\tFin: " + is_set(Flags[1].getValue()));
        sb.append("\n\t\tWindow size value: " + (Window_size_value[0].getValue() * 256 + Window_size_value[1].getValue()));
        sb.append("\n\t\tChecksum: 0x" + Checksum[0].getHexValue() + Checksum[1].getHexValue());
        sb.append("\n\t\tUrgent pointer: " + (Urgent_pointer[0].getValue() * 256 + Urgent_pointer[1].getValue()));
        // to do options
        return sb.toString();
    }
}
