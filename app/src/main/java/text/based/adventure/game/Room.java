package text.based.adventure.game;

import java.util.*;

public class Room {
    private String name;
    private String description;
    private Map<String, Room> exits;
    private List<Item> items;
    private Puzzle puzzle;
    private boolean visited;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.visited = false;
    }

    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        
        // Room name with enhanced formatting
        sb.append("📍 ").append(name.toUpperCase()).append(" 📍\n");
        sb.append("===================================\n");
        
        // Add atmosphere based on room name
        String atmosphere = getAtmosphericDetails();
        
        // Room description with added atmosphere
        sb.append(description);
        if (atmosphere != null) {
            sb.append("\n\n").append(atmosphere);
        }
        
        // List items with clear formatting and item type indicators
        if (!items.isEmpty()) {
            sb.append("\n\n👜 You can see:");
            for (Item item : items) {
                String itemSymbol = item.isCombinable() ? "🔄" : "🔍";
                sb.append("\n • ").append(itemSymbol).append(" ").append(item.getName());
            }
        } else {
            sb.append("\n\nThere are no notable items in this area.");
        }
        
        // Show available exits with enhanced direction indicators
        sb.append("\n\n🚪 Available exits: ");
        if (exits.isEmpty()) {
            sb.append("none (you're trapped!)");
        } else {
            List<String> exitList = new ArrayList<>(exits.keySet());
            for (int i = 0; i < exitList.size(); i++) {
                String direction = exitList.get(i);
                String dirSymbol = "";
                
                // Add direction symbol
                switch (direction) {
                    case "north": dirSymbol = "↑"; break;
                    case "south": dirSymbol = "↓"; break;
                    case "east": dirSymbol = "→"; break;
                    case "west": dirSymbol = "←"; break;
                    case "up": dirSymbol = "⤴"; break;
                    case "down": dirSymbol = "⤵"; break;
                }
                
                // Check if this exit has a puzzle
                Room exitRoom = exits.get(direction);
                if (exitRoom.getPuzzle() != null && !exitRoom.getPuzzle().isSolved()) {
                    dirSymbol += "🔒"; // Add lock symbol to indicate blocked path
                }
                
                sb.append(direction).append(" ").append(dirSymbol);
                
                if (i < exitList.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        
        // Add puzzle hint if there's an unsolved puzzle
        if (puzzle != null && !puzzle.isSolved()) {
            sb.append("\n\n🧩 PUZZLE: ").append(puzzle.getHint());
            
            // If player has made partial progress on this puzzle
            if (!puzzle.getUsedItems().isEmpty()) {
                sb.append("\nProgress: ").append(puzzle.getUsedItems().size())
                  .append("/").append(puzzle.getRequiredItems().size())
                  .append(" steps completed.");
            }
        }
        
        // Add a footer
        sb.append("\n===================================");
        
        return sb.toString();
    }
    
    // New method to add atmospheric details based on room type
    private String getAtmosphericDetails() {
        switch (name.toLowerCase()) {
            case "foyer":
                return "Moonlight casts long shadows through tall windows. Your footsteps echo quietly on the marble floor.";
            case "gallery":
                return "Famous paintings hang in dim lighting. Motion sensors glint occasionally in the corners.";
            case "vault":
                return "The reinforced walls are lined with high-security measures. The air feels colder here.";
            case "security office":
                return "Screens flicker with camera feeds. A coffee cup sits abandoned, still warm.";
            case "atrium":
                return "A glass ceiling reveals the night sky above. The floor tiles are suspiciously uniform.";
            case "rooftop":
                return "Cold night air rushes past you. The city lights spread out in all directions.";
            case "archives":
                return "Filing cabinets and document boxes line the walls. The air smells of old paper.";
            case "workshop":
                return "Tools hang neatly on wall mounts. A half-finished project sits on the workbench.";
            case "hall of sculptures":
                return "Stone and metal figures watch you silently. Their shadows seem to shift when you're not looking.";
            case "server room":
                return "Fans whir constantly as servers blink with activity. The air conditioning is almost too cold.";
            case "library":
                return "Ancient tomes and rare manuscripts rest in glass cases. The scent of leather bindings fills the air.";
            case "surveillance room":
                return "Multiple monitors display camera feeds from throughout the museum. One screen shows static.";
            case "loading dock":
                return "Large crates bear shipping labels from countries around the world. A forklift sits idle nearby.";
            case "exhibit hall":
                return "Valuable artifacts rest in secure cases. Plaques describe their historical significance.";
            case "control room":
                return "The central hub for all museum systems. From here, you can access everything.";
            default:
                return null; // No special atmosphere for other rooms
        }
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void listItems() {
        if (items.isEmpty()) {
            System.out.println("There are no items here.");
        } else {
            for (Item item : items) {
                System.out.println("- " + item);
            }
        }
    }

    public Item takeItem(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getFormattedExits() {
        if (exits.isEmpty()) {
            return "none (you're trapped!)";
        }
        
        StringBuilder sb = new StringBuilder();
        List<String> exitList = new ArrayList<>(exits.keySet());
        
        for (int i = 0; i < exitList.size(); i++) {
            String direction = exitList.get(i);
            String dirSymbol = "";
            
            // Add direction symbol
            switch (direction) {
                case "north": dirSymbol = "↑"; break;
                case "south": dirSymbol = "↓"; break;
                case "east": dirSymbol = "→"; break;
                case "west": dirSymbol = "←"; break;
                case "up": dirSymbol = "⤴"; break;
                case "down": dirSymbol = "⤵"; break;
            }
            
            sb.append(direction).append(" ").append(dirSymbol);
            
            if (i < exitList.size() - 1) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
}
