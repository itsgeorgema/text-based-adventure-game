package text.based.adventure.game;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;

public class Game {
    private Player player;
    private Scanner scanner;
    private List<Room> rooms;
    private FuzzyMatcher fuzzyMatcher;

    public void start() {
        setupWorld();
        scanner = new Scanner(System.in);
        
        System.out.println("""
===========================
  ART HEIST ADVENTURE
===========================

You are an elite infiltrator hired to break into the most secure art museum in the world. You are breaking in during the middle of the night when the museum is closed to maximize stealth.

🎯 Objective:
Navigate undetected through rooms, avoid advanced security systems, and retrieve the priceless artifact locked behind a multi-layered vault system.

📍 Starting Location:
You are in the Foyer — the marble-floored entry of the museum. Use the blueprint in your inventory to plan your movements.

🧩 What to Do:
- Explore rooms using commands like 'go north', 'take blueprint', or 'inspect item'
- Use or combine the right items to bypass puzzles and unlock new rooms
- Solve layered puzzles to reach the final control room
- Type 'help' to see all available commands
- Type 'quit' anytime to exit the game

Good luck. The museum won't be closed for long...
""");
        ArrayList<String> commandList = new ArrayList<>();

        // Add default commands in case the file cannot be loaded
        commandList.add("go north");
        commandList.add("go south");
        commandList.add("go east");
        commandList.add("go west");
        commandList.add("look");
        commandList.add("items");
        commandList.add("inventory");
        commandList.add("help");
        commandList.add("quit");
        commandList.add("map");
        commandList.add("hint");
        commandList.add("take");
        commandList.add("use");
        commandList.add("inspect");
        commandList.add("combine");

        try {
            // ALWAYS use the root directory path for commands - this resolves path issues between IDE and Gradle
            String projectRootDir = findProjectRootDir();
            Path commandFilePath = Paths.get(projectRootDir, "all_game_commands.txt");
            
            boolean fileLoaded = false;
            
            // Generate updated commands list
            ArrayList<String> allCommands = new ArrayList<>(commandList);
            
            // Add item-specific commands
            String[] itemNames = {
                "laser mirror", "emp device", "glass cutter", "blueprint", "infrared goggles", 
                "pressure plate", "vault code", "keycard alpha", "keycard beta", "voice recorder",
                "thermal drill", "power cell", "override chip", "zipline hook", "signal jammer",
                "coded ledger", "sensor cloak", "adhesive pad", "decoy badge", "magnet tool",
                "night vision visor", "battery pack", "multi-tool", "elevator override",
                "admin password", "camera loop usb", "surveillance map", "guard schedule",
                "vault fingerprint", "maintenance radio", "art crate key", "ceiling harness",
                "anti-static gloves", "dummy camera", "biometric bypass", "wireless bug",
                "director's ring", "master override"
            };
            
            // Add take, use, inspect commands for all items
            for (String item : itemNames) {
                allCommands.add("take " + item);
                allCommands.add("use " + item); 
                allCommands.add("inspect " + item);
            }
            
            // Add common combine commands
            allCommands.add("combine emp device with decoy badge");
            allCommands.add("combine thermal drill with power cell");
            allCommands.add("combine glass cutter with adhesive pad");
            
            try {
                // Create the project directory if it doesn't exist
                Files.createDirectories(commandFilePath.getParent());
                
                // Write commands to the root file
                Files.write(commandFilePath, allCommands);
                System.out.println("Command list written to: " + commandFilePath);
                
                // Load commands from the root file
                List<String> loadedCommands = Files.readAllLines(commandFilePath);
                commandList.clear();
                commandList.addAll(loadedCommands);
                System.out.println("Command list loaded successfully from: " + commandFilePath);
                fileLoaded = true;
            } catch (Exception e) {
                System.out.println("Error with command file: " + e.getMessage());
            }

            // If file couldn't be loaded or created, use default commands
            if (!fileLoaded) {
                System.out.println("Warning: Using default command list as file operations failed.");
            }
            
            // If no file could be loaded or created, use default commands
            if (!fileLoaded) {
                System.out.println("Warning: Using default command list as all file operations failed.");
            }
        } catch (Exception e) {
            System.out.println("Error handling commands: " + e.getMessage());
            System.out.println("Using default commands instead.");
        }
    
        fuzzyMatcher = new FuzzyMatcher(commandList);
        
        gameLoop();
    }
    
