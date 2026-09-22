package com.wimp.app.specs.support;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper class to make accessing the file system for testing easier. See Test
 * File System pattern for details.
 * <p>
 * This class is a Cucumber glue class (because it uses a hook to capture the
 * current Scenario object), so Cucumber manages the lifetime of it. Because of
 * this, it cannot be registered as a normal Spring Boot component.
 */
public class TestFileSystem {
    /**
     * This setting needs to be adjusted according to the project structure.
     */
    public static final String TEST_RESOURCES_ROOT = "src/test/resources";

    public static final String TEST_RUN_TIMESTAMP =
        toPath(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")));

    private Scenario scenario;

    @Before
    public void captureCurrentScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public String getOutputFolder() {
        return ensureFolderExists(Paths.get(System.getProperty("user.dir"), "target", "wimp-output", TEST_RUN_TIMESTAMP).toString());
    }

    /**
     * Uses the resources folder as the default input folder.
     */
    public String getInputFolder() {
        return Paths.get(System.getProperty("user.dir"), TEST_RESOURCES_ROOT).toAbsolutePath().toString();
    }

    public String getFeatureInputFolder() {
        // Cucumber URI format looks like: "classpath:features/MyFeature.feature" or "file:src/test/resources/..." or "file:///C:/project/src/test/resources/..."
        var scenarioUri = scenario.getUri();
        String fullFeatureFilePath;
        if (scenarioUri.getScheme().equals("classpath")) {
            fullFeatureFilePath = Paths.get(getInputFolder(), scenarioUri.getSchemeSpecificPart()).toString();
        } else if (scenarioUri.getScheme().equals("file")) {
            if (scenarioUri.isAbsolute() && scenarioUri.getSchemeSpecificPart().startsWith("/")) {
                fullFeatureFilePath = Paths.get(scenarioUri).toAbsolutePath().toString();
            } else {
                String schemeSpecificPart = scenarioUri.getSchemeSpecificPart(); // e.g., "src/test/resources/"
                fullFeatureFilePath = Paths.get(System.getProperty("user.dir")).resolve(schemeSpecificPart).toAbsolutePath().toString();
            }
        } else {
            throw new IllegalStateException("Unable to get feature file path from scenario URI: " + scenarioUri);
        }
        return fullFeatureFilePath.replaceFirst("[/\\\\]?[^/\\\\]*\\.feature$", "");
    }

    public String getTempFolder() {
        return ensureFolderExists(
            Paths.get(System.getProperty("java.io.tmpdir"), "WIMP", TEST_RUN_TIMESTAMP).toString()
        );
    }

    public String getScenarioSpecificFileName() {
        return getScenarioSpecificFileName("");
    }

    public String getScenarioSpecificFileName(String extension) {
        String scenarioNameAsPath = toPath(scenario.getName());

        // Extract Cucumber's unique ID for the current row (e.g., "classpath:features/test.feature:12")
        String uniqueRowId = toPath(scenario.getId());

        return scenarioNameAsPath + "_" + uniqueRowId + extension;
    }

    /**
     * Makes a string path-compatible by removing invalid symbols and replacing
     * whitespaces with underscores.
     */
    public static String toPath(String s) {
        if (s == null) {
            return "";
        }
        String clean = s.replaceAll("[\\\\/:*?\"<>|\\x00-\\x1F]", "");
        return clean.replace(' ', '_');
    }

    private String ensureFolderExists(String pathStr) {
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            synchronized (this) {
                if (!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to generate test directory: " + pathStr, e);
                    }
                }
            }
        }
        return pathStr;
    }
}
