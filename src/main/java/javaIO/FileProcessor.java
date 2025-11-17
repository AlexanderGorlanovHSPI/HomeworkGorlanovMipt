package javaIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileProcessor {

    /**
     * Разбивает файл на части указанного размера
     * @param sourcePath путь к исходному файлу
     * @param outputDir директория для сохранения частей
     * @param partSize размер каждой части в байтах
     * @return список путей к созданным частям
     */
    public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
        List<Path> partPaths = new ArrayList<>();
        Path sourceFile = Paths.get(sourcePath);

        if (!Files.exists(sourceFile)) {
            throw new NoSuchFileException("Исходный файл не найден: " + sourcePath);
        }

        Path outputDirectory = Paths.get(outputDir);
        Files.createDirectories(outputDirectory);

        String fileName = sourceFile.getFileName().toString();
        long fileSize = Files.size(sourceFile);

        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
            int partNumber = 1;
            long position = 0;

            ByteBuffer buffer = ByteBuffer.allocateDirect(partSize);

            while (position < fileSize) {
                String partFileName = String.format("%s.part%d", fileName, partNumber);
                Path partPath = outputDirectory.resolve(partFileName);

                try (FileChannel partChannel = FileChannel.open(partPath,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

                    buffer.clear();
                    int bytesRead = sourceChannel.read(buffer, position);

                    if (bytesRead > 0) {
                        buffer.flip();
                        partChannel.write(buffer);
                        partPaths.add(partPath);
                        position += bytesRead;
                        partNumber++;
                    }
                }
            }
        }

        return partPaths;
    }

    /**
     * Объединяет части файла обратно в один файл
     * @param partPaths список путей к частям файла (в правильном порядке)
     * @param outputPath путь для результирующего файла
     */
    public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
        Path outputFile = Paths.get(outputPath);

        partPaths.sort(Comparator.comparing(this::extractPartNumber));

        try (FileChannel outputChannel = FileChannel.open(outputFile,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            for (Path partPath : partPaths) {
                if (!Files.exists(partPath)) {
                    throw new NoSuchFileException("Часть файла не найдена: " + partPath);
                }

                try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
                    long partSize = Files.size(partPath);
                    long transferred = 0;

                    while (transferred < partSize) {
                        transferred += partChannel.transferTo(transferred,
                                partSize - transferred, outputChannel);
                    }
                }
            }
        }
    }

    /**
     * Извлекает номер части из имени файла
     */
    private int extractPartNumber(Path partPath) {
        String fileName = partPath.getFileName().toString();
        try {
            String numberStr = fileName.replaceAll(".*\\.part(\\d+)$", "$1");
            return Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат имени файла части: " + fileName);
        }
    }

    /**
     * Дополнительный метод: эффективное копирование файла с использованием transferTo
     */
    public void efficientCopy(String sourcePath, String destPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(destPath);

        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(dest,
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long size = sourceChannel.size();
            long transferred = 0;

            while (transferred < size) {
                transferred += sourceChannel.transferTo(transferred, size - transferred, destChannel);
            }
        }
    }

    /**
     * Дополнительный метод: работа с MappedByteBuffer для больших файлов
     */
    public void processWithMappedBuffer(String filePath, int bufferSize) throws IOException {
        Path path = Paths.get(filePath);

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = Files.size(path);
            long position = 0;

            while (position < fileSize) {
                long size = Math.min(bufferSize, fileSize - position);

                ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);

                processBufferData(buffer);

                position += size;
            }
        }
    }

    private void processBufferData(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
        }
    }
}