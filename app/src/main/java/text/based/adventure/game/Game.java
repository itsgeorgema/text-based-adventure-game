package text.based.adventure.game;

import java.util.*;

public class Game {
    private Player player;
    private Scanner scanner;

    public void start() {
        setupWorld();
        scanner = new Scanner(System.in);
        System.out.println("\nWelcome to the Art Heist Adventure! Type 'help' for commands.");

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
                player.move(input.substring(3));
            } else if (input.startsWith("take ")) {
                player.takeItem(input.substring(5));
            } else if (input.startsWith("use ")) {
                player.useItem(input.substring(4));
            } else if (input.equals("inventory")) {
                player.showInventory();
            } else if (input.equals("help")) {
                System.out.println("Commands: go [direction], look, items, take [item], use [item], inventory, help, quit");
            } else {
                System.out.println("I don't understand that.");
            }
        }
    }

    private void setupWorld() {
        // Setup Rooms
        List<Room> rooms = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            rooms.add(new Room("Room" + i, "This is room number " + i + ", filled with high-tech security and valuable art."));
        }

        // Connect rooms bidirectionally
        for (int i = 0; i < rooms.size() - 1; i++) {
            rooms.get(i).setExit("east", rooms.get(i + 1));
            rooms.get(i + 1).setExit("west", rooms.get(i));
        }

        // Create and distribute 40 unique items
        String[] itemNames = {"lockpick", "blueprint", "grappling hook", "glass cutter", "usb stick", "keycard", "vault code", "diamond cutter",
            "painting frame", "replica sculpture", "laser mirror", "EMP device", "camera jammer", "night vision goggles", "audio bug",
            "suction cups", "security manual", "disguise kit", "thermal lens", "drill", "motion sensor disruptor", "staff badge",
            "c4 charge", "magnetic glove", "invisible ink map", "infrared pen", "pressure plate", "fake ID", "vault schematics",
            "power cell", "alarm code override", "ceiling harness", "flash drive", "crowbar", "microphone tap", "decoy sculpture",
            "encrypted tablet", "art crate", "master key"};

        for (int i = 0; i < itemNames.length; i++) {
            Item item = new Item(itemNames[i], "A tool for the heist: " + itemNames[i]);
            rooms.get(i % rooms.size()).addItem(item);
        }

        // Add thematic puzzles
        rooms.get(2).setPuzzle(new Puzzle("vault code", "A locked vault door. You need the vault code.", "The vault beeps and unlocks."));
        rooms.get(6).setPuzzle(new Puzzle("grappling hook", "A ledge too high to reach.", "You scale the wall with your hook."));
        rooms.get(10).setPuzzle(new Puzzle("EMP device", "A wall of active laser sensors.", "The lasers power down silently."));
        rooms.get(14).setPuzzle(new Puzzle("fake ID", "An ID scanner blocks access.", "The scanner accepts your ID."));
        rooms.get(18).setPuzzle(new Puzzle("master key", "A final locked chamber.", "The master key opens the chamber.") );

        // Start player
        player = new Player(rooms.get(0));
        player.addItem(new Item("blueprint", "A layout of the entire museum."));
        player.addItem(new Item("lockpick", "Useful for basic locks."));
    }
}
