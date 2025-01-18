package de.lquenti;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BlockingMain {
    public static void main(String[] args) throws Exception {
        File file = new File("../../../mondial-europe-no-dtd.xml");
        String targetUrl = "http://localhost:8080/reverseserver_war_exploded/reverse";

        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setChunkedStreamingMode(0);

        OutputStream toServer = connection.getOutputStream();
        InputStream fileInput = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[4096];
        int bytesRead;

        // Send fully
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            System.out.println(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(buffer)));
            toServer.write(buffer, 0, bytesRead);
            Thread.sleep(100);
        }

        // then close
        toServer.flush();
        toServer.close();
        fileInput.close();

        // then read
        InputStream fromServer = connection.getInputStream();
        while ((bytesRead = fromServer.read(buffer)) != -1) {
            System.out.write(buffer, 0, bytesRead);
        }

        fromServer.close();
        connection.disconnect();
    }
}