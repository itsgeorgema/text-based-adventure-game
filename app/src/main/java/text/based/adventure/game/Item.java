package text.based.adventure.game;

public class Item {
    private String name;
    private String description;
    private boolean combinable;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
        this.combinable = false;
    }

    public Item(String name, String description, boolean combinable) {
        this.name = name;
        this.description = description;
        this.combinable = combinable;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCombinable() {
        return combinable;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
