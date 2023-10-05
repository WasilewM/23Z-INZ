package org.number_factorizator;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            long requestedNumber = readRequest();
            sendResponse(requestedNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    private Long readRequest() {
        long requestedNumber;
        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            requestedNumber = inputStream.readLong();
            System.out.println("Requested number: " + requestedNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return requestedNumber;
    }

    private void sendResponse(long requestedNumber) throws IOException {
        outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeLong(requestedNumber);
    }

    private void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
