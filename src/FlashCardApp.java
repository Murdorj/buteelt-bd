import java.io.*;
import java.util.*;

public class Main {
    private List<FlashCard> cards = new ArrayList<>();
    private Map<String, Integer> mistakeCounts = new HashMap<>();
    private Map<String, Integer> correctCounts = new HashMap<>();
    private boolean invertCards = false;
    private int repetitions = 1;
    private String order = "random";

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        if (args.length == 0 || args[0].equals("--help")) {
            printHelp();
            return;
        }

        String filePath = args[0];
        parseOptions(Arrays.copyOfRange(args, 1, args.length));

        loadCards(filePath);
        startQuiz();
        evaluateAchievements();
    }

    private void parseOptions(String[] options) {
        for (int i = 0; i < options.length; i++) {
            switch (options[i]) {
                case "--order":
                    if (i + 1 < options.length) order = options[++i];
                    break;
                case "--repetitions":
                    if (i + 1 < options.length) repetitions = Integer.parseInt(options[++i]);
                    break;
                case "--invertCards":
                    invertCards = true;
                    break;
            }
        }
    }

    private void loadCards(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    cards.add(new FlashCard(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void startQuiz() {
        Scanner scanner = new Scanner(System.in);
        CardOrganizer organizer = getCardOrganizer();
        List<FlashCard> sortedCards = organizer.organize(cards, mistakeCounts);

        for (int i = 0; i < repetitions; i++) {
            for (FlashCard card : sortedCards) {
                askQuestion(scanner, card);
            }
        }
    }

    private CardOrganizer getCardOrganizer() {
        switch (order) {
            case "recent-mistakes-first":
                return new RecentMistakesFirstSorter();
            default:
                return new RandomSorter();
        }
    }

    private void askQuestion(Scanner scanner, FlashCard card) {
        String question = invertCards ? card.getAnswer() : card.getQuestion();
        String correctAnswer = invertCards ? card.getQuestion() : card.getAnswer();

        System.out.println("Question: " + question);
        String userAnswer = scanner.nextLine().trim();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            System.out.println("Correct!");
            correctCounts.put(question, correctCounts.getOrDefault(question, 0) + 1);
        } else {
            System.out.println("Wrong! Correct answer is: " + correctAnswer);
            mistakeCounts.put(question, mistakeCounts.getOrDefault(question, 0) + 1);
        }
    }

    private void evaluateAchievements() {
        boolean allCorrect = cards.stream().allMatch(card -> correctCounts.getOrDefault(card.getQuestion(), 0) > 0);
        boolean repeatAchieved = mistakeCounts.values().stream().anyMatch(count -> count > 5);
        boolean confidentAchieved = correctCounts.values().stream().anyMatch(count -> count >= 3);

        System.out.println("Achievements:");
        if (allCorrect) System.out.println("  - CORRECT: All cards were answered correctly at least once.");
        if (repeatAchieved) System.out.println("  - REPEAT: A card was answered incorrectly more than 5 times.");
        if (confidentAchieved) System.out.println("  - CONFIDENT: A card was answered correctly at least 3 times.");
    }

    private void printHelp() {
        System.out.println("Usage: flashcard <cards-file> [options]");
        System.out.println("Options:");
        System.out.println("  --help            Show this help message");
        System.out.println("  --order <order>   Sorting method: random, worst-first, recent-mistakes-first");
        System.out.println("  --repetitions <n> Number of times each card should be repeated");
        System.out.println("  --invertCards     Swap question and answer");
    }
}

interface CardOrganizer {
    List<FlashCard> organize(List<FlashCard> cards, Map<String, Integer> mistakeCounts);
}

class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<FlashCard> organize(List<FlashCard> cards, Map<String, Integer> mistakeCounts) {
        List<FlashCard> sorted = new ArrayList<>(cards);
        sorted.sort((a, b) -> Integer.compare(mistakeCounts.getOrDefault(b.getQuestion(), 0),
                mistakeCounts.getOrDefault(a.getQuestion(), 0)));
        return sorted;
    }
}

class RandomSorter implements CardOrganizer {
    @Override
    public List<FlashCard> organize(List<FlashCard> cards, Map<String, Integer> mistakeCounts) {
        List<FlashCard> shuffled = new ArrayList<>(cards);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}

class FlashCard {
    private final String question;
    private final String answer;

    public FlashCard(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}