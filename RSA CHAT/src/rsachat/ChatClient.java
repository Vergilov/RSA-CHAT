package rsachat;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Random;

public class ChatClient implements Runnable {

    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;
    private RSA rsa = new RSA();
    private ChatWindow w = null;

    public ChatClient(String serverName, int serverPort) {
        w = new ChatWindow(this);
        w.setVisible(true);
        Random r = new Random();
        w.setTitle("Klient: " + (Integer.toString(r.nextInt())));
        System.out.println("Nawiązywanie połączenia. Proszę czekać ...");
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Połączony: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host nieznany: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Niespodziewany błąd: " + ioe.getMessage());
        }
    }

    public void run() {
        BigInteger normal = null;
        BigInteger zakod = null;
        String read = null;
        while (thread != null) {
            try {
                read = console.readLine();
                normal = new BigInteger(read.getBytes()); //console.readLine().getBytes()
                zakod = rsa.kodowanie(normal);
                streamOut.writeUTF(new String(zakod.toString()));
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Wysłanie błędu: " + ioe.getMessage());
                stop();
            }
        }
    }

    public void BntRun(String msg) {

        try {

            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println("Wysłanie błędu: " + ioe.getMessage());
            stop();
        }

    }

    public void handle(String msg) {
        BigInteger zakod = null;
        BigInteger normal = null;

        //Co robi z wiadomoscia
        zakod = new BigInteger(msg);
        normal = rsa.dekodowanie(zakod);
        w.ShowMsg("Zaszyfrowany Tekst:  " + zakod);
        w.ShowMsg("Wyslany tekst:  " + new String(normal.toByteArray()));
        System.out.println("Zaszyfrowany Tekst:  " + zakod);
        System.out.println("Wyslany tekst:  " + new String(normal.toByteArray()));
    }

    public void start() throws IOException {
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
        try {
            if (console != null) {
                console.close();
            }
            if (streamOut != null) {
                streamOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            System.out.println("Błąd krytyczny ...");
        }
        client.zamknij();
        client.stop();
    }

    public static void main(String args[]) {
        ChatClient client = null;

        client = new ChatClient("localhost", 2);
    }
}
