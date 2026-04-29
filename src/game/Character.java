package game;

import network.ClientHandler;

import java.io.PrintWriter;

public class Character {
    public String name;
    public int hp;
    public int maxHp;
    public int attack;
    public int level;
    public int exp;
    public int nextExp;
    public Inventory inventory;

    public Character(String name) {
        this.name = name;
        this.hp = 100;
        this.maxHp = 100;
        this.attack = 10;
        this.level = 1;
        this.exp = 0;
        this.nextExp = 50;
        this.inventory = new Inventory();
    }

    public void gainExp(int amount, ClientHandler handler) {
        exp += amount;
        handler.sendMessage("경험치 + " + amount + " (현재: " + exp + "/" + nextExp + ")");

        if(exp >= nextExp) {
            levelUp(handler);
        }
    }

    private void levelUp(ClientHandler handler) {
        level++;
        exp = 0;
        nextExp = nextExp + 30;
        maxHp += 20;
        hp = maxHp;
        attack += 5;

        handler.sendMessage("\n** 레벨 업! **");
        handler.sendMessage("레벨: " + (level - 1) + " → " + level);
        handler.sendMessage("최대 HP: " + (maxHp - 20) + " → " + maxHp);
        handler.sendMessage("공격력: " + (attack - 5) + " → " + attack);
    }
    public void showStatus(){
        System.out.println("\n[ " + name + " 의 상태 ]");
        System.out.println("레벨 : " + level);
        System.out.println("HP: " + hp + " / " + maxHp);
        System.out.println("공격력: " + attack);
        System.out.println("경험치: " + exp + " / " + nextExp);
    }

    public void showStatus(PrintWriter out){
        out.println("\n[ " + name + " 의 상태 ]");
        out.println("레벨 : " + level);
        out.println("HP: " + hp + " / " + maxHp);
        out.println("공격력: " + attack);
        out.println("경험치: " + exp + " / " + nextExp);
    }
}
