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
        itemName = itemName.toLowerCase();
        if (requiredItems.contains(itemName)) {
            usedItems.add(itemName);
            if (usedItems.containsAll(requiredItems)) {
                solved = true;
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
