public class FlashCard {
    private String question;
    private String answer;
    private int correctCount = 0;
    private int totalAttempts = 0;
    private long lastMistakeTime = 0;

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

    public int getCorrectCount() {
        return correctCount;
    }

    public void incrementCorrectCount() {
        correctCount++;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void incrementAttempts() {
        totalAttempts++;
    }

    public void setLastMistakeTime(long time) {
        lastMistakeTime = time;
    }

    public long getLastMistakeTime() {
        return lastMistakeTime;
    }
}