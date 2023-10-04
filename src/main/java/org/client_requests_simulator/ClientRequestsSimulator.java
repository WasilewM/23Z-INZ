package org.client_requests_simulator;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class ClientRequestsSimulator {
    private final String address;
    private final int port;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public ClientRequestsSimulator(String address, String port) {
        this.address = address;
        this.port = parsePortNumber(port);
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
            long request = simulateRequestData();
            output.writeLong(request);
            System.out.println("Sent: " + request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Long simulateRequestData() {
        return Math.abs(new Random().nextLong());
    }

    private void readResponseData() {
        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            System.out.println("Received: " + input.readLong());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
