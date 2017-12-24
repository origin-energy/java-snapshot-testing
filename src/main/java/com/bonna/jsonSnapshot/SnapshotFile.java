package com.bonna.jsonSnapshot;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnapshotFile {

    private static final String SPLIT_STRING = "\n-------\n";
    private static final String FILE_PATH = "src/test/java/";

    private String fileName;

    @Getter
    private List<String> rawSnapshots;

    SnapshotFile(String fileName) throws IOException {

        this.fileName = FILE_PATH + fileName;

        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }

            String fileText = fileContent.toString();
            if (StringUtils.isNotBlank(fileText)) {
                rawSnapshots = Stream.of(fileContent.toString().split(SPLIT_STRING)).collect(Collectors.toList());
            }
            else {
                rawSnapshots = new ArrayList<>();
            }
        } catch (IOException e) {
            createFile(this.fileName);
            rawSnapshots = new ArrayList<>();
        }
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
