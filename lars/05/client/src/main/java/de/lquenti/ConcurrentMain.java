package de.lquenti;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConcurrentMain {
    public static void main(String[] args) throws Exception{
        File file = new File("/home/lquenti/code/xml-p/mondial-europe-no-dtd.xml");
        String targetUrl = "http://localhost:8080/reverseserver_war_exploded/reverse";

        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setChunkedStreamingMode(0);

        InputStream fileInput = new BufferedInputStream(new FileInputStream(file));
        OutputStream toServer = connection.getOutputStream();

        // TODO at this point the server already thinks I am done with my POST, while I havent even sent sth
        InputStream fromServer = connection.getInputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileInput.read(buffer)) != -1) {
            // first send to server
            toServer.write(buffer, 0, bytesRead);
            toServer.flush();

            // next, recv on same buffer
            int len = fromServer.read(buffer);
            if (len > 0) {
                System.out.write(buffer, 0, len);
            }
        }

        // final try
        int len = fromServer.read(buffer);
        if (len > 0) {
            System.out.write(buffer, 0, len);
        }

        fileInput.close();
        toServer.close();
        connection.disconnect();
    }
}