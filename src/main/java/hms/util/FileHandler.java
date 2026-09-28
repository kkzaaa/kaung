package hms.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileHandler {
    private static final String SPLIT_PATTERN = "\\" + Constants.DELIMITER;

    public static List<String[]> readRecords(String filePath) {
        List<String[]> records = new ArrayList<>();
        Integer expected = Constants.FIELD_COUNTS.get(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] fields = line.split(SPLIT_PATTERN, -1);
                if (expected != null && fields.length != expected) {
                    System.out.println("Skipping malformed line " + lineNumber + " in " + filePath + " (expected "
                            + expected + " fields, found " + fields.length + "): " + line);
                    continue;
                }
                records.add(fields);
            }
        } catch (IOException e) {
            System.out.println("Error reading " + filePath + ": " + e.getMessage());
        }
        return records;
    }

    public static void appendRecord(String filePath, String[] fields) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(fields));
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing " + filePath + ": " + e.getMessage());
        }
    }

    public static void rewriteFile(String filePath, List<String[]> records) {
        // readRecords skips malformed lines, so carry them over unchanged instead of silently deleting them.
        List<String> malformedLines = readMalformedLines(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String[] r : records) {
                bw.write(toLine(r));
                bw.newLine();
            }
            for (String line : malformedLines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error rewriting " + filePath + ": " + e.getMessage());
        }
    }

    // Money is always stored with 2 decimals and a "." (Locale.ROOT), so 120.5 is saved as "120.50" on any PC.
    public static String formatAmount(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }

    private static List<String> readMalformedLines(String filePath) {
        List<String> malformed = new ArrayList<>();
        Integer expected = Constants.FIELD_COUNTS.get(filePath);
        if (expected == null) {
            return malformed;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank() && line.split(SPLIT_PATTERN, -1).length != expected) {
                    malformed.add(line);
                }
            }
        } catch (IOException e) {
            // Nothing to preserve if the file can't be read yet.
        }
        return malformed;
    }

    // A "|" or line break inside user-typed text would split one record into broken fields/lines.
    private static String toLine(String[] fields) {
        String[] clean = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String value = fields[i] == null ? "" : fields[i];
            clean[i] = value.replace(Constants.DELIMITER, "/").replaceAll("\\R", " ");
        }
        return String.join(Constants.DELIMITER, clean);
    }

    private FileHandler() {
    }
}
