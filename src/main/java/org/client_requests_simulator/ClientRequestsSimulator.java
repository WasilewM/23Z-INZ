package org.client_requests_simulator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ClientRequestsSimulator {
    private final int port;
    private final int request;
    private final String address;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public ClientRequestsSimulator(String address, String port) {
        this.address = address;
        this.port = parsePortNumber(port);
        this.request = Integer.MAX_VALUE / 1000;
    }

    public void simulateTraffic() {
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeRequestData();
        readResponseData();
    }

    public void closeConnection() {
        try {
            input.close();
            output.close();
            socket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
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

    private void writeRequestData() {
        try {
            input = new DataInputStream(System.in);
            output = new DataOutputStream(socket.getOutputStream());
            int request = simulateRequestData();
            output.writeInt(request);
            System.out.println("Sent: " + request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer simulateRequestData() {
        return Math.abs(new Random().nextInt() % (request));
    }

    private void readResponseData() {
        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            ArrayList<Integer> factorizationSolution = new ArrayList<>();
            int numsLeftToRead = input.readInt();
            while (numsLeftToRead > 0) {
                factorizationSolution.add(input.readInt());
                numsLeftToRead -= 1;
            }
            System.out.println("Received: " + factorizationSolution);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
