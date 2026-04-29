package network;

import game.Monster;
import system.Dungeon;
import system.DungeonState;

import java.util.List;
import java.util.Random;

import static system.Dungeon.createRandomMonster;

public class GameLoop implements Runnable {
    private static final int TICK_RATE = 20;
    private static final long TICK_TIME = 1000 / TICK_RATE;
    private boolean running = true;

    @Override
    public void run() {
        System.out.println("게임 루프 시작! (" + TICK_RATE + " TPS)");

        while(running) {
            long start = System.currentTimeMillis();

            tick();

            long elapsed = System.currentTimeMillis() - start;
            long sleep = TICK_TIME - elapsed;

            if(sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    private void tick() {
        System.out.println("틱 실행 중...접속자: " + GameServer.clients.size() + "명");
        for(ClientHandler handler : GameServer.clients) {

            String input = handler.inputQueue.poll();

            switch(handler.dungeonState) {
                case NONE -> handlerTown(handler, input);
                case WAITING -> handlerWaiting(handler);
                case IN_ROOM -> handleRoom(handler, input);
                case IN_BATTLE -> handleBattle(handler, input);
                case CLEAR -> handleClear(handler);
            }
        }
    }

    private void handlerTown(ClientHandler handler, String input) {
        if (input == null) return;
        if(input.equals("1")) {
            handler.dungeonState = DungeonState.WAITING;
            Dungeon.enterTick(handler);
        }
        else if (input.equals("2")) {
            handler.character.hp = handler.character.maxHp;
            handler.sendMessage("HP가 회복됐습니다!");
            handler.waitingInput = false;
        }
        else if (input.equals("3")) {
            handler.character.showStatus(handler.getOut());
            handler.waitingInput = false;
        }
        else if (input.equals("5")) {
            handler.sendMessage("게임을 종료했습니다.");
            handler.waitingInput = false;
        }

        if (!handler.waitingInput) {
            showTownMenu(handler);
        }
    }

    private void handlerWaiting(ClientHandler handler) {

    }

    private void handleRoom(ClientHandler handler, String input) {
        if(handler.waitingInput) return;

        Random random = new Random();
        handler.currentRoom++;

        if (handler.currentRoom > 3) {
            handler.dungeonState = DungeonState.CLEAR;
            return;
        }

        for (ClientHandler member : handler.party) {
            member.sendMessage("\n[ " + handler.currentRoom + "번째 방 ]");
        }

        if (random.nextInt(10) < 7) {
            Monster monster = createRandomMonster(random);
            for (ClientHandler member : handler.party) {
                member.currentMonster = monster;
                member.dungeonState = DungeonState.IN_BATTLE;
                member.sendMessage(monster.name + "이(가) 나타났다!");
                member.sendMessage("1. 공격 2. 소지품 3. 도망");
                member.waitingInput = true;
            }
        }
        else {
            for(ClientHandler member : handler.party) {
                member.sendMessage("아무것도 없는 방이다...");
                member.waitingInput = false;
            }
        }
    }

    private void handleBattle(ClientHandler handler, String input) {
        if(input == null) return;

        Monster monster = handler.currentMonster;
        List<ClientHandler> party = handler.party;

        if (input.equals("1")) {
            //공격
            monster.hp -= handler.character.attack;
            for (ClientHandler member : party) {
                member.sendMessage(handler.character.name + "이(가) " + monster.name + "에게 " + handler.character.attack + " 데미지!");
                member.sendMessage(monster.name + " HP: " + Math.max(monster.hp, 0));
            }

            if (monster.hp <= 0) {
                for (ClientHandler member : party) {
                    member.sendMessage("\n" + monster.name + "을 쓰러뜨렸습니다!");
                    member.character.gainExp(monster.expReward, member);
                    member.dungeonState = DungeonState.IN_ROOM;
                    member.currentMonster = null;
                    member.waitingInput = false;
                }
                if (monster.dropItem != null && Math.random() <= monster.dropItem.dropRate) {
                    ClientHandler lucky = party.get(0);
                    lucky.character.inventory.addItem(monster.dropItem, lucky.getOut());
                }
            }
            else {
                for(ClientHandler member : party) {
                    member.character.hp -= monster.attack;
                    member.sendMessage(monster.name + "의 반격! " + monster.attack + " 데미지!");
                    member.sendMessage("내 HP: " + member.character.hp + "/" + member.character.maxHp);
                    member.sendMessage("1. 공격 2. 소지품 3. 도망");
                    member.waitingInput = true;
                }
            }
        }
        else if (input.equals("3")) {
            for (ClientHandler member : party) {
                member.sendMessage(handler.character.name + "이(가) 도망쳤습니다!");
                member.dungeonState = DungeonState.NONE;
                member.party = null;
                member.waitingInput = false;
            }
        }
    }

    private void handleClear(ClientHandler handler) {
        handler.sendMessage("\n던전을 클리어했습니다!");
        handler.dungeonState = DungeonState.NONE;
        handler.currentRoom = 0;
        handler.waitingInput = false;
        showTownMenu(handler);
    }

    private void showTownMenu(ClientHandler handler) {
        handler.sendMessage("\n== 마을 ==");
        handler.sendMessage("1. 던전 입장");
        handler.sendMessage("2. 휴식 (HP 회복)");
        handler.sendMessage("3. 상태 확인");
        handler.sendMessage("4. 소지품 확인");
        handler.sendMessage("5. 게임 종료");
        handler.sendMessage("선택: ");
        handler.waitingInput = true;
    }

    public void stop() {
        running = false;
    }
}