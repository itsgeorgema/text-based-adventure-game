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
            System.out.println("\n🎒 YOUR INVENTORY IS EMPTY");
            System.out.println("===================================");
            System.out.println("Try exploring the museum and picking up items with 'take [item]'.");
            System.out.println("Items are crucial for solving puzzles and advancing through secured areas.");
        } else {
            System.out.println("\n🎒 YOUR INVENTORY");
            System.out.println("===================================");
            
            // Display combinable items first
            List<Item> combinableItems = new ArrayList<>();
            List<Item> regularItems = new ArrayList<>();
            
            for (Item item : inventory) {
                if (item.isCombinable()) {
                    combinableItems.add(item);
                } else {
                    regularItems.add(item);
                }
            }
            
            // Display combinable items first with special marking and count
            if (!combinableItems.isEmpty()) {
                System.out.println("🔄 COMBINABLE ITEMS (" + combinableItems.size() + "):");
                System.out.println("-----------------------------------");
                for (Item item : combinableItems) {
                    System.out.println("🔄 " + item.getName() + " - " + item.getDescription());
                }
            }
            
            // Display regular items with count
            if (!regularItems.isEmpty()) {
                if (!combinableItems.isEmpty()) {
                    System.out.println("\n🔍 OTHER ITEMS (" + regularItems.size() + "):");
                    System.out.println("-----------------------------------");
                }
                for (Item item : regularItems) {
                    System.out.println("🔍 " + item.getName() + " - " + item.getDescription());
                }
            }
            
            System.out.println("===================================");
            
            // More interactive hints based on inventory
            System.out.println("📖 ACTIONS:");
            System.out.println("• Use 'inspect [item]' for detailed examination");
            
            if (!combinableItems.isEmpty() && combinableItems.size() >= 2) {
                System.out.println("• Try 'combine [item1] with [item2]' with 🔄 items");
                System.out.println("  (Combining items can create powerful tools for puzzles)");
            }
            
            // Contextual hint if inventory has items that can be used nearby
            Room currentRoom = getCurrentRoom();
            boolean foundUsecase = false;
            
            // Check current room puzzle
            Puzzle roomPuzzle = currentRoom.getPuzzle();
            if (roomPuzzle != null && !roomPuzzle.isSolved()) {
                for (Item item : inventory) {
                    if (roomPuzzle.getRequiredItems().contains(item.getName().toLowerCase()) &&
                        !roomPuzzle.getUsedItems().contains(item.getName().toLowerCase())) {
                        System.out.println("\n💡 HINT: One of your items might help with a puzzle in this room.");
                        foundUsecase = true;
                        break;
                    }
                }
            }
            
            // Check adjacent rooms if no usecase found in current room
            if (!foundUsecase) {
                for (Room adjacentRoom : currentRoom.getExits().values()) {
                    Puzzle adjacentPuzzle = adjacentRoom.getPuzzle();
                    if (adjacentPuzzle != null && !adjacentPuzzle.isSolved()) {
                        for (Item item : inventory) {
                            if (adjacentPuzzle.getRequiredItems().contains(item.getName().toLowerCase())) {
                                System.out.println("\n💡 HINT: Something in your inventory might be useful in a nearby room.");
                                foundUsecase = true;
                                break;
                            }
                        }
                        if (foundUsecase) break;
                    }
                }
            }
        }
    }

    public void inspectItem(String name) {
        // First check if the item is in the current room
        Item roomItem = null;
        for (Item item : currentRoom.getItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                roomItem = item;
                break;
            }
        }
        
        if (roomItem != null) {
            System.out.println("\n" + (roomItem.isCombinable() ? "� " : "�🔍 ") + roomItem.getName().toUpperCase() + " (in room)");
            System.out.println("===================================");
            System.out.println(roomItem.getDescription());
            
            System.out.println("\n📖 ITEM ANALYSIS:");
            if (roomItem.isCombinable()) {
                System.out.println("• This item can be combined with another compatible item");
            }
            
            // Add thematic description based on item name
            addThematicItemDetails(roomItem.getName());
            
            System.out.println("\n⚡ ACTIONS:");
            System.out.println("• Take this item with 'take " + roomItem.getName() + "'");
            return;
        }
        
        // Check if the item is in inventory
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                String itemType = item.isCombinable() ? "🔄" : "🔎";
                
                System.out.println("\n" + itemType + " " + item.getName().toUpperCase() + " (in inventory)");
                System.out.println("===================================");
                System.out.println(item.getDescription());
                
                System.out.println("\n📖 ITEM ANALYSIS:");
                
                // Determine if the item is part of a solved puzzle
                boolean usedInSolvedPuzzle = false;
                boolean canBeUsedNow = false;
                Room puzzleRoom = null;
                
                // Check if this item is needed for any puzzle
                for (Room room : getAllRooms()) {
                    Puzzle puzzle = room.getPuzzle();
                    if (puzzle != null) {
                        // Check if this item was used in this puzzle
                        if (puzzle.isSolved() && puzzle.getRequiredItems().contains(item.getName().toLowerCase()) &&
                            puzzle.getUsedItems().contains(item.getName().toLowerCase())) {
                            usedInSolvedPuzzle = true;
                            break;
                        }
                        
                        // Check if can be used in current room
                        if (!puzzle.isSolved() && puzzle.getRequiredItems().contains(item.getName().toLowerCase()) &&
                            !puzzle.getUsedItems().contains(item.getName().toLowerCase())) {
                            if (room == currentRoom) {
                                canBeUsedNow = true;
                                puzzleRoom = room;
                                break;
                            } else if (currentRoom.getExits().containsValue(room)) {
                                puzzleRoom = room;
                            }
                        }
                    }
                }
                
                // Add hints based on puzzle relationship
                if (usedInSolvedPuzzle) {
                    System.out.println("• You already used this item to solve a puzzle");
                } else if (canBeUsedNow) {
                    System.out.println("• This item looks useful for something in this room");
                    System.out.println("• Try 'use " + item.getName() + "' here");
                } else if (puzzleRoom != null) {
                    String direction = "nearby";
                    for (Map.Entry<String, Room> exit : currentRoom.getExits().entrySet()) {
                        if (exit.getValue() == puzzleRoom) {
                            direction = exit.getKey();
                            break;
                        }
                    }
                    System.out.println("• This item might help with a puzzle to the " + direction);
                }
                
                // Add information about combinability
                if (item.isCombinable()) {
                    System.out.println("• This item can be combined with another compatible item");
                    
                    // Get other combinable items
                    List<Item> otherCombinables = inventory.stream()
                        .filter(i -> i != item && i.isCombinable())
                        .toList();
                    
                    if (!otherCombinables.isEmpty()) {
                        if (otherCombinables.size() <= 3) {
                            System.out.println("• You could try combining it with: " + 
                                otherCombinables.stream()
                                    .map(Item::getName)
                                    .collect(java.util.stream.Collectors.joining(", ")));
                        } else {
                            System.out.println("• You have " + otherCombinables.size() + " other combinable items to try with this");
                        }
                    }
                }
                
                // Add thematic description
                addThematicItemDetails(item.getName());
                
                System.out.println("\n⚡ ACTIONS:");
                System.out.println("• Use this item with 'use " + item.getName() + "'");
                if (item.isCombinable() && hasCombinableItems()) {
                    System.out.println("• Combine with another item using 'combine " + item.getName() + " with [item]'");
                }
                
                return;
            }
        }
        
        System.out.println("You don't see any '" + name + "' here or in your inventory.");
        
        // Suggest similar items if available
        List<String> inventoryItemNames = inventory.stream().map(Item::getName).toList();
        List<String> roomItemNames = currentRoom.getItems().stream().map(Item::getName).toList();
        
        // Check for similar item names to help the player
        String closestMatch = findClosestMatch(name, inventoryItemNames, roomItemNames);
        if (closestMatch != null) {
            System.out.println("Did you mean '" + closestMatch + "'?");
        }
    }
    
    // Helper method to add thematic details to item inspections
    private void addThematicItemDetails(String itemName) {
        // Add custom thematic details based on item name
        switch (itemName.toLowerCase()) {
            case "emp device":
                System.out.println("• Specialized device that emits an electromagnetic pulse");
                System.out.println("• Can temporarily disable electronic security systems");
                break;
            case "laser mirror":
                System.out.println("• High-polished reflective surface perfect for redirecting laser beams");
                System.out.println("• Museum security often uses laser grids that this could manipulate");
                break;
            case "glass cutter":
                System.out.println("• Diamond-tipped precision tool for clean cuts through glass");
                System.out.println("• Leaves minimal evidence when used properly");
                break;
            case "blueprint":
                System.out.println("• Detailed floor plans showing the museum layout");
                System.out.println("• Reveals some hidden passages not on public maps");
                break;
            case "thermal drill":
                System.out.println("• Industrial grade tool that generates intense heat");
                System.out.println("• Can breach reinforced materials with enough time");
                break;
            case "admin password":
                System.out.println("• High-level security credentials written on a small card");
                System.out.println("• Could grant access to restricted systems");
                break;
            case "director's ring":
                System.out.println("• Ornate gold ring belonging to the museum director");
                System.out.println("• Possibly contains hidden microchips or security tokens");
                break;
            case "master override":
                System.out.println("• Advanced security bypass tool");
                System.out.println("• Ultimate access key for the entire building");
                break;
        }
    }
    
    // Helper method to get all rooms in the game
    private List<Room> getAllRooms() {
        // This should be replaced with a proper implementation
        // that actually gets all rooms from the game
        List<Room> visited = new ArrayList<>();
        visited.add(currentRoom);
        
        // Add adjacent rooms
        visited.addAll(currentRoom.getExits().values());
        
        return visited;
    }
    
    // Helper method to check if player has multiple combinable items
    private boolean hasCombinableItems() {
        int count = 0;
        for (Item item : inventory) {
            if (item.isCombinable()) {
                count++;
                if (count >= 2) return true;
            }
        }
        return false;
    }
    
    // Find the closest matching item name from available items
    private String findClosestMatch(String input, List<String> inventoryItems, List<String> roomItems) {
        List<String> allItems = new ArrayList<>();
        allItems.addAll(inventoryItems);
        allItems.addAll(roomItems);
        
        String bestMatch = null;
        int lowestDistance = Integer.MAX_VALUE;
        
        for (String itemName : allItems) {
            int distance = levenshteinDistance(input.toLowerCase(), itemName.toLowerCase());
            if (distance < lowestDistance && distance <= 3) { // Only suggest if reasonably close
                lowestDistance = distance;
                bestMatch = itemName;
            }
        }
        
        return bestMatch;
    }
    
    // Simple Levenshtein distance algorithm for string similarity
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(dp[i - 1][j] + 1, // deletion
                          Math.min(dp[i][j - 1] + 1, // insertion
                                  dp[i - 1][j - 1] + cost)); // substitution
            }
        }
        
        return dp[a.length()][b.length()];
    }

    public void combineItems(String name1, String name2) {
        Item first = null;
        Item second = null;
        
        // Find both items in inventory
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(name1)) first = item;
            if (item.getName().equalsIgnoreCase(name2)) second = item;
        }
        
        // Check if both items exist and are combinable
        if (first == null || second == null) {
            if (first == null && second == null) {
                System.out.println("You don't have either of those items in your inventory.");
            } else if (first == null) {
                System.out.println("You don't have '" + name1 + "' in your inventory.");
            } else {
                System.out.println("You don't have '" + name2 + "' in your inventory.");
            }
            return;
        }
        
        if (!first.isCombinable() || !second.isCombinable()) {
            if (!first.isCombinable() && !second.isCombinable()) {
                System.out.println("Neither of these items can be combined with other items.");
            } else if (!first.isCombinable()) {
                System.out.println("The " + first.getName() + " can't be combined with other items.");
            } else {
                System.out.println("The " + second.getName() + " can't be combined with other items.");
            }
            return;
        }
        
        // Special combinations with unique results and descriptions
        String combinedName;
        String combinedDescription;
        boolean specialCombination = true;
        
        if ((first.getName().equalsIgnoreCase("emp device") && second.getName().equalsIgnoreCase("decoy badge")) ||
            (second.getName().equalsIgnoreCase("emp device") && first.getName().equalsIgnoreCase("decoy badge"))) {
            
            combinedName = "access override";
            combinedDescription = "Advanced security bypass that can fool and disable electronic locks simultaneously";
            
        } else if ((first.getName().equalsIgnoreCase("thermal drill") && second.getName().equalsIgnoreCase("power cell")) ||
                  (second.getName().equalsIgnoreCase("thermal drill") && first.getName().equalsIgnoreCase("power cell"))) {
            
            combinedName = "powered drill";
            combinedDescription = "High-powered cutting tool capable of breaching any physical security";
            
        } else if ((first.getName().equalsIgnoreCase("glass cutter") && second.getName().equalsIgnoreCase("adhesive pad")) ||
                  (second.getName().equalsIgnoreCase("glass cutter") && first.getName().equalsIgnoreCase("adhesive pad"))) {
            
            combinedName = "precision cutter";
            combinedDescription = "Ultra-precise glass removal tool that leaves no traces";
            
        } else {
            // Generic combination
            combinedName = first.getName() + "+" + second.getName();
            combinedDescription = "Combined form of " + first.getName() + " and " + second.getName();
            specialCombination = false;
        }
        
        // Remove original items and add combined item
        inventory.remove(first);
        inventory.remove(second);
        Item combined = new Item(combinedName, combinedDescription);
        inventory.add(combined);
        
        // Show a more detailed and atmospheric message for special combinations
        if (specialCombination) {
            System.out.println("\n🔧 ITEMS COMBINED SUCCESSFULLY 🔧");
            System.out.println("===================================");
            System.out.println("You carefully fit the " + first.getName() + " and " + second.getName() + " together.");
            System.out.println("After some precise adjustments, they lock into place with a satisfying click.");
            System.out.println("\nYou created: " + combined.getName().toUpperCase());
            System.out.println(combinedDescription);
            System.out.println("===================================");
            System.out.println("This new tool has been added to your inventory.");
        } else {
            System.out.println("\nYou combined the items into: " + combined.getName());
            System.out.println("It's not clear how useful this will be, but it's now in your inventory.");
        }
    }

    public void move(String direction) {
        direction = direction.toLowerCase().trim();
        Room next = currentRoom.getExit(direction);
        
        if (next == null) {
            // Add more descriptive and atmospheric feedback based on direction
            String feedback;
            switch (direction) {
                case "north":
                    feedback = "The path north is blocked. A solid wall prevents further progress.";
                    break;
                case "south":
                    feedback = "You can't go south from here. The way is obstructed.";
                    break;
                case "east":
                    feedback = "The eastern passage is inaccessible. You'll need to find another route.";
                    break;
                case "west":
                    feedback = "The western path is sealed off. No exit that way.";
                    break;
                case "up":
                    feedback = "There's no way to climb up from here. The ceiling is too high.";
                    break;
                case "down":
                    feedback = "You can't descend from this location. The floor is solid.";
                    break;
                default:
                    feedback = "You can't go that way. Try another direction.";
            }
            System.out.println(feedback);
            
            // Suggest available directions with direction symbols for better navigation
            if (!currentRoom.getExits().isEmpty()) {
                System.out.println("\nAvailable directions:");
                for (String exit : currentRoom.getExits().keySet()) {
                    String symbol = "";
                    switch (exit) {
                        case "north": symbol = "↑"; break;
                        case "south": symbol = "↓"; break;
                        case "east": symbol = "→"; break;
                        case "west": symbol = "←"; break;
                        case "up": symbol = "⤴"; break;
                        case "down": symbol = "⤵"; break;
                    }
                    System.out.println("- " + exit + " " + symbol);
                }
            }
        } else if (next.getPuzzle() != null && !next.getPuzzle().isSolved()) {
            System.out.println("\n🚫 Blocked: " + next.getPuzzle().getHint());
            // Add hints about puzzle requirements
            Set<String> requiredItems = next.getPuzzle().getRequiredItems();
            Set<String> usedItems = next.getPuzzle().getUsedItems();
            
            if (!requiredItems.isEmpty()) {
                // Check if player has any of the required items
                boolean hasRequiredItem = false;
                for (String itemName : requiredItems) {
                    if (hasItem(itemName) && !usedItems.contains(itemName)) {
                        System.out.println("\n💡 Hint: You might be able to use the " + itemName + " in your inventory here.");
                        hasRequiredItem = true;
                        break;
                    }
                }
                
                if (!hasRequiredItem) {
                    System.out.println("\n💡 You need to find the right tools to proceed this way.");
                }
                
                if (!usedItems.isEmpty()) {
                    System.out.println("Progress: " + usedItems.size() + "/" + requiredItems.size() + " requirements met.");
                }
            }
        } else {
            setCurrentRoom(next);
            
            // Give different descriptions based on whether room has been visited
            if (!next.isVisited()) {
                next.setVisited(true);
                System.out.println("\n🚶 You move " + direction + " into a new area...");
                // Show brief description on first visit
                System.out.println(next.getBriefDescription());
                // Hint to look around
                System.out.println("\n� Type 'look' to examine your surroundings in detail.");
            } else {
                System.out.println("\n� You return to the " + next.getName());
                // Brief description for all rooms now
                System.out.println(next.getBriefDescription());
                
                // Remind about puzzle if applicable
                if (next.getPuzzle() != null && !next.getPuzzle().isSolved()) {
                    System.out.println("\n🧩 " + next.getPuzzle().getHint());
                }
            }
        }
    }

    public void useItem(String itemName) {
        // First check if player actually has the item
        Item usedItem = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                usedItem = item;
                break;
            }
        }
        
        if (usedItem == null) {
            System.out.println("You don't have that item in your inventory.");
            
            // Check if the item exists in the current room
            for (Item roomItem : currentRoom.getItems()) {
                if (roomItem.getName().equalsIgnoreCase(itemName)) {
                    System.out.println("You see the " + itemName + " here, but you need to take it first with 'take " + itemName + "'");
                    return;
                }
            }
            
            // Suggest similar items in inventory
            if (!inventory.isEmpty()) {
                String closestMatch = findClosestMatch(itemName, 
                    inventory.stream().map(Item::getName).toList(), 
                    new ArrayList<>());
                
                if (closestMatch != null) {
                    System.out.println("Did you mean to use '" + closestMatch + "'?");
                }
            }
            
            return;
        }
        
        // Now try to use the item on a puzzle
        Puzzle puzzle = currentRoom.getPuzzle();
        if (puzzle != null && !puzzle.isSolved()) {
            System.out.println("\nYou attempt to use the " + usedItem.getName() + "...");
            
            if (puzzle.trySolve(itemName)) {
                if (puzzle.isSolved()) {
                    System.out.println("\n✅ SUCCESS! " + puzzle.getSolvedMessage());
                    
                    // If this was the last puzzle in the path to the win condition
                    if (currentRoom.getName().equals("IT Closet") && 
                        currentRoom.getExits().containsKey("north") && 
                        currentRoom.getExit("north").getName().equals("Control Room")) {
                        
                        System.out.println("\nThe path to the Control Room is now open. Victory is within your grasp!");
                    }
                } else {
                    // Provide feedback on partial progress
                    int completed = puzzle.getUsedItems().size();
                    int total = puzzle.getRequiredItems().size();
                    
                    System.out.println("\n⚙️ PARTIAL SUCCESS!");
                    System.out.println("You used the " + itemName + " correctly. (" + completed + "/" + total + " steps completed)");
                    
                    // Give a hint about what else might be needed
                    for (String requiredItem : puzzle.getRequiredItems()) {
                        if (!puzzle.getUsedItems().contains(requiredItem) && hasItem(requiredItem)) {
                            System.out.println("\n💡 Hint: You might also need to use the " + requiredItem + " here.");
                            break;
                        }
                    }
                }
            } else {
                // More detailed feedback on why the item doesn't work
                System.out.println("\n❌ That doesn't seem to work here.");
                
                // Check if the item was already used
                if (puzzle.getUsedItems().contains(itemName.toLowerCase())) {
                    System.out.println("You've already used the " + itemName + " on this puzzle.");
                } else {
                    System.out.println("This puzzle requires different tools.");
                    
                    // Provide a subtle hint if player has a required item
                    for (String requiredItem : puzzle.getRequiredItems()) {
                        if (hasItem(requiredItem)) {
                            System.out.println("\n💡 Hint: Look carefully at what the puzzle needs.");
                            break;
                        }
                    }
                }
            }
        } else {
            // Give special responses for certain items
            if (itemName.equalsIgnoreCase("blueprint")) {
                System.out.println("\nYou unfold the blueprint and study the museum layout.");
                System.out.println("This should help you understand how the rooms are connected.");
                System.out.println("Try using the 'map' command to see a visual representation.");
            } else if (itemName.equalsIgnoreCase("infrared goggles")) {
                System.out.println("\nYou put on the infrared goggles and scan the room.");
                if (!currentRoom.getItems().isEmpty()) {
                    System.out.println("You notice items that might have been missed:");
                    for (Item item : currentRoom.getItems()) {
                        System.out.println("- " + item.getName() + ": " + item.getDescription());
                    }
                } else {
                    System.out.println("You don't see anything of interest that you missed before.");
                }
            } else {
                System.out.println("There's nothing to use that on in this room.");
                
                // Check if this item is needed in an adjacent room
                boolean foundUse = false;
                for (Room adjacentRoom : currentRoom.getExits().values()) {
                    Puzzle adjacentPuzzle = adjacentRoom.getPuzzle();
                    if (adjacentPuzzle != null && !adjacentPuzzle.isSolved() && 
                        adjacentPuzzle.getRequiredItems().contains(itemName.toLowerCase())) {
                        
                        // Find which direction this room is
                        String direction = "unknown";
                        for (Map.Entry<String, Room> exit : currentRoom.getExits().entrySet()) {
                            if (exit.getValue() == adjacentRoom) {
                                direction = exit.getKey();
                                break;
                            }
                        }
                        
                        System.out.println("💡 Hint: This item might be useful if you go " + direction + ".");
                        foundUse = true;
                        break;
                    }
                }
                
                if (!foundUse) {
                    System.out.println("Try exploring more of the museum to find where this item can be used.");
                }
            }
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
