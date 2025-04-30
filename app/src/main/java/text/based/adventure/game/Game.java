package text.based.adventure.game;

import java.util.*;

public class Game {
    private Player player;
    private Scanner scanner;

    public void start() {
        setupWorld();
        scanner = new Scanner(System.in);
        System.out.println("Welcome to the Dungeon! Type 'help' for a list of commands.");

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

    public void setupWorld() {
        // Create 20 uniquely themed rooms
        Room[] rooms = new Room[20];
        rooms[0] = new Room("Kitchen", "A room with a faint smell of spices and old wood.");
        rooms[1] = new Room("Hallway", "A long hallway with flickering lights.");
        rooms[2] = new Room("Vault", "A secured room with laser grids and heavy doors.");
        rooms[3] = new Room("Gallery", "An elegant room filled with valuable art.");
        rooms[4] = new Room("Security Office", "Multiple monitors display camera feeds.");
        rooms[5] = new Room("Rooftop", "The wind howls over the open rooftop.");
        rooms[6] = new Room("Basement", "Damp and dark with exposed pipes.");
        rooms[7] = new Room("Lobby", "A grand entrance hall with marble floors.");
        rooms[8] = new Room("Library", "Shelves of dusty books and security manuals.");
        rooms[9] = new Room("Armory", "Filled with old weapons and uniforms.");
        rooms[10] = new Room("Janitor's Closet", "Cramped and filled with cleaning supplies.");
        rooms[11] = new Room("Exhibit Hall", "Temporary exhibits from various museums.");
        rooms[12] = new Room("Archives", "A room of file cabinets and historical records.");
        rooms[13] = new Room("Storage Room", "Crates stacked to the ceiling.");
        rooms[14] = new Room("Workshop", "Tools and half-built electronics are scattered around.");
        rooms[15] = new Room("Break Room", "Coffee machines and a flickering vending machine.");
        rooms[16] = new Room("Server Room", "Flashing lights and humming servers fill the space.");
        rooms[17] = new Room("Elevator Shaft", "Dark and dangerous, the elevator is missing.");
        rooms[18] = new Room("Observation Deck", "A panoramic view of the surrounding city.");
        rooms[19] = new Room("Control Room", "Buttons, levers, and blinking lights cover the walls.");

        // Interconnect all rooms in a basic web pattern
        for (int i = 0; i < rooms.length; i++) {
            Room current = rooms[i];
            Room next = rooms[(i + 1) % rooms.length];
            Room back = rooms[(i - 1 + rooms.length) % rooms.length];
            current.setExit("next", next);
            current.setExit("back", back);
            if (i + 5 < rooms.length) current.setExit("down", rooms[i + 5]);
            if (i - 5 >= 0) current.setExit("up", rooms[i - 5]);
        }

        // Create 40 uniquely named and functional items
        Item[] items = new Item[] {
            new Item("Lockpick Set", "Helps unlock simple mechanical locks."),
            new Item("Keycard", "Grants access to electronically locked rooms."),
            new Item("Glass Cutter", "Cuts display glass without triggering alarms."),
            new Item("Blueprint", "Detailed museum layout."),
            new Item("Disguise Kit", "Allows you to move undetected in certain rooms."),
            new Item("Rope", "Useful for scaling or climbing obstacles."),
            new Item("Flash Drive", "Contains software to disable server security."),
            new Item("Security Override Chip", "Bypasses motion sensor systems."),
            new Item("EMP Device", "Temporarily disables all nearby electronics."),
            new Item("Fake ID", "Allows access to restricted staff-only areas."),
            new Item("Night Vision Goggles", "See in complete darkness."),
            new Item("Laser Mirror", "Redirects laser beams in vaults."),
            new Item("Sculpture Fragment", "Could unlock a hidden mechanism."),
            new Item("Ancient Coin", "Triggers weight-based puzzle when placed."),
            new Item("Van Gogh Painting", "A high-value target for the heist."),
            new Item("Security Manual", "Reveals guard patrol routes."),
            new Item("Codebook", "Deciphers numeric codes on locked panels."),
            new Item("Audio Bug", "Planted to monitor security conversations."),
            new Item("Silent Drill", "Breaks through safes quietly."),
            new Item("Museum Badge", "Used to impersonate staff."),
            new Item("C4 Charge", "Blows open high-security doors (one use)."),
            new Item("Adhesive Gel", "Temporarily sticks objects in place for puzzle solutions."),
            new Item("Motion Detector", "Detects moving entities in nearby rooms."),
            new Item("Thermal Imager", "Reveals hidden heat signatures in walls."),
            new Item("Fingerprint Kit", "Reveals biometric panel usage."),
            new Item("Code Scanner", "Extracts numeric sequences from terminals."),
            new Item("Painting Frame", "Contains a hidden key."),
            new Item("Encrypted Tablet", "Needs decryption to access security logs."),
            new Item("USB Exploit", "Crashes outdated terminals."),
            new Item("Camera Jammer", "Blinds cameras for 10 seconds."),
            new Item("Crowbar", "Pry open locked crates."),
            new Item("Magnet Tool", "Retrieves keys behind locked glass."),
            new Item("Mirror Shard", "Redirects infrared beams."),
            new Item("Guard Uniform", "Disguises player from detection."),
            new Item("Surveillance Tape", "Contains incriminating footage."),
            new Item("Pressure Plate", "Used to simulate object weight."),
            new Item("Security Key", "Opens control room doors."),
            new Item("Vault Combo", "Used to unlock the main vault door."),
            new Item("Empty Frame", "Used to replace stolen painting."),
            new Item("Master Key", "Opens any mechanical lock in the museum.")
        };

        // Distribute items across rooms
        for (int i = 0; i < items.length; i++) {
            rooms[i % rooms.length].addItem(items[i]);
        }

        // Add puzzles to specific rooms
        rooms[2].setPuzzle(new Puzzle("Vault Combo", "The vault door unlocks and opens with a click."));
        rooms[5].setPuzzle(new Puzzle("Rope", "You use the rope to descend the rooftop safely."));
        rooms[16].setPuzzle(new Puzzle("Flash Drive", "Security systems disabled. The server whirs down."));
        rooms[19].setPuzzle(new Puzzle("Master Key", "You override the entire control room lock system."));

        // Player setup
        player = new Player("Thief");
        player.setCurrentRoom(rooms[0]);

        // Add useful starter items
        player.getInventory().add(new Item("Lockpick Set", "Helps unlock simple mechanical locks."));
        player.getInventory().add(new Item("Blueprint", "Detailed museum layout."));}
        public Player getPlayer() {
            return player;}