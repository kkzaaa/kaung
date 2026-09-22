package hms.util;

public class IdGenerator {

    public static String nextId(String filePath, String prefix) {
        int max = 0;
        for (String[] row : FileHandler.readRecords(filePath)) {
            String id = row[0];
            if (id.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(id.substring(prefix.length()));
                    max = Math.max(max, number);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return prefix + String.format("%03d", max + 1);
    }

    private IdGenerator() {
    }
}
