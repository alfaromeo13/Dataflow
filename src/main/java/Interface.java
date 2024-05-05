import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.awt.*;
import java.text.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class Interface {
    private int num;
    private static int i;
    private long sum, sum2;
    private JTable table;
    private List<File> files;
    private JButton fileChooser;
    private DefaultTableModel model;
    private JTextField usernameField;
    private JButton sendAll;
    private JScrollPane sp;
    private JLabel logo;
    private JButton exit;
    private JLabel usernameText;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    private JPanel panel6;
    private JLabel leftTime;
    private JPanel rootPanel;
    private JPanel panel7;
    private JPanel panel9;
    private final Client client;
    private final JFrame frame;
    private boolean isActive;
    private JLabel isActive1;
    private JLabel isActive2;
    private JPanel panel8;
    private JLabel speed;
    private JLabel slika;
    Instant startTime;
    private int numOfSpare = 9;
    private JDialog dialog;
    private JOptionPane optionPane;
    private final ButtonRenderer buttonRenderer = new ButtonRenderer();

    public Interface(Client client, JFrame frame) {
        this.client = client;
        this.frame = frame;
        initAll();
    }

    private void send() {
        DataOutputStream dos = client.getDos();
        try {
            dos.writeUTF("####");//check if the receiver is online
            dos.flush();
            //write the name of receiver
            dos.writeUTF(usernameField.getText());
            dos.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initAll() {
        rootPanel.setOpaque(true);
        panel1.setOpaque(false);
        panel2.setOpaque(false);
        panel3.setOpaque(false);
        panel4.setOpaque(false);
        panel5.setOpaque(false);
        panel6.setOpaque(false);
        panel7.setOpaque(false);
        panel8.setOpaque(false);
        panel9.setOpaque(false);
        speed.setVisible(false);
        slika.setVisible(false);
        files = new ArrayList<>();
        rootPanel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        usernameText.setText(client.getUsername());
        usernameText.setBorder(new RoundedCornerBorder());
        BufferedImage img = null;
        try {
            img = ImageIO.read(Objects.requireNonNull(MainClient.class.getResource("/Images/upload.png")));
            new ImageIcon();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        Image dimg = Objects.requireNonNull(img).getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        fileChooser.setIcon(new ImageIcon(dimg));

        img = null;
        try {
            img = ImageIO.read(Objects.requireNonNull(MainClient.class.getResource("/Images/project.png")));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        dimg = Objects.requireNonNull(img).getScaledInstance(50, 36, Image.SCALE_SMOOTH);

        usernameField.setBackground(Color.decode("#161a1d"));
        usernameField.setForeground(Color.white);
        usernameField.setCaretColor(Color.orange);
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                send();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                send();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                send();
            }
        });

        logo.setIcon(new ImageIcon(dimg));
        fileChooser.setContentAreaFilled(false);
        fileChooser.setOpaque(false);
        fileChooser.setFocusPainted(false);
        fileChooser.setForeground(Color.black);
        fileChooser.setBorderPainted(false);
        fileChooser.addActionListener(e -> {
            new MP3("click").start();
            FileDialog fd = new FileDialog(frame, "Choose files to send", FileDialog.LOAD);
            fd.setMultipleMode(true);
            fd.setVisible(true);
            List<File> newSelectedFiles = Arrays.asList(fd.getFiles());
            populateFiles(newSelectedFiles);
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        isActive1.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/greenL.png"))));
        isActive2.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/cccc(1).png"))));
        exit.setContentAreaFilled(false);
        exit.setOpaque(false);
        exit.setBorderPainted(false);
        exit.setFocusPainted(false);
        exit.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/x1.png"))));

        exit.addActionListener(e -> closeWindow());
        try {
            img = ImageIO.read((Objects.requireNonNull(MainClient.class.getResource("/Images/send.png"))));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        dimg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        sendAll.setIcon(new ImageIcon(dimg));
        sendAll.setContentAreaFilled(false);
        sendAll.setOpaque(false);
        sendAll.setFocusPainted(false);
        sendAll.setBorderPainted(false);
        sendAll.addActionListener(x -> {
            try {
                new MP3("click").start();
                int n = files.size();
                if (isActive && n != 0 && !usernameField.getText().equals(client.getUsername())) {
                    DataOutputStream dos = client.getDos();
                    dos.writeUTF("#");//'#' to indicate sending of files
                    dos.flush();
                    //write the name of receiver
                    dos.writeUTF(usernameField.getText());
                    dos.flush();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });

        String[] columns = {"File Name", "File Size", "Status", ""};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.addMouseListener(new MouseAdapter() { //actions with aborting file
            public void mouseClicked(MouseEvent e) {
                int colIndex = table.getSelectedColumn();
                int rowIndex = table.getSelectedRow();
                //if we click x button for a specific file
                if (colIndex == 3 && rowIndex < files.size()) {
                    //if we are in the process of sending `sendAll` button is disabled
                    if (!sendAll.isEnabled()) {
                        int value = (int) model.getValueAt(rowIndex, 2);
                        //clicking the button will work if the value is neither 100 nor -1
                        if (value != 100 && value != -1) {
                            new MP3("remove").start();
                            JLabel label = new JLabel();
                            label.setText("Abort the transfer of this file??");
                            label.setForeground(Color.white);
                            label.setFont(new Font("", Font.BOLD, 16));
                            int dialogResult = JOptionPane.showConfirmDialog(frame, label, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (dialogResult == JOptionPane.YES_OPTION) {
                                if (!files.isEmpty() && (int) model.getValueAt(rowIndex, 2) != 100) {//if ==100 it was sent while we decided
                                    model.setValueAt("This file was aborted", rowIndex, 0);
                                    model.setValueAt("-1", rowIndex, 1);
                                    model.setValueAt(-1, rowIndex, 2);
                                }
                            }
                        }
                    } else { //if we aren't sending any files
                        new MP3("remove").start();
                        sum -= files.get(rowIndex).length();
                        files.remove(rowIndex);
                        buttonRenderer.setN(files.size());
                        model.removeRow(rowIndex);
                        if (files.size() < 10) {
                            model.addRow(new Object[]{"", "", 0});
                            numOfSpare++;
                        }
                        i--;
                    }
                }
            }
        });

        rootPanel.setDropTarget(new DropTarget() {
            @SuppressWarnings("unchecked")
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    populateFiles(droppedFiles);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        for (int i = 0; i <= numOfSpare; i++)
            model.addRow(new Object[]{"", "", 0});

        table.setModel(model);
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getColumnModel().getColumn(3).setCellRenderer(buttonRenderer);//custom cell renderer for close button
        table.getColumn("Status").setCellRenderer(new ProgressRenderer());//custom cell renderer for column status
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(0).setMinWidth(470);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(30);
        table.getColumnModel().getColumn(3).setMinWidth(40);
        table.getColumnModel().getColumn(3).setMaxWidth(30);
        table.setBorder(BorderFactory.createLineBorder(Color.white));
        table.setGridColor(Color.white);
        JTableHeader header = table.getTableHeader();
        header.setBorder(BorderFactory.createLineBorder(Color.white));
        header.setBackground(Color.decode("#bfc1c3"));
        header.setForeground(Color.decode("#161a1d"));
        header.setFont(new Font("", Font.PLAIN, 16));
        for (int i = 0; i < 2; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected,
                            hasFocus, row, col);
                    label.setBackground(Color.decode("#161a1d"));
                    label.setFont(new Font("", Font.BOLD, 16));
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setForeground(Color.white);
                    return label;
                }
            });
        }
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBackground(new Color(0, 0, 0, 0));
        sp.getVerticalScrollBar().setUI(new MyScrollBarUI());
        sp.setBorder(BorderFactory.createEmptyBorder());
        receive();
    }

    private void populateFiles(List<File> newSelectedFiles) {
        files.addAll(newSelectedFiles);//append new files to already existing ones
        buttonRenderer.setN(files.size());//for every added file add X (remove field)
        for (File file : newSelectedFiles) {
            sum += file.length();
            model.insertRow(i++, new Object[]{file.getName(), humanReadableByteCountSi(file.length()), 0});
            if (numOfSpare >= 0) {
                model.removeRow(model.getRowCount() - 1);
                numOfSpare--;
            }
        }
    }

    String humanReadableByteCountSi(long bytes) {
        if (-1000 < bytes && bytes < 1000)
            return bytes + " B";
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }


    public String transformBytesPerSecond(long bytesPerSecond) {
        if (bytesPerSecond < 1000) return bytesPerSecond + " Bps";
        else if (bytesPerSecond < 1000 * 1000) return (bytesPerSecond / 1000) + " Kbps";
        else if (bytesPerSecond < 1000 * 1000 * 1000) return (bytesPerSecond / (1000 * 1000)) + " Mbps";
        else return (bytesPerSecond / (1000 * 1000 * 1000)) + " Gbps";
    }

    String humanReadableTime(long milliseconds, boolean flag) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String time = "";
        if (days > 0)
            time += days + "d ";
        if (hours % 24 > 0)
            time += hours % 24 + "h ";
        if (minutes % 60 > 0)
            time += minutes % 60 + "m ";
        if (seconds % 60 > 0)
            time += seconds % 60 + "s ";
        if (flag && milliseconds % 1000 > 0)
            time += milliseconds % 1000 + "ms ";
        return time.trim();
    }


  /* TODO: in v2. implement method for extracting ip address for local faster transaction.
   private String getIpV4() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ifs = interfaces.nextElement();
            if (ifs.isLoopback() || !ifs.isUp() || ifs.isVirtual() || ifs.isPointToPoint())
                continue;
            Enumeration<InetAddress> addresses = ifs.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (Inet4Address.class == addr.getClass())
                    return addr.getHostAddress();
            }
        }
        return "";
    }
   */

    private void closeWindow() {
        try {
            new MP3("click").start();
            JLabel label = new JLabel();
            label.setText("Are you sure you want to exit the app?");
            label.setForeground(Color.white);
            label.setFont(new Font("", Font.BOLD, 16));
            int dialogResult = JOptionPane.showConfirmDialog(frame, label, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (dialogResult == JOptionPane.YES_OPTION) {
                DataOutputStream dos = client.getDos();
                dos.writeUTF("###");//'###' to indicate closing the app
                dos.flush();
                System.exit(0);
            }
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
    }

    private void receive() {
        Thread a = new Thread(() -> {
            while (true) { //while our program runs, we listen for incoming files
                DataInputStream dis = client.getDis();
                DataOutputStream dos = client.getDos();
                try {
                    //we listen for incoming commands
                    String sender = dis.readUTF(); //sender name or just a basic command
                    if (sender.equals("#")) {
                        //~ waiting for confirmation of transfer from receiver
                        boolean response = dis.readBoolean();
                        if (dialog.isShowing()) {
                            dos.writeBoolean(response);
                            dos.flush();
                            dialog.dispose();
                            if (response) {
                                startTime = Instant.now();
                                this.speed.setVisible(true);
                                slika.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/speed2.png"))));
                                slika.setVisible(true);
                                new FileSender(this, 0).execute();
                            } else {
                                new MP3("error").start();
                                JLabel label = new JLabel("Transfer aborted by receiver");
                                label.setForeground(Color.white);
                                label.setFont(new Font("", Font.BOLD, 16));
                                JOptionPane.showMessageDialog(frame, label, "Aborted", JOptionPane.WARNING_MESSAGE);
                                fileChooser.setEnabled(true);
                                sendAll.setEnabled(true);
                                usernameField.setEnabled(true);
                            }
                        }
                        continue;
                    } else if (sender.equals("##")) {
                        //send the number of files to the server
                        dos.writeInt(files.size());
                        dos.flush();
                        //send a sum of file lengths
                        dos.writeLong(sum);
                        dos.flush();
                        fileChooser.setEnabled(false);
                        sendAll.setEnabled(false);
                        usernameField.setEnabled(false);
                        new Thread(() -> {
                            JLabel label = new JLabel();
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            label.setHorizontalTextPosition(SwingConstants.LEFT);
                            ImageIcon icon = new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/load3.gif")));
                            label.setIcon(icon);
                            label.setIconTextGap(5);
                            label.setText("Waiting for client confirmation");
                            label.setForeground(Color.white);
                            label.setFont(new Font("", Font.BOLD, 16));
                            optionPane = new JOptionPane(label, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{});
                            dialog = new JDialog(frame);
                            dialog.setTitle("Waiting...");
                            dialog.setModal(true);
                            dialog.setContentPane(optionPane);
                            dialog.pack();
                            dialog.setLocationRelativeTo(frame);
                            dialog.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    try {
                                        fileChooser.setEnabled(true);
                                        sendAll.setEnabled(true);
                                        usernameField.setEnabled(true);
                                        //we canceled transaction
                                        dos.writeBoolean(false);
                                        dos.flush();
                                        //we notify receiver
                                        dos.writeUTF("#####");
                                        dos.flush();
                                        dos.writeUTF(usernameField.getText());
                                        dos.flush();
                                        dialog.dispose();
                                    } catch (IOException ioException) {
                                        System.out.println(ioException.getMessage());
                                    }
                                }
                            });
                            dialog.setVisible(true);
                        }).start();
                        continue;
                    } else if (sender.equals("####")) {
                        //~ check if user is online
                        if (dis.readBoolean()) {//if a user is online
                            isActive = true;
                            isActive2.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/greenL.png"))));
                        } else {
                            isActive = false;
                            isActive2.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/cccc(1).png"))));
                        }
                        continue;
                    } else if (sender.equals("#####")) {
                        dialog.dispose();
                        continue;
                    } else if (sender.equals("######")) {
                        this.speed.setVisible(true);
                        startTime = Instant.now();
                        slika.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/speed2.png"))));
                        slika.setVisible(true);
                        int n = 0;
                        byte[] buf = new byte[8192];
                        //we put receiving chunks in buffer and we save it on filesystem
                        for (int i = 0; i < num; i++) { //loop for each file
                            String filename = dis.readUTF();
                            long fileSize = dis.readLong();
                            boolean aborted = dis.readBoolean();
                            model.insertRow(Interface.i, new Object[]{filename, aborted ? "-1" : humanReadableByteCountSi(fileSize), aborted ? -1 : 0});
                            if (aborted) {//sending of this file was aborted, so we skip to the next
                                sum2 -= fileSize;
                                Interface.i++;
                                continue;
                            }
                            if (numOfSpare >= 0) {
                                model.removeRow(model.getRowCount() - 1);
                                numOfSpare--;
                            }
                            long current = 0;
                            long estimatedTime;
                            long startTime = System.currentTimeMillis();
                            FileOutputStream fos = new FileOutputStream("./Received files/" + filename);
                            //if we receive -1, the whole file was sent (meaning there is nothing more to be read)
                            //or file was aborted while sending.In both cases the while loop will exit
                            while (current != fileSize && (n = dis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                                sum2 -= n;
                                current += n;
                                fos.write(buf, 0, n);
                                int percentLoaded = (int) (100 * current / fileSize);
                                model.setValueAt(percentLoaded, Interface.i, 2);
                                // calculate average transfer rate, estimated time and download speed
                                long elapsedTime = System.currentTimeMillis() - startTime + 1;
                                long averageTransferRate = (current * 1000) / elapsedTime;
                                estimatedTime = (sum2 / averageTransferRate) * 1000 + 500;
                                long downloadSpeed = current / elapsedTime * 1000;
                                setLeftTime("Time remaining: " + humanReadableTime(estimatedTime, false));
                                setDownloadSpeed("Download speed: " + transformBytesPerSecond(downloadSpeed));
                                boolean interrupted = dis.readBoolean();
                                if (interrupted) {//if sender aborts file which we are currently storing
                                    sum2 -= (fileSize - current);
                                    model.setValueAt("This file was aborted", Interface.i, 0);
                                    model.setValueAt("-1", Interface.i, 1);
                                    model.setValueAt(-1, Interface.i, 2);
                                    //we delete that file
                                    boolean deleted = new File("./Received files/" + filename).delete();
                                    if (deleted) System.out.println("File deleted locally.");
                                    break;
                                }
                            }
                            if (n == -1) { //EOS detected.
                                //send request to exit server
                                dos.writeUTF("###");
                                dos.flush();
                            }
                            Interface.i++;
                            fos.close();
                        }
                        speed.setVisible(false);
                        slika.setVisible(false);
                        Instant endTime = Instant.now();
                        JLabel label2 = new JLabel();
                        label2.setHorizontalAlignment(SwingConstants.CENTER);
                        label2.setHorizontalTextPosition(SwingConstants.LEFT);
                        label2.setIcon(new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/done.png"))));
                        label2.setIconTextGap(10);
                        setLeftTime("Time remaining: ");
                        Duration timeElapsed = Duration.between(startTime, endTime);
                        label2.setText("<html><font color='white'>Done! Time spent: " + humanReadableTime(timeElapsed.toMillis(), true) +
                                " </br> Click <font color='#4dff00'>'OK'</font> to view files</font></html>");
                        label2.setFont(new Font("", Font.BOLD, 16));
                        dos.writeUTF("#####"); //we notify server to reset our socket timeout.
                        dos.flush();
                        JOptionPane.showMessageDialog(frame, label2, "Done", JOptionPane.PLAIN_MESSAGE);
                        i = 0;
                        while (num-- > 0) model.removeRow(0);
                        setNumOfSpare(9);
                        for (int i = model.getRowCount(); i <= 9; i++)
                            model.addRow(new Object[]{"", "", 0});
                        File dirToOpen = new File("./Received files");
                        Desktop.getDesktop().open(dirToOpen);
                        continue;
                    }//if we received a name
                    while (!files.isEmpty()) {
                        files.remove(0);
                        model.removeRow(0);
                    }
                    setNumOfSpare(9);
                    for (int i = model.getRowCount(); i <= 9; i++)
                        model.addRow(new Object[]{"", "", 0});
                    buttonRenderer.setN(0);
                    //number of files to receive
                    num = dis.readInt();
                    //sum of files lengths
                    sum2 = dis.readLong();
                    //confirm or abort transaction
                    new MP3("good").start();
                    JLabel label = new JLabel();
                    label.setText("<html>Accept " + num + " file/s (total " + humanReadableByteCountSi(sum2) + ") from user: <font color='#4dff00'><strong><i>@" + sender + "  </i></strong></font>?</html>");
                    label.setForeground(Color.white);
                    label.setFont(new Font("", Font.BOLD, 16));
                    new Thread(() -> {
                        try {
                            optionPane = new JOptionPane(label, JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                            dialog = optionPane.createDialog(frame, "Transfer");
                            dialog.setModal(true);
                            dialog.setContentPane(optionPane);
                            dialog.pack();
                            dialog.setLocationRelativeTo(frame);
                            dialog.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    try {
                                        dos.writeUTF("##");
                                        dos.flush();
                                        dos.writeUTF(sender);
                                        dos.flush();
                                        dos.writeBoolean(false);
                                        dos.flush();
                                    } catch (IOException ioException) {
                                        System.err.println(ioException.getMessage());
                                    }
                                }
                            });
                            dialog.setVisible(true);
                            Object value = optionPane.getValue();
                            int res = -10;
                            if (value instanceof Integer)
                                res = (int) value;
                            //res=0/yes  res=1/no  res=-10/disposed()
                            if (res != -10) {
                                dos.writeUTF("##");
                                dos.flush();
                                dos.writeUTF(sender);
                                dos.flush();
                                dos.writeBoolean(res == JOptionPane.YES_OPTION);
                                dos.flush();
                            }
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }).start();
                } catch (IOException e) {
                    this.isActive = false;
                    new MP3("error").start();
                    JLabel label = new JLabel();
                    label.setForeground(Color.white);
                    label.setFont(new Font("", Font.BOLD, 16));
                    label.setText("Lost connection to the server");
                    try {
                        JOptionPane.showMessageDialog(frame, label, "Disconnected", JOptionPane.WARNING_MESSAGE);
                        this.client.getClientSocket().close();
                        Container contentPane = frame.getContentPane();
                        frame.remove(contentPane);
                        contentPane.removeAll();
                        Login login = new Login(frame);
                        frame.setContentPane(login.getRootPanel());
                        frame.setSize(430, 630);
                        frame.setLocationRelativeTo(null);
                        login.getUsernameField().requestFocusInWindow();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        });
        a.start();
    }

    public long getSum() {
        return sum;
    }

    public void setI(int i) {
        Interface.i = i;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JLabel getSlika() {
        return slika;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JButton getFileChooser() {
        return fileChooser;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public JLabel getSpeed() {
        return speed;
    }

    public JButton getSendAll() {
        return sendAll;
    }

    public List<File> getFiles() {
        return files;
    }

    public Client getClient() {
        return client;
    }

    public ButtonRenderer getButtonRenderer() {
        return buttonRenderer;
    }

    public void setNumOfSpare(int numOfSpare) {
        this.numOfSpare = numOfSpare;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public void setLeftTime(String leftTime) {
        this.leftTime.setText(leftTime);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setSpeed(String text) {
        this.speed.setText(text);
    }

    public void setDownloadSpeed(String text) {
        this.speed.setText(text);
    }

    private void createUIComponents() {
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
                setOpaque(false);
                setBorder(new RoundedCornerBorder());
            }
        };

        rootPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.setColor(Color.decode("#161a1d"));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 50, 50);
                super.paintComponent(g);
            }
        };
    }
}