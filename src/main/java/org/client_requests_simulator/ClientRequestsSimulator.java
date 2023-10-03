package org.client_requests_simulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import static java.lang.System.exit;

public class ClientRequestsSimulator {
    private final String address = "localhost";
    private final int port = 5000;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Long simulateRequestData() {
        return Math.abs(new Random().nextLong());
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
            exit(1);
        }

        ClientRequestsSimulator crs = new ClientRequestsSimulator();
        crs.simulateTraffic();
        crs.closeConnection();
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