    private void gameLoop() {
        // Show welcome message and initial room description
        System.out.println("\nWelcome to the Art Heist Adventure! You are now in the museum...");
        System.out.println("\n" + player.getCurrentRoom().getFullDescription());
        
        // Give an initial nudge to the player
        System.out.println("\n💡 Hint: Try using 'look' to examine your surroundings and 'inventory' to see what you have.");
        
        // Main game loop
        boolean gameRunning = true;
        while (gameRunning) {
            try {
                System.out.print("\n> ");
                
                // Make sure we have input available
                String input;
                try {
                    // Recreate the scanner for each input to prevent issues with Gradle
                    scanner = new Scanner(System.in);
                    
                    // Check if System.in is available
                    if (System.in.available() == 0) {
                        input = scanner.nextLine().trim().toLowerCase();
                    } else {
                        input = scanner.nextLine().trim().toLowerCase();
                    }
                } catch (NoSuchElementException | IllegalStateException e) {
                    // Handle interrupted input stream more gracefully
                    System.out.println("\n⚠️ Input interrupted. Reconnecting...");
                    try {
                        // Close and recreate scanner
                        if (scanner != null) {
                            scanner.close();
                        }
                        scanner = new Scanner(System.in);
                        System.out.println("Input stream reconnected. Please enter a command:");
                        System.out.print("> ");
                        input = scanner.nextLine().trim().toLowerCase();
                    } catch (Exception ex) {
                        // Last resort protection against terminal issues
                        System.out.println("Unable to read input. The game will continue in 2 seconds.");
                        input = "look"; // Default to a safe command
                        try { Thread.sleep(2000); } catch (InterruptedException ie) {}
                    }
                } catch (IOException e) {
                    // Handle IO exception
                    System.out.println("IO Error: " + e.getMessage());
                    input = "look"; // Default to a safe command
                }

                // Step 1: check base commands
                if (input.equals("look") || input.equals("items") || input.equals("inventory") ||
                    input.equals("help") || input.equals("quit")) {
                    // Proceed as normal
                } else if (input.startsWith("go")) {
                    if (input.equals("go")) {
                        System.out.println("Please specify a direction. Example: go north");
                        continue;
                    }
                } else if (input.startsWith("take")) {
                    if (input.equals("take")) {
                        System.out.println("Please specify an item to take. Example: take blueprint");
                        continue;
                    }
                } else if (input.startsWith("use")) {
                    if (input.equals("use")) {
                        System.out.println("Please specify an item to use. Example: use emp device");
                        continue;
                    }
                } else if (input.startsWith("inspect")) {
                    if (input.equals("inspect")) {
                        System.out.println("Please specify an item to inspect. Example: inspect glass cutter");
                        continue;
                    }
                } else if (input.startsWith("combine")) {
                    if (!input.contains(" with ")) {
                        System.out.println("Use format: combine [item1] with [item2]");
                        continue;
                    }
                } else if (input.startsWith("map")) {
                    // New feature: display a map of visited rooms
                    showMap();
                    continue;
                } else if (input.startsWith("hint")) {
                    // New feature: provide hints based on current room and progress
                    provideHint();
                    continue;
                } else {
                    // Check for similar commands
                    String corrected = fuzzyMatcher.getBestMatch(input);
                    if (corrected == null) {
                        System.out.println("I didn't understand that command. Try 'help' to see all available commands.");
                        continue;
                    } else if (!corrected.equals(input)) {
                        System.out.println("Did you mean: '" + corrected + "'? [y/N]");
                        try {
                            String confirm = scanner.nextLine().trim().toLowerCase();
                            if (confirm.equals("y")) {
                                input = corrected;
                                System.out.println("Using command: " + corrected);
                            } else {
                                continue;
                            }
                        } catch (NoSuchElementException e) {
                            // Recover from closed input stream
                            scanner = new Scanner(System.in);
                            continue;
                        }
                    }
                }

                // Process the command
                if (input.equals("quit")) {
                    System.out.println("\nAre you sure you want to quit? Your progress will not be saved. [y/N]");
                    try (Scanner confirmScanner = new Scanner(System.in)) {
                        // Using try-with-resources to automatically close the scanner
                        String confirm = confirmScanner.nextLine().trim().toLowerCase();
                        if (confirm.equals("y")) {
                            System.out.println("\nThanks for playing the Art Heist Adventure!");
                            // Force immediate clean exit
                            System.exit(0);
                        }
                    } catch (Exception e) {
                        // If there's an error reading input, exit anyway
                        System.out.println("\nExiting game due to input error.");
                        System.exit(0);
                    }
                } else if (input.equals("look") || input.equals("l")) {
                    System.out.println(player.getCurrentRoom().getFullDescription());
                    
                    // Check if player has reached the end game
                    if (player.getCurrentRoom().getName().equals("Control Room")) {
                        System.out.println("\n🎉 CONGRATULATIONS! 🎉");
                        System.out.println("You've successfully reached the Control Room and completed the museum heist!");
                        System.out.println("With access to the security system, you can now escape with your prize.");
                        System.out.println("\nWould you like to play again? [y/N]");
                        
                        // Use try-with-resources to ensure clean handling
                        String again;
                        try (Scanner winScanner = new Scanner(System.in)) {
                            again = winScanner.nextLine().trim().toLowerCase();
                        } catch (Exception e) {
                            // If there's an error, assume "no"
                            again = "n";
                        }
                        if (again.equals("y")) {
                            System.out.println("\nRestarting the game...\n");
                            setupWorld();
                            System.out.println("You are back at the " + player.getCurrentRoom().getName());
                        } else {
                            System.out.println("\nThanks for playing!");
                            gameRunning = false;
                            System.exit(0); // Ensure clean exit
                        }
                    }
                } else if (input.equals("items") || input.equals("i")) {
                    player.getCurrentRoom().listItems();
                } else if (input.equals("inventory") || input.equals("inv")) {
                    player.showInventory();
                } else if (input.equals("help") || input.equals("h")) {
                    System.out.println("\n📋 AVAILABLE COMMANDS:");
                    System.out.println("- go [direction] - Move in a direction (north, south, east, west)");
                    System.out.println("- look (l) - Examine your surroundings");
                    System.out.println("- items (i) - List items in the current room");
                    System.out.println("- take [item] - Pick up an item");
                    System.out.println("- use [item] - Use an item (often on puzzles)");
                    System.out.println("- inspect [item] - Examine an item in your inventory");
                    System.out.println("- combine [item1] with [item2] - Combine two compatible items");
                    System.out.println("- inventory (inv) - Show your current inventory");
                    System.out.println("- map - Display a map of the museum (shows visited rooms)");
                    System.out.println("- hint - Get a contextual hint for your current situation");
                    System.out.println("- help (h) - Show this help message");
                    System.out.println("- quit - Exit the game");
                } else if (input.startsWith("go ")) {
                    player.move(input.substring(3));
                } else if (input.startsWith("take ")) {
                    player.takeItem(input.substring(5));
                } else if (input.startsWith("use ")) {
                    player.useItem(input.substring(4));
                } else if (input.startsWith("inspect ")) {
                    player.inspectItem(input.substring(8));
                } else if (input.startsWith("combine ")) {
                    String[] parts = input.substring(8).split(" with ");
                    if (parts.length == 2) {
                        player.combineItems(parts[0].trim(), parts[1].trim());
                    } else {
                        System.out.println("Use format: combine [item1] with [item2]");
                    }
                } else if (input.equals("map")) {
                    showMap();
                } else if (input.equals("hint")) {
                    provideHint();
                } else {
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("\nInput stream was closed. Reconnecting...");
                try {
                    // Try to recreate the scanner
                    scanner = new Scanner(System.in);
                    System.out.println("Input reconnected. Continue playing.");
                } catch (Exception ex) {
                    System.out.println("Could not reconnect input. The game will exit.");
                    gameRunning = false;
                }
            } catch (Exception e) {
                System.out.println("\n⚠️ An unexpected error occurred: " + e.getMessage());
                System.out.println("The game will continue, but you may experience issues.");
                // Print stack trace to console for debugging but in a controlled way
                System.out.println("\nError details (for reporting bugs):");
                System.out.println("------------------------------------");
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String stackTrace = sw.toString();
                // Print only first few lines to avoid overwhelming the player
                String[] lines = stackTrace.split("\n");
                for (int i = 0; i < Math.min(5, lines.length); i++) {
                    System.out.println(lines[i]);
                }
                System.out.println("------------------------------------");
                System.out.println("Press Enter to continue...");
                
                try {
                    scanner.nextLine();
                } catch (Exception ex) {
                    // If reading the next line fails, recreate the scanner
                    scanner = new Scanner(System.in);
                }
            }
        }
    }

    private void setupWorld() {
        // Define 20 unique rooms
        String[] roomNames = {
            "Foyer", "Gallery", "Vault", "Security Office", "Atrium", "Rooftop", "Archives", "Workshop", "Hall of Sculptures", "Server Room",
            "Library", "Surveillance Room", "Loading Dock", "Exhibit Hall", "Break Room", "Storage Room", "Director's Office", "Conservatory", "IT Closet", "Control Room"
        };
        String[] roomDescs = {
            "The museum's marble-floored entry.",
            "Paintings hang elegantly across velvet walls.",
            "A steel-reinforced vault with fingerprint scanner.",
            "Rows of monitors blink rapidly.",
            "Open skylight and pressure-sensitive floors.",
            "High vantage with zipline access.",
            "Filing cabinets hide blueprints.",
            "Tools and 3D-printed parts scattered around.",
            "Pedestals with missing sculptures.",
            "Buzzing servers hum next to AC vents.",
            "Ancient books and coded ledgers.",
            "Surveillance feeds loop in sequence.",
            "Docked crates for recent deliveries.",
            "Interactive digital displays dimly lit.",
            "Microwaves and unplugged vending machines.",
            "Stacked shelves labeled with barcodes.",
            "A desk covered in notes and hidden keys.",
            "Glass ceiling lets moonlight beam in.",
            "Cables tangle across floor tiles.",
            "One switch controls every door in the museum."
        };

        rooms = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            rooms.add(new Room(roomNames[i], roomDescs[i]));
        }

        // Connect rooms logically
        rooms.get(0).setExit("north", rooms.get(1));
        rooms.get(1).setExit("east", rooms.get(2));
        rooms.get(1).setExit("west", rooms.get(3));
        rooms.get(3).setExit("north", rooms.get(4));
        rooms.get(2).setExit("north", rooms.get(5));
        rooms.get(4).setExit("east", rooms.get(6));
        rooms.get(6).setExit("north", rooms.get(7));
        rooms.get(7).setExit("east", rooms.get(8));
        rooms.get(8).setExit("north", rooms.get(9));
        rooms.get(5).setExit("west", rooms.get(10));
        rooms.get(10).setExit("north", rooms.get(11));
        rooms.get(11).setExit("east", rooms.get(12));
        rooms.get(12).setExit("north", rooms.get(13));
        rooms.get(13).setExit("east", rooms.get(14));
        rooms.get(14).setExit("north", rooms.get(15));
        rooms.get(15).setExit("east", rooms.get(16));
        rooms.get(16).setExit("north", rooms.get(17));
        rooms.get(17).setExit("east", rooms.get(18));
        rooms.get(18).setExit("north", rooms.get(19));

        // Define 40 unique items
        String[][] items = {
            {"laser mirror", "Redirects laser traps"},
            {"emp device", "Disables nearby electronics", "true"},
            {"glass cutter", "Removes panels without alerting alarms"},
            {"blueprint", "Shows hidden exits on museum floorplan"},
            {"infrared goggles", "Reveal motion sensors"},
            {"pressure plate", "Simulates weight of stolen items"},
            {"vault code", "A sticky note with a 4-digit pin"},
            {"keycard alpha", "Opens west wing access panels"},
            {"keycard beta", "Grants access to the server room"},
            {"voice recorder", "Used to mimic guard commands"},
            {"thermal drill", "Cuts through vault hinges"},
            {"power cell", "Activates emergency systems"},
            {"override chip", "Bypasses firmware locks"},
            {"replica sculpture", "Swap for originals without triggering weight sensors"},
            {"hacking tablet", "Exploits system vulnerabilities"},
            {"zipline hook", "Traverse downward gaps"},
            {"signal jammer", "Interrupts surveillance drones"},
            {"coded ledger", "Contains key to decipher passphrases"},
            {"sensor cloak", "Allows stealth movement for 5 seconds"},
            {"adhesive pad", "Can fix mirrors in place"},
            {"decoy badge", "Fools some card readers", "true"},
            {"magnet tool", "Retrieves keys behind grates"},
            {"night vision visor", "Lets you see when power is out"},
            {"battery pack", "Recharges devices temporarily"},
            {"multi-tool", "Swiss-army knife of heist tools"},
            {"elevator override", "Allows access to restricted floors"},
            {"admin password", "Overrides control panel logins"},
            {"camera loop usb", "Plugs into feed to loop footage"},
            {"surveillance map", "Shows blind spots"},
            {"guard schedule", "Predicts patrol routes"},
            {"vault fingerprint", "Synthetic mold of director's thumb"},
            {"maintenance radio", "Broadcasts static"},
            {"art crate key", "Unlocks hidden compartments"},
            {"ceiling harness", "Hooks to skylights"},
            {"anti-static gloves", "Prevents fingerprint detection"},
            {"dummy camera", "Fools motion sensors"},
            {"biometric bypass", "Works on locked panels"},
            {"wireless bug", "Transmits security updates"},
            {"director's ring", "Unlocks his private safe"},
            {"master override", "Universal key for all doors", "true"}
        };

        for (int i = 0; i < items.length; i++) {
            String name = items[i][0];
            String desc = items[i][1];
            boolean combinable = items[i].length == 3 && Boolean.parseBoolean(items[i][2]);
            rooms.get(i % rooms.size()).addItem(new Item(name, desc, combinable));
        }

        // Add layered puzzles that span rooms and require specific or combined items
        rooms.get(2).setPuzzle(new Puzzle(List.of("vault code", "thermal drill"), "A vault door secured with both code and bolts.", "You breach the vault with code and power."));
        rooms.get(9).setPuzzle(new Puzzle(List.of("override chip", "admin password"), "Login console denies access.", "Root access granted."));
        rooms.get(13).setPuzzle(new Puzzle(List.of("glass cutter", "replica sculpture"), "Case sealed with glass sensor.", "The item is swapped without alarm."));
        rooms.get(18).setPuzzle(new Puzzle(List.of("director's ring"), "A hidden safe requiring special access.", "Click. The hidden safe unlocks."));
        rooms.get(19).setPuzzle(new Puzzle(List.of("master override"), "Final door needs system-wide key.", "Every lock disengages silently."));

        // Initialize player
        player = new Player(rooms.get(0));
        player.addItem(new Item("blueprint", "Museum map for navigation"));
        player.addItem(new Item("emp device", "Short-circuits devices", true));
        player.addItem(new Item("decoy badge", "Fake access card", true));
    }
    
