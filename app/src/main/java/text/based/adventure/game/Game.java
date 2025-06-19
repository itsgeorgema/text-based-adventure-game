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
        
        System.out.println(
            "===========================" + "\n" +
            "  ART HEIST ADVENTURE" + "\n" +
            "===========================" + "\n" +
            "\n" +
            "You are an elite infiltrator hired to break into the most secure art museum in the world. You are breaking in during the middle of the night when the museum is closed to maximize stealth." + "\n" +
            "\n" +
            "🎯 Objective:" + "\n" +
            "Navigate undetected through rooms, avoid advanced security systems, and retrieve the priceless artifact locked behind a multi-layered vault system." + "\n" +
            "\n" +
            "📍 Starting Location:" + "\n" +
            "You are in the Foyer — the marble-floored entry of the museum. Use the blueprint in your inventory to plan your movements." + "\n" +
            "\n" +
            "🧩 What to Do:" + "\n" +
            "- Explore rooms using commands like 'go north', 'take blueprint', or 'inspect item'" + "\n" +
            "- Use or combine the right items to bypass puzzles and unlock new rooms" + "\n" +
            "- Solve layered puzzles to reach the final control room" + "\n" +
            "- Type 'help' to see all available commands" + "\n" +
            "- Type 'quit' anytime to exit the game" + "\n" +
            "\n" +
            "Good luck. The museum won't be closed for long..." 
        );
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
        System.out.println("\n" + player.getCurrentRoom().getBriefDescription());
        
        // Give an initial nudge to the player
        System.out.println("\n💡 Hint: Try using 'look' to examine your surroundings in detail and 'inventory' to see what you have.");
        
        // Main game loop
        boolean gameRunning = true;
        while (gameRunning) {
            try {
                System.out.print("\n> ");
                
                // Make sure we have input available
                String input;
                try {
                    // Only recreate scanner if it's null to prevent closing System.in too often
                    if (scanner == null) {
                        scanner = new Scanner(System.in);
                    }
                    
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
                    try {
                        // Use existing scanner instead of creating a new one that closes System.in
                        String confirm = scanner.nextLine().trim().toLowerCase();
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
                    
                    // No longer automatically win on reaching the Control Room
                    if (player.getCurrentRoom().getName().equals("Control Room")) {
                        System.out.println("\nYou've reached the Control Room, but the security system is still active.");
                        System.out.println("You need to use the master override device to complete your heist.");
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

        // Connect rooms in a logical layout - Main Floor Level (horizontal)
        // Ground floor main path: Foyer → Gallery → Security Office → Atrium → Archives → Workshop → Hall of Sculptures → Server Room
        rooms.get(0).setExit("north", rooms.get(1));     // Foyer → Gallery
        rooms.get(1).setExit("south", rooms.get(0));     // Gallery → Foyer
        rooms.get(1).setExit("west", rooms.get(3));      // Gallery → Security Office
        rooms.get(3).setExit("east", rooms.get(1));      // Security Office → Gallery
        rooms.get(3).setExit("north", rooms.get(4));     // Security Office → Atrium
        rooms.get(4).setExit("south", rooms.get(3));     // Atrium → Security Office
        rooms.get(4).setExit("east", rooms.get(6));      // Atrium → Archives
        rooms.get(6).setExit("west", rooms.get(4));      // Archives → Atrium
        rooms.get(6).setExit("east", rooms.get(7));      // Archives → Workshop
        rooms.get(7).setExit("west", rooms.get(6));      // Workshop → Archives
        rooms.get(7).setExit("east", rooms.get(8));      // Workshop → Hall of Sculptures
        rooms.get(8).setExit("west", rooms.get(7));      // Hall of Sculptures → Workshop

        // Secondary connections from main floor
        rooms.get(1).setExit("east", rooms.get(2));      // Gallery → Vault
        rooms.get(2).setExit("west", rooms.get(1));      // Vault → Gallery
        rooms.get(8).setExit("north", rooms.get(9));     // Hall of Sculptures → Server Room (locked until puzzle solved)
        rooms.get(9).setExit("south", rooms.get(8));     // Server Room → Hall of Sculptures

        // Upper level access from Vault
        rooms.get(2).setExit("north", rooms.get(5));     // Vault → Rooftop (access point to upper level)
        rooms.get(5).setExit("south", rooms.get(2));     // Rooftop → Vault

        // Upper floor layout: Rooftop → Library → Surveillance Room → Loading Dock → Exhibit Hall → Break Room
        rooms.get(5).setExit("west", rooms.get(10));     // Rooftop → Library
        rooms.get(10).setExit("east", rooms.get(5));     // Library → Rooftop
        rooms.get(10).setExit("west", rooms.get(11));    // Library → Surveillance Room
        rooms.get(11).setExit("east", rooms.get(10));    // Surveillance Room → Library
        rooms.get(11).setExit("west", rooms.get(12));    // Surveillance Room → Loading Dock
        rooms.get(12).setExit("east", rooms.get(11));    // Loading Dock → Surveillance Room
        rooms.get(12).setExit("west", rooms.get(13));    // Loading Dock → Exhibit Hall
        rooms.get(13).setExit("east", rooms.get(12));    // Exhibit Hall → Loading Dock
        rooms.get(13).setExit("west", rooms.get(14));    // Exhibit Hall → Break Room
        rooms.get(14).setExit("east", rooms.get(13));    // Break Room → Exhibit Hall

        // Vertical tower (Final Path): Hall of Sculptures → Storage Room → Director's Office → IT Closet → Control Room
        rooms.get(8).setExit("south", rooms.get(15));    // Hall of Sculptures → Storage Room
        rooms.get(15).setExit("north", rooms.get(8));    // Storage Room → Hall of Sculptures
        rooms.get(15).setExit("south", rooms.get(16));   // Storage Room → Director's Office
        rooms.get(16).setExit("north", rooms.get(15));   // Director's Office → Storage Room
        rooms.get(16).setExit("south", rooms.get(18));   // Director's Office → IT Closet
        rooms.get(18).setExit("north", rooms.get(16));   // IT Closet → Director's Office
        rooms.get(18).setExit("south", rooms.get(19));   // IT Closet → Control Room (locked until puzzle solved)
        rooms.get(19).setExit("north", rooms.get(18));   // Control Room → IT Closet

        // Clear any existing items
        for (Room room : rooms) {
            room.clearItems();
        }

        // Place items strategically throughout the museum
        // Ground floor items (easily accessible)
        rooms.get(0).addItem(new Item("blueprint", "Museum layout map"));
        rooms.get(0).addItem(new Item("emp device", "Disables electronic devices", true));
        
        rooms.get(1).addItem(new Item("laser mirror", "Redirects laser beams"));
        
        rooms.get(2).addItem(new Item("vault code", "A 4-digit combination: 7394"));
        
        rooms.get(3).addItem(new Item("infrared goggles", "Reveals invisible motion sensors"));
        
        rooms.get(4).addItem(new Item("pressure plate", "Bypasses weight sensors", true));
        
        rooms.get(6).addItem(new Item("glass cutter", "Cuts through reinforced glass", true));
        
        rooms.get(7).addItem(new Item("thermal drill", "For cutting through metal", true));
        rooms.get(7).addItem(new Item("power cell", "Powers electronic tools", true));
        
        rooms.get(8).addItem(new Item("admin password", "Access code: ROOT_ADMIN_2024"));
        
        rooms.get(9).addItem(new Item("server access card", "Grants system privileges"));
        
        // Upper floor items (require vault access)
        rooms.get(5).addItem(new Item("zipline hook", "For emergency escape routes"));
        
        rooms.get(10).addItem(new Item("coded ledger", "Contains cipher keys"));
        
        rooms.get(11).addItem(new Item("camera loop usb", "Loops security footage"));
        
        rooms.get(12).addItem(new Item("override chip", "Bypasses security systems", true));
        
        rooms.get(13).addItem(new Item("night vision goggles", "For dark environments"));
        
        rooms.get(14).addItem(new Item("keycard beta", "Secondary access card"));
        
        // Final tower items (advanced area)
        rooms.get(15).addItem(new Item("biometric scanner", "Reads fingerprints"));
        
        rooms.get(16).addItem(new Item("director's ring", "Master authentication key"));
        
        // Move the master override to the IT Closet - player has to take it and use it in Control Room
        rooms.get(18).addItem(new Item("master override", "Universal access device to complete the heist", true));

        // Setup logical puzzles with accessible items
        // Vault puzzle - requires items from ground floor
        rooms.get(2).setPuzzle(new Puzzle(
            List.of("vault code", "powered drill"), 
            "The vault door has both a code lock and reinforced bolts.",
            "The code unlocks the mechanism, and the drill cuts through the bolts."
        ));
        
        // Server Room puzzle - requires items from main path
        rooms.get(9).setPuzzle(new Puzzle(
            List.of("admin password", "server access card"), 
            "The server requires both admin credentials and physical access.",
            "System authentication successful. Access granted."
        ));
        
        // Director's Office puzzle - requires item from same room
        rooms.get(16).setPuzzle(new Puzzle(
            List.of("director's ring"), 
            "A hidden biometric safe requires the director's authentication.",
            "The safe recognizes the director's ring and unlocks silently."
        ));
        
        // Control Room no longer has a puzzle that auto-solves
        // Instead, the player needs to use the master override item explicitly

        // Initialize player in the Foyer
        player = new Player(rooms.get(0));
        // Mark the starting room as visited
        rooms.get(0).setVisited(true);
        
        // Player starts with basic infiltration gear
        player.addItem(new Item("lock picks", "Basic lock manipulation tools"));
        player.addItem(new Item("flashlight", "Illuminates dark areas"));
    }
    
    // New method to display a map of visited rooms
    private void showMap() {
        System.out.println("\n📍 MUSEUM MAP 📍");
        System.out.println("(Visited rooms are marked with ✓)");
        System.out.println("(YOUR CURRENT LOCATION is marked with 🔸)");
        System.out.println("-----------------------------------------");
        
        // Create a map that matches the actual room connections
        String[][] mapLines = {
            {"", "", "", "", "", "", "", "", "", "", "", "", "[UPPER FLOOR]", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "     |  ", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "CONTROL ROOM", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "   ↑   ", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "IT CLOSET", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "   ↑   ", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "DIRECTOR'S OFFICE", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "   ↑   ", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "STORAGE ROOM", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "   ↑   ", "", ""},
            {"[UPPER LEVEL]", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→"},
            {"ROOFTOP", "←", "LIBRARY", "←", "SURVEILLANCE", "←", "LOADING DOCK", "←", "EXHIBIT HALL", "←", "BREAK ROOM", "", "", "", ""},
            {"   ↑   ", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"VAULT", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"   ↑   ", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"FOYER", "→", "GALLERY", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "   ↑   ", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"[GROUND FLOOR]", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→", "→"},
            {"", "", "SECURITY", "→", "ATRIUM", "→", "ARCHIVES", "→", "WORKSHOP", "→", "HALL OF", "→", "SERVER ROOM", "", ""},
            {"", "", "OFFICE", "", "", "", "", "", "", "", "SCULPTURES", "", "", "", ""},
        };

        // First mark visited rooms
        for (Room room : rooms) {
            if (room.isVisited() && room != player.getCurrentRoom()) {
                updateMapCell(mapLines, room.getName(), "✓");
            }
        }
        
        // Then mark current room with a player symbol
        updateMapCell(mapLines, player.getCurrentRoom().getName(), "🔸");
        
        // Display the map
        for (String[] line : mapLines) {
            System.out.println(String.join(" ", line));
        }
        
        System.out.println("-----------------------------------------");
    }
    
    // Helper method to update a map cell with a marker
    private void updateMapCell(String[][] map, String roomName, String marker) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                String cell = map[i][j];
                // Only check non-empty cells that aren't arrows
                if (cell != null && !cell.isEmpty() && 
                    !cell.equals("→") && !cell.equals("←") && 
                    !cell.equals("↑") && !cell.equals("↓")) {
                    
                    // Normalize strings for comparison
                    String normalizedCell = cell.toUpperCase().replace(" ", "");
                    String normalizedRoomName = roomName.toUpperCase().replace(" ", "");
                    
                    // Handle specific room matches
                    if ((normalizedCell.equals("FOYER") && normalizedRoomName.equals("FOYER")) ||
                        (normalizedCell.equals("GALLERY") && normalizedRoomName.equals("GALLERY")) ||
                        (normalizedCell.equals("VAULT") && normalizedRoomName.equals("VAULT")) ||
                        (normalizedCell.equals("SECURITY") && normalizedRoomName.equals("SECURITYOFFICE")) ||
                        (normalizedCell.equals("OFFICE") && normalizedRoomName.equals("SECURITYOFFICE")) ||
                        (normalizedCell.equals("ATRIUM") && normalizedRoomName.equals("ATRIUM")) ||
                        (normalizedCell.equals("ARCHIVES") && normalizedRoomName.equals("ARCHIVES")) ||
                        (normalizedCell.equals("WORKSHOP") && normalizedRoomName.equals("WORKSHOP")) ||
                        (normalizedCell.equals("HALLOF") && normalizedRoomName.equals("HALLOFSCULPTURES")) ||
                        (normalizedCell.equals("SCULPTURES") && normalizedRoomName.equals("HALLOFSCULPTURES")) ||
                        (normalizedCell.equals("SERVERROOM") && normalizedRoomName.equals("SERVERROOM")) ||
                        (normalizedCell.equals("ROOFTOP") && normalizedRoomName.equals("ROOFTOP")) ||
                        (normalizedCell.equals("LIBRARY") && normalizedRoomName.equals("LIBRARY")) ||
                        (normalizedCell.equals("SURVEILLANCE") && normalizedRoomName.equals("SURVEILLANCEROOM")) ||
                        (normalizedCell.equals("LOADINGDOCK") && normalizedRoomName.equals("LOADINGDOCK")) ||
                        (normalizedCell.equals("EXHIBITHALL") && normalizedRoomName.equals("EXHIBITHALL")) ||
                        (normalizedCell.equals("BREAKROOM") && normalizedRoomName.equals("BREAKROOM")) ||
                        (normalizedCell.equals("STORAGEROOM") && normalizedRoomName.equals("STORAGEROOM")) ||
                        (normalizedCell.equals("DIRECTOR'SOFFICE") && normalizedRoomName.equals("DIRECTOR'SOFFICE")) ||
                        (normalizedCell.equals("ITCLOSET") && normalizedRoomName.equals("ITCLOSET")) ||
                        (normalizedCell.equals("CONTROLROOM") && normalizedRoomName.equals("CONTROLROOM"))) {
                        
                        // For current room (🔸), add marker
                        if (marker.equals("🔸")) {
                            map[i][j] = cell + " " + marker;
                        } else {
                            map[i][j] = cell + " " + marker;
                        }
                        return;
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
        
        // If no puzzle in current room, provide guidance based on room and progress
        switch(roomName) {
            case "Foyer":
                System.out.println("Start by taking the blueprint and emp device, then go north to the Gallery.");
                break;
            case "Gallery":
                System.out.println("From here you can go east to the Vault or west to the Security Office. Take the laser mirror first!");
                break;
            case "Security Office":
                System.out.println("Get the infrared goggles here, then go north to the Atrium to continue the main path.");
                break;
            case "Atrium":
                System.out.println("Take the pressure plate, then go east to Archives to progress through the museum.");
                break;
            case "Vault":
                System.out.println("This vault needs both a code and a powered drill to open. Combine thermal drill with power cell first!");
                break;
            case "Archives":
                System.out.println("Get the glass cutter here, then continue east to the Workshop.");
                break;
            case "Workshop":
                System.out.println("Take both the thermal drill and power cell. Combine them to create a 'powered drill'!");
                break;
            case "Hall of Sculptures":
                System.out.println("Take the admin password. You can go north to Server Room or south to Storage Room.");
                break;
            case "Server Room":
                System.out.println("This room needs admin password and server access card to unlock its secrets.");
                break;
            case "Rooftop":
                System.out.println("You're on the upper level! Go west to Library to explore the upper floor.");
                break;
                            case "Control Room":
                    System.out.println("You need to use the master override to complete the heist!");
                    break;
            default:
                // Generic hint about exploring and taking items
                System.out.println("Look around for useful items and check all available exits.");
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
