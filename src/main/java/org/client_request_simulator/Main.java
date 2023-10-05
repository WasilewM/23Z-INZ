package org.client_request_simulator;

import org.number_factorizor.NumberFactorizor;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
            exit(1);
        }

        String address = args[0];
        String port = args[1];

        ClientRequestSimulator crs = new ClientRequestSimulator(address, port, NumberFactorizor.FACTORIZATION_RANGE);
        crs.simulateTraffic();
        crs.closeConnection();
    }
}
