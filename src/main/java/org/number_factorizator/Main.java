package org.number_factorizator;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Expected 2 but received " + args.length);
            exit(1);
        }

        Server server = new Server();
        server.serve();
        server.closeServer();
    }
}
