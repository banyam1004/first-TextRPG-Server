package network;

import game.Monster;
import system.Dungeon;
import system.DungeonState;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientHandler implements Runnable{
    private Socket socket;
    private String clientName;
    private BufferedReader in;
    private PrintWriter out;
    public game.Character character;
    public Queue<String> inputQueue = new ConcurrentLinkedQueue<>();
    public boolean waitingInput = false;
    public DungeonState dungeonState = DungeonState.NONE;
    public int currentRoom = 0;
    public Monster currentMonster = null;
    public List<ClientHandler> party = null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            this.out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true
            );

            out.println("접속을 환영합니다! 이름을 입력하세요. ");

            clientName = in.readLine();
            character = new game.Character(clientName);
            character.showStatus(out);
            GameServer.broadcast(clientName + "님이 입장했습니다!", this);

            String message;
            while (character.hp > 0) {
                if(!waitingInput) {
                    out.println("\n== 마을 ==");
                    out.println("1. 던전 입장");
                    out.println("2. 휴식 (HP 회복)");
                    out.println("3. 상태 확인");
                    out.println("4. 소지품 확인");
                    out.println("5. 게임 종료");
                    out.println("선택: ");
                    waitingInput = true;
                }
                String input = in.readLine();
                if (input != null) {
                    inputQueue.add(input);
                    waitingInput = true;
                }
            }
            if (character.hp <= 0) {
                out.println("\n== 게임 오버 ==");
                out.println(clientName + "님의 모험이 끝났습니다...");
            }
        } catch (IOException e) {
            System.out.println(clientName + " 연결 끊김");
        } finally {
            GameServer.clients.remove(this);
            GameServer.broadcast(clientName + " 님이 퇴장했습니다.", this);
        }
    }
    public String getInput() throws IOException {
        return in.readLine();
    }
    public PrintWriter getOut() {
        return out;
    }
}