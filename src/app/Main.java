package app;

import game.Character;
import game.Inventory;
import system.Dungeon;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================");
        System.out.println("        텍스트 게임        ");
        System.out.println("=========================");
        System.out.print("캐릭터 이름을 입력하세요: ");

        String name = scanner.nextLine();
        Character player = new Character(name);

        System.out.println("\n어서오세요, " + name +"님!");
        player.showStatus();
    }
}