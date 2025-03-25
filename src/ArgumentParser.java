import java.util.*;

public class ArgumentParser {
    public String cardsFile;
    public String order = "random";
    public boolean help = false;
    public int repetitions = 1;
    public boolean invertCards = false;

    public ArgumentParser(String[] args) {
        if (args.length == 0) {
            help = true;
            return;
        }
        cardsFile = args[0];
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> help = true;
                case "--order" -> {
                    if (i + 1 < args.length)
                        order = args[++i];
                }
                case "--repetitions" -> {
                    if (i + 1 < args.length)
                        repetitions = Integer.parseInt(args[++i]);
                }
                case "--invertCards" -> invertCards = true;
            }
        }
    }
}
