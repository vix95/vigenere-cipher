import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class VignereCipher {
    private int m = 26; // chars qty
    private int key_chars_qty;
    private String key;
    private char[][] cipher_array = new char[26][26];

    public VignereCipher() {
        this.prepareArrayForCipher();
    }

    VignereCipher(String key) {
        this.key = key;
        this.key_chars_qty = key.length();
        this.prepareArrayForCipher();
    }

    public int getKey_chars_qty() {
        return key_chars_qty;
    }

    private void prepareArrayForCipher() {
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.m; j++) {
                int offset = i + j;
                if (offset >= this.m) offset -= this.m;

                cipher_array[i][j] = (char) ('a' + offset);
            }
        }

        // print for debug
        /* for (int i = 0; i < this.m; i++) {
            if (i < 10) System.out.print(' ');
            System.out.print(i + ": ");
            for (int j = 0; j < this.m; j++) {
                System.out.print(Character.toString(cipher_array[i][j]) + ' ');
            }

            System.out.print('\n');
        } */
    }

    private char[] prepareKeyForLine(char[] line) {
        char[] prepared_key = new char[line.length];
        int j = 0;
        for (int i = 0; i < line.length; i++) {
            if (Character.isLetter(line[i])) {
                prepared_key[i] = key.toCharArray()[j];
                j++;

                if (j == key.length()) j = 0;
            } else {
                prepared_key[i] = ' ';
            }
        }

        return prepared_key;
    }

    private char[] encrypt(char[] line, char[] key) {
        char[] encrypted_line = new char[line.length];
        for (int i = 0; i < line.length; i++) {
            int row = line[i] - 'a'; // plain
            int col = key[i] - 'a'; // key

            if (Character.isLetter(line[i])) encrypted_line[i] = this.cipher_array[row][col];
        }

        return encrypted_line;
    }

    void encryptFile(String path) throws IOException {
        if (this.key != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/crypto.txt"));
            Scanner scanner = new Scanner(new File(path + "/plain.txt"));

            while (scanner.hasNextLine()) {
                char[] line = scanner.nextLine().toCharArray();
                char[] encrypted_line = this.encrypt(line, this.prepareKeyForLine(line));

                System.out.println("plain line: " + String.valueOf(line));
                System.out.println("encrypted line: " + String.valueOf(encrypted_line) + "\n");

                writer.write(encrypted_line);
                writer.write('\n');
            }

            writer.close();
            scanner.close();
        }
    }

    private int findLetter(int row, char c) {
        for (int i = 0; i < cipher_array[row].length; i++) {
            if (cipher_array[row][i] == c) return i;
        }

        return 0;
    }

    private char[] decrypt(char[] line, char[] key) {
        char[] decrypted_line = new char[line.length];
        for (int i = 0; i < line.length; i++) {
            if (Character.isLetter(line[i])) {
                int row = key[i] - 'a'; // key
                int col = this.findLetter(row, line[i]); // encrypted

                decrypted_line[i] = this.cipher_array[0][col];
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
                char[] decrypted_line = this.decrypt(line, this.prepareKeyForLine(line));

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
                    this.key = "1";
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
