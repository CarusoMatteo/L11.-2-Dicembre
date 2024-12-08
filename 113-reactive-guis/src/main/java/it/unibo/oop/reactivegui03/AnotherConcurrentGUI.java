package it.unibo.oop.reactivegui03;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

// Enable copy-paste detection suppression for this file,
// because the point of the exercise isn't avoid reusing code.
// CPD-OFF

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();
    private final List<JButton> buttons = new ArrayList<>(3);

    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton up = new JButton("up");
        panel.add(up);
        final JButton down = new JButton("down");
        panel.add(down);
        final JButton stop = new JButton("stop");
        panel.add(stop);
        buttons.addAll(List.of(up, down, stop));

        final var counter = new CounterThread();
        final var timeout = new TimeoutThread(counter);

        up.addActionListener((e) -> counter.countUp());
        down.addActionListener((e) -> counter.countDown());
        stop.addActionListener((e) -> counter.stopCounting());

        this.getContentPane().add(panel);
        this.setVisible(true);

        counter.start();
        timeout.start();
    }

    private void disableButtons() {
        for (final JButton button : this.buttons) {
            button.setEnabled(false);
        }
    }

    private final class CounterThread extends Thread {
        private volatile boolean countingUp = true;
        private volatile boolean stopped;

        private int counter;

        @Override
        public void run() {
            while (!this.stopped) {
                final var nextText = Integer.toString(this.counter);
                try {
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    this.counter += this.countingUp ? 1 : -1;
                    sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    e.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stopped = true;
        }

        /**
         * External command to count up.
         */
        public void countUp() {
            countingUp = true;
        }

        /**
         * External command to count down.
         */
        public void countDown() {
            countingUp = false;
        }
    }

    private final class TimeoutThread extends Thread {

        private static final int TIMEOUT_TIME = 10_000;
        private final CounterThread counter;

        private TimeoutThread(final CounterThread counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            try {
                sleep(TIMEOUT_TIME);
                this.counter.stopCounting();
                AnotherConcurrentGUI.this.disableButtons();
            } catch (final InterruptedException e) {
                /*
                 * This is just a stack trace print, in a real program there
                 * should be some logging and decent error reporting
                 */
                e.printStackTrace();
            }
        }
    }
}