    // New method to display a map of visited rooms
    private void showMap() {
        System.out.println("\n📍 MUSEUM MAP 📍");
        System.out.println("(Visited rooms are marked with ✓)");
        System.out.println("-----------------------------------------");
        
        // Create a simplified version of the map
        String[][] mapLines = {
            {"", "", "", "", "CONTROL ROOM", "", ""},
            {"", "", "", "", "   ↑   ", "", ""},
            {"", "", "", "", "IT CLOSET", "", ""},
            {"", "", "", "", "   ↑   ", "", ""},
            {"", "ROOFTOP", "←", "LIBRARY", "→", "CONSERVATORY", "→", "IT CLOSET"},
            {"", "   ↑   ", "", "   ↑   ", "", "     ↑     ", "", ""},
            {"", "VAULT", "", "SURVEILLANCE", "→", "LOADING DOCK", "→", "EXHIBIT HALL"},
            {"", "   ↑   ", "", "", "", "", "", ""},
            {"FOYER", "→", "GALLERY", "", "", "", "", "BREAK ROOM"},
            {"", "", "   ↓   ", "", "", "", "", "   ↑   "},
            {"", "", "SECURITY", "→", "ATRIUM", "→", "ARCHIVES", "→", "WORKSHOP", "→", "HALL OF SCULPTURES", "→", "SERVER ROOM"},
            {"", "", "", "", "", "", "", "", "", "", "     ↓     ", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "STORAGE ROOM", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "     ↓     ", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "DIRECTOR'S OFFICE", "", ""}
        };

        // Mark visited rooms
        for (Room room : rooms) {
            if (room.isVisited()) {
                markRoomOnMap(mapLines, room.getName() + " ✓");
            }
        }
        
        // Mark current room with a player symbol
        markRoomOnMap(mapLines, player.getCurrentRoom().getName() + " 🔸");
        
        // Display the map
        for (String[] line : mapLines) {
            System.out.println(String.join(" ", line));
        }
        
        System.out.println("-----------------------------------------");
    }
    
