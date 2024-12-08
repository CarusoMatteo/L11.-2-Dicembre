package it.unibo.oop.lab.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.io.Serial;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * <br>
 * 1) Convert to lowercase
 * <br>
 * 2) Count the number of chars
 * <br>
 * 3) Count the number of lines
 * <br>
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word.
 * "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    @Serial
    private static final long serialVersionUID = 1760990730218643730L;

    private enum Command {
        /**
         * Commands.
         */
        IDENTITY("No modifications", Function.identity()),
        TO_LOWER("To lower case", Command::toLowerCase),
        COUNT_CHARS("Count the number of chars", Command::numOfChars),
        COUNT_LINES("Count the number of lines", Command::numOfLines),
        SORT_ALPHABETICAL("List all the words in alphabetical order", Command::sortAlphabetical),
        COUNT_WORDS("Write the count for each word", Command::countWords);

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            commandName = name;
            fun = process;
        }

        @Override
        public String toString() {
            return commandName;
        }

        public String translate(final String s) {
            return fun.apply(s);
        }

        private static String toLowerCase(final String s) {
            return s.toLowerCase(Locale.getDefault());
        }

        private static String numOfChars(final String s) {
            return String.valueOf(s.length());
        }

        private static String numOfLines(final String s) {
            return String.valueOf(s.lines().count());
        }

        private static String sortAlphabetical(final String s) {
            return List.of(s.split(" "))
                    .stream()
                    .sorted(String::compareTo)
                    .map(str -> str.concat(" "))
                    .reduce(String::concat)
                    .orElse("");
        }

        private static String countWords(final String s) {
            return List.of(s.split(" "))
                    .stream()
                    .collect(Collectors.toMap(word -> word, word -> 1, (a, b) -> a + b))
                    .entrySet().stream()
                    .map(entry -> entry.getKey() + " -> " + entry.getValue() + " ")
                    .reduce(String::concat)
                    .orElse("");
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev -> right.setText(
                ((Command) Objects.requireNonNull(combo.getSelectedItem()))
                        .translate(left.getText())));
        panel1.add(apply, BorderLayout.SOUTH);
        setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
