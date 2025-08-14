import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
* 05506014 Computer Graphics
* Assignment 1 : WHHAT IF I REBORNED
* 
* Made by : 66050010 Kittamet
            66050145 Nattawut
* Computer Science, KMITL
*/

public class Assignment1 extends JPanel implements Runnable{
    public static void main(String[] args) {
        Assignment1 m = new Assignment1();
        JFrame f = new JFrame();

        // Viewport
        f.add(m); // add panel to frame
        f.setTitle("ASSIGNMENT 1 : WHAT IF I REBORNED"); // title
        f.setSize(600, 600); // set size
        f.setResizable(true); // make it unresizable
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // make it close when exit
        f.setVisible(true); // make it visible

        // Animation
        (new Thread(m)).start();
    }
    
    @Override
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime;

        while (true) {
            // Control time
            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;

            // Update logic

            // Display
            repaint();

            // Control frame rate (adjust sleep time as needed)
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
         super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Call your drawing methods here
        pic1(g2d);
    }

    private void pic1(Graphics2D g2d) {
        g2d.translate(173.33, 600);
        bezierCurve(g2d, 0, 0, -0.9, -2.36, -2.7, -4.09, -3.85, -6.4, 1);
        bezierCurve(g2d, BOTTOM_ALIGNMENT, WIDTH, SOMEBITS, PROPERTIES, HEIGHT, FRAMEBITS, ERROR, ALLBITS, ABORT);
    }

    private void pic2(Graphics2D g2d) {
        // test add and commit
    }

    private void pic3(Graphics2D g2d) {
        
    }

    private void pic4(Graphics2D g2d) {
        
    }

    private void pic5(Graphics2D g2d) {
        
    }

    private void pic6(Graphics2D g2d) {
        
    }

    private void pic7(Graphics2D g2d) {
        
    }

    private void pic8(Graphics2D g2d) {
        
    }

    private void pic9(Graphics2D g2d) {
        
    }

    private void pic10(Graphics2D g2d) {
        
    }

    private void pic11(Graphics2D g2d) {
        
    }

    private void pic12(Graphics2D g2d) {
        
    }

    private void plot(Graphics g, int x, int y, int size) {
        g.fillRect(x, y, size, size);
    }

    
    private void bezierCurve(Graphics2D g2, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int size) {
        for (double t = 0; t <= 1; t += 0.001) {
            double xt = Math.pow(1 - t, 3) * x1 +
            3 * Math.pow(1 - t, 2) * t * x2 +
            3 * (1 - t) * Math.pow(t, 2) * x3 +
            Math.pow(t, 3) * x4;
            
            double yt = Math.pow(1 - t, 3) * y1 +
            3 * Math.pow(1 - t, 2) * t * y2 +
            3 * (1 - t) * Math.pow(t, 2) * y3 +
            Math.pow(t, 3) * y4;

            g2.fillRect((int) Math.round(xt), (int) Math.round(yt), size, size);
        }
    }

    private void bresenhamLine(Graphics2D g, int x1, int y1, int x2, int y2) {

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        boolean isSwap = false;

        if (dy > dx) {
            int tmp = dx;
             dy = dx;
             dx = tmp;
             isSwap = true;
        }
        int D = 2 * dy - dx;

        int x = x1;
        int y = y1;

        for (int i = 1; i <= dx; i++) {
            plot(g, x, y, 1);
            if (D >= 0) {
                if (isSwap) {
                    x += sx;
                }
                else {
                    y += sy;
                }

                D -= 2 * dx;
            }

            if (isSwap) {
                y += sy;
            }
            else {
                x += sx;
            }

            D += 2 * dy;
        }
    }

    private BufferedImage floodFill(BufferedImage m, int x, int y, Color targetColor, Color replaceColor) {
        
        Graphics2D g2 = m.createGraphics(); // สร้างปากกาขึ้นจากรูป
        Queue<Point> q = new LinkedList<>();

        if (m.getRGB(x, y) == targetColor.getRGB()) {
            g2.setColor(replaceColor);
            plot(g2, x, y, 1);
            q.add(new Point(x, y));
        }

        while (!q.isEmpty()) {
            Point p = q.poll();

            // south
            if (p.y < 600 && m.getRGB(p.x, p.y + 1) == targetColor.getRGB()) {
                g2.setColor(replaceColor);
                plot(g2, p.x, p.y + 1, 1);
                q.add(new Point(p.x, p.y + 1));
            }

            // north
             if (p.y > 0 && m.getRGB(p.x, p.y - 1) == targetColor.getRGB()) {
                g2.setColor(replaceColor);
                plot(g2, p.x, p.y - 1, 1);
                q.add(new Point(p.x, p.y - 1));
            }

            // west
            if (p.x > 0 && m.getRGB(p.x - 1, p.y) == targetColor.getRGB()) {
                g2.setColor(replaceColor);
                plot(g2, p.x - 1, p.y, 1);
                q.add(new Point(p.x - 1, p.y));
            }

            // east
            if (p.x < 600 && m.getRGB(p.x + 1, p.y) == targetColor.getRGB()) {
                g2.setColor(replaceColor);
                plot(g2, p.x + 1, p.y, 1);
                q.add(new Point(p.x + 1, p.y));
            }

        }

        return m;
    }

    private void midpointEllipse(Graphics g, int xc, int yc, int a, int b) {
        int x, y, d;
        int a2 = a*a, b2 = b*b;
        int twoA2 = 2*a2, twoB2 = 2*b2;

        // REGION 1
        x = 0;
        y = b;
        d = Math.round(b2 - a2*b + a2/4);

        while (b2*x <= a2*y) {
            plot(g, x + xc, y + yc, 3);
            plot(g, -x + xc, y + yc, 3);
            plot(g, x + xc, -y + yc, 3);
            plot(g, -x + xc, -y + yc, 3);

            x++;

            if (d >= 0) {
                y--;
                d = d - twoA2*y;
            }
            d = d + twoB2*x + b2;
        }

        // REGION 2
        x = a;
        y = 0;
        d = Math.round(a2 - b2*a + b2/4);

        while (b2*x >= a2*y) {
            plot(g, x + xc, y + yc, 3);
            plot(g, -x + xc, y + yc, 3);
            plot(g, x + xc, -y + yc, 3);
            plot(g, -x + xc, -y + yc, 3);

            y++;

            if (d >= 0) {
                x--;
                d = d  - twoB2*x;
            }
            d = d + twoA2*y + a2;
        }
    }

    private void midpointCircle(Graphics g, int xc, int yc, int r) {
        int x = 0;
        int y = r;
        int d = 1 - r;

        while (x <= y) {
            plot(g, x + xc, y + yc, 3);
            plot(g, -x + xc, y + yc, 3);
            plot(g, x + xc, -y + yc, 3);
            plot(g, -x + xc, -y + yc, 3);
            plot(g, y + xc, x + yc, 3);
            plot(g, -y + xc, x + yc, 3);
            plot(g, y + xc, -x + yc, 3);
            plot(g, -y + xc, -x + yc, 3);

            x++;

            if (d >= 0) {
                y--;
                d = d - 2 * y;
            }
            d = d + 2 * x + 1;
        }
    }
}
