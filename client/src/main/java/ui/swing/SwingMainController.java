package ui.swing;

import java.util.List;
import java.util.Objects;

import app.Shutdown;
import entities.Priority;
import entities.Ticket;
import entities.TicketException;
import entities.Type;

public class SwingMainController {

    private SwingMainModel model;
    private MainFrame mainFrame;
    private Shutdown connector;

    /**
     * Create a controller that only has a shutdown connector for now.
     * Model / Frame can be set later via setters.
     */
    public SwingMainController(Shutdown shutdownConnector) {
        this.connector = shutdownConnector;
    }

    /**
     * Create a fully wired controller.
     */
    public SwingMainController(SwingMainModel model, MainFrame mf) {
        this(model, mf, null);
    }

    /**
     * Create a fully wired controller with shutdown connector.
     */
    public SwingMainController(SwingMainModel model, MainFrame mf, Shutdown shutdownConnector) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.mainFrame = Objects.requireNonNull(mf, "mainFrame must not be null");
        this.connector = shutdownConnector;
    }

    public void setSwingMainModel(SwingMainModel model) {
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    public void setMainFrame(MainFrame mf) {
        this.mainFrame = Objects.requireNonNull(mf, "mainFrame must not be null");
    }

    public void start() {
        if (mainFrame == null || model == null) {
            System.out.println("Controller not fully initialized: model or mainFrame is null");
            return;
        }
        mainFrame.showUI();
    }

    public void getAndShowTicketById(int id) {
        if (mainFrame == null || model == null) return;
        try {
            Ticket t = model.getTicket(id);
            if (t == null) {
                mainFrame.clearTicketDetails();
                mainFrame.showErrorDialog("Ticket with Id " + id + " does not exist!", null);
            } else {
                mainFrame.showTicketDetails(t);
            }
        } catch (TicketException e) {
            mainFrame.clearTicketDetails();
            mainFrame.showErrorDialog("Error retrieving ticket with Id " + id + ".", e);
        }
    }

    public void acceptTicket(int id) {
        if (mainFrame == null || model == null) return;
        try {
            model.acceptTicket(id);
            mainFrame.showTicketDetails(model.getTicket(id));
        } catch (TicketException exec) {
            mainFrame.showErrorDialog("Invalid status change.", exec);
        }
    }

    public void closeTicket(int id) {
        if (mainFrame == null || model == null) return;
        try {
            model.closeTicket(id);
            mainFrame.showTicketDetails(model.getTicket(id));
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Invalid status change.", e);
        }
    }

    public void rejectTicket(int id) {
        if (mainFrame == null || model == null) return;
        try {
            model.rejectTicket(id);
            mainFrame.showTicketDetails(model.getTicket(id));
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Invalid status change.", e);
        }
    }

    public void createNewTicket(String reporter, String topic, String description, Type type, Priority priority) {
        if (mainFrame == null || model == null) return;
        try {
            Ticket ticket = model.createNewTicket(reporter, topic, description, type, priority);
            if (ticket != null) {
                mainFrame.showTicketDetails(ticket);
            } else {
                mainFrame.showErrorDialog("Failed to create ticket.", null);
            }
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Failed to create ticket.", e);
        }
    }
    
    public void searchTicket(String name, Type type) {
        if (mainFrame == null || model == null) return;
        try {
            List<Ticket> tickets = model.searchTicket(name, type);
            mainFrame.updateTable(tickets);
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Could not perform ticket search", e);
        }
    }

    public void refreshTicketList() {
        if (mainFrame == null || model == null) return;
        try {
            mainFrame.updateTable(model.getAllTickets());
        } catch (TicketException e) {
            mainFrame.showErrorDialog("Error refreshing list of tickets", e);
        }
    }

    public void triggerApplicationShutdown() {
        // attempt to trigger shutdown if connector is provided; always dispose UI if present
        if (connector != null) {
            try {
                connector.triggerShutdown();
            } catch (Exception e) {
                System.out.println("Error triggering shutdown: " + e.getMessage());
            }
        } else {
            System.out.println("No shutdown connector configured; performing UI dispose only.");
        }
        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }
}
