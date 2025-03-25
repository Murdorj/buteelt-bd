import java.util.*;

public class RandomSorter implements CardOrganizer {
    public List<FlashCard> organize(List<FlashCard> cards) {
        List<FlashCard> copy = new ArrayList<>(cards);
        Collections.shuffle(copy);
        return copy;
    }
}
