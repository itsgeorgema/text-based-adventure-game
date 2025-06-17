package text.based.adventure.game;
import java.util.*;

public class FuzzyMatcher {
    private ArrayList<String> dictionary;

    public FuzzyMatcher(ArrayList<String> dictionary){
        this.dictionary = dictionary;
    }
    public String getBestMatch(String input) {
        input = input.toLowerCase().trim();
        String[] inputWords = input.split("\\s+");
        int inputWordCount = inputWords.length;
    
        String bestMatch = null;
        double bestScore = Double.MAX_VALUE;
    
        for (String candidate : dictionary) {
            candidate = candidate.toLowerCase().trim();
            String[] candidateWords = candidate.split("\\s+");
            int candidateWordCount = candidateWords.length;
    
            int distance = levenshteinDistance(input, candidate);
            int wordCountPenalty = Math.abs(candidateWordCount - inputWordCount) * 2;
            int lengthPenalty = Math.abs(candidate.length() - input.length()); // new: penalize extreme length differences
    
            // Reward overlapping words
            int overlapBonus = 0;
            for (String w1 : inputWords) {
                for (String w2 : candidateWords) {
                    if (w1.equals(w2)) {
                        overlapBonus -= 1;
                    }
                }
            }
    
            double score = distance + wordCountPenalty + lengthPenalty + overlapBonus;
    
            if (score < bestScore) {
                bestScore = score;
                bestMatch = candidate;
    
                if (score <= 1) break; // early exit if super close
            }
        }
    
        return bestMatch;
    }
    public static int levenshteinDistance(String input, String word) {
        // Handle null or empty strings
        if (input == null || word == null) {
            return 0;
        }
        
        if (input.isEmpty()) {
            return word.length();
        }
        
        if (word.isEmpty()) {
            return input.length();
        }
        
        int[][] dp = new int[input.length() + 1][word.length() + 1];
        
        // Initialize first row
        for(int i = 0; i <= input.length(); i++) {
            dp[i][0] = i;
        }
        
        // Initialize first column
        for(int j = 0; j <= word.length(); j++) {
            dp[0][j] = j;
        }
        
        // Fill the dp table
        for(int i = 1; i <= input.length(); i++) {
            for (int j = 1; j <= word.length(); j++) {
                if(input.charAt(i - 1) == word.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation because characters are the same
                } else {
                    // Find minimum edits: deletion, insertion, or substitution
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j] + 1));
                }
            }
        }
        
        // Return the value at the bottom right of the table
        return dp[input.length()][word.length()];
    }
}
