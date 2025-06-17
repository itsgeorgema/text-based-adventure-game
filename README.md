# Text Based Adventure Game

An immersive, text-based adventure game where you play as an elite infiltrator breaking into a high-security art museum. Navigate through secured rooms, bypass sophisticated puzzles, find and combine items, and reach the Control Room to complete the heist.

## 📋 Table of Contents

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

- `go [direction]` - Move north, south, east, west, up, or down
- `look` or `l` - Examine your current surroundings
- `items` or `i` - List items in the current room
- `take [item]` - Pick up an item
- `use [item]` - Use an item (often on puzzles)
- `inspect [item]` - Examine an item in detail
- `combine [item1] with [item2]` - Combine two compatible items
- `inventory` or `inv` - Show your current inventory
- `map` - Display a map of the museum (shows visited rooms)
- `hint` - Get contextual help for your current situation
- `help` or `h` - Show all available commands
- `quit` - Exit the game

## 🗺️ Museum Layout

```
                                         Control Room
                                              ↑
                                          IT Closet
                                              ↑
                                      Director's Office
                                              ↑
                                        Storage Room
                                              ↑
          Rooftop ← Library → Surveillance → Loading Dock → Exhibit Hall
             ↑        ↑             ↑               ↑             ↑
          Vault       |             |               |          Break Room
             ↑        |             |               |             ↑
Foyer → Gallery       |             |               |         Server Room
             ↓        |             |               |             ↑
    Security Office → Atrium → Archives → Workshop → Hall of Sculptures
```

## 🧪 Winning Gameplay Path

