import java.util.*;

public class RecentMistakesFirstSorter implements CardOrganizer {
    public List<FlashCard> organize(List<FlashCard> cards) {
        List<FlashCard> mistakes = new ArrayList<>();
        List<FlashCard> others = new ArrayList<>();

        for (FlashCard card : cards) {
            if (card.getLastMistakeTime() > 0) {
                mistakes.add(card);
            } else {
                others.add(card);
            }
        }

        List<FlashCard> result = new ArrayList<>();
        result.addAll(mistakes);
        result.addAll(others);
        return result;
    }
}
