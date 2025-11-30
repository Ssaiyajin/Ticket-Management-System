package backend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/*
 * UDPRemoteAccess: improved UDP listener for ticket RPC/messages.
 * - Better naming, charset-safe string parsing, graceful shutdown handling.
 * - Non-static active flag so multiple instances behave correctly.
 */
public class UDPRemoteAccess implements RemoteAccess, Runnable {

    private static final int DEFAULT_PORT = 1140;

    private volatile boolean active = true;
    private DatagramSocket datagramSocket;

    public UDPRemoteAccess() {
    }

    @Override
    public void run() {
        // Ensure socket is available
        if (datagramSocket == null) {
            System.out.println("UDP socket is not initialized. Call prepareStartup(...) first.");
            return;
        }

        while (active && !datagramSocket.isClosed()) {
            byte[] buffer = new byte[64507]; // max UDP payload for IPv4
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocket.receive(packet);

                String rawJsonData = new String(packet.getData(), packet.getOffset(), packet.getLength(),
                        StandardCharsets.UTF_8).trim();

                InetAddress remoteAddr = packet.getAddress();
                int remotePort = packet.getPort();
                System.out.println("Received UDP packet from " + remoteAddr + ":" + remotePort +
                        " - payload length=" + packet.getLength());
                // TODO: parse rawJsonData and forward to ticket store / processing
                System.out.println("Payload: " + rawJsonData);

            } catch (SocketException se) {
                // Socket closed or network error - exit loop if socket closed
                if (datagramSocket == null || datagramSocket.isClosed()) {
                    System.out.println("UDP socket closed, stopping listener.");
                    break;
                }
                System.out.println("SocketException in UDP listener: " + se.getMessage());
            } catch (IOException ioe) {
                System.out.println("I/O error while receiving UDP packet: " + ioe.getMessage());
                // continue listening unless socket closed
            } catch (Exception e) {
                System.out.println("Unexpected error in UDP listener: " + e.getMessage());
            }
        }
    }

    @Override
    public void prepareStartup(TicketStore ticketStore) {
        // initialize and bind socket; try binding to local host, fallback to wildcard address
        try {
            InetAddress bindAddr;
            try {
                bindAddr = InetAddress.getLocalHost();
            } catch (UnknownHostException uhe) {
                bindAddr = InetAddress.getByName("0.0.0.0");
            }
            this.datagramSocket = new DatagramSocket(new InetSocketAddress(bindAddr, DEFAULT_PORT));
            System.out.println("UDP Socket is running on " + datagramSocket.getLocalSocketAddress().toString());
        } catch (SocketException | UnknownHostException e) {
            System.out.println("Error creating UDP socket binding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        this.active = false;
        if (this.datagramSocket != null && !this.datagramSocket.isClosed()) {
            try {
                this.datagramSocket.close();
            } catch (Exception e) {
                // closing a DatagramSocket rarely throws, but log if it happens
                System.out.println("Error closing UDP socket: " + e.getMessage());
            }
        }
    }
}
