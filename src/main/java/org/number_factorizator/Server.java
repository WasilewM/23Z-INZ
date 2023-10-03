package org.number_factorizator;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.exit;

public class Server {
    private final int port = 5000;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private DataInputStream input = null;

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
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            handleRequestData();
            closeConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequestData() {
        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            Long number = input.readLong();
            System.out.println(number);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        try {
            input.close();
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
