package text.based.adventure.game;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;

public class Game {
    private Player player;
    private Scanner scanner;
    private List<Room> rooms;
    private FuzzyMatcher fuzzyMatcher;

    public void start() {
        setupWorld();
        scanner = new Scanner(System.in);
        System.out.println("\nWelcome to the Art Heist Adventure! Type 'help' for commands.");
        ArrayList<String> commandList = new ArrayList<>();

        try {
            commandList.addAll(Files.readAllLines(Paths.get("all_game_commands.txt")));
        } catch (IOException e) {
            System.out.println("Error loading commands:");
            e.printStackTrace();
        }
    
        fuzzyMatcher = new FuzzyMatcher(commandList);
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim().toLowerCase();

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
            } else {
                String corrected = fuzzyMatcher.getBestMatch(input);
                if (corrected == null) {
                    System.out.println("I didn't understand that. Try 'help' to see commands.");
                    continue;
                } else if (!corrected.equals(input)) {
                    System.out.println("Did you mean: '" + corrected + "'? [y/N]");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        input = corrected;
                    } else {
                        continue;
                    }
                }}

            // Existing command logic below
            if (input.equals("quit")) {
                System.out.println("Thanks for playing!");
                break;
            } else if (input.equals("look")) {
                System.out.println(player.getCurrentRoom().getFullDescription());
            } else if (input.equals("items")) {
                player.getCurrentRoom().listItems();
            } else if (input.equals("inventory")) {
                player.showInventory();
            } else if (input.equals("help")) {
                System.out.println("Commands: go [direction], look, items, take [item], use [item], inspect [item], combine [item1] with [item2], inventory, help, quit");
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
            } else {
                System.out.println("Unknown command.");
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
            {"vault fingerprint", "Synthetic mold of director’s thumb"},
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
}
