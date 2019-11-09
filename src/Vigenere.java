import java.io.*;
import java.util.Scanner;

public class Vigenere {
    private static String path = System.getProperty("user.dir") + "/test_files";
    private static VigenereMenu vigenereMenu;

    public static void main(String[] args) {
        vigenereMenu = new VigenereMenu();
        System.out.println(vigenereMenu.toString());

        try {
            String cmd;
            String key;

            do {
                cmd = vigenereMenu.readInput();
                if (cmd != null && !cmd.equals("-q")) {
                    switch (cmd) {
                        case "-p":  // prepare a file
                            preparePlainFile();
                            break;
                        case "-e":  // encrypt
                            key = readKey();

                            if (key != null) {
                                VignereCipher vignereCipher = new VignereCipher(key);
                                vignereCipher.encryptFile(path);
                            }

                            break;
                        case "-d":  // decrypt
                            key = readKey();

                            if (key != null) {
                                VignereCipher vignereCipher = new VignereCipher(key);
                                vignereCipher.decryptFile(path);
                            }

                            break;
                        case "-k":  // break key
                            VignereCipher vignereCipher = new VignereCipher();
                            vignereCipher.breakCipher(path);
                            break;
                    }
                }
            } while (cmd == null || !cmd.equals("-q"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void preparePlainFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/plain.txt"));
        Scanner scanner = new Scanner(new File(path + "/orig.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String prepared_line = prepareLine(String.valueOf(line));

            System.out.println("orig line: " + line);
            System.out.println("prepared line: " + prepared_line + "\n");

            writer.write(prepared_line);
            writer.write('\n');
        }

        writer.close();
        scanner.close();
    }

    private static String prepareLine(String line) {
        return line.replaceAll("[^a-zA-Z]+", "").toLowerCase();
    }

    private static String readKey() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path + "/key.txt"));

        try {
            String key = scanner.nextLine().toLowerCase();
            if (!key.equals(key.replaceAll("[^a-zA-Z ]+", ""))) {
                vigenereMenu.printWrongKey();
                return null;
            }

            return key.replace(" ", "");
        } catch (Exception e) {
            vigenereMenu.printWrongKey();
            return null;
        }
    }
}
