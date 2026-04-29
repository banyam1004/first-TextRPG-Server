package game;

import java.io.PrintWriter;

public class Monster {
    public String name;
    public int hp;
    public int attack;
    public int expReward;
    public Item dropItem;

    public Monster(String name, int hp, int attack, int expReward, Item item) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.expReward = expReward;
        this.dropItem = item;
    }

    public void showStatus(PrintWriter out) {
        System.out.println("[ " + name + " ] HP: " + hp + " 공격력: " + attack);
    }
}
