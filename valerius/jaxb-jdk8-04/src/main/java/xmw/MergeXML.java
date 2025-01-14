package xmw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MergeXML {
    public static String mergeXML(String mondialPath, String catdataPath) throws IOException {
        String mondialString = "";
        String catdataString = "";

        // read file into string
        try {
            mondialString = new String(Files.readAllBytes(Paths.get(mondialPath)));
        } catch (IOException e) {
            throw new FileNotFoundException("not there." + mondialPath);
        }
        try {
            catdataString = new String(Files.readAllBytes(Paths.get(catdataPath)));
        } catch (IOException e) {
            throw new FileNotFoundException("not there." + catdataPath);
        }

        // find the line number of the string 'car_code="E"'
        int lineNumber = 0;
        String[] lines = mondialString.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("car_code=\"E\"")) {
                lineNumber = i;
                break;
            }
        }
        // find all lines with '</country>'
        int endLineNumber = 0;
        for (int i = lineNumber; i < lines.length; i++) {
            if (lines[i].contains("</country>") && i > lineNumber) {
                endLineNumber = i;
                break;
            }
        }

        // concat <lines before endLineNumber inclusive> + catdataString + <lines after endLineNumber>

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= endLineNumber; i++) {
            sb.append(lines[i]);
            sb.append("\n");
        }
        sb.append(catdataString);
        sb.append("\n");
        for (int i = endLineNumber + 1; i < lines.length; i++) {
            sb.append(lines[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
