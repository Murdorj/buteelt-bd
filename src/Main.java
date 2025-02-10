import java.io.*;
import java.util.*;

public class Main {
    private List<FlashCard> cards = new ArrayList<>();
    private Map<String, Integer> mistakeCounts = new HashMap<>();
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
        List<FlashCard> sortedCards = getSortedCards();

        for (int i = 0; i < repetitions; i++) {
            for (FlashCard card : sortedCards) {
                askQuestion(scanner, card);
            }
        }
    }

    private List<FlashCard> getSortedCards() {
        List<FlashCard> sorted = new ArrayList<>(cards);
        switch (order) {
            case "worst-first":
                sorted.sort(Comparator.comparingInt(c -> mistakeCounts.getOrDefault(c.getQuestion(), 0)));
                break;
            case "recent-mistakes-first":
                Collections.reverse(sorted);
                break;
            default:
                Collections.shuffle(sorted);
                break;
        }
        return sorted;
    }

    private void askQuestion(Scanner scanner, FlashCard card) {
        String question = invertCards ? card.getAnswer() : card.getQuestion();
        String correctAnswer = invertCards ? card.getQuestion() : card.getAnswer();

        System.out.println("Question: " + question);
        String userAnswer = scanner.nextLine().trim();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            System.out.println("Correct!");
        } else {
            System.out.println("Wrong! Correct answer is: " + correctAnswer);
            mistakeCounts.put(question, mistakeCounts.getOrDefault(question, 0) + 1);
        }
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
