package text.based.adventure.game;

import java.util.*;

public class Room {
    private String name;
    private String description;
    private Map<String, Room> exits;
    private List<Item> items;
    private Puzzle puzzle;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
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
        StringBuilder sb = new StringBuilder(name + ": " + description);
        if (!items.isEmpty()) {
            sb.append("\nItems here: ");
            for (Item item : items) sb.append(item.getName()).append(", ");
        }
        sb.append("\nExits: ").append(String.join(", ", exits.keySet()));
        return sb.toString();
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
}
