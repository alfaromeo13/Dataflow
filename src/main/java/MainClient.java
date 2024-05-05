import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Objects;

public class MainClient {
    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, IOException, KeyManagementException {
        //change the image scaling for the entire application:
        System.setProperty("sun.java2d.uiScale", "1.0");

        // two lines below are used to set system properties to enable TLS 1.3
        System.setProperty("jdk.tls.client.protocols", "TLSv1.3");
        System.setProperty("jdk.tls.server.protocols", "TLSv1.3");

        // Load keystore
        InputStream keyStoreStream = MainClient.class.getResourceAsStream("/Certificates/clientkeystore.p12");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keyStoreStream, "password".toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "password".toCharArray());

        // Load truststore
        InputStream trustStoreStream = MainClient.class.getResourceAsStream("/Certificates/clienttruststore.jks");
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(trustStoreStream, "password".toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Set SSL context
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        SSLContext.setDefault(sslContext);

        UIManager.put("OptionPane.background", Color.decode("#161a1d"));
        UIManager.put("Panel.background", Color.decode("#161a1d"));
        UIManager.put("Button.background", Color.white);
        UIManager.put("Button.foreground", Color.decode("#161a1d"));

        new MP3("empty").start();//we do this to remove the delay of first sound at the beginning
        JFrame a = new JFrame("");
        Login login = new Login(a);
        a.setContentPane(login.getRootPanel());
        a.setSize(430, 630);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(MainClient.class.getResource("/Images/logoFinal.png")));
        a.setIconImage(icon.getImage());
        a.setResizable(false);
        a.setLocationRelativeTo(null);
        a.setUndecorated(true);
        a.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        a.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        DragListener drag = new DragListener();
        a.addMouseListener(drag);
        a.addMouseMotionListener(drag);
        a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        a.setVisible(true);
        login.getUsernameField().requestFocusInWindow();
    }
}
