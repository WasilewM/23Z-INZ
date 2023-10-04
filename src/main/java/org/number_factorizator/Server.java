package org.number_factorizator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private final int port = 5000;
    private final ServerSocket serverSocket;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Server() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serve() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            try {
                socket = serverSocket.accept();
                long requestedNumber = readRequestData();
                output = new DataOutputStream(socket.getOutputStream());
                output.writeLong(requestedNumber);
                closeConnection();
            } catch (IOException e) {
                shouldContinue = false;
            }
        }
    }

    public void closeServer() {
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Long readRequestData() {
        Long requestedNumber = null;
        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            requestedNumber = input.readLong();
            System.out.println("Requested number: " + requestedNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return requestedNumber;
    }

    private void closeConnection() {
        try {
            input.close();
            output.close();
            socket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
