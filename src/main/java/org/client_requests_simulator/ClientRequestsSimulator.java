package org.client_requests_simulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientRequestsSimulator {
    private String address = "localhost";
    private int port = 5000;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Long simulateRequestData() {
        return Math.abs(new Random().nextLong());
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
        }

        ClientRequestsSimulator crs = new ClientRequestsSimulator();
        crs.simulateTraffic();
    }

    private void simulateTraffic() {
        try {
            socket = new Socket(address, port);
            input = new DataInputStream(System.in);
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeRequestData();
        closeConnection();
    }

    private void writeRequestData() {
        try {
            output.writeLong(simulateRequestData());
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
