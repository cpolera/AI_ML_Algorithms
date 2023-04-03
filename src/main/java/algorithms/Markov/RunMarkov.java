package algorithms.Markov;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RunMarkov {

    //list of words
    static String[] listOfWords = new String[]{"steam", "meet", "team", "mates", "seat", "tea", "same", "tame", "mess", "stats"};
    //get unique letters
    static ArrayList<String> letters = new ArrayList<>();
    static int[][] grid;
    static int largestCount = 0;


    public static void main(String[] args) {

        createLetterArray();

        createGrid();

        for (int i = 10; i > 0; i--) {
            System.out.println();
            System.out.println(createWord());
        }

//
//        0.3	0.53	0.17	0.395604396	0.285714286	0.318681319	0.233333333	0.35	0.416666667
//        30	53	17	36	26	29	14	21	25
//        H	H	H	S	S	S	B	B	B
//        100			91			60
//        HS	HH	HB	SS	SH	SB	BB	BH	BS


        double BB = 0.233333333;
        double BS = 0.416666667;
        double BH = 0.35;
        double SB = 0.318681319;
        double SS = 0.395604396;
        double SH = 0.285714286;
        double HB = 0.17;
        double HH = 0.53;
        double HS = 0.3;


        ArrayList<String> states = new ArrayList<>();
        states.add("B");
        states.add("S");
        states.add("H");
        double[][] probablities = new double[3][3];
        probablities[0] = new double[]{BB, BS, BH};
        probablities[1] = new double[]{SB, SS, SH};
        probablities[2] = new double[]{HB, HS, HH};


        Random random = new Random();
        int ranIntStart = random.nextInt(3);
        getNextLetter(ranIntStart);

        for (int i = 50; i > 0; i--) {
            System.out.print(" " + states.get(ranIntStart));

            double tempRan = random.nextDouble();
            double cumulative = 0.0;
            int counter = 0;
            boolean found = false;
            for (double d : probablities[ranIntStart]) {
                cumulative += d;
                if (tempRan < cumulative && !found) {
                    ranIntStart = counter;
                    found = true;
                }
                counter++;
            }
        }

    }

    public static String predictTrade(String previousTrade) {
        double BB = 0.233333333;
        double BS = 0.416666667;
        double BH = 0.35;
        double SB = 0.318681319;
        double SS = 0.395604396;
        double SH = 0.285714286;
        double HB = 0.17;
        double HH = 0.53;
        double HS = 0.3;

        ArrayList<String> states = new ArrayList<>();
        states.add("B");
        states.add("S");
        states.add("H");
        double[][] probablities = new double[3][3];
        probablities[0] = new double[]{BB, BS, BH};
        probablities[1] = new double[]{SB, SS, SH};
        probablities[2] = new double[]{HB, HS, HH};


        Random random = new Random();
//        int ranIntStart = random.nextInt(3);
//        getNextLetter(ranIntStart);

        int ranIntStart = 0;

        if (previousTrade.equalsIgnoreCase("B"))
            ranIntStart = 0;
        if (previousTrade.equalsIgnoreCase("S"))
            ranIntStart = 1;
        if (previousTrade.equalsIgnoreCase("H"))
            ranIntStart = 2;


        double tempRan = random.nextDouble();
        double cumulative = 0.0;
        int counter = 0;
        boolean found = false;
        for (double d : probablities[ranIntStart]) {
            cumulative += d;
            if (tempRan < cumulative && !found) {
                ranIntStart = counter;
                found = true;
            }
            counter++;
        }
        System.out.print(" " + states.get(ranIntStart));
        return states.get(ranIntStart);
    }


    private static String createWord() {
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

    private static int getNextLetter(int letterIndex) {

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

    private static int getFirstLetter() {
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

    private static int getLastLetter(int currentLetterIndex) {
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

    private static void createLetterArray() {
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

    public static void createGrid() {
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

    public static int checkLastLetter(String letter) {
        int count = 0;

        for (String word : listOfWords) {
            String lastChar = word.substring(word.length() - 1);
            if (lastChar.equals(letter)) {
                count++;
            }
        }
        return count;
    }

    public static int checkFirstLetter(String letter) {
        int count = 0;

        for (String word : listOfWords) {
            String firstChar = word.substring(0, 1);
            if (firstChar.equals(letter)) {
                count++;
            }
        }
        return count;
    }

    public static void removeDuplicates() {
        HashSet<String> conversionSet = new HashSet<>(letters);
        letters = new ArrayList<>(conversionSet);
    }

    public static int checkLetterCombinationAllWords(String firstLetter, String secondLetter) {
        int count = 0;

        for (String word : listOfWords) {
            count = count + checkLetterCombination(firstLetter, secondLetter, word);
        }

        return count;
    }

    public static int checkLetterCombination(String firstLetter, String secondLetter, String word) {
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
