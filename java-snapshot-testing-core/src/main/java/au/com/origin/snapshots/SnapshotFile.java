package au.com.origin.snapshots;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
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
  public synchronized File createFileIfNotExists() {
    Path path = Paths.get(this.fileName);
    if (!Files.exists(path)) {
      Files.createDirectories(path.getParent());
      Files.createFile(path);
    }
    return path.toFile();
  }

  public void push(Snapshot snapshot) {
    snapshots.add(snapshot);

    File file = createFileIfNotExists();

    TreeSet<String> rawSnapshots = snapshots
            .stream()
            .map(Snapshot::raw)
            .collect(Collectors.toCollection(TreeSet::new));

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
      if (Files.size(path) == 0) {
        deleteDebugFile();
        delete();
      } else {
        String content = new String(Files.readAllBytes(Paths.get(this.fileName)), StandardCharsets.UTF_8);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
      }
    }
  }
}
