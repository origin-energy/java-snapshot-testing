package au.com.origin.snapshots;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SnapshotFile {

  public static final String SPLIT_STRING = "\n\n\n";

  private final String fileName;
  private final Class<?> testClass;
  @Getter
  private Set<Snapshot> snapshots = Collections.synchronizedSortedSet(new TreeSet<>());
  private Set<Snapshot> debugSnapshots = Collections.synchronizedSortedSet(new TreeSet<>());

  SnapshotFile(String srcDirPath, String fileName, Class<?> testClass) throws IOException {
    this.testClass = testClass;
    this.fileName = srcDirPath + File.separator + fileName;
    log.info("Snapshot File: " + this.fileName);

    StringBuilder fileContent = new StringBuilder();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), StandardCharsets.UTF_8))) {

      String sCurrentLine;

      while ((sCurrentLine = br.readLine()) != null) {
        fileContent.append(sCurrentLine + "\n");
      }

      String fileText = fileContent.toString();
      if (!"".equals(fileText.trim())) {
        snapshots =
            Collections.synchronizedSortedSet(
                Stream.of(fileContent.toString().split(SPLIT_STRING))
                    .map(String::trim)
                    .map(Snapshot::parse)
                    .collect(Collectors.toCollection(TreeSet::new)));
      }
    } catch (IOException e) {
      // ...
    }

    deleteDebugFile();
  }

  private String getDebugFilename() {
    return this.fileName + ".debug";
  }

  public File createDebugFile(Snapshot snapshot) {
    File file = null;
    try {
      file = new File(getDebugFilename());
      file.getParentFile().mkdirs();
      file.createNewFile();

      try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
        fileStream.write(snapshot.raw().getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return file;
  }

  @SneakyThrows
  public void deleteDebugFile() {
    Files.deleteIfExists(Paths.get(getDebugFilename()));
  }

  @SneakyThrows
  public void delete() {
    Files.deleteIfExists(Paths.get(this.fileName));
  }

  @SneakyThrows
  public synchronized File createFileIfNotExists(String filename) {
    Path path = Paths.get(filename);
    if (!Files.exists(path)) {
      Files.createDirectories(path.getParent());
      Files.createFile(path);
    }
    return path.toFile();
  }

  public synchronized void pushSnapshot(Snapshot snapshot) {
    snapshots.add(snapshot);
    TreeSet<String> rawSnapshots = snapshots
            .stream()
            .map(Snapshot::raw)
            .collect(Collectors.toCollection(TreeSet::new));
    updateFile(this.fileName, rawSnapshots);
  }

  public synchronized void pushDebugSnapshot(Snapshot snapshot) {
    debugSnapshots.add(snapshot);
    TreeSet<String> rawDebugSnapshots = debugSnapshots
            .stream()
            .map(Snapshot::raw)
            .collect(Collectors.toCollection(TreeSet::new));
    updateFile(getDebugFilename(), rawDebugSnapshots);
  }

  private void updateFile(String fileName, Set<String> rawSnapshots) {
    File file = createFileIfNotExists(fileName);
    try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
      byte[] myBytes = String.join(SPLIT_STRING, rawSnapshots).getBytes(StandardCharsets.UTF_8);
      fileStream.write(myBytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SneakyThrows
  public void cleanup() {
    Path path = Paths.get(this.fileName);
    if (Files.exists(path)) {
      if (Files.size(path) == 0 || snapshotsAreTheSame()) {
        deleteDebugFile();
      }

      if (Files.size(path) == 0) {
        delete();
      } else {
        String content = new String(Files.readAllBytes(Paths.get(this.fileName)), StandardCharsets.UTF_8);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
      }
    }
  }

  @SneakyThrows
  private boolean snapshotsAreTheSame() {
    Path path = Paths.get(this.getDebugFilename());
    if (Files.exists(path)) {
      List<String> snapshotFileContent = Files.readAllLines(Paths.get(this.fileName), StandardCharsets.UTF_8);
      List<String> debugSnapshotFileContent = Files.readAllLines(Paths.get(this.getDebugFilename()), StandardCharsets.UTF_8);
      return Objects.equals(snapshotFileContent, debugSnapshotFileContent);
    }

    return false;
  }
}
