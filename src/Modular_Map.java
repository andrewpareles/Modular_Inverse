
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.HashMap;

public class Modular_Map {

    // modMap maps (m, N) to a map of all values (a -> a1)
    // 1 <= a < m
    // a1 is the inverse of a
    private HashMap<Pair<Integer, Integer>, HashMap<Integer, Integer>> modMap = new HashMap<>();

    private static JFrame frame;
    private static JTextField min;
    private static JTextField max;
    private static JSlider slider;
    private static JLabel m;
    private static JTextField N;

    private static int EXTRA_SPACE = 150;
    private static int HEIGHT = 600 + EXTRA_SPACE;
    private static int WIDTH = 600;
    private static double percentFillScreen = .85;


    private static Modular_Map map = new Modular_Map();


    static {
        resetFrame(true);
    }


    public static void resetFrame(boolean firstTime) {
        if (firstTime) {
            frame = new JFrame();
            min = new JTextField("1");
            max = new JTextField("100");
            N = new JTextField("1");
            slider = new JSlider(Integer.parseInt(min.getText()), Integer.parseInt(max.getText()));
            m = new JLabel(Integer.toString(slider.getValue()));
        } else {
            WIDTH = frame.getWidth();
            HEIGHT = frame.getHeight();
        }
        min.setSize(new Dimension(80, 40));
        max.setSize(new Dimension(80, 40));
        N.setSize(new Dimension(80, 40));
        slider.setSize(new Dimension(WIDTH - 50, 60));
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        min.setLocation(10, HEIGHT - 170);
        max.setLocation(WIDTH - 110, HEIGHT - 170);
        N.setLocation(WIDTH - 120, 30);
        slider.setLocation(10, HEIGHT - 125);
        m.setLocation(40, 0);
        m.setSize(new Dimension(100, 100));
        m.setFont(new Font(null, Font.PLAIN, 50));

        frame.getContentPane().add(min);
        frame.getContentPane().add(max);
        frame.getContentPane().add(slider);
        frame.getContentPane().add(m);
        frame.getContentPane().add(N);

        frame.getContentPane().add(new Box(0));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setSize(WIDTH, HEIGHT);

        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) throws InterruptedException {
        Integer prevM = null;

        String prevMin = min.getText();
        String prevMax = min.getText();
        String prevN = N.getText();


        int prevWidth = frame.getWidth();
        int prevHeight = frame.getHeight();

        while (true) {

            int m = slider.getValue();

            try {
                String minValS = min.getText();
                int minVal = Integer.parseInt(minValS);

                String maxValS = max.getText();
                int maxVal = Integer.parseInt(maxValS);

                String nS = N.getText();
                int n = Integer.parseInt(nS);

                if (n < 0 || minVal <= 0 || maxVal <= 0 || maxVal <= minVal) throw new Exception();

                slider.setMinimum(minVal);
                slider.setMaximum(maxVal);

                prevMin = minValS;
                prevMax = maxValS;
                if (!prevN.equals(nS)) {
                    prevN = nS;

                    map.calculateForMod(m);

                    frame.repaint();
                    Modular_Map.m.setText(Integer.toString(m));
                    Thread.sleep(10);
                    drawModMap(map.getModMap(new Pair<>(m, Integer.parseInt(N.getText()))));

                }


            } catch (Exception e) {
                min.setText(prevMin);
                max.setText(prevMax);
                N.setText(prevN);

            }


            Thread.sleep(100);
            if (prevM == null || prevM != m) {
                prevM = m;
                map.calculateForMod(m);

                frame.repaint();
                Modular_Map.m.setText(Integer.toString(m));
                Thread.sleep(10);
                drawModMap(map.getModMap(new Pair<>(m, Integer.parseInt(N.getText()))));
            }

            if (prevHeight != frame.getHeight() || prevWidth != frame.getWidth()) {
                Thread.sleep(1000);
                resetFrame(false);

                prevWidth = frame.getWidth();
                prevHeight = frame.getHeight();

                Thread.sleep(10);
                drawModMap(map.getModMap(new Pair<>(m, Integer.parseInt(N.getText()))));

            }

        }

    }

    /**
     * @param map a map from a to a1 for m, where m = map.size()
     */
    public static void drawModMap(HashMap<Integer, Integer> map) {

        Graphics2D g2d = (Graphics2D) frame.getGraphics();

        final int FONT_SIZE = 10;

        final int n = map.size();
        final double dTheta = 2 * Math.PI / n;
        double theta = dTheta;

        int xs[] = new int[n];
        int ys[] = new int[n];

        for (int i = n - 1; i >= 0; i--) {

            xs[i] = (int) Math.round(WIDTH * (1 + percentFillScreen * -Math.sin(theta)) / 2);
            ys[i] = (int) Math.round((HEIGHT - EXTRA_SPACE) * (1 - percentFillScreen * Math.cos(theta)) / 2) + 50;
            theta += dTheta;
        }

        for (int i = 0; i < n; i++) {
            g2d.setStroke(new BasicStroke(3));

            g2d.drawString(Integer.toString(i), xs[i] - FONT_SIZE / 2, ys[i]);
            Integer inverse = map.get(i);
            if (inverse != null) {
                if (inverse == i)
                    g2d.fillOval(xs[i] - 6, ys[i], 12, 12);
                else
                    g2d.draw(new Line2D.Float(xs[i], ys[i], xs[inverse], ys[inverse]));
            }

        }

        // System.out.println(map);


    }

    public Modular_Map() {
    }

    public void calculateForMod(int m) {
        int n = Integer.parseInt(N.getText());
        Pair<Integer, Integer> p = new Pair<>(m, n);

        //already calculated:
        if (modMap.containsKey(p)) return;

        int a = 0;
        Integer a1;
        while (a < m) {
            for (a1 = 1; a1 < m; a1++) {
                if ((a * a1) % m == n)
                    break;
            }
            if (a1 == m) a1 = null;

            modMap.computeIfAbsent(p, k -> new HashMap<>());
            modMap.get(p).put(a, a1);

            a++;
        }
    }

    public HashMap<Integer, Integer> getModMap(Pair<Integer, Integer> p) {
        return modMap.get(p);
    }
}
