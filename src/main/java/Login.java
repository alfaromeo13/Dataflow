import java.io.*;
import java.awt.*;
import java.util.Objects;
import javax.swing.*;

public class Login {
    private JPanel rootPanel;
    private JLabel logo;
    private JButton exit;
    private JTextField usernameField;
    private JPanel panel1;
    private JLabel enterUSernameLabel;

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public Login(JFrame frame) {
        rootPanel.setBackground(Color.white);
        logo.setIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/Images/logoFinal.png"))));
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        exit.setFocusPainted(false);
        exit.setIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/Images/x1.png"))));
        JLabel label = new JLabel();
        label.setForeground(Color.black);
        label.setFont(new Font("", Font.BOLD, 16));
        exit.addActionListener(e -> {
            try {
                new MP3("click").start();
                Thread.sleep(50);
                System.exit(0);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        usernameField.setColumns(20);
        usernameField.setFont(new Font("", Font.PLAIN, 18));
        usernameField.setForeground(Color.white);
        usernameField.setBackground(Color.decode("#161a1d"));
        usernameField.setCaretColor(Color.orange);//blinking color
        usernameField.requestFocusInWindow();
        usernameField.addActionListener(x -> {
            String text = usernameField.getText();
            if (!text.trim().isEmpty()) {
                if (text.length() > 3 && text.length() < 13) {
                    try {
                        Client client = new Client(usernameField.getText());
                        client.startClient("165.232.73.39", 5555);
                        //you can try with ngrok -> ("4.tcp.eu.ngrok.io", 14433); or even localhost
                        DataInputStream dis = client.getDis();
                        DataOutputStream dos = client.getDos();
                        //send your username to register on GreenServer
                        dos.writeUTF(client.getUsername());
                        dos.flush();
                        if (!dis.readBoolean() && !client.getUsername().equals("####")
                                && !client.getUsername().equals("#####") && !client.getUsername().equals("######")) {
                            //if username isn't already taken
                            frame.setContentPane(new Interface(client, frame).getRootPanel());
                            frame.setSize(760, 675);
                            frame.setLocationRelativeTo(null);
                        } else {
                            new MP3("error").start();
                            label.setForeground(Color.white);
                            label.setText(" Username already taken, try something else ");
                            JOptionPane.showMessageDialog(frame, label, "Already in use", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                        //java.net.ConnectException: Connection refused (Connection refused)
                        //This exception means that there is no service listening on the
                        //IP/port you are trying to connect to:
                        // -wrong IP/Host or port
                        // - You have not started your server
                        // - listen backlog queue is full.
                        //
                        //or simply we can't connect because we aren't connected to the internet
                        new MP3("error").start();
                        label.setForeground(Color.white);
                        label.setText("Can't connect to the server ");
                        JOptionPane.showMessageDialog(frame, label, "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    new MP3("error").start();
                    label.setForeground(Color.white);
                    label.setText(" Username must be 4 to 12 characters long ");
                    JOptionPane.showMessageDialog(frame, label, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: We place custom component creation code here
        rootPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.setColor(Color.decode("#161a1d"));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 50, 50);
                g2.dispose();
            }
        };

        panel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#161a1d"));
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight(), 100, 100);
                g2.dispose();
            }
        };

        usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedCornerBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(getBackground());
                    g2.fill(((RoundedCornerBorder) getBorder()).getBorderShape(
                            0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setBorder(new RoundedCornerBorder());
            }
        };
    }
}