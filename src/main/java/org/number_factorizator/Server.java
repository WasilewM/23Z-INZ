package org.number_factorizator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.exit;

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

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
            exit(1);
        }

        Server server = new Server();
        server.serve();
        server.closeServer();
    }

    private void serve() {
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

    private void closeServer() {
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
