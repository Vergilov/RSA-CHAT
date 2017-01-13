package rsachat;

import java.net.*;
import java.io.*;
import java.math.BigInteger;

public class ChatServer implements Runnable {

    private ChatServerThread clients[] = new ChatServerThread[50];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;

    public ChatServer(int port) {
        try {
            System.out.println("Ustawienie na port " + port + ", Proszę czekać  ...");
            server = new ServerSocket(port);
            System.out.println("Serwer Ruszył: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println("Nie mogę dołączyć do portu " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                System.out.println("Czekam na Klienta ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Błąd po stronie serwera: " + ioe);
                stop();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    public synchronized void handle(String input) {
        RSA rsa = new RSA();
        BigInteger normal = new BigInteger(input.getBytes()); //console.readLine().getBytes() //normalna wiadomosc
        BigInteger zakod = rsa.kodowanie(normal); //zakodowana wiadomosc
        for (int i = 0; i < clientCount; i++) {
            clients[i].send(zakod.toString());
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            System.out.println("Klient zaakceptował: " + socket);
            clients[clientCount] = new ChatServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Błąd otwarcia wątku: " + ioe);
            }
        } else {
            System.out.println("Klient odmówił: Maksimum " + clients.length + " Osiągnięte.");
        }
    }

    public static void main(String args[]) {
        ChatServer server = new ChatServer(2);
    }
}
