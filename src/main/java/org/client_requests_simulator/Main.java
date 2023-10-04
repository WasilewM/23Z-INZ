package org.client_requests_simulator;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
            exit(1);
        }

        ClientRequestsSimulator crs = new ClientRequestsSimulator();
        crs.simulateTraffic();
        crs.closeConnection();
    }
}
