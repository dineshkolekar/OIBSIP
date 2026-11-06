import java.util.Random;
import java.util.Scanner;

/**
 * Number Guessing Game
 * Oasis Infobyte Internship Program - Java Development
 * Author: Dinesh Balaso Kolekar
 */
public class NumberGuessingGame {

    // Difficulty settings: {minNumber, maxNumber, maxAttempts}
    private static final int[][] DIFFICULTY = {
        {1, 50, 10},   // Easy
        {1, 100, 7},   // Medium
        {1, 200, 5}    // Hard
    };
    private static final String[] LEVEL_NAMES = {"Easy", "Medium", "Hard"};

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("     WELCOME TO NUMBER GUESSING GAME  ");
        System.out.println("=====================================");

        int round = 0;
        int totalScore = 0;
        int roundsWon = 0;
        boolean playAgain = true;

        while (playAgain) {
            round++;
            int level = chooseDifficulty();
            int min = DIFFICULTY[level][0];
            int max = DIFFICULTY[level][1];
            int maxAttempts = DIFFICULTY[level][2];

            int secret = random.nextInt(max - min + 1) + min;
            System.out.println("\n--- Round " + round + " (" + LEVEL_NAMES[level] + ") ---");
            System.out.println("I have picked a number between " + min + " and " + max + ".");
            System.out.println("You have " + maxAttempts + " attempts. Good luck!\n");

            int attempts = 0;
            boolean won = false;

            while (attempts < maxAttempts) {
                int guess = readGuess(min, max, attempts + 1, maxAttempts);
                attempts++;

                if (guess == secret) {
                    System.out.println("Correct!");
                    won = true;
                    break;
                } else if (guess > secret) {
                    System.out.println("Too High!");
                } else {
                    System.out.println("Too Low!");
                }
            }

            if (won) {
                int points = (maxAttempts - attempts + 1) * 10;
                totalScore += points;
                roundsWon++;
                System.out.println("Round " + round + " - guessed in " + attempts + " attempts.");
                System.out.println("You earned " + points + " points!");
            } else {
                System.out.println("You Lost! The number was " + secret + ".");
            }

            System.out.println("Total Score: " + totalScore + " | Rounds Won: " + roundsWon + "/" + round);
            playAgain = askPlayAgain();
        }

        System.out.println("\n=====================================");
        System.out.println("            GAME OVER");
        System.out.println("Rounds Played : " + round);
        System.out.println("Rounds Won    : " + roundsWon);
        System.out.println("Final Score   : " + totalScore);
        System.out.println("Thanks for playing!");
        System.out.println("=====================================");
        scanner.close();
    }

    private static int chooseDifficulty() {
        while (true) {
            System.out.println("\nChoose difficulty:");
            System.out.println("1. Easy   (1-50,  10 attempts)");
            System.out.println("2. Medium (1-100,  7 attempts)");
            System.out.println("3. Hard   (1-200,  5 attempts)");
            System.out.print("Enter choice (1-3): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2") || input.equals("3")) {
                return Integer.parseInt(input) - 1;
            }
            System.out.println("Invalid choice. Please enter 1, 2 or 3.");
        }
    }

    private static int readGuess(int min, int max, int attemptNo, int maxAttempts) {
        while (true) {
            System.out.print("Attempt " + attemptNo + "/" + maxAttempts + " - Enter your guess: ");
            String input = scanner.nextLine().trim();
            try {
                int guess = Integer.parseInt(input);
                if (guess < min || guess > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return guess;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private static boolean askPlayAgain() {
        while (true) {
            System.out.print("\nPlay again? (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) return true;
            if (input.equals("no") || input.equals("n")) return false;
            System.out.println("Please type yes or no.");
        }
    }
}