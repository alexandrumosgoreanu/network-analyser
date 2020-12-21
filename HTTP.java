import java.util.List;

public class HTTP {
    List<String> list;

    public HTTP(List<String> list) {
        this.list = list;
    }
@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\tHypertext Transfer Protocol\n");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("0d") && list.get(i + 1).equals("0a")) {
                if (list.get(i + 2).equals("0d") && list.get(i + 3).equals("0a")) {
                    break; // fin de l'entete
                }
                sb.append("\n\t\t");
                i++;
            } else {
                sb.append(hex_to_ascii(list.get(i)));
            }
        }
        return sb.toString();
    }


    public char hex_to_ascii(String s) {
        return (char) Integer.parseInt(s, 16);
    }




}
