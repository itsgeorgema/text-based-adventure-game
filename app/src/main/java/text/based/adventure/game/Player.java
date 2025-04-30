package text.based.adventure.game;

import java.util.*;

public class Player {
    private Room currentRoom;
    private List<Item> inventory;

    public Player(Room startingRoom) {
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public boolean hasItem(String name) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Inventory:");
            for (Item item : inventory) {
                System.out.println("- " + item);
            }
        }
    }

    public void move(String direction) {
        Room next = currentRoom.getExit(direction);
        if (next == null) {
            System.out.println("You can't go that way.");
        } else if (next.getPuzzle() != null && !next.getPuzzle().isSolved()) {
            System.out.println("Blocked: " + next.getPuzzle().getHint());
        } else {
            setCurrentRoom(next);
            System.out.println(next.getFullDescription());
        }
    }

    public void useItem(String itemName) {
        Puzzle puzzle = currentRoom.getPuzzle();
        if (puzzle != null && !puzzle.isSolved()) {
            if (puzzle.trySolve(itemName)) {
                System.out.println(puzzle.getSolvedMessage());
            } else {
                System.out.println("That item doesn’t work here.");
            }
        } else {
            System.out.println("There is nothing to use that on.");
        }
    }

    public void takeItem(String itemName) {
        Item item = currentRoom.takeItem(itemName);
        if (item != null) {
            addItem(item);
            System.out.println("You picked up: " + item.getName());
        } else {
            System.out.println("That item is not here.");
        }
    }
}
