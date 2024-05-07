// FileWrite class

/**
 * Writes output to the specified file.
 * 
 * @author Yusuf Anil Yazici
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWrite {
    private static BufferedWriter writer = null;
    private static final int FLUSH_INTERVAL = 1000; // Flush every 1000 lines
    private static int writeCount = 0;

    // Take the filepath of the output file and create a bufferedwriter.
    public static void initWriter(String filePath) {
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writeCount = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write the specified line to the output file.
    public static void writeToFile(String line) {
        try {
            if (writer != null) {
                writer.write(line);
                writer.newLine();
                writeCount++;

                if (writeCount % FLUSH_INTERVAL == 0) {
                    writer.flush(); // Flush periodically
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Close the writer.
    public static void closeWriter() {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
