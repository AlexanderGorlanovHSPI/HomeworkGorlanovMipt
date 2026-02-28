package javaIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

class FileProcessorTest {

    @TempDir
    Path tempDir;

    @Test
    void testSplitAndMergeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile(tempDir, "test", ".dat");
        byte[] testData = new byte[1500];
        new Random().nextBytes(testData);
        Files.write(testFile, testData);

        Path outputDir = Files.createTempDirectory(tempDir, "parts");
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir.toString(), 500);

        assertEquals(3, parts.size());
        assertEquals(500, Files.size(parts.get(0)));
        assertEquals(500, Files.size(parts.get(1)));
        assertEquals(500, Files.size(parts.get(2)));

        Path mergedFile = Files.createTempFile(tempDir, "merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());

        assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));
    }

    @Test
    void testEfficientCopy() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path sourceFile = Files.createTempFile(tempDir, "source", ".dat");
        byte[] testData = new byte[8192]; // 8KB
        new Random().nextBytes(testData);
        Files.write(sourceFile, testData);

        Path destFile = Files.createTempFile(tempDir, "dest", ".dat");
        processor.efficientCopy(sourceFile.toString(), destFile.toString());

        assertArrayEquals(Files.readAllBytes(sourceFile), Files.readAllBytes(destFile));
    }

    @Test
    void testSplitWithDifferentSizes() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path smallFile = Files.createTempFile(tempDir, "small", ".dat");
        Files.write(smallFile, new byte[100]);

        Path exactFile = Files.createTempFile(tempDir, "exact", ".dat");
        Files.write(exactFile, new byte[1000]);

        Path largeFile = Files.createTempFile(tempDir, "large", ".dat");
        Files.write(largeFile, new byte[10000]);

        Path outputDir = Files.createTempDirectory(tempDir, "multi_parts");

        List<Path> smallParts = processor.splitFile(smallFile.toString(),
                outputDir.resolve("small").toString(), 500);
        assertEquals(1, smallParts.size());

        List<Path> exactParts = processor.splitFile(exactFile.toString(),
                outputDir.resolve("exact").toString(), 250);
        assertEquals(4, exactParts.size());

        List<Path> largeParts = processor.splitFile(largeFile.toString(),
                outputDir.resolve("large").toString(), 1024);
        assertTrue(largeParts.size() >= 10);
    }
}