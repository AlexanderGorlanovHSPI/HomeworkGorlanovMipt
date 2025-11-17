package javaIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class TextFileAnalyzerTest {

    @TempDir
    Path tempDir;

    @Test
    void testAnalyzeFile() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path testFile = Files.createTempFile(tempDir, "test", ".txt");
        List<String> lines = Arrays.asList("Hello world!", "This is test.", "Third line");
        Files.write(testFile, lines);

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

        assertEquals(3, result.getLineCount());
        assertEquals(7, result.getWordCount());
        assertTrue(result.getCharCount() > 30);
        assertTrue(result.getFileSize() > 0);
        assertTrue(result.getCharFrequency().containsKey('l'));
    }

    @Test
    void testAnalyzeFileWithNIO2() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path testFile = Files.createTempFile(tempDir, "test_nio2", ".txt");
        List<String> lines = Arrays.asList("NIO2 test", "Вторая строка", "123");
        Files.write(testFile, lines);

        TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFileWithNIO2(testFile.toString());

        assertEquals(3, result.getLineCount());
        assertEquals(5, result.getWordCount());
        assertTrue(result.getFileSize() > 0);
    }

    @Test
    void testSaveAnalysisResult() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        java.util.Map<Character, Integer> frequency = new java.util.HashMap<>();
        frequency.put('a', 3);
        frequency.put('b', 2);
        frequency.put(' ', 5);

        TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(2, 5, 20, frequency, 100);

        Path outputFile = Files.createTempFile(tempDir, "analysis", ".txt");
        analyzer.saveAnalysisResult(result, outputFile.toString());

        assertTrue(Files.size(outputFile) > 0);
        String content = Files.readString(outputFile);
        assertTrue(content.contains("Количество строк: 2"));
        assertTrue(content.contains("Количество слов: 5"));
        assertTrue(content.contains("[ПРОБЕЛ]"));
    }
}