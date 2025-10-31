package clientPackage;

import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        Scanner scanner = new Scanner(System.in);
        try {
            socket = new DatagramSocket();
            System.out.print("Entrez votre nom d'utilisateur : ");
            String nomUtilisateur = scanner.nextLine().trim();
            System.out.println("\nConnecté au serveur sur le port 1234");
            System.out.println("Tapez 'quit' pour quitter\n");
            ThreadReception threadReception = new ThreadReception(socket);
            threadReception.start();
            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("quit")) {
                    System.out.println("Déconnexion...");
                    threadReception.arreter();
                    break;
                }
                if (message.trim().isEmpty()) {
                    continue;
                }
                String messageComplet = "[" + nomUtilisateur + "] : " + message;
                byte[] buffer = messageComplet.getBytes();
                DatagramPacket paquetEmis = new DatagramPacket(buffer,buffer.length,InetAddress.getByName("localhost"),1234);
                socket.send(paquetEmis);
            }
            scanner.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
class ThreadReception extends Thread {
    private DatagramSocket socket;
    private boolean enMarche = true;
    private byte[] buffer = new byte[1024];
    public ThreadReception(DatagramSocket socket) {
        this.socket = socket;
    }
    public void arreter() {
        enMarche = false;
    }
    @Override
    public void run() {
        try {
            while (enMarche) {
                buffer = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("\n" + message);
            }
        } catch (Exception e) {
            if (enMarche) {
                System.err.println("Erreur réception : " + e.getMessage());
            }
        }
    }
}