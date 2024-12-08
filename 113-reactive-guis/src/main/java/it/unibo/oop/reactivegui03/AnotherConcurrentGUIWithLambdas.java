package it.unibo.oop.reactivegui03;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

// Enable copy-paste detection suppression for this file,
// because the point of the exercise isn't avoid reusing code.
// CPD-OFF

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUIWithLambdas extends JFrame {

    private static final int TIMEOUT_TIME = 10_000;

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUIWithLambdas() {
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

        final var counter = new CounterThread();

        up.addActionListener((e) -> counter.countUp());
        down.addActionListener((e) -> counter.countDown());
        stop.addActionListener((e) -> counter.stopCounting());

        this.getContentPane().add(panel);
        this.setVisible(true);

        counter.start();
        new Thread(() -> {
            try {
                Thread.sleep(TIMEOUT_TIME);
                counter.stopCounting();
                disableButtons(up, down, stop);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Disables a collection of buttons.
     * 
     * @param buttons the buttons to disable.
     */
    private void disableButtons(final JButton... buttons) {
        for (final var button : buttons) {
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
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUIWithLambdas.this.display.setText(nextText));
                    this.counter += this.countingUp ? 1 : -1;
                    sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
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
}
