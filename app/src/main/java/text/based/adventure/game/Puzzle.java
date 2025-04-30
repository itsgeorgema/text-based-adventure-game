package text.based.adventure.game;

public class Puzzle {
    private String solutionItem;
    private String hint;
    private String solvedMessage;
    private boolean solved;

    public Puzzle(String solutionItem, String hint, String solvedMessage) {
        this.solutionItem = solutionItem;
        this.hint = hint;
        this.solvedMessage = solvedMessage;
        this.solved = false;
    }

    public boolean trySolve(String itemName) {
        if (itemName.equalsIgnoreCase(solutionItem)) {
            solved = true;
            return true;
        }
        return false;
    }

    public boolean isSolved() {
        return solved;
    }

    public String getHint() {
        return hint;
    }

    public String getRequiredItem() {
        return solutionItem;
    }

    public String getSolvedMessage() {
        return solvedMessage;
    }
} 
