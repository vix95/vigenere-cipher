import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class VigenereMenu {
    VigenereMenu() {
    }

    String readInput() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String cmd = bufferedReader.readLine();
        String[] menu = new String[]{"-p", "-e", "-d", "-k"};

        if (cmd.equals("-q")) return cmd;
        else if (cmd.split(" ").length != 1) {
            System.out.println("Error: wrong number of args, need exactly 1 arg");
            return null;
        } else if (cmd.split("").length != 2) {
            System.out.println("Error: unrecognized arg");
            return null;
        } else if (Arrays.toString(menu).contains(cmd)) {
            return cmd;
        } else {
            System.out.println("Error: unrecognized arg");
            return null;
        }
    }

    void printWrongKey() {
        System.out.println("Error: unrecognized key, the key must be a positive number and meet the requirements");
    }

    @Override
    public String toString() {
        return "-p (przygotowanie tekstu jawnego do szyfrowania)\n" +
                "-e (szyfrowanie)\n" +
                "-d (odszyfrowywanie)\n" +
                "-k (kryptoanaliza wylacznie w oparciu o kryptogram)";
    }
}
