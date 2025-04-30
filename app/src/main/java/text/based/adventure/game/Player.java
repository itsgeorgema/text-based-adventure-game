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
        return inventory.stream().anyMatch(i -> i.getName().equalsIgnoreCase(name));
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

    public void inspectItem(String name) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                System.out.println(item.getDescription());
                return;
            }
        }
        System.out.println("You don't have that item.");
    }

    public void combineItems(String name1, String name2) {
        Item first = null;
        Item second = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name1)) first = item;
            if (item.getName().equalsIgnoreCase(name2)) second = item;
        }
        if (first != null && second != null && first.isCombinable() && second.isCombinable()) {
            inventory.remove(first);
            inventory.remove(second);
            Item combined = new Item(name1 + "+" + name2, "Combined form of " + name1 + " and " + name2);
            inventory.add(combined);
            System.out.println("You combined the items into: " + combined.getName());
        } else {
            System.out.println("You can't combine those items.");
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
            if (!next.isVisited()) {
                next.setVisited(true);
                System.out.println(next.getFullDescription());
            } else {
                System.out.println("You return to the " + next.getName());
            }
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
