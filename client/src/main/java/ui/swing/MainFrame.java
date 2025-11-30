package ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import entities.Priority;
import entities.Status;
import entities.Ticket;
import entities.TicketException;

public class MainFrame extends JFrame implements Observer {

    private static final long serialVersionUID = -6236283098582578310L;
    private final SwingMainController controller;
    private final SwingMainModel mainModel;

    private int currentId;

    private TicketTableModel tableModel;
    private JPanel ticketPanel;
    private JTextArea descArea;
    private JComboBox<entities.Type> typeBox;
    private JComboBox<Priority> prioBox;
    private JTextField repField;
    private JTextField topicField;
    private JLabel numberLabel;
    private JButton acceptButton;
    private JButton rejectButton;
    private JButton closeButton;
    private JButton saveButton;
    private JLabel statusLabel;

    public MainFrame(SwingMainController controller, SwingMainModel mainModel) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.mainModel = Objects.requireNonNull(mainModel, "mainModel must not be null");
        this.mainModel.addObserver(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void showUI() {
        try {
            init();
            this.setVisible(true);
        } catch (TicketException e) {
            showErrorDialog(
                    "Unable to create UI as there were problems connecting to the backend! Shutting down system...", e);
            controller.triggerApplicationShutdown();
        }
    }

    private void init() throws TicketException {
        this.setTitle("Ticket Management System 5000");
        this.setResizable(false);
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        getContentPane().add(panel, BorderLayout.CENTER);

        panel.setLayout(new BorderLayout());

        JPanel newRefreshPanel = new JPanel();
        JButton newButton = new JButton("Create new Ticket");
        newButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                prepareCreationOfTicket();
            }
        });
        newRefreshPanel.add(newButton);

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.refreshTicketList();
            }

        });

        newRefreshPanel.add(refreshButton);

        JPanel searchAndTablePanel = new JPanel();
        searchAndTablePanel.setLayout(new BorderLayout());
        searchAndTablePanel.add(createSearchPanel(), BorderLayout.NORTH);
        searchAndTablePanel.add(createTicketTablePanel(), BorderLayout.CENTER);

        panel.add(searchAndTablePanel, BorderLayout.NORTH);
        panel.add(newRefreshPanel, BorderLayout.CENTER);
        createTicketPanel();
        panel.add(ticketPanel, BorderLayout.SOUTH);

        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                // do nothing
            }

            @Override
            public void windowIconified(WindowEvent e) {
                // do nothing
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // do nothing
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // do nothing
            }

            @Override
            public void windowClosing(WindowEvent e) {
                controller.triggerApplicationShutdown();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // do nothing
            }

            @Override
            public void windowActivated(WindowEvent e) {
                // do nothing
            }
        });
        pack();
    }

    private void showSaveButton() {
        if (saveButton != null) saveButton.setVisible(true);
        if (rejectButton != null) rejectButton.setVisible(false);
        if (acceptButton != null) acceptButton.setVisible(false);
        if (closeButton != null) closeButton.setVisible(false);
    }

    private void createTicketPanel() {
        ticketPanel = new JPanel();

        ticketPanel.setLayout(new GridBagLayout());

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        numberLabel = new JLabel("#X");
        ticketPanel.add(numberLabel, c1);

        topicField = new JTextField();
        topicField.setColumns(50);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 1;
        c2.gridy = 0;
        c2.gridwidth = 5;
        ticketPanel.add(topicField, c2);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 1;
        ticketPanel.add(new JLabel("Reporter:"), c3);

        repField = new JTextField();
        repField.setColumns(15);
        c3 = new GridBagConstraints();
        c3.gridx = 1;
        c3.gridy = 1;
        ticketPanel.add(repField, c3);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        ticketPanel.add(new JLabel("Priority:"), c);
        prioBox = new JComboBox<>(Priority.values());
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 1;
        ticketPanel.add(prioBox, c);

        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 1;
        ticketPanel.add(new JLabel("Type:"), c);
        typeBox = new JComboBox<>(entities.Type.values());
        typeBox.setEnabled(false);
        c = new GridBagConstraints();
        c.gridx = 5;
        c.gridy = 1;
        ticketPanel.add(typeBox, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        ticketPanel.add(new JLabel("Description:"), c);

        descArea = new JTextArea();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 5;
        descArea.setColumns(50);
        descArea.setRows(5);
        ticketPanel.add(new JScrollPane(descArea), c);

        statusLabel = new JLabel("Update Status:");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        ticketPanel.add(statusLabel, c);

        acceptButton = new JButton("Accept");
        acceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.acceptTicket(currentId);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        ticketPanel.add(acceptButton, c);

        rejectButton = new JButton("Reject");
        rejectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.rejectTicket(currentId);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 3;
        ticketPanel.add(rejectButton, c);

        closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.closeTicket(currentId);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 3;
        ticketPanel.add(closeButton, c);

        saveButton = new JButton("Save New Ticket");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.createNewTicket(repField.getText(), topicField.getText(), descArea.getText(),
                        (entities.Type) typeBox.getSelectedItem(), (Priority) prioBox.getSelectedItem());
            }
        });
        saveButton.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 5;
        c.gridwidth = 2;
        ticketPanel.add(saveButton, c);

        topicField.setEditable(false);
        repField.setEditable(false);
        prioBox.setEnabled(false);
        typeBox.setEnabled(false);
        descArea.setEditable(false);
        acceptButton.setEnabled(false);
        rejectButton.setEnabled(false);
        closeButton.setVisible(false);
        saveButton.setVisible(false);
    }

    public void showTicketDetails(Ticket ticket) {
        if (ticket == null) {
            clearTicketDetails();
            return;
        }
        currentId = ticket.getId();
        numberLabel.setText("#" + ticket.getId());
        topicField.setText(safe(ticket.getTopic()));
        topicField.setEditable(false);
        repField.setText(safe(ticket.getReporter()));
        repField.setEditable(false);
        prioBox.setSelectedItem(ticket.getPriority());
        prioBox.setEnabled(false);
        typeBox.setSelectedItem(ticket.getType());
        typeBox.setEnabled(false);
        descArea.setText(safe(ticket.getDescription()));
        descArea.setEditable(false);
        showButtonsDependendOnStatus(ticket.getStatus());

    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    public void showErrorDialog(String msg, Exception exec) {
        if (exec == null) {
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // create and configure a text area - fill it with exception text.
        final JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
        textArea.setEditable(false);
        StringWriter writer = new StringWriter();
        exec.printStackTrace(new PrintWriter(writer));
        textArea.setText(writer.toString());

        // stuff it in a scrollpane with a controlled size.
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));

        // pass the scrollpane to the joptionpane.
        JOptionPane.showMessageDialog(this, scrollPane, msg, JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createTicketTablePanel() throws TicketException {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(new JLabel("Tickets currently available:"), BorderLayout.NORTH);
        tableModel = new TicketTableModel();
        try {
            List<Ticket> all = mainModel.getAllTickets();
            tableModel.updateData(all == null ? new ArrayList<>() : all);
        } catch (TicketException e) {
            // propagate so caller can react
            throw e;
        }
        final JTable table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(580, 250));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);

        ListSelectionModel listSelect = table.getSelectionModel();

        listSelect.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    Object val = table.getValueAt(table.getSelectedRow(), 1);
                    if (val instanceof Integer) {
                        controller.getAndShowTicketById((Integer) val);
                    } else {
                        try {
                            controller.getAndShowTicketById(Integer.parseInt(String.valueOf(val)));
                        } catch (NumberFormatException nfe) {
                            // ignore invalid id cell
                        }
                    }
                }
            }
        });
        table.setSelectionModel(listSelect);

        JScrollPane scrollPane = new JScrollPane(table);
        pane.add(scrollPane, BorderLayout.CENTER);
        return pane;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField();
        searchField.setColumns(15);
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Type:"));
        ArrayList<entities.Type> types = new ArrayList<>();
        types.add(null);
        types.addAll(Arrays.asList(entities.Type.values()));
        JComboBox<entities.Type> localTypeBox = new JComboBox<>(types.toArray(new entities.Type[types.size()]));
        searchPanel.add(localTypeBox);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entities.Type type = (entities.Type) localTypeBox.getSelectedItem();
                controller.searchTicket(searchField.getText(), type);
            }
        });
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private void showButtonsDependendOnStatus(Status status) {
        // default: hide everything
        if (acceptButton != null) acceptButton.setVisible(false);
        if (rejectButton != null) rejectButton.setVisible(false);
        if (closeButton != null) closeButton.setVisible(false);
        if (saveButton != null) saveButton.setVisible(false);

        if (status == null) return;

        // Backwards-compatible: Status.NEW is an alias for OPEN in the shared enum
        if (status == Status.NEW || status == Status.OPEN) {
            if (rejectButton != null) {
                rejectButton.setVisible(true);
                rejectButton.setEnabled(true);
            }
            if (acceptButton != null) {
                acceptButton.setVisible(true);
                acceptButton.setEnabled(true);
            }
            if (closeButton != null) {
                closeButton.setVisible(false);
            }
        } else if (status == Status.IN_PROGRESS) {
            if (closeButton != null) {
                closeButton.setVisible(true);
                closeButton.setEnabled(true);
            }
        } else if (status == Status.RESOLVED || status == Status.CLOSED) {
            if (closeButton != null) {
                closeButton.setVisible(true);
                closeButton.setEnabled(false);
            }
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        try {
            List<Ticket> all = mainModel.getAllTickets();
            updateTable(all == null ? new ArrayList<>() : all);
        } catch (TicketException e) {
            showErrorDialog("Updating ticket data failed!", e);
        }

    }

    public void updateTable(List<Ticket> allTickets) {
        if (tableModel == null) return;
        tableModel.updateData(allTickets == null ? new ArrayList<>() : allTickets);
    }

    public void clearTicketDetails() {
        numberLabel.setText("");
        descArea.setText("");
        topicField.setText("");
        repField.setText("");
        prioBox.setEnabled(false);
        typeBox.setEnabled(false);
        descArea.setEditable(false);
    }

    private void prepareCreationOfTicket() {
        clearTicketDetails();
        numberLabel.setText("Topic:");
        statusLabel.setVisible(false);
        prioBox.setEnabled(true);
        typeBox.setEnabled(true);
        repField.setEditable(true);
        topicField.setEditable(true);
        descArea.setEditable(true);
        showSaveButton();
    }

    private class TicketTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -2733923133633436528L;

        private String[] columns = { "Status", "#", "Topic", "Reporter", "Prio", "Type" };

        private List<Ticket> ticketsToShow = new ArrayList<>();

        @Override
        public String getColumnName(int i) {
            return columns[i];
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public int getRowCount() {
            return ticketsToShow == null ? 0 : ticketsToShow.size();
        }

        @Override
        public Object getValueAt(int arg0, int arg1) {
            if (ticketsToShow == null || arg0 < 0 || arg0 >= ticketsToShow.size()) return null;
            Ticket ticket = ticketsToShow.get(arg0);
            if (arg1 == 0) {
                return ticket.getStatus();
            } else if (arg1 == 1) {
                return ticket.getId();
            } else if (arg1 == 2) {
                return ticket.getTopic();
            } else if (arg1 == 3) {
                return ticket.getReporter();
            } else if (arg1 == 4) {
                return ticket.getPriority();
            } else if (arg1 == 5) {
                return ticket.getType();
            }
            return null;

        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void updateData(List<Ticket> allTickets) {
            this.ticketsToShow = allTickets == null ? new ArrayList<>() : allTickets;
            this.fireTableDataChanged();
        }

    }

}
