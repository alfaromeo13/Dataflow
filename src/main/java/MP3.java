
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import jaco.mp3.player.MP3Player;

public class MP3 extends Thread {
    private final String file;

    public MP3(String file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            // Load the resource using ClassLoader
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Sounds/" + this.file + ".mp3");

            // Check if resource is found
            if (inputStream == null) {
                throw new FileNotFoundException("File not found: " + this.file);
            }

            // Create a temporary file to hold the input stream content
            File tempFile = File.createTempFile(this.file, ".mp3");
            tempFile.deleteOnExit(); // Delete the temp file when the JVM exits

            // Copy the content of input stream to the temporary file
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Create an MP3Player with the temporary file
            MP3Player mp3Player = new MP3Player(tempFile);
            mp3Player.play();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}