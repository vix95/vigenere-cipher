import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class VignereCipher {
    private int m = 26; // chars qty
    private char[] key;

    public VignereCipher() {
    }

    VignereCipher(String key) {
        this.key = key.toCharArray();
    }

    private char[] encrypt(char[] line) {
        char[] encrypted_line = new char[line.length];
        for (int i = 0, j = 0; i < line.length; i++) {
            int int_line = line[i] - 'a'; // plain
            if (Character.isLetter(line[i])) {
                encrypted_line[i] = (char) (((int_line + (key[j] - 'a')) % this.m) + 'a');
                j = ++j % this.key.length; // key factor
            }
        }

        return encrypted_line;
    }

    void encryptFile(String path) throws IOException {
        if (this.key != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/crypto.txt"));
            Scanner scanner = new Scanner(new File(path + "/plain.txt"));

            while (scanner.hasNextLine()) {
                char[] line = scanner.nextLine().toCharArray();
                char[] encrypted_line = this.encrypt(line);

                System.out.println("plain line: " + String.valueOf(line));
                System.out.println("encrypted line: " + String.valueOf(encrypted_line) + "\n");

                writer.write(encrypted_line);
                writer.write('\n');
            }

            writer.close();
            scanner.close();
        }
    }

    private char[] decrypt(char[] line) {
        char[] decrypted_line = new char[line.length];
        for (int i = 0, j = 0; i < line.length; i++) {
            if (Character.isLetter(line[i])) {
                char encrypted = (char) (((line[i] - 'a') - (key[j] - 'a') + this.m) % this.m + 'a'); // encrypted
                decrypted_line[i] = encrypted;
                j = ++j % this.key.length; // key factor
            }
        }

        return decrypted_line;
    }

    void decryptFile(String path) throws IOException {
        if (this.key != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/decrypt.txt"));
            Scanner scanner = new Scanner(new File(path + "/crypto.txt"));

            while (scanner.hasNextLine()) {
                char[] line = scanner.nextLine().toCharArray();
                char[] decrypted_line = this.decrypt(line);

                System.out.println("encrypted line: " + String.valueOf(line));
                System.out.println("decrypted line: " + String.valueOf(decrypted_line) + "\n");

                writer.write(decrypted_line);
                writer.write('\n');
            }

            writer.close();
            scanner.close();
        }
    }

    void decryptFileFindKey(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/decrypt.txt"));
        BufferedWriter writerKey = new BufferedWriter(new FileWriter(path + "/key-new.txt"));
        Scanner scanner = new Scanner(new File(path + "/crypto.txt"));
        Scanner scannerPlain = new Scanner(new File(path + "/plain.txt"));
        char[] template = scannerPlain.nextLine().toCharArray();

        boolean found_key = false;
        while (scanner.hasNextLine()) {
            char[] line = scanner.nextLine().toCharArray();
            char[] decrypted_line = new char[0];

            // find the key
            if (!found_key) {
                for (int key = 1; key < 20; key++) {
                    //this.key = ;
                    //decrypted_line = this.decrypt(line);

                    if (String.valueOf(decrypted_line).contains(String.valueOf(template))) {
                        found_key = true;
                        System.out.println("Found key: " + this.key);
                        writerKey.write(this.key.toString());
                        writerKey.close();
                        writer.write(decrypted_line);
                        writer.write('\n');
                        break;
                    }
                }
            } else {
                //decrypted_line = this.decrypt(line);
                writer.write(decrypted_line);
                writer.write('\n');
            }

            if (found_key) {
                System.out.println("encrypted line: " + String.valueOf(line));
                System.out.println("decrypted line: " + String.valueOf(decrypted_line) + "\n");
            } else {
                System.out.println("Error: finding the key is impossible");
                break;
            }
        }

        writer.close();
        scanner.close();
    }
}
