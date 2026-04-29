package network;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    public static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        GameLoop gameLoop = new GameLoop();
        Thread loopThread = new Thread(gameLoop);
        loopThread.start();

        System.out.println("서버 시작! 포트: " + port);
        System.out.println("클라이언트 접속 대기 중...");

        while(true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket);
            clients.add(handler);

            Thread thread = new Thread(handler);
            thread.start();
        }
    }
    public static void broadcast(String message, ClientHandler sender) {
        for(ClientHandler client : clients) {
            if(client != sender){
                client.sendMessage(message);
            }
        }
    }
}