    // Helper method to mark a room on the map
    private void markRoomOnMap(String[][] map, String roomName) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                String cell = map[i][j];
                // Only check non-empty cells
                if (cell != null && !cell.isEmpty() && !cell.equals("→") && !cell.equals("←") && !cell.equals("↑") && !cell.equals("↓")) {
                    // Check if the cell contains the room name (without the checkmark)
                    String roomBaseName = roomName.contains(" ✓") ? roomName.substring(0, roomName.indexOf(" ✓")) : 
                                         (roomName.contains(" 🔸") ? roomName.substring(0, roomName.indexOf(" 🔸")) : roomName);
                    
                    if (cell.contains(roomBaseName)) {
                        map[i][j] = roomName;
                    }
                }
            }
        }
    }
    
    // New method to provide contextual hints to the player
    private void provideHint() {
        Room currentRoom = player.getCurrentRoom();
        String roomName = currentRoom.getName();
        
        System.out.println("\n💡 HINT:");
        
        // Check for unsolved puzzle in current room
        Puzzle puzzle = currentRoom.getPuzzle();
        if (puzzle != null && !puzzle.isSolved()) {
            Set<String> requiredItems = puzzle.getRequiredItems();
            Set<String> usedItems = puzzle.getUsedItems();
            
            System.out.println("This room has a puzzle: " + puzzle.getHint());
            
            // Find which items the player is missing
            Set<String> missingItems = new HashSet<>(requiredItems);
            missingItems.removeAll(usedItems);
            
            if (!missingItems.isEmpty()) {
                System.out.println("You need to find or use: " + String.join(", ", missingItems));
                
                // Check if player has any of the needed items
                for (String itemName : missingItems) {
                    if (player.hasItem(itemName)) {
                        System.out.println("Try using the " + itemName + " that's in your inventory!");
                        return;
                    }
                }
            }
            return;
        }
        
        // If no puzzle in current room, provide guidance based on room
        switch(roomName) {
            case "Foyer":
                System.out.println("Try going north to the Gallery to begin your exploration.");
                break;
            case "Gallery":
                System.out.println("From here you can go east to find the Vault or west to the Security Office.");
                break;
            case "Security Office":
                System.out.println("Security systems can be bypassed with the right tools. Try going north to the Atrium.");
                break;
            case "Atrium":
                System.out.println("The open skylight provides multiple paths. Try going east to the Archives.");
                break;
            case "Vault":
                System.out.println("You've breached the vault! Try going north to the Rooftop for a better vantage point.");
                break;
            case "Control Room":
                System.out.println("You've reached the final control room! You've successfully completed the heist!");
                break;
            default:
                // Generic hint about taking items and exploring
                System.out.println("Make sure to take any items in this room and explore all possible exits.");
                List<String> directions = new ArrayList<>(currentRoom.getExits().keySet());
                if (!directions.isEmpty()) {
                    System.out.println("From here you can go: " + String.join(", ", directions));
                }
        }
    }
    
    /**
     * Find the project's root directory by checking for the existence of key files
     * This helps ensure consistent file paths between IDE and Gradle execution
     */
    private String findProjectRootDir() {
        String currentDir = System.getProperty("user.dir");
        Path rootCandidate = Paths.get(currentDir);
        
        // If we're in the app directory, move up one level
        if (rootCandidate.getFileName().toString().equals("app")) {
            rootCandidate = rootCandidate.getParent();
        }
        
        // Look for common root directory indicators
        Path gradlew = rootCandidate.resolve("gradlew");
        Path readme = rootCandidate.resolve("README.md");
        Path settings = rootCandidate.resolve("settings.gradle");
        
        if (Files.exists(gradlew) || Files.exists(readme) || Files.exists(settings)) {
            return rootCandidate.toString();
        }
        
        // If we can't find the root directory, use the current directory
        return currentDir;
    }
}
