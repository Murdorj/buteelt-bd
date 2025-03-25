import java.io.*;
import java.util.*;

public class FlashCardApp {
    public static void main(String[] args) throws IOException {
        ArgumentParser options = new ArgumentParser(args);

        if (options.help || options.cardsFile == null) {
            printHelp();
            return;
        }

        List<FlashCard> cards = loadCards(options.cardsFile);
        CardOrganizer organizer = switch (options.order) {
            case "worst-first" -> new WorstFirstSorter();
            case "recent-mistakes-first" -> new RecentMistakesFirstSorter();
            default -> new RandomSorter();
        };

        List<FlashCard> sortedCards = organizer.organize(cards);
        Scanner scanner = new Scanner(System.in);

        long startTime = System.currentTimeMillis();
        boolean allCorrectThisRound = true;
        List<FlashCard> incorrectCards = new ArrayList<>();

        for (FlashCard card : sortedCards) {
            String question = options.invertCards ? card.getAnswer() : card.getQuestion();
            String expected = options.invertCards ? card.getQuestion() : card.getAnswer();

            System.out.println("Q: " + question);
            System.out.print("Your answer: ");
            String input = scanner.nextLine().trim();
            card.incrementAttempts();

            if (input.equalsIgnoreCase(expected)) {
                System.out.println("Correct!\n");
                card.incrementCorrectCount();
            } else {
                System.out.println("Wrong. Correct answer is: " + expected + "\n");
                card.setLastMistakeTime(System.currentTimeMillis());
                allCorrectThisRound = false;
                incorrectCards.add(card);
            }
        }

        for (FlashCard card : incorrectCards) {
            int correct = 0;
            while (correct < options.repetitions) {
                String question = options.invertCards ? card.getAnswer() : card.getQuestion();
                String expected = options.invertCards ? card.getQuestion() : card.getAnswer();

                System.out.println("(Retry) Q: " + question);
                System.out.print("Your answer: ");
                String input = scanner.nextLine().trim();
                card.incrementAttempts();

                if (input.equalsIgnoreCase(expected)) {
                    System.out.println("Correct!\n");
                    correct++;
                    card.incrementCorrectCount();
                } else {
                    System.out.println("Wrong. Correct answer is: " + expected + "\n");
                    card.setLastMistakeTime(System.currentTimeMillis());
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        System.out.println("\n--- Achievements ---");
        if (allCorrectThisRound) {
            System.out.println("CORRECT: All cards were answered correctly in this round!");
        }

        boolean repeatShown = false;
        boolean confidentShown = false;

        for (FlashCard card : cards) {
            if (card.getTotalAttempts() > 5 && !repeatShown) {
                System.out.println("REPEAT: More than 5 attempts on a single card.");
                repeatShown = true;
            }
            if (card.getCorrectCount() >= 3 && !confidentShown) {
                System.out.println("CONFIDENT: At least 3 correct answers for a single card.");
                confidentShown = true;
            }
        }

        if ((duration / cards.size()) < 5) {
            System.out.println("SPEEDSTER: Average response time under 5 seconds per card.");
        }
    }

    public static List<FlashCard> loadCards(String filename) throws IOException {
        List<FlashCard> cards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    cards.add(new FlashCard(parts[0].trim(), parts[1].trim()));
                }
            }
        }
        return cards;
    }

    public static void printHelp() {
        System.out.println("Usage: flashcard <cards-file> [options]\n");
        System.out.println("Options:");
        System.out.println("  --help                Show this help message and exit");
        System.out.println("  --order <order>       Card ordering type (default: random)");
        System.out.println("                       [random, worst-first, recent-mistakes-first]");
        System.out.println("  --repetitions <num>   Number of times each card must be answered correctly");
        System.out.println("  --invertCards         Invert question and answer for each card\n");
    }
}
