package org.common;

public class PortParser {
    public static final int MAX_PORT_AVAILABLE = 65535;
    public static final int MIN_PORT_AVAILABLE = 0;

    public static int parsePortNumber(String port) throws IllegalArgumentException{
        int portNumber = tryToParsePortNumber(port);
        validatePortNumberRange(portNumber);
        return portNumber;
    }

    private static void validatePortNumberRange(int portNumber) {
        if (portNumber < MIN_PORT_AVAILABLE) {
            throw new IllegalArgumentException("IP ports cannot be smaller than " + MIN_PORT_AVAILABLE);
        }

        if (portNumber > MAX_PORT_AVAILABLE) {
            throw new IllegalArgumentException("IP ports cannot be greater than " + MAX_PORT_AVAILABLE);
        }
    }

    private static int tryToParsePortNumber(String port) {
        int portNumber;
        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            String msg = "Expected port number in range " + MIN_PORT_AVAILABLE + " - " + MAX_PORT_AVAILABLE;
            msg += ", but received \"" + port + "\"";
            throw new IllegalArgumentException(msg);
        }
        return portNumber;
    }
}
