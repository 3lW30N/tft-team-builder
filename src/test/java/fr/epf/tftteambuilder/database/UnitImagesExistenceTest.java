package fr.epf.tftteambuilder.database;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitImagesExistenceTest {
    private static final Pattern IMAGE_PATH_PATTERN = Pattern.compile("'(?<path>img/[^']+)'"
    );

    @Test
    void allImageLinksInDatabaseDumpsShouldExist() throws IOException {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path dumpDir = projectRoot.resolve("src/main/resources/database/dump");
        Path staticDir = projectRoot.resolve("src/main/resources/static");

        List<String> missingImages = new ArrayList<>();

        try (Stream<Path> dumpFiles = Files.list(dumpDir)) {
            dumpFiles
                    .filter(path -> path.getFileName().toString().endsWith(".sql"))
                    .forEach(sqlFile -> {
                        try {
                            String content = Files.readString(sqlFile);
                            Matcher matcher = IMAGE_PATH_PATTERN.matcher(content);
                            while (matcher.find()) {
                                String imagePath = matcher.group("path");
                                if (!Files.exists(staticDir.resolve(imagePath))) {
                                    missingImages.add(sqlFile.getFileName() + " -> " + imagePath);
                                }
                            }
                        } catch (IOException e) {
                            throw new IllegalStateException("Unable to read " + sqlFile, e);
                        }
                    });
        }

        assertTrue(
                missingImages.isEmpty(),
                () -> "Missing image files referenced by database dumps: " + missingImages
        );
    }
}

