package text.based.adventure.game;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public String getExitString() {
        return "Exits: " + String.join(", ", exits.keySet());
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void listItems() {
        if (items.isEmpty()) {
            System.out.println("No items here.");
        } else {
            for (Item item : items) {
                System.out.println("- " + item.getName() + ": " + item.getDescription());
            }
        }
    }

    public Item takeItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
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

    public String getFullDescription() {
        return name + ": " + description + "\n" + getExitString();
    }
}
