package org.client_requests_simulator;

import java.io.*;
import java.net.Socket;
import java.util.Random;

import static java.lang.System.exit;

public class ClientRequestsSimulator {
    private final String address = "localhost";
    private final int port = 5000;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
            exit(1);
        }

        ClientRequestsSimulator crs = new ClientRequestsSimulator();
        crs.simulateTraffic();
        crs.closeConnection();
    }

    public Long simulateRequestData() {
        return Math.abs(new Random().nextLong());
    }

    private void simulateTraffic() {
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeRequestData();
        readResponseData();
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

    private void readResponseData() {
        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            System.out.println("Received: " + input.readLong());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
