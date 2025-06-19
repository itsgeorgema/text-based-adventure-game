# Text Based Adventure Game

An immersive, text-based adventure game where you play as an elite infiltrator breaking into a high-security art museum. Navigate through secured rooms, bypass sophisticated puzzles, find and combine items, and reach the Control Room to complete the heist.

## Table of Contents

- [Features](#-features)
- [Installation & Setup](#-installation--setup)
- [Build & Run Options](#️-build--run-options)
- [Commands](#-commands)
- [Museum Layout](#️-museum-layout)
- [Winning Gameplay Path](#-winning-gameplay-path)
- [Tips for Success](#-tips-for-success)
- [Project Structure](#-project-structure)

## 🚀 Features

- Engaging room exploration with detailed atmospheric descriptions
- Sophisticated puzzle system requiring specific items and combinations
- Full inventory management with item inspection and combination
- Interactive command feedback with contextual hints
- Dynamic map display showing your progress through the museum
- Enhanced error handling for a seamless gameplay experience
- Object-oriented architecture with `Room`, `Item`, `Player`, `Puzzle`, and `Game` classes
- Fuzzy command matching for improved user experience
- Persistent command list stored in project root

## 📥 Installation & Setup

### Dependencies
- Java
- Java Development Kit (JDK) 21 or later
- Git
- Gradle

### Clone the Repository
```bash
# Clone the repository to your local machine
git clone https://github.com/yourusername/text-based-adventure-game.git

# Navigate to the project directory
cd text-based-adventure-game
```

### Setup
The project uses Gradle, which will automatically download all required dependencies.

```bash
# Make the run script executable (for Unix/macOS)
chmod +x run_game.sh
```

### Running for the First Time
```bash
# Run the game with the optimized script
./run_game.sh
```

The first run will:
1. Download Gradle if needed
2. Compile the Java source files
3. Create the `all_game_commands.txt` file in the project root
4. Start the game

## 🛠️ Build & Run Options

### Option 1: Using the run_game.sh script (Recommended)
```bash
./run_game.sh
```
This provides the cleanest game experience with no Gradle progress indicators.

### Option 2: IDE Run Button
If you're using an IDE like IntelliJ IDEA, Eclipse, or VS Code, you can run the game using the IDE's run button after importing the project.

## 🎮 Commands

### Movement & Exploration
- `go [direction]` - Move north, south, east, west, up, or down
- `look` or `l` - Examine your current surroundings in detail
- `map` - Display the museum layout with visited rooms marked

### Item Management
- `items` or `i` - List items in the current room
- `inventory` or `inv` - Show your current inventory
- `take [item]` - Pick up an item from the current room
- `inspect [item]` - Examine an item in your inventory for details

### Puzzle Solving
- `use [item]` - Use an item to solve puzzles or interact with the environment
- `combine [item1] with [item2]` - Combine two compatible items to create tools
- `hint` - Get contextual help for your current situation

### Game Navigation
- `help` or `h` - Show all available commands
- `quit` - Exit the game

## 🗺️ Museum Layout

```
                              [UPPER FLOOR]
                                    |
                              CONTROL ROOM
                                    ↑
                               IT CLOSET
                                    ↑
                           DIRECTOR'S OFFICE
                                    ↑
                             STORAGE ROOM
                                    ↑
[UPPER LEVEL] → → → → → → → → → → → → → → → → → → → → → → → →
ROOFTOP ← LIBRARY ← SURVEILLANCE ← LOADING DOCK ← EXHIBIT HALL ← BREAK ROOM
   ↑
VAULT
   ↑
FOYER → GALLERY
           ↑
[GROUND FLOOR] → → → → → → → → → → → → → → → → → → → → → → → →
     SECURITY OFFICE → ATRIUM → ARCHIVES → WORKSHOP → HALL OF SCULPTURES → SERVER ROOM
```

### Layout Overview:
- **Ground Floor Main Path**: Foyer → Gallery → Security Office → Atrium → Archives → Workshop → Hall of Sculptures → Server Room
- **Vault Branch**: Gallery → Vault → Rooftop (access to upper level)  
- **Upper Level**: Rooftop → Library → Surveillance Room → Loading Dock → Exhibit Hall → Break Room
- **Final Tower**: Hall of Sculptures → Storage Room → Director's Office → IT Closet → Control Room
- **Server Room**: Accessible from Hall of Sculptures (requires puzzle)

## 🎯 Winning Gameplay Path

Here's an efficient walkthrough to complete the museum heist:

```
# Starting in the Foyer (Ground Floor)
> look                         # Examine your surroundings
> take blueprint               # Take the museum layout map
> take emp device              # Take the EMP device
> go north                     # Move to Gallery

# Gallery (Ground Floor)
> take laser mirror            # Take the laser mirror
> go west                      # Move to Security Office

# Security Office (Ground Floor)
> take infrared goggles        # Take the infrared goggles
> go north                     # Move to Atrium

# Atrium (Ground Floor)
> take pressure plate          # Take the pressure plate
> go east                      # Move to Archives

# Archives (Ground Floor)
> take glass cutter            # Take the glass cutter
> go east                      # Move to Workshop

# Workshop (Ground Floor)
> take thermal drill           # Take the thermal drill
> take power cell              # Take the power cell
> combine thermal drill with power cell  # Create the powered drill
> go east                      # Move to Hall of Sculptures

# Hall of Sculptures (Ground Floor)
> take admin password          # Take the admin password
> go north                     # Move to Server Room

# Server Room (Ground Floor)
> take server access card      # Take the server access card
> use admin password           # Use the admin password on the puzzle
> use server access card       # Use the server access card on the puzzle
> go south                     # Return to Hall of Sculptures

# Start Final Tower Path
> go south                     # Move to Storage Room
> take biometric scanner       # Take the biometric scanner
> go south                     # Move to Director's Office

# Director's Office
> take director's ring         # Take the director's ring
> use director's ring          # Use the ring to solve the biometric puzzle
> go south                     # Move to IT Closet

# IT Closet
> take master override         # Take the master override device
> go south                     # Move to Control Room

# Control Room - Final Step
> use master override          # Use the override to complete the heist and win
```

**Alternative Path (Exploring Upper Level):**
This path allows you to access the Vault, explore the upper level, then return to the main path:

```
# You must have the powered drill (combine thermal drill and power cell first)
# From Gallery
> go east                      # Move to Vault
> take vault code              # Take the vault code
> use vault code               # Use the code on the vault puzzle
> use powered drill            # Use the powered drill on the vault puzzle
> go north                     # Access the Rooftop (Upper Level)

# Rooftop (Upper Level)
> take zipline hook            # Take the zipline hook
> go west                      # Move to Library

# Library (Upper Level)
> take coded ledger            # Take the coded ledger
> go west                      # Move to Surveillance Room

# Surveillance Room (Upper Level)
> take camera loop usb         # Take the camera loop USB
> go west                      # Move to Loading Dock

# Loading Dock (Upper Level)
> take override chip           # Take the override chip
> go west                      # Move to Exhibit Hall

# You can then continue exploring the upper level
# Or return to the main path to complete the heist
```

**Key Strategy Notes:**
- **Item Combinations**: thermal drill + power cell = "powered drill" (essential for vault)
- **Required Puzzles**: Vault (vault code + powered drill), Server Room (admin password + server access card), Director's Office (director's ring)
- **Win Condition**: Reach the Control Room and use the master override device to complete the heist
- **Navigation**: Use `map` command to track progress and `hint` for guidance
- **Exploration**: Upper level accessible via Vault → Rooftop provides alternative routes and items

## 💡 Tips for Success

1. **Start Systematically** - Begin by taking all items in the Foyer, then follow the main path through Gallery → Security Office → Atrium → Archives → Workshop → Hall of Sculptures → Server Room
2. **Essential Combination** - Always combine the thermal drill + power cell to create the "powered drill" needed for the Vault
3. **Puzzle Strategy** - Each puzzle requires specific items that can be found nearby or on the logical path to that room:
   - Vault: requires vault code + powered drill
   - Server Room: requires admin password + server access card
   - Director's Office: requires director's ring
4. **Use Navigation Tools** - `map` shows your progress and room layout, `hint` provides contextual guidance for your current situation
5. **Explore Thoroughly** - Use `look` and `items` in each room, some rooms contain multiple useful items
6. **Upper Level Access** - The Vault → Rooftop path opens access to additional rooms and items on the upper level
7. **Final Tower Path** - From Hall of Sculptures, go south to access the final tower: Storage Room → Director's Office → IT Closet → Control Room
8. **Win Condition** - You must use the master override in the Control Room to complete the heist

### Core Game Features to Experience:
- **Item Management**: Take, inspect, and combine items strategically
- **Puzzle Solving**: Each major room has a unique puzzle requiring specific items  
- **Room Exploration**: 20 unique rooms with detailed descriptions and hidden connections
- **Progressive Difficulty**: Earlier rooms provide items needed for later challenges

## 📂 Project Structure

```
app/
  └── src/
      └── main/
          └── java/
              └── text/
                  └── based/
                      └── adventure/
                          └── game/
                              ├── App.java          - Main application entry point
                              ├── Game.java         - Core game logic and world setup
                              ├── Player.java       - Player state and inventory management
                              ├── Room.java         - Room definition and connections
                              ├── Item.java         - In-game items and their properties
                              ├── Puzzle.java       - Puzzle mechanics and solutions
                              └── FuzzyMatcher.java - Command input matching system

# Key Configuration Files
all_game_commands.txt  - List of all possible game commands
run_game.sh            - Convenient script to run the game
```