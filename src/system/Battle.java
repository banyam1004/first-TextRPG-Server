package system;

import game.Character;
import game.Monster;
import network.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Battle {
    public static void start(List<ClientHandler> party, Monster monster) {
        for(ClientHandler handler : party){
            handler.sendMessage("\n== 전투 시작 ==");
            handler.sendMessage("[ " + monster.name + "] HP: " + monster.hp + " 공격력: " + monster.attack);
        }

        while (monster.hp > 0) {
            for (ClientHandler handler : party) {
                if (monster.hp <= 0) break;

                handler.sendMessage("\n" + handler.character.name + "의 차례!");
                handler.sendMessage("1. 공격 2. 소지품 확인 3. 도망");

                try {
                    String input = handler.getInput();

                    if (input.equals("1")) {
                        monster.hp -= handler.character.attack;

                        for (ClientHandler member : party) {
                            member.sendMessage(handler.character.name + "이(가) " + monster.name + "에게 " + handler.character.attack + " 데미지!");
                            member.sendMessage(monster.name + "HP: " + Math.max(monster.hp, 0));
                        }
                    } else if (input.equals("3")) {
                        for (ClientHandler member : party) {
                            member.sendMessage(handler.character.name + "이(가) 도망쳤습니다!");
                        }
                        return;
                    }
                } catch (IOException e) {
                    handler.sendMessage("입력 오류");
                }
            }
            if (monster.hp > 0) {
                for (ClientHandler handler : party) {
                    handler.character.hp -= monster.attack;
                    handler.sendMessage(monster.name + "의 반격! " + monster.attack + " 데미지!");
                    handler.sendMessage("내 HP: " + handler.character.hp + "/" + handler.character.maxHp);
                }
            }
        }
        for(ClientHandler handler : party) {
            handler.sendMessage("\n" + monster.name + "을 쓰러뜨렸습니다.");
            handler.character.gainExp(monster.expReward, handler);
        }
        if(monster.dropItem != null && Math.random() <= monster.dropItem.dropRate) {
            ClientHandler lucky = party.get(0);
            lucky.character.inventory.addItem(monster.dropItem, lucky.getOut());
        }
    }
}
