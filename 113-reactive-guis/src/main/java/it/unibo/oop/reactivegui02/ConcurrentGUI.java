package it.unibo.oop.reactivegui02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
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
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));

                    if (this.countingUp) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
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
}
