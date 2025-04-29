package text.based.adventure.game;

import java.util.*;

public class Player {
    private Room currentRoom;
    private List<Item> inventory = new ArrayList<>();

    public Player(Room room) {
        this.currentRoom = room;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public boolean hasItem(String name) {
        return inventory.stream().anyMatch(i -> i.getName().equals(name));
    }

    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("You are not carrying anything.");
        } else {
            System.out.println("Inventory: ");
            inventory.forEach(i -> System.out.println("- " + i.getName()));
        }
    }
}