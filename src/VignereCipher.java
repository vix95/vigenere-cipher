import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class VignereCipher {
    private int m = 26; // chars qty
    private char[] key;
    private final static int num_trials = 30;
    private ArrayList<String> keys = new ArrayList<>(); // keys, it's related with standard deviation

    private final static double[] freq = { // frequency of every character; taken from wiki
            0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070,
            0.002, 0.008, 0.040, 0.024, 0.067, 0.075, 0.029, 0.001, 0.060,
            0.063, 0.091, 0.028, 0.010, 0.023, 0.001, 0.020, 0.001
    };

    VignereCipher() {
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

    void encryptFile(final String path) throws IOException {
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

    void decryptFile(final String path) throws IOException {
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

    private boolean cryptanalysis(final char[] encrypted) {
        // after collected all lines can break the cipher
        double fit, best_fit = 1e100;
        double[] stddev = new double[num_trials - 1]; // keep all standard deviation
        int[] encrypted_int = new int[encrypted.length];
        this.key = new char[encrypted.length];

        // write to array int codes for characters without spaces
        int key_length = 0;
        for (char c : encrypted) if (Character.isLetter(c)) encrypted_int[key_length++] = c - 'a';

        // check configuration for j = 1, 2, 3, ...
        for (int j = 1; j < num_trials; j++) {
            fit = frequencyEveryPosition(encrypted_int, key_length, j, key); // fitness to key
            stddev[j - 1] = fit;

            // only for to see
            StringBuilder builder = new StringBuilder();
            for (char c : key) {
                if (Character.isLetter(c)) {
                    builder.append(c);
                }
            }

            keys.add(builder.toString());
            //System.out.printf("%f, key length: %2d - %s\n", fit, j, builder.toString());
            if (fit < best_fit) best_fit = fit;
        }

        // looking for the correct key based on standard deviation
        boolean found_key = false;
        double min = stddev[0];
        int index = 0;
        for (int i = 0; i < stddev.length; i++) {
            if (stddev[i] < min) {
                min = stddev[i];
                index = i;
                found_key = true;
            }
        }

        System.out.printf("The key is: '%s' for stddev(fit) = %f\n\n", keys.get(index), min);
        this.key = keys.get(index).toCharArray();

        return found_key;
    }

    void breakCipher(final String path) throws IOException {
        Scanner scanner = new Scanner(new File(path + "/crypto.txt"));
        ArrayList<String> lines = new ArrayList<>();

        while (scanner.hasNextLine()) lines.add(scanner.nextLine());

        // get the length of array
        int array_length = 0;
        for (String s : lines) array_length += s.length();

        if (array_length == 0) {
            System.out.print("No text to decrypt\n");
            return;
        }

        char[] encrypted = new char[array_length + lines.size() - 1];

        int pos = 0;
        for (String s : lines) {
            for (char c : s.toCharArray()) {
                if (Character.isLetter(c)) encrypted[pos++] = c;
                else encrypted[pos++] = ' ';
            }
            pos++;
        }

        scanner.close();

        if (this.cryptanalysis(encrypted)) {
            char[] decrypted = this.decrypt(encrypted);
            System.out.printf("encrypted line: %s\n", String.valueOf(encrypted));
            System.out.printf("decrypted line: %s\n", String.valueOf(decrypted));

            // build the string from every character form key array
            StringBuilder builder = new StringBuilder();
            for (char c : key) {
                if (Character.isLetter(c)) {
                    builder.append(c);
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/decrypt.txt"));
            BufferedWriter writerKey = new BufferedWriter(new FileWriter(path + "/key-crypto.txt"));

            writer.write(decrypted);
            writerKey.write(builder.toString());

            writer.close();
            writerKey.close();
        } else {
            System.out.println("Error: finding the key is impossible");
        }
    }

    private int checkMatch(final int[] freq_chars) {
        double sum = 0;

        // sum of every frequency of char
        for (int i = 0; i < this.m; i++)
            sum += freq_chars[i];

        // looking for the best rotate
        // the best rotate can describe chars of key
        int best_offset = 0;
        double best_fit = 0;
        for (int offset = 0; offset < this.m; offset++) {
            double fit = 0; //fit of every offset
            for (int i = 0; i < this.m; i++) {
                // it works based on Caesar, on every offset (k = 1, 2, 3, ...)
                // is checking the fit by formula
                // [frequency form chars by every offset] / [total of frequency of all chars] - [frequency char by char]
                double v = freq_chars[(i + offset) % this.m] / sum - freq[i];
                fit += v * v / freq[i]; // calculate the fit on scalar vectors
            }

            // looking for the best fit
            // less fit is better fit
            if (best_fit == 0) {
                best_fit = fit;
                best_offset = offset;
            } else if (fit < best_fit) {
                best_fit = fit;
                best_offset = offset;
            }
        }

        return best_offset;
    }

    private double frequencyEveryPosition(final int[] encrypted, int len, int offset, char[] key) {
        double[] accurate = new double[this.m];

        for (int j = 0; j < offset; j++) {
            int[] freq_chars = new int[this.m]; // frequency of every char

            // count of frequency of every char by offset
            for (int i = j; i < len; i += offset)
                freq_chars[encrypted[i]]++;

            int match = checkMatch(freq_chars); // get the best match basing on vectors and fit

            // write the letter for key
            try {
                key[j] = (char) (match + 'a');
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }

            // looking for accurate for matched char
            for (int i = 0; i < this.m; i++) accurate[i] += freq_chars[(i + match) % this.m];
        }

        // sum of accurate
        double sum = 0;
        for (int i = 0; i < this.m; i++) sum += accurate[i];

        // retrieve the fit
        double fit = 0;
        for (int i = 0; i < this.m; i++) {
            double d = accurate[i] / sum - freq[i];
            fit += d * d / freq[i];
        }

        key[offset] = '\0';
        return fit;
    }
}
