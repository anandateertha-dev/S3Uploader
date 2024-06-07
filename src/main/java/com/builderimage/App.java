package com.builderimage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) {
        try {
            // Set the working directory to "output"
            File workingDirectory = new File("output");
            if (!workingDirectory.exists()) {
                throw new RuntimeException("Output directory does not exist.");
            }

            // Run "npm install"
            runCommand(workingDirectory, "npm install");

            // Run "npm run build"
            runCommand(workingDirectory, "npm run build");

            // Check if the "dist" directory exists
            File distFolder = new File(workingDirectory, "dist");
            if (distFolder.exists() && distFolder.isDirectory()) {
                System.out.println("Dist folder exists.");
                // Upload files from the "dist" directory
                S3Upload s3Upload = new S3Upload();
                String bucketKeyPrefix = System.getenv("USER_ID") + "/" + System.getenv("PROJECT_ID") + "/"
                        + System.getenv("DEPLOYMENT_ID") + "/";

                s3Upload.uploadDirectory(distFolder, bucketKeyPrefix);

            } else {
                System.out.println("Dist folder does not exist or is not a directory.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runCommand(File directory, String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(directory);
        processBuilder.command("bash", "-c", command);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code " + exitCode);
        }
    }
}
