package org.number_factorizor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
            int requestedNumber = readRequest();
            System.out.println("requestedNumber: " + requestedNumber);
            ArrayList<Integer> factorizationSolution = createResponse(requestedNumber);
            System.out.println("factorizationSolution: " + factorizationSolution);
            sendResponse(factorizationSolution);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    private Integer readRequest() {
        int requestedNumber;
        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            requestedNumber = inputStream.readInt();
            System.out.println("Requested number: " + requestedNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return requestedNumber;
    }

    private static ArrayList<Integer> createResponse(int requestedNumber) {
        NumberFactorizor nf = new NumberFactorizor();
        return nf.factorize(requestedNumber);
    }

    private void sendResponse(ArrayList<Integer> factorizationSolution) throws IOException {
        outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeInt(factorizationSolution.size());
        for (int num : factorizationSolution) {
            outputStream.writeInt(num);
        }
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
