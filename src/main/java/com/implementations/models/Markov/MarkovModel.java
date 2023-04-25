package com.implementations.models.Markov;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MarkovModel {

    //list of words
    String[] listOfWords;
    //get unique letters
    ArrayList<String> letters = new ArrayList<>();
    int[][] grid;
    int largestCount = 0;

    public MarkovModel(String[] listOfWords) {
        this.listOfWords = listOfWords;
    }

    public String createWord() {
        int letterIndex = getFirstLetter();
        String newWord = letters.get(letterIndex);

        Random random = new Random();
        int length = random.nextInt(4) + 2;

        for (int i = 0; i < length; i++) {
            letterIndex = getNextLetter(letterIndex);
            newWord = newWord + letters.get(letterIndex);
        }

        letterIndex = getLastLetter(letterIndex);

        newWord = newWord + letters.get(letterIndex);

        return newWord;
    }

    public int getNextLetter(int letterIndex) {

        Random random = new Random();
        int totalWithoutBlank = grid[letterIndex][letters.size()] - grid[letterIndex][letters.size() - 1];
        int index = random.nextInt(totalWithoutBlank);//get total at end of row

        int cumulativeProbability = 0;
        for (int i = 0; i < grid[letterIndex].length - 2; i++) {
            cumulativeProbability += grid[letterIndex][i];
            if (index <= cumulativeProbability && grid[letterIndex][i] > 0) {
                return i;
            }
        }

        return 0;
    }

    private int getFirstLetter() {
        Random random = new Random();
        int index = random.nextInt(grid[letters.size() - 1][letters.size()]);

        int cumulativeProbability = 0;
        for (int i = 0; i < grid[letters.size() - 1].length; i++) {
            cumulativeProbability += grid[letters.size() - 1][i];
            if (index <= cumulativeProbability && grid[letters.size() - 1][i] > 0) {
                return i;
            }
        }

        return 0;
    }

    private int getLastLetter(int currentLetterIndex) {
        Random random = new Random();
        int index = random.nextInt(grid[letters.size() - 1][letters.size()]);
        System.out.println();
        System.out.print(index + "     ");

        //not correct logic
        int cumulativeProbability = 0;
        for (int i = 0; i < grid[letters.size() - 1].length; i++) {
            cumulativeProbability += grid[letters.size() - 1][i];
            if (index <= cumulativeProbability && grid[letters.size() - 1][i] > 0) {
                System.out.println(letters.get(i));
                return (i);
            }
        }

        return 0;
    }

    public void createLetterArray() {
        for (String word : listOfWords) {
            for (char character : word.toCharArray()) {
                letters.add(Character.toString(character));
            }
        }

        removeDuplicates();

        letters.add("0");
        System.out.print("    ");
        for (String letter : letters) {
            System.out.print(letter + "   ");
        }
    }

    public void createGrid() {
        grid = new int[letters.size()][letters.size() + 1];//increase size to include row totals

        for (int i = 0; i < grid[0].length - 1; i++) {
            int runningTotal = 0;
            int countToInsert = 0;
            int lastJ = 0;

            System.out.println();
            System.out.print(letters.get(i) + "   ");

            for (int j = 0; j < grid.length; j++) {
                lastJ = j;
                if (i == grid[0].length - 2) {
                    countToInsert = checkFirstLetter(letters.get(j));//FINAL ROW WITH FIRST LETTER COUNT
                } else if (j == grid[0].length - 2) {
                    countToInsert = checkLastLetter(letters.get(i));//FINAL COLUMN LAST LETTER CHECK
                } else {
                    countToInsert = checkLetterCombinationAllWords(letters.get(i), letters.get(j));
                }
                grid[i][j] = countToInsert;
                runningTotal = runningTotal + countToInsert;
                System.out.print(grid[i][j] + "   ");
            }
            grid[i][lastJ + 1] = runningTotal;

            if (runningTotal > largestCount) {
                largestCount = runningTotal;
            }

            System.out.print(grid[i][lastJ]);
        }
    }

    public int checkLastLetter(String letter) {
        int count = 0;

        for (String word : listOfWords) {
            String lastChar = word.substring(word.length() - 1);
            if (lastChar.equals(letter)) {
                count++;
            }
        }
        return count;
    }

    public int checkFirstLetter(String letter) {
        int count = 0;

        for (String word : listOfWords) {
            String firstChar = word.substring(0, 1);
            if (firstChar.equals(letter)) {
                count++;
            }
        }
        return count;
    }

    public void removeDuplicates() {
        HashSet<String> conversionSet = new HashSet<>(letters);
        letters = new ArrayList<>(conversionSet);
    }

    public int checkLetterCombinationAllWords(String firstLetter, String secondLetter) {
        int count = 0;

        for (String word : listOfWords) {
            count = count + checkLetterCombination(firstLetter, secondLetter, word);
        }

        return count;
    }

    public int checkLetterCombination(String firstLetter, String secondLetter, String word) {
        int count = 0;

        char[] characters = word.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            if (Character.toString(characters[i]).equals(firstLetter) && i + 1 < characters.length) {
                if (Character.toString(characters[i + 1]).equals(secondLetter)) {
                    count++;
                }
            }
        }

        return count;
    }
}
