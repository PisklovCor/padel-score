package com.padelscore;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.fail;

public class YamlLintTest {

  @Test
  void yamllintShouldPassOnAllYamlFiles() throws Exception {
    // Линтим все YAML в репозитории, используя .yamllint.yml в корне
    runYamllint(".");
  }

  private void runYamllint(String path) throws IOException, InterruptedException {
    ProcessBuilder pb = new ProcessBuilder(
        "yamllint",
        "--strict",   // чтобы warnings тоже отдавали exit code
        path
    );
    pb.redirectErrorStream(true);

    Process process = pb.start();

    StringBuilder output = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append(System.lineSeparator());
      }
    }

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      fail("yamllint failed with exit code " + exitCode + ":\n" + output);
    }
  }
}
