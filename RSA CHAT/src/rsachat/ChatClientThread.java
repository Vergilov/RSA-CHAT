package rsachat;

import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread {

    private Socket socket = null;
    private ChatClient client = null;
    private DataInputStream streamIn = null;

    public ChatClientThread(ChatClient _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        start();
    }

    public void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            System.out.println("Błąd pobrania danych strumieniowych: " + ioe);
            client.stop();
        }
    }

    public void zamknij() {
        try {
            if (streamIn != null) {
                streamIn.close();
            }
        } catch (IOException ioe) {
            System.out.println("Błąd zamknięcia danych strumieniowych: " + ioe);
        }
    }

    public void run() {
        while (true) {
            try {
                client.handle(streamIn.readUTF());
            } catch (IOException ioe) {
                System.out.println("Błąd: " + ioe.getMessage());
                client.stop();
            }
        }
    }
}
