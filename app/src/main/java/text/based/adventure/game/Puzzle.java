package text.based.adventure.game;

import java.util.*;

public class Puzzle {
    private Set<String> requiredItems;
    private String hint;
    private String solvedMessage;
    private boolean solved;
    private Set<String> usedItems;

    public Puzzle(Collection<String> requiredItems, String hint, String solvedMessage) {
        this.requiredItems = new HashSet<>();
        for (String item : requiredItems) {
            this.requiredItems.add(item.toLowerCase());
        }
        this.usedItems = new HashSet<>();
        this.hint = hint;
        this.solvedMessage = solvedMessage;
        this.solved = false;
    }

    public boolean trySolve(String itemName) {
        if (solved) return true;
        
        // Safety check for null item names
        if (itemName == null) return false;
        
        itemName = itemName.toLowerCase().trim();
        
        // Handle empty strings
        if (itemName.isEmpty()) return false;
        
        if (requiredItems.contains(itemName)) {
            // Check if we've already used this item
            if (usedItems.contains(itemName)) {
                System.out.println("You've already used the " + itemName + " on this puzzle.");
                return false; // Can't use the same item twice
            }
            
            usedItems.add(itemName);
            
            // Create detailed feedback on progress
            int totalItems = requiredItems.size();
            int usedCount = usedItems.size();
            
            // If all required items are used, puzzle is solved
            if (usedItems.containsAll(requiredItems)) {
                solved = true;
                return true;
            } else {
                // Return true with theatrical partial progress
                String[] progressMessages = {
                    "The " + itemName + " clicks into place. Something's happening!",
                    "You hear a mechanical sound as the " + itemName + " activates.",
                    "The " + itemName + " fits perfectly. Progress!",
                    "The " + itemName + " triggers a reaction in the mechanism."
                };
                
                // Select a message based on the item name's hash code for variety but consistency
                int messageIndex = Math.abs(itemName.hashCode() % progressMessages.length);
                System.out.println(progressMessages[messageIndex]);
                System.out.println("Progress: " + usedCount + "/" + totalItems + " steps completed.");
                
                // Give a hint about what else might be needed
                if (totalItems - usedCount == 1) {
                    System.out.println("You're very close! Just one more component needed...");
                } else {
                    System.out.println("You'll need " + (totalItems - usedCount) + " more components to solve this puzzle.");
                }
                return true;
            }
        }
        return false;
    }

    public boolean isSolved() {
        return solved;
    }

    public String getHint() {
        return hint;
    }

    public String getSolvedMessage() {
        return solvedMessage;
    }

    public Set<String> getRequiredItems() {
        return requiredItems;
    }

    public Set<String> getUsedItems() {
        return usedItems;
    }
}
