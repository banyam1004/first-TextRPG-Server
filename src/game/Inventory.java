package game;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();

    }

    public void addItem(Item item) {
        items.add(item);
        System.out.println(item.name + "를 획득했습니다.");
    }

    public void addItem(Item item, PrintWriter out) {
        items.add(item);
        out.println(item.name + "를 획득했습니다.");
    }

    public void showItems(PrintWriter out) {
        if(items.isEmpty()) {
            out.println("인벤토리가 비어있습니다.");
            return;
        }
        int i = 1;
        for(Item item : items){
            out.println(i + ". " + item.name + " (회복량: " + item.healAmount + ")");
            i++;
        }
    }

    public void useItem(int index, Character player, PrintWriter out) {

        if(index == -1) {
            out.println("아이템 사용을 취소했습니다.");
            return;
        }

        else if(index < 0 || index >= items.size()) {
            out.println("잘못된 번호입니다.");
            return;
        }

        Item item = items.get(index);
        player.hp = Math.min(player.hp + item.healAmount, player.maxHp);
        out.println(item.name + "을 사용했습니다. HP +" + item.healAmount);
        items.remove(index);
    }
}