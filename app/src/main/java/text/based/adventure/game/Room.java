package text.based.adventure.game;

import java.util.*;

public class Room {
    private String name;
    private String description;
    private Map<String, Room> exits = new HashMap<>();
    private List<Item> items = new ArrayList<>();
    private Puzzle puzzle;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setExit(String direction, Room room) {
        exits.put(direction, room);
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder(name + ": " + description);
        if (!items.isEmpty()) {
            sb.append(" Items here: ");
            items.forEach(i -> sb.append(i.getName()).append(", "));
        }
        sb.append(" Exits: ").append(exits.keySet());
        return sb.toString();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item takeItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
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
