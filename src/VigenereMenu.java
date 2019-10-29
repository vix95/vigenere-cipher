import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class VigenereMenu {
    public VigenereMenu() {
    }

    String readInput() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String cmd = bufferedReader.readLine();
        String[] menu = new String[]{"-c", "-a", "-e", "-d", "-j", "-k"};
        String[] ciphers = new String[]{"-c", "-a"};

        if (cmd.equals("-q")) return cmd;
        else if (cmd.split(" ").length != 2) {
            System.out.println("Error: wrong number of args, need exactly 2 args");
            return null;
        } else if ((Arrays.toString(menu).contains(cmd.split(" ")[0]) &&
                Arrays.toString(menu).contains(cmd.split(" ")[1])) &&
                cmd.split(" ")[0].equals(cmd.split(" ")[1])) {
            System.out.println("Error: typed two the same args");
            return null;
        } else if (Arrays.toString(ciphers).contains(cmd.split(" ")[0]) &&
                Arrays.toString(ciphers).contains(cmd.split(" ")[1])) {
            System.out.println("Error: can not use two cipher args");
            return null;
        } else if (Arrays.toString(menu).contains(cmd.split(" ")[0]) &&
                Arrays.toString(menu).contains(cmd.split(" ")[1])) {
            return cmd;
        } else {
            System.out.println("Error: unrecognized args");
            return null;
        }
    }

    void printWrongKey() {
        System.out.println("Error: unrecognized key, the key must be a positive number and meet the requirements");
    }

    void printWrongFactor() {
        System.out.println("Error: unrecognized factor, the factor must be a positive number and meet the requirements");
    }

    @Override
    public String toString() {
        return "-c (szyfr Cezara)\n" +
                "-a (szyfr afiniczny)\n" +
                "-e (szyfrowanie)\n" +
                "-d (odszyfrowywanie)\n" +
                "-j (kryptoanaliza z tekstem jawnym)\n" +
                "-k (kryptoanaliza wylacznie w oparciu o kryptogram)";
    }
}
