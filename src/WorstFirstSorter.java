import java.util.*;

public class WorstFirstSorter implements CardOrganizer {
    public List<FlashCard> organize(List<FlashCard> cards) {
        List<FlashCard> copy = new ArrayList<>(cards);
        copy.sort(Comparator.comparingInt(FlashCard::getCorrectCount));
        return copy;
    }
}
