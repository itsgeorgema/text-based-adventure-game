package text.based.adventure.game;



public class App {
    public static void main(String[] args) {
        // Set system properties for console IO
        System.setProperty("java.awt.headless", "true");
        System.setProperty("file.encoding", "UTF-8");
        
        // Create and start the game
        Game game = new Game();
        game.start();
        
        // Ensure clean exit
        System.exit(0);
    }
}