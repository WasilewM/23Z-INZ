package org.client_request_simulator;

import org.common.PortParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ClientRequestSimulator {
    private final int port;
    private final int max_request_value;
    private final String address;
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public ClientRequestSimulator(String address, String port, int max_request_value) {
        this.address = address;
        this.port = PortParser.parsePortNumber(port);
        this.max_request_value = max_request_value;
    }

    public void simulateTraffic() {
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendRequestData();
        readResponse();
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

    private void sendRequestData() {
        try {
            input = new DataInputStream(System.in);
            output = new DataOutputStream(socket.getOutputStream());
            int request = createRandomRequest();
            output.writeInt(request);
            System.out.println("Sent: " + request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer createRandomRequest() {
        return Math.abs(new Random().nextInt() % (max_request_value));
    }

    private void readResponse() {
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
