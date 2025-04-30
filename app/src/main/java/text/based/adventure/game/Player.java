package text.based.adventure.game;

import java.util.*;

public class Player {
    private String name;
    private Room currentRoom;
    private List<Item> inventory;

    public Player(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Inventory:");
            for (Item item : inventory) {
                System.out.println("- " + item.getName() + ": " + item.getDescription());
            }
        }
    }

    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom == null) {
            System.out.println("You can't go that way.");
        } else if (nextRoom.getPuzzle() != null && !hasItem(nextRoom.getPuzzle().getRequiredItem())) {
            System.out.println("Puzzle blocks the way: " + nextRoom.getPuzzle().getDescription());
        } else {
            setCurrentRoom(nextRoom);
            System.out.println("You move to the " + nextRoom.getName() + ".");
            System.out.println(nextRoom.getFullDescription());
        }
    }

    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public void useItem(String itemName) {
        Room room = getCurrentRoom();
        Puzzle puzzle = room.getPuzzle();

        if (puzzle != null && itemName.equalsIgnoreCase(puzzle.getRequiredItem()) && hasItem(itemName)) {
            System.out.println(puzzle.getSolvedMessage());
            room.setPuzzle(null);  // remove puzzle after solving
        } else {
            System.out.println("That item can't be used here.");
        }
    }

    public void takeItem(String itemName) {
        Item item = currentRoom.takeItem(itemName);
        if (item != null) {
            inventory.add(item);
            System.out.println("You picked up: " + item.getName());
        } else {
            System.out.println("Item not found.");
        }
    }
}