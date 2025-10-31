package serverPackage;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static byte[] buffer = new byte[1024];
    static List<InetSocketAddress> clientsList = new ArrayList<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", 1234);
            socket = new DatagramSocket(serverAddress);
            System.out.println("Serveur démarré sur le port 1234");
            System.out.println("En attente de messages des clients...\n");
            while (true) {
                buffer = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                InetAddress adresseClient = paquetRecu.getAddress();
                int portClient = paquetRecu.getPort();
                InetSocketAddress clientAddress = new InetSocketAddress(adresseClient, portClient);
                if (!clientsList.contains(clientAddress)) {
                    clientsList.add(clientAddress);
                    System.out.println("Nouveau client connecté : " + adresseClient.getHostAddress() + ":" + portClient);
                    System.out.println("Nombre total de clients : " + clientsList.size() + "\n");
                }
                System.out.println("Message reçu de [" + adresseClient.getHostAddress() + ":" + portClient + "] : " + message);
                diffuserMessage(socket, message, clientAddress);
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    private static void diffuserMessage(DatagramSocket socket, String message, InetSocketAddress expediteur) {
        byte[] messageBytes = message.getBytes();
        for (InetSocketAddress client : clientsList) {
            if (!client.equals(expediteur)) {
                try {
                    DatagramPacket paquetEmis = new DatagramPacket(messageBytes,messageBytes.length,client.getAddress(),client.getPort());
                    socket.send(paquetEmis);
                    System.out.println("Message diffusé à " + client.getAddress().getHostAddress() + ":" + client.getPort());
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'envoi à " + client.getAddress().getHostAddress() + ":" + client.getPort());
                }
            }
        }
        System.out.println();
    }
}