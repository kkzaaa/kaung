package hms.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String SPLIT_PATTERN = "\\" + Constants.DELIMITER;

    public static List<String[]> readRecords(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(line.split(SPLIT_PATTERN, -1));
            }
        } catch (IOException e) {
            System.out.println("Error reading " + filePath + ": " + e.getMessage());
        }
        return records;
    }

    public static void appendRecord(String filePath, String[] fields) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(String.join(Constants.DELIMITER, fields));
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing " + filePath + ": " + e.getMessage());
        }
    }

    public static void rewriteFile(String filePath, List<String[]> records) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String[] r : records) {
                bw.write(String.join(Constants.DELIMITER, r));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error rewriting " + filePath + ": " + e.getMessage());
        }
    }

    private FileHandler() {
    }
}
