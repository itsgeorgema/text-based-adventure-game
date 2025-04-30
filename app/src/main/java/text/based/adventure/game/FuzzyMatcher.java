package text.based.adventure.game;
import java.util.*;

public class FuzzyMatcher {
    private ArrayList<String> dictionary;

    public FuzzyMatcher(ArrayList<String> dictionary){
        this.dictionary = dictionary;
    }
    public String getBestMatch(String input){
        input=input.toLowerCase().trim();
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;
        for(String word: dictionary){
            int distance=levenshteinDistance(input, word);
            if(distance<bestDistance){
                bestDistance=distance;
                bestMatch=word;
            }
        }
        return bestMatch;
    }
    public static int levenshteinDistance(String input, String word){
        int[][] dp =new int[input.length() +1][word.length()+1];
        for(int i=0; i<input.length();i++){ /*number of deletions needed to get from 
            input to an empty string (rows) (also number of chars in input)*/
            dp[i][0]=i;
        }
        for(int j=0; j<word.length();j++){
            //number of insertions needed to get from empty string to word (columns)
            //also number of chars in words
            dp[0][j]=j;
        }
        for(int i=1;i<dp.length;i++){ 
            /*start at 1 because the 0 index for rows and columns are 
            taken by insertions and deletions*/
            for (int j=1;j<dp[0].length;j++){
                if(input.charAt(i-1)==word.charAt(j-1)){
                    dp[i][j]=dp[i-1][j-1]; //no operation beacsue same
                }else{
                    //find minimum num edits to match: deletion, insertion, or substitution
                    //consider trying match s1 and s2 at the prior indices
                    //deletion: dp[i-1][j]=how many edits to match s1 and s2 after deleting a char from s2, then add 1 to indicate singular deletion
                    //insertion: dp[i][j-1]=how many edits to match s1 and s2 prior to inserting, and add 1 to make an insert edit
                    //substitution: dp[i-1][j-1]=how many edits to match s1 and s2 at both prior indices and add 1 to indiciate an edit in which you substitute one letter for the other to get the same
                    dp[i][j]=Math.min(dp[i-1][j-1]+1,Math.min(dp[i][j-1]+1, dp[i-1][j]+1));
                }
            }
        }
        //return the final, bottom right corner of the 2D array b/c that is how many edits it took to get from one word to the other at each end of both words
        return dp[input.length() ][word.length()]; 
    }
}
