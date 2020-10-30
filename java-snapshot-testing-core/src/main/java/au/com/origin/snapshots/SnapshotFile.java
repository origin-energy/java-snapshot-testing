package au.com.origin.snapshots;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class SnapshotFile {

    private static final String SPLIT_STRING = "\n\n\n";

    private final String fileName;

    private String getDebugFilename() {
        return this.fileName + ".debug";
    }

    @Getter
    private Set<String> rawSnapshots;

    SnapshotFile( String srcDirPath, String fileName) throws IOException {
        this.fileName = srcDirPath + File.separator + fileName;
        log.info("Snapshot File: " + this.fileName);

        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {

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
            createFile(this.fileName);
            rawSnapshots = new TreeSet<>();
        }
    }

    public File createDebugFile(String snapshot) {
        File file = null;
        try {
            file = new File(getDebugFilename());
            file.getParentFile().mkdirs();
            file.createNewFile();

            try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
                fileStream.write(snapshot.getBytes());
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

    private File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }

    public void push(String snapshot) {

        rawSnapshots.add(snapshot);

        File file = null;

        try {
            file = createFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
            byte[] myBytes = StringUtils.join(rawSnapshots, SPLIT_STRING).getBytes();
            fileStream.write(myBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
