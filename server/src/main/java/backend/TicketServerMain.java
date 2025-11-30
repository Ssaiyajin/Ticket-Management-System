package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TicketServerMain {

    public static void main(String[] args) {
        TicketStore simpleTestStore = new SimpleTicketStore();

        List<RemoteAccess> remoteAccessImplementations = getAvailableRemoteAccessImplementations(args);
        System.out.println("Starting server with " + remoteAccessImplementations.size() + " remote access implementation(s).");

        List<Thread> threads = new ArrayList<>();
        for (RemoteAccess implementation : remoteAccessImplementations) {
            try {
                implementation.prepareStartup(simpleTestStore);
                Thread t = new Thread((Runnable) implementation, implementation.getClass().getSimpleName());
                t.start();
                threads.add(t);
                System.out.println("Started: " + implementation.getClass().getSimpleName());
            } catch (Exception e) {
                System.out.println("Failed to start " + implementation.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        try (BufferedReader shutdownReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Press enter to shutdown system.");
            shutdownReader.readLine();
            System.out.println("Shutting down...");
        } catch (IOException e) {
            System.out.println("Error while waiting for shutdown input: " + e.getMessage());
        } finally {
            // notify implementations to stop
            for (RemoteAccess implementation : remoteAccessImplementations) {
                try {
                    implementation.shutdown();
                    System.out.println("Shutdown requested for: " + implementation.getClass().getSimpleName());
                } catch (Exception e) {
                    System.out.println("Error shutting down " + implementation.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }

            // wait briefly for threads to finish, interrupt if still alive
            for (Thread t : threads) {
                try {
                    t.join(2000);
                    if (t.isAlive()) {
                        System.out.println("Thread " + t.getName() + " did not stop, interrupting.");
                        t.interrupt();
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("Completed. Bye!");
        }
    }

    private static List<RemoteAccess> getAvailableRemoteAccessImplementations(String[] args) {
        List<RemoteAccess> implementations = new ArrayList<>();

        // add available RemoteAccess implementations here
        implementations.add(new UDPRemoteAccess());

        return implementations;
    }
}
