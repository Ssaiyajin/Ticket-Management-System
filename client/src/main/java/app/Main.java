package app;

import ui.swing.MainFrame;
import ui.swing.SwingMainController;
import ui.swing.SwingMainModel;

import javax.swing.SwingUtilities;

/**
 * Main class to start the TicketManagement5000 client application Currently
 * only a local backend implementation is registered.<br>
 * 
 * To add additional implementations modify the method
 * <code>evaluateArgs(String[] args)</code>
 *
 * @see #evaluateArgs(String[])
 */
public class Main {

    /**
     * Starts the TicketManagement5000 application based on the given arguments
     * 
     * <p>
     * <b>TODO No changes needed here - but documentation of allowed args should
     * be updated</b>
     * </p>
     * 
     * @param args
     */
    
    public static void main(String[] args) {
        final TicketManagementBackend backendToUse = evaluateArgs(args);

        // Create UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                SwingMainController control = new SwingMainController(backendToUse);
                SwingMainModel model = new SwingMainModel(backendToUse);
                MainFrame mf = new MainFrame(control, model);

                control.setMainFrame(mf);
                control.setSwingMainModel(model);

                control.start();
            } catch (Exception e) {
                System.err.println("Failed to start UI: " + e.getMessage());
                e.printStackTrace();
                // Best-effort shutdown of backend
                try {
                    if (backendToUse != null) backendToUse.triggerShutdown();
                } catch (Exception ignored) {}
            }
        });
    }

    /**
     * Determines which {@link TicketManagementBackend} should be used by
     * evaluating the given {@code args}.
     * 
     * If <code>null</code>, an empty array or an unknown argument String is
     * passed, the default {@code LocalTicketManagementBackend} is used.
     * 
     * <p>
     * <b>This method must be modified in order to register new implementations
     * of {@code TicketManagementBackend}.</b>
     * </p>
     * 
     * @param args
     *            a String array to be evaluated
     * @return the selected {@link TicketManagementBackend} implementation
     * 
     * @see TicketManagementBackend
     */
    private static TicketManagementBackend evaluateArgs(String[] args) {

        if (args == null || args.length == 0) {
            System.out.println("No arguments passed. Using local backend implementation.");
            return new LocalTicketManagementBackend();
            
        } else {
            switch (args[0]) {
            case "local":
                return new LocalTicketManagementBackend();
                
            case "udp": {
                // optional args: udp [host] [port]
                String host = "127.0.0.1";
                int port = 1140;
                if (args.length >= 2 && args[1] != null && !args[1].isEmpty()) {
                    host = args[1];
                }
                if (args.length >= 3) {
                    try {
                        port = Integer.parseInt(args[2]);
                    } catch (NumberFormatException nfe) {
                        System.err.println("Invalid port specified, falling back to default 1140");
                        port = 1140;
                    }
                }
                System.out.println("Using UDP backend -> " + host + ":" + port);
                return new UDPTicketManagementBackend(); // UDPTicketManagementBackend reads system props or defaults; consider adding ctor(host,port) if needed
            }
            case "jms" :
                    return new JMSTicketManagementBackend();
            // TODO Register new backend implementations here as additional
            // cases. E.g.:
            // case "udp":
            // String host = args[1];
            // int port = Integer.parseInt(args[2]);
            // return new UdpTicketManagementBackend(host, port);

            // Default case for unknown implentations
            default:
                System.out.println("Unknown backend type. Using local backend implementation.");
                return new LocalTicketManagementBackend();
            }

        }
    }
}
