package org.number_factorizor;


import org.common.PortParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private final int port;
    private final ServerSocket serverSocket;

    public Server(String port) {
        this.port = PortParser.parsePortNumber(port);
        serverSocket = initServerSocket();
    }

    public void serve() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            try {
                Socket socket = serverSocket.accept();
                new ClientThread(socket).start();
            } catch (IOException e) {
                shouldContinue = false;
            }
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

    public void closeServer() {
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
