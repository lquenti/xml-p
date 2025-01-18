package de.lquenti;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketConcurrentMain {
    public static void main(String[] args) throws Exception {
        File file = new File("../../../mondial-europe-no-dtd.xml");
        String hostname = "localhost";
        int port = 8080;
        String path = "/reverseserver-1.0-SNAPSHOT/reverse";

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostname, port));

        OutputStream toServer = socket.getOutputStream();
        InputStream fromServer = socket.getInputStream();
        InputStream fileInput = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[4096];
        int bytesRead;

        // build own header
        String headers = "POST " + path + " HTTP/1.1\r\n"
                + "Host: " + hostname + "\r\n"
                + "Content-Type: application/xml\r\n"
                + "Transfer-Encoding: chunked\r\n"
                + "Connection: close\r\n"
                + "\r\n";
        toServer.write(headers.getBytes(StandardCharsets.UTF_8));
        toServer.flush();

        // allow to read as soon as we get data
        Thread readerThread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fromServer, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        readerThread.start();

        // send data
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            // Send chunk size in hex followed by CRLF
            String chunkSize = Integer.toHexString(bytesRead) + "\r\n";
            toServer.write(chunkSize.getBytes(StandardCharsets.UTF_8));

            // Send the chunk data followed by CRLF
            toServer.write(buffer, 0, bytesRead);
            toServer.write("\r\n".getBytes(StandardCharsets.UTF_8));
            toServer.flush();
        }

        // Send zero-length chunk to indicate end of data
        toServer.write("0\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        toServer.flush();
        readerThread.join();

        // Close output stream since we've finished sending
        toServer.close();
        fileInput.close();

        // Wait for the reader thread to finish
        readerThread.join();
        socket.close();
    }
}