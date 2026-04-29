package network;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 8080;

        Socket socket = new Socket(host, port);
        System.out.println("서버에 접속했습니다!");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true
        );

        Scanner scanner = new Scanner(System.in);

        System.out.println(in.readLine());

        System.out.print("이름 입력: ");
        String name = scanner.nextLine();
        out.println(name);

        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("서버 연결 끊김");
            }
        });
        receiveThread.start();

        while(true) {
            String input = scanner.nextLine();
            if(input.equals("exit")){
                break;
            }
            out.println(input);
        }
        socket.close();
    }
}
