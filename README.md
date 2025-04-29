# Text-Based Java Adventure Game

A modular, object-oriented text-based adventure game built in Java, inspired by the classic Zork. Explore rooms, solve puzzles, collect items, and interact with a fully functional CLI game engine.

## 🚀 Features

- Room navigation via `HashMap`-based exits
- Puzzle-solving with item-triggered logic
- Player inventory system using `ArrayList`
- Object-oriented architecture with `Room`, `Item`, `Player`, `Puzzle`, and `Game` classes
- Commands: `go [direction]`, `look`, `items`, `take [item]`, `use [item]`, `inventory`, `help`, `quit`

## 🗺️ Sample Room Layout

```
Foyer -- North --> Hall -- East --> Kitchen -- North --> Vault (locked)
```

## 🧪 Example Gameplay

```text
> look
Foyer: A small entryway with a dusty rug. Exits: [north]

> go north
You moved north.
Hall: A long hallway with paintings. Exits: [south, east]

> go east
You moved east.
Kitchen: An old kitchen with a locked door north. Items here: key. Exits: [west, north]

> take key
You took the key

> use key
You used the key to solve a puzzle!

> go north
You moved north.
Vault: A hidden room with treasure!
```

## 📂 Project Structure

```
src/
  └── main/
      └── java/
          └── text/based/adventure/game/
              ├── App.java
              ├── Game.java
              ├── Player.java
              ├── Room.java
              ├── Item.java
              └── Puzzle.java
```

## 🛠️ Build & Run (Gradle)

```bash
./gradlew build
./gradlew run
```

## Dependencies

- Java 11+
- Gradle (for build automation)
