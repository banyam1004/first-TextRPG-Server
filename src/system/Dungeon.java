package system;

import game.Character;
import game.Item;
import game.Monster;
import network.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Dungeon {

    private static List<ClientHandler> waitingPlayers = new CopyOnWriteArrayList<>();
    private static final int MIN_PLAYERS = 2;

    public static synchronized void enterTick(ClientHandler handler) {
        waitingPlayers.add(handler);
        handler.sendMessage("던전 입장 대기 중...(" + waitingPlayers.size() + "/" + MIN_PLAYERS + ")");

        if(waitingPlayers.size() >= MIN_PLAYERS) {
            List<ClientHandler> party = new ArrayList<>(waitingPlayers);
            waitingPlayers.clear();

            Random random = new Random();
            for (ClientHandler member : party) {
                member.party = party;
                member.dungeonState = DungeonState.IN_ROOM;
                member.currentRoom = 1;
                member.currentMonster = null;
                member.sendMessage("\n== 던전에 입장했습니다 ==");
                member.sendMessage("파티원: " + party.size() + "명");
                member.sendMessage("\n[ 1번째 방 ]");

                if (random.nextInt(10) < 7) {
                    Monster monster = createRandomMonster(random);
                    member.currentMonster = monster;
                    member.dungeonState = DungeonState.IN_BATTLE;
                    member.sendMessage(monster.name + "이(가) 나타났다!");
                    member.sendMessage("1. 공격 2.소지품 3.도망");
                    member.waitingInput = true;
                } else {
                    member.sendMessage("아무것도 없는 방이다...");
                    member.waitingInput = false;
                }
            }
        }
    }
    private static void startDungeon(List<ClientHandler> party) {
        for (ClientHandler handler : party) {
            handler.sendMessage("\n== 던전에 입장했습니다 ==");
            handler.sendMessage("파티원: " + party.size() + "명");
        }

        Random random = new Random();
        int roomCount = 3;

        for (int i = 1; i <= roomCount; i++) {
            for (ClientHandler handler : party) {
                handler.sendMessage("\n[ " + i + "번째 밤 ]");
            }
            if (random.nextInt(10) < 7) {
                Monster monster = createRandomMonster(random);
                for (ClientHandler handler : party) {
                    handler.sendMessage(monster.name + "이(가) 나타났다!");
                }
                Battle.start(party, monster);
            } else {
                for (ClientHandler handler : party) {
                    handler.sendMessage("아무것도 없는 방이다...");
                }
            }
        }
        for (ClientHandler handler : party) {
            handler.sendMessage("\n던전을 클리어했습니다!");
        }
    }
    public static Monster createRandomMonster(Random random) {
        int type = random.nextInt(3);
        if(type == 0) return new Monster("슬라임", 20, 4, 15, new Item("소형 HP 포션", 30, 0.7));
        if(type == 1) return new Monster("고블린", 35, 8, 25, new Item("중형 HP 포션", 70, 0.5));
        return new Monster("오크", 50, 12, 40, new Item("대형 HP 포션", 150, 0.3));
    }
}