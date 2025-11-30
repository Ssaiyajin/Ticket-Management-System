package app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lightweight, robust UDP connection helper.
 *
 * - create connection with makeConnection()
 * - sendData(payload, expectResponse) sends payload (splits into safe UDP sized chunks) and optionally waits for a single response
 * - sendData(payload) convenience overload (delegates to sendData(payload, true))
 * - close()/shutdown()/endConnection() close the socket
 *
 * This class is intentionally conservative: it uses a connected DatagramSocket and synchronous send/receive.
 */
public class UDPConnManager implements Runnable {

    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int MAX_UDP_PAYLOAD = 60_000; // safe threshold under MTU

    private final String ip;
    private final int port;

    private DatagramSocket socket;
    private InetAddress remoteAddress;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public UDPConnManager(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public synchronized void makeConnection() throws SocketException, UnknownHostException {
        if (connected.get()) return;

        DatagramSocket s = new DatagramSocket(null);
        s.setSoTimeout(DEFAULT_TIMEOUT_MS);
        remoteAddress = Inet4Address.getByName(this.ip);
        s.connect(remoteAddress, this.port);
        this.socket = s;
        connected.set(true);
        System.out.println("UDPConnManager: connected to " + this.ip + ":" + this.port);
    }

    /**
     * Close the connection / socket.
     */
    public synchronized void endConnection() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
        connected.set(false);
        socket = null;
        System.out.println("UDPConnManager: connection closed");
    }

    // provide common aliases expected by other code/reflection
    public void closeConnection() { endConnection(); }
    public void shutdown() { endConnection(); }
    public void close() { endConnection(); }

    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Send data. If expectResponse==true this method will wait for a single UDP response and return it (may be null).
     * If expectResponse==false method returns immediately after sending all chunks and returns "Sent!".
     */
    public String sendData(String data, boolean expectResponse) throws IOException {
        if (data == null) data = "";
        if (!isConnected()) throw new IllegalStateException("Not connected. Call makeConnection() first.");

        byte[] payloadBytes = data.getBytes(StandardCharsets.UTF_8);
        int totalLen = payloadBytes.length;
        int chunks = (totalLen + MAX_UDP_PAYLOAD - 1) / MAX_UDP_PAYLOAD;
        if (chunks <= 0) chunks = 1;

        for (int i = 0; i < chunks; i++) {
            int offset = i * MAX_UDP_PAYLOAD;
            int len = Math.min(MAX_UDP_PAYLOAD, totalLen - offset);
            byte[] piece = new byte[len];
            System.arraycopy(payloadBytes, offset, piece, 0, len);
            DatagramPacket packet = new DatagramPacket(piece, piece.length);
            socket.send(packet);
        }

        if (!expectResponse) return "Sent!";

        // wait for single response
        byte[] recvBuf = new byte[65507];
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
        try {
            socket.receive(recvPacket);
            return new String(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength(), StandardCharsets.UTF_8);
        } catch (SocketException se) {
            if (!isConnected()) {
                return null;
            }
            throw se;
        } catch (IOException ioe) {
            // timeout or IO error -> return null to indicate no response
            return null;
        }
    }

    /**
     * Convenience single-arg sendData used by some callers.
     */
    public String sendData(String data) throws IOException {
        return sendData(data, true);
    }

    /**
     * Runnable loop - optional listener that prints incoming messages while connected.
     * Not required for synchronous request/response usage but provided for completeness.
     */
    @Override
    public void run() {
        // simple listener that prints received packets until socket closed
        while (isConnected() && socket != null && !socket.isClosed()) {
            byte[] buf = new byte[65507];
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(p);
                String msg = new String(p.getData(), p.getOffset(), p.getLength(), StandardCharsets.UTF_8);
                System.out.println("UDPConnManager received: " + msg);
            } catch (IOException e) {
                // timeout expected; continue loop unless socket closed
                if (socket == null || socket.isClosed()) break;
            }
        }
    }
}
