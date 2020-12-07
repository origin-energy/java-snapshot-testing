package au.com.origin.snapshots;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class SnapshotFile {

    private static final String SPLIT_STRING = "\n\n\n";

    @Getter
    private final String snapshotFilePath;
    private final Class<?> testClass;
    private final BiFunction<Class<?>, String, String> onSaveSnapshotFile;

    @Getter
    private Set<String> rawSnapshots;

    SnapshotFile(String srcDirPath, String snapshotFilePath, Class<?> testClass, BiFunction<Class<?>, String, String> onSaveSnapshotFile) throws IOException {
        this.testClass = testClass;
        this.onSaveSnapshotFile = onSaveSnapshotFile;
        this.snapshotFilePath = srcDirPath + File.separator + snapshotFilePath;
        log.info("Snapshot File: " + this.snapshotFilePath);

        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(this.snapshotFilePath))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }

            String fileText = fileContent.toString();
            if (StringUtils.isNotBlank(fileText)) {
                rawSnapshots =
                    Stream.of(fileContent.toString().split(SPLIT_STRING))
                        .map(String::trim)
                        .collect(Collectors.toCollection(TreeSet::new));
            } else {
                rawSnapshots = new TreeSet<>();
            }
        } catch (IOException e) {
            rawSnapshots = new TreeSet<>();
        }
    }

    @SneakyThrows
    public void delete() {
        Files.deleteIfExists(Paths.get(this.snapshotFilePath));
    }

    @SneakyThrows
    public File createFileIfNotExists() {
        Path path = Paths.get(this.snapshotFilePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        return path.toFile();
    }

    public void push(String snapshot) {
        rawSnapshots.add(snapshot);

        File file = createFileIfNotExists();

        try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
            byte[] myBytes = StringUtils.join(rawSnapshots, SPLIT_STRING).getBytes();
            fileStream.write(myBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void cleanup() {
        Path path = Paths.get(this.snapshotFilePath);
        if (Files.size(path) == 0) {
            delete();
        } else {
            String content = new String(Files.readAllBytes(Paths.get(this.snapshotFilePath)));
            String modified = onSaveSnapshotFile.apply(testClass, content);
            Files.write(path, modified.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