```
> look
📍 FOYER 📍
===================================
The museum's marble-floored entry. Moonlight casts long shadows through tall windows.
Your footsteps echo quietly on the marble floor.

👜 You can see:
 • 🔍 laser mirror
 • 🔄 decoy badge

🚪 Available exits: north ↑
===================================

> inventory
🎒 YOUR INVENTORY
===================================
🔄 COMBINABLE ITEMS (1):
-----------------------------------
🔄 emp device - Short-circuits devices

🔍 OTHER ITEMS (2):
-----------------------------------
🔍 blueprint - Museum map for navigation
🔍 decoy badge - Fake access card
===================================

> take laser mirror
You picked up: laser mirror

> go north
🚶 You move north into a new area...
📍 GALLERY 📍
===================================
Paintings hang elegantly across velvet walls. Famous paintings hang in dim lighting.
Motion sensors glint occasionally in the corners.

👜 You can see:
 • 🔍 thermal drill

🚪 Available exits: east →🔒, west ←
===================================

> take thermal drill
You picked up: thermal drill

> go west
🚶 You move west into a new area...
📍 SECURITY OFFICE 📍
===================================
Rows of monitors blink rapidly. Screens flicker with camera feeds.
A coffee cup sits abandoned, still warm.

👜 You can see:
 • 🔍 infrared goggles

🚪 Available exits: north ↑
===================================

> take infrared goggles
You picked up: infrared goggles

> go north
🚶 You move north into a new area...
📍 ATRIUM 📍
===================================
Open skylight and pressure-sensitive floors. A glass ceiling reveals the night sky above.
The floor tiles are suspiciously uniform.

👜 You can see:
 • 🔄 override chip

🚪 Available exits: east →
===================================

> take override chip
You picked up: override chip

> go east
🚶 You move east into a new area...
📍 ARCHIVES 📍
===================================
Filing cabinets hide blueprints. Filing cabinets and document boxes line the walls.
The air smells of old paper.

👜 You can see:
 • 🔍 power cell

🚪 Available exits: north ↑
===================================

> take power cell
You picked up: power cell

> combine thermal drill with power cell
🔧 ITEMS COMBINED SUCCESSFULLY 🔧
===================================
You carefully fit the thermal drill and power cell together.
After some precise adjustments, they lock into place with a satisfying click.

You created: POWERED DRILL
High-powered cutting tool capable of breaching any physical security
===================================
This new tool has been added to your inventory.

> go north
🚶 You move north into a new area...
📍 WORKSHOP 📍
===================================
Tools and 3D-printed parts scattered around. Tools hang neatly on wall mounts.
A half-finished project sits on the workbench.

👜 You can see:
 • 🔍 zipline hook

🚪 Available exits: east →
===================================

> take zipline hook
You picked up: zipline hook

> go east
🚶 You move east into a new area...
📍 HALL OF SCULPTURES 📍
===================================
Pedestals with missing sculptures. Stone and metal figures watch you silently.
Their shadows seem to shift when you're not looking.

👜 You can see:
 • 🔍 admin password

🚪 Available exits: north ↑🔒
===================================

🧩 PUZZLE: Login console denies access.

> take admin password
You picked up: admin password

> go north
🚫 Blocked: Login console denies access.

💡 Hint: You might be able to use the admin password in your inventory here.
💡 You might be able to use the override chip that's in your inventory!

> use admin password
You attempt to use the admin password...
⚙️ PARTIAL SUCCESS!
You used the admin password correctly. (1/2 steps completed)
You'll need 1 more components to solve this puzzle.

> use override chip
You attempt to use the override chip...
✅ SUCCESS! Root access granted.

> go north
🚶 You move north into a new area...
📍 SERVER ROOM 📍
===================================
Buzzing servers hum next to AC vents. Fans whir constantly as servers blink with activity.
The air conditioning is almost too cold.

👜 You can see:
 • 🔍 biometric bypass

🚪 Available exits: 
===================================

> take biometric bypass
You picked up: biometric bypass

> map

📍 MUSEUM MAP 📍
(Visited rooms are marked with ✓)
-----------------------------------------
    CONTROL ROOM      
       ↑       
    IT CLOSET      
       ↑       
                                
                                
                                
                                
                                
FOYER ✓  GALLERY ✓                      
       ↓                  
    SECURITY OFFICE ✓  ATRIUM ✓  ARCHIVES ✓  WORKSHOP ✓  HALL OF SCULPTURES ✓  SERVER ROOM 🔸
                                                 ↓       
                                          STORAGE ROOM      
                                                 ↓       
                                        DIRECTOR'S OFFICE      
-----------------------------------------

> go east
The eastern passage is inaccessible. You'll need to find another route.

Available directions:
- south ↓

> go south
🚶 You move south into a new area...
📍 HALL OF SCULPTURES 📍
===================================
Pedestals with missing sculptures. Stone and metal figures watch you silently.
Their shadows seem to shift when you're not looking.

There are no notable items in this area.

🚪 Available exits: north ↑, south ↓
===================================

> go south
🚶 You move south into a new area...
📍 STORAGE ROOM 📍
===================================
Stacked shelves labeled with barcodes.

👜 You can see:
 • 🔄 master override

🚪 Available exits: north ↑, south ↓
===================================

> take master override
You picked up: master override

> go south
🚶 You move south into a new area...
📍 DIRECTOR'S OFFICE 📍
===================================
A desk covered in notes and hidden keys.

👜 You can see:
 • 🔍 director's ring

🚪 Available exits: north ↑, south ↓
===================================

🧩 PUZZLE: A hidden safe requiring special access.

> take director's ring
You picked up: director's ring

> use director's ring
You attempt to use the director's ring...
✅ SUCCESS! Click. The hidden safe unlocks.

> go south
🚶 You move south into a new area...
📍 IT CLOSET 📍
===================================
Cables tangle across floor tiles.

👜 You can see:
 • 🔍 camera loop usb

🚪 Available exits: north ↑🔒, south ↑
===================================

🧩 PUZZLE: Final door needs system-wide key.

> take camera loop usb
You picked up: camera loop usb

> use master override
You attempt to use the master override...
✅ SUCCESS! Every lock disengages silently.

The path to the Control Room is now open. Victory is within your grasp!

> go north
🚶 You move north into a new area...
📍 CONTROL ROOM 📍
===================================
One switch controls every door in the museum. The central hub for all museum systems.
From here, you can access everything.

There are no notable items in this area.

🚪 Available exits: south ↓
===================================

🎉 CONGRATULATIONS! 🎉
You've successfully reached the Control Room and completed the museum heist!
With access to the security system, you can now escape with your prize.
```
### Commands Only
```
> look
> inventory
> take laser mirror
> go north
> take thermal drill
> go west
> take infrared goggles
> go north
> take override chip
> go east
> take power cell
> combine thermal drill with power cell
> go north
> take zipline hook
> go east
> take admin password
> go north
> use admin password
> use override chip
> go north
> take biometric bypass
> map
> go east
> go south
> go south
> take master override
> go south
> take director's ring
> go south
> take camera loop usb
> use master override
> go north
```

## 💡 Tips for Success

1. Always `look` around when entering a new room
2. `inspect` items to learn how they might be useful
3. Remember that some items can be `combine`d to create more powerful tools
4. Use the `map` and `hint` commands when you're stuck
5. Pay attention to puzzle descriptions - they contain clues about which items you need

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