package common_tests;

import org.common.PortParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PortParserTest {
    @Test
    public void givenParsePortMethod_whenZeroAsPortNumber_thenZeroIsParsed() {
        assertEquals(0, PortParser.parsePortNumber("0"));
    }

    @Test
    public void givenParsePortMethod_whenHttpsPortNumber_then443IsParsed() {
        assertEquals(443, PortParser.parsePortNumber("443"));
    }

    @Test
    public void givenParsePortMethod_whenMaxPortNumber_thenMaxPortNumberIsParsed() {
        assertEquals(PortParser.MAX_PORT_AVAILABLE, PortParser.parsePortNumber("65535"));
    }

    @Test
    public void givenParsePortMethod_whenPortNumberExceedingMax_thenIllegalArgumentExceptionIsThrown() {
        boolean exceptionCaught = false;
        try {
            PortParser.parsePortNumber("65536");
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
            String expectedMsg = "IP ports cannot be greater than 65535";
            assertEquals(expectedMsg, e.getMessage());
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenParsePortMethod_whenNegativePortNumber_thenIllegalArgumentExceptionIsThrown() {
        boolean exceptionCaught = false;
        try {
            PortParser.parsePortNumber("-1");
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
            String expectedMsg = "IP ports cannot be smaller than 0";
            assertEquals(expectedMsg, e.getMessage());
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenParsePortMethod_whenTextAsInput_thenIllegalArgumentExceptionIsThrown() {
        boolean exceptionCaught = false;
        try {
            PortParser.parsePortNumber("abc");
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
            String expectedMsg = "Expected port number in range 0 - 65535, but received \"abc\"";
            assertEquals(expectedMsg, e.getMessage());
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenParsePortMethod_whenWhiteSpaceFollowedByValidNumber_thenIllegalArgumentExceptionIsThrown() {
        boolean exceptionCaught = false;
        try {
            PortParser.parsePortNumber(" 56");
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
            String expectedMsg = "Expected port number in range 0 - 65535, but received \" 56\"";
            assertEquals(expectedMsg, e.getMessage());
        }
        assertTrue(exceptionCaught);
    }
}
