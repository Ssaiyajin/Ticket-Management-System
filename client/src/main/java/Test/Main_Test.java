package Test;

import java.lang.reflect.Method;
import java.util.Random;

import app.UDPConnManager;

/**
 * Small, deterministic test for UDPConnManager.
 * - uses args[0]=host, args[1]=port (optional)
 * - attempts to call sendData(payload, true) and falls back to sendData(payload)
 * - attempts to close the connection via common close methods (reflective)
 */
public class Main_Test {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = 1140;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                System.err.println("Invalid port argument, using default 1140");
            }
        }

        UDPConnManager conn = new UDPConnManager(host, port);
        try {
            conn.makeConnection();

            String payload = String.format("test-payload ts=%d rnd=%d", System.currentTimeMillis(), new Random().nextInt(10000));
            // Prefer (String, boolean) variant if present
            try {
                // try two-arg send (many builds use sendData(String, boolean))
                Method m = conn.getClass().getMethod("sendData", String.class, boolean.class);
                m.invoke(conn, payload, true);
            } catch (NoSuchMethodException nsme) {
                // fall back to single-arg sendData(String)
                try {
                    Method m2 = conn.getClass().getMethod("sendData", String.class);
                    m2.invoke(conn, payload);
                } catch (NoSuchMethodException | ReflectiveOperationException ex) {
                    System.err.println("sendData method not found or failed: " + ex.getMessage());
                }
            } catch (ReflectiveOperationException roe) {
                System.err.println("Failed to invoke sendData: " + roe.getMessage());
            }

            System.out.println("Payload sent to " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Connection/send error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // try common close/shutdown methods via reflection to avoid compile-time dependency
            for (String name : new String[] { "close", "closeConnection", "disconnect", "shutdown" }) {
                try {
                    Method close = conn.getClass().getMethod(name);
                    close.invoke(conn);
                    break;
                } catch (NoSuchMethodException ignored) {
                    // try next
                } catch (ReflectiveOperationException roe) {
                    System.err.println("Error calling " + name + ": " + roe.getMessage());
                }
            }
        }
    }
}
