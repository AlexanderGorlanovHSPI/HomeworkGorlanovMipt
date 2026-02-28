package javaIO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TextFileAnalyzer {

    public static class AnalysisResult {
        private final long lineCount;
        private final long wordCount;
        private final long charCount;
        private final Map<Character, Integer> charFrequency;
        private final long fileSize;

        public AnalysisResult(long lineCount, long wordCount, long charCount,
                              Map<Character, Integer> charFrequency, long fileSize) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.charCount = charCount;
            this.charFrequency = charFrequency;
            this.fileSize = fileSize;
        }

        public long getLineCount() { return lineCount; }
        public long getWordCount() { return wordCount; }
        public long getCharCount() { return charCount; }
        public Map<Character, Integer> getCharFrequency() { return charFrequency; }
        public long getFileSize() { return fileSize; }

        @Override
        public String toString() {
            return String.format("AnalysisResult{lines=%d, words=%d, chars=%d, size=%d bytes}",
                    lineCount, wordCount, charCount, fileSize);
        }
    }

    public AnalysisResult analyzeFile(String filePath) throws IOException {
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;
        Map<Character, Integer> charFrequency = new HashMap<>();

        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                charCount += line.length();

                if (!line.trim().isEmpty()) {
                    String[] words = line.trim().split("\\s+");
                    wordCount += words.length;
                }

                for (char c : line.toCharArray()) {
                    charFrequency.put(c, charFrequency.getOrDefault(c, 0) + 1);
                }

                charCount++;
            }

            if (charCount > 0) charCount--;

        }

        return new AnalysisResult(lineCount, wordCount, charCount, charFrequency, fileSize);
    }

    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("=== АНАЛИЗ ТЕКСТОВОГО ФАЙЛА ===\n");
            writer.write("================================\n\n");
            writer.write(String.format("Размер файла: %,d байт\n", result.getFileSize()));
            writer.write(String.format("Количество строк: %,d\n", result.getLineCount()));
            writer.write(String.format("Количество слов: %,d\n", result.getWordCount()));
            writer.write(String.format("Количество символов: %,d\n", result.getCharCount()));

            writer.write("\n=== ЧАСТОТА СИМВОЛОВ ===\n");
            writer.write("========================\n");

            result.getCharFrequency().entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(entry -> {
                        try {
                            char c = entry.getKey();
                            String charDisplay = getCharDisplay(c);
                            String unicode = String.format("U+%04X", (int) c);
                            writer.write(String.format("%-12s %-8s : %d\n",
                                    charDisplay, unicode, entry.getValue()));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private String getCharDisplay(char c) {
        switch (c) {
            case ' ': return "[ПРОБЕЛ]";
            case '\t': return "[ТАБ]";
            case '\n': return "[НОВАЯ_СТРОКА]";
            case '\r': return "[ВОЗВРАТ_КАРЕТКИ]";
            default: return "'" + c + "'";
        }
    }

    public AnalysisResult analyzeFileWithNIO2(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;
        Map<Character, Integer> charFrequency = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                charCount += line.length();

                if (!line.trim().isEmpty()) {
                    String[] words = line.trim().split("\\s+");
                    wordCount += words.length;
                }

                for (char c : line.toCharArray()) {
                    charFrequency.put(c, charFrequency.getOrDefault(c, 0) + 1);
                }

                charCount++;
            }

            if (charCount > 0) charCount--;
        }

        return new AnalysisResult(lineCount, wordCount, charCount, charFrequency, fileSize);
    }
}