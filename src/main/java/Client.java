import java.io.*;
import java.net.SocketException;
import javax.net.*;
import javax.net.ssl.*;

public class Client {
    private final String username;
    private DataInputStream dis;
    private DataOutputStream dos;
    private  SSLSocket clientSocket;

    public String getUsername() {
        return username;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public Client(String username) {
        this.username = username;
    }

    public SSLSocket getClientSocket() {
        return clientSocket;
    }

    public void startClient(String host, int port) throws IOException {
        SocketFactory factory = SSLSocketFactory.getDefault();
        clientSocket = (SSLSocket) factory.createSocket(host, port);
        clientSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
        clientSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
        clientSocket.setTcpNoDelay(true);
        dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
    }
}