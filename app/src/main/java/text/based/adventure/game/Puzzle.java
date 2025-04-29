package text.based.adventure.game;

public class Puzzle {
    private String solutionItem;
    private String hint;
    private boolean solved = false;

    public Puzzle(String solutionItem, String hint) {
        this.solutionItem = solutionItem;
        this.hint = hint;
    }

    public boolean trySolve(String itemName) {
        if (itemName.equals(solutionItem)) {
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
}


