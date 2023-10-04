package org.number_factorizator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private final int port;
    private final ServerSocket serverSocket;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Server(String port) {
        this.port = parsePortNumber(port);
        serverSocket = initServerSocket();
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

    private ServerSocket initServerSocket() {
        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverSocket;
    }

    private static Integer parsePortNumber(String port) {
        int portNumber;
        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        return portNumber;
    }

    private Long readRequestData() {
        long requestedNumber;
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
