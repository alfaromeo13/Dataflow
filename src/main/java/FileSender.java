import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

// We use SwingWorker to handle each file and progress bar in its own thread
public class FileSender extends SwingWorker<Integer, Integer> {
    private final int key;
    private long current;
    private final Client client;
    private final List<File> files;
    private final DefaultTableModel model;
    private final ButtonRenderer renderer;
    private final JButton fileChooser;
    private final JButton sendAll;
    private final Interface myInterface;
    private final JFrame frame;

    public FileSender(Interface myInterface, int key) {
        this.key = key;
        this.myInterface = myInterface;
        this.frame = myInterface.getFrame();
        this.files = myInterface.getFiles();
        this.model = myInterface.getModel();
        this.client = myInterface.getClient();
        this.fileChooser = myInterface.getFileChooser();
        this.sendAll = myInterface.getSendAll();
        this.renderer = myInterface.getButtonRenderer();
    }

    @Override
    protected Integer doInBackground() throws IOException {
        File file = files.get(key);
        DataOutputStream dos = client.getDos();
        int n;
        long fileSize;
        byte[] buf = new byte[8192];
        long sum = myInterface.getSum();
        //send file name and length
        int value = (int) model.getValueAt(key, 2);
        dos.writeUTF(value == -1 ? "This file was aborted" : file.getName());
        fileSize = file.length();
        dos.writeLong(fileSize);
        dos.writeBoolean(value == -1);
        if (value == -1) {//if it was aborted before it's sending
            myInterface.setSum(sum - file.length());
            publish(-1);
            //publish is used from inside the doInBackground method to deliver intermediate results
            //for processing on the Event Dispatch Thread inside the process method.It sends data chunks
            //to the process(java.util.List) method.
            return -1;
        }
        long estimatedTime;
        //create new file input stream for file
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        long startTime = System.currentTimeMillis();
        //read parts of a file and write them to dos
        while ((n = bis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
            current += n;
            int percentLoaded = (int) (100 * current / fileSize);
            dos.write(buf, 0, n);
            publish(percentLoaded);
            sum -= n;
            if ((int) model.getValueAt(key, 2) == -1) {//if it is aborted while sending
                dos.writeBoolean(true);//if it was aborted we will send true
                myInterface.setSum(sum - (fileSize - current));
                publish(-1);
                return -1;
            }
            // calculate average transfer rate and upload speed
            long elapsedTime = System.currentTimeMillis() - startTime + 1;
            long averageTransferRate = (current * 1000) / elapsedTime;
            long uploadSpeed = current / elapsedTime * 1000; // Bytes per second
            estimatedTime = (sum / averageTransferRate) * 1000 + 500;
            myInterface.setLeftTime("Time remaining: " + myInterface.humanReadableTime(estimatedTime, false));
            myInterface.setSpeed("Upload speed: " + myInterface.transformBytesPerSecond(uploadSpeed));
            dos.writeBoolean(false);
        }
        dos.flush();
        myInterface.setSum(sum);
        bis.close();
        return 1;
    }

    @Override
    protected void process(List c) {
        //This method is used to modify the value of progress bar
        //receives data chunks from the publish method asynchronously on the Event Dispatch Thread.
        //Because this method is invoked asynchronously, publish() may have been called multiple times.
        model.setValueAt(c.get(c.size() - 1), key, 2);
    }

    @Override
    protected void done() {
        // This method is called when the thread finished its execution.
        // Also, any value returned by the doInBackground() function can be received
        // Further, updates can be made to GUI inside this function.
        // Thus, the function executed on the Event Dispatch Thread after the doInBackground method is finished.
        if (key < files.size() - 1)
            new FileSender(myInterface, key + 1).execute();
        else {
            if (myInterface.isActive()) {
                Instant endTime = Instant.now();
                new MP3("good").start();
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setHorizontalTextPosition(SwingConstants.LEFT);
                label.setIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/Images/done.png"))));
                label.setIconTextGap(10);
                Duration timeElapsed = Duration.between(myInterface.startTime, endTime);
                label.setText("Everything was sent in " + myInterface.humanReadableTime(timeElapsed.toMillis(), true));
                label.setForeground(Color.white);
                label.setFont(new Font("", Font.BOLD, 16));
                myInterface.getSpeed().setVisible(false);
                myInterface.getSlika().setVisible(false);
                JOptionPane.showMessageDialog(frame, label, "Done", JOptionPane.PLAIN_MESSAGE);
            }
            myInterface.setI(0);
            myInterface.setSum(0);
            myInterface.getUsernameField().setEnabled(true);
            finish();
        }
    }

    private void finish() {
        renderer.setN(0);
        while (!files.isEmpty()) {
            files.remove(0);
            model.removeRow(0);
        }
        myInterface.setNumOfSpare(9);
        for (int i = model.getRowCount(); i <= 9; i++)
            model.addRow(new Object[]{"", "", 0});
        fileChooser.setEnabled(true);
        sendAll.setEnabled(true);
        this.myInterface.setLeftTime("Time remaining: ");
    }
}