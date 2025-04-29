package text.based.adventure.game;

import java.util.*;

public class Game {
    private Player player;
    private Scanner scanner;

    public void start() {
        setupWorld();
        scanner = new Scanner(System.in);
        System.out.println("Welcome to Zork Advanced! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("quit")) {
                System.out.println("Thanks for playing!");
                break;
            } else if (input.equals("look")) {
                System.out.println(player.getCurrentRoom().getFullDescription());
            } else if (input.equals("items")) {
                player.getCurrentRoom().listItems();
            } else if (input.startsWith("go ")) {
                handleMovement(input.substring(3));
            } else if (input.startsWith("take ")) {
                handleTake(input.substring(5));
            } else if (input.startsWith("use ")) {
                handleUse(input.substring(4));
            } else if (input.equals("inventory")) {
                player.showInventory();
            } else if (input.equals("help")) {
                System.out.println("Commands: go [direction], look, items, take [item], use [item], inventory, help, quit");
            } else {
                System.out.println("I don't understand that.");
            }
        }
        scanner.close();
    }

    private void handleMovement(String direction) {
        Room next = player.getCurrentRoom().getExit(direction);
        if (next == null) {
            System.out.println("You can't go that way.");
        } else if (next.getPuzzle() != null && !next.getPuzzle().isSolved()) {
            System.out.println("The way is blocked: " + next.getPuzzle().getHint());
        } else {
            player.setCurrentRoom(next);
            System.out.println(next.getFullDescription());
        }
    }

    private void handleTake(String itemName) {
        Item item = player.getCurrentRoom().takeItem(itemName);
        if (item != null) {
            player.addItem(item);
            System.out.println("You took the " + item.getName());
        } else {
            System.out.println("That item is not here.");
        }
    }

    private void handleUse(String itemName) {
        if (!player.hasItem(itemName)) {
            System.out.println("You don't have that item.");
            return;
        }

        for (Room room : player.getCurrentRoom().getExits().values()) {
            Puzzle puzzle = room.getPuzzle();
            if (puzzle != null && !puzzle.isSolved() && puzzle.trySolve(itemName)) {
                System.out.println("You used the " + itemName + " to solve a puzzle!");
                return;
            }
        }
        System.out.println("Nothing happened.");
    }

    private void setupWorld() {
        Room foyer = new Room("Foyer", "A small entryway with a dusty rug.");
        Room hall = new Room("Hall", "A long hallway with paintings.");
        Room kitchen = new Room("Kitchen", "An old kitchen with a locked door north.");
        Room vault = new Room("Vault", "A hidden room with treasure!");

        Item key = new Item("key", "A small brass key.");
        kitchen.addItem(key);

        Puzzle lock = new Puzzle("key", "The door is locked. Maybe a key could open it.");
        vault.setPuzzle(lock);

        foyer.setExit("north", hall);
        hall.setExit("south", foyer);
        hall.setExit("east", kitchen);
        kitchen.setExit("west", hall);
        kitchen.setExit("north", vault);

        player = new Player(foyer);
    }
}