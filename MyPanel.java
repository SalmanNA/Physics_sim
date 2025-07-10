import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Main panel that handles rendering, physics update, and SAT collision detection.
 */
public class MyPanel extends JPanel implements ActionListener {
    private final int width;
    private final int height;
    private boolean isMouseHeld = false;
    private boolean isMoving = false;
    private Timer timer;
    private double deltaTime = 0.0;
    private long lastFrameTime = System.nanoTime();
    private List<Shape> shapes = new ArrayList<>();
    private int mouseX = 0;
    private int mouseY = 0;
    private int heldIndex = -1;
    private final double gravity = 9810; // pixels per second^2

    public MyPanel() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.width;
        height = screenSize.height - 25;
        setPreferredSize(new Dimension(width, height));

        // Sample shapes
        for (int i = 1; i < 10; i++) {
            shapes.add(new Circle(i * 60, 100, 50, 50, 0, 0));
        }
        // Add more shapes (e.g., Square) here...

        timer = new Timer(1000 / 165, this);
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isMouseHeld = true;
                mouseX = e.getX(); mouseY = e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                isMouseHeld = false;
                mouseX = e.getX(); mouseY = e.getY();
                isMoving = false;
                if (heldIndex >= 0) {
                    Shape s = shapes.get(heldIndex);
                    s.hVel = (s.xPos - s.xPosPrev) / deltaTime;
                    s.vVel = (s.yPos - s.yPosPrev) / deltaTime;
                    s.setHeld(false);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isMouseHeld) {
                    mouseX = e.getX(); mouseY = e.getY();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, width, height);
        for (Shape s : shapes) {
            s.draw(g2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long current = System.nanoTime();
        deltaTime = (current - lastFrameTime) / 1e9;
        lastFrameTime = current;

        // Physics update
        for (int i = 0; i < shapes.size(); i++) {
            Shape s = shapes.get(i);
            s.xPosPrev = s.xPos;
            s.yPosPrev = s.yPos;

            // Dragging
            if (!isMoving && isMouseHeld && mouseX >= s.xPos && mouseX <= s.xPos + s.width
                    && mouseY >= s.yPos && mouseY <= s.yPos + s.height) {
                isMoving = true;
                heldIndex = i;
                s.setHeld(true);
            }

            if (s.isHeld()) {
                s.xPos = mouseX - s.width / 2;
                s.yPos = mouseY - s.height / 2;
                s.hVel = s.vVel = 0;
            } else {
                // Gravity
                if (s.yPos + s.height < height) s.vVel += gravity * deltaTime;
                // Integrate
                s.xPos += s.hVel * deltaTime;
                s.yPos += s.vVel * deltaTime;
            }
            // Wall/ground
            collisionDetect(s);
        }

        // SAT collisions
        handleShapeCollisions();

        repaint();
    }

    private void collisionDetect(Shape s) {
        // Ground
        if (s.yPos + s.height >= height) {
            double overshoot = (s.yPos + s.height) - height;
            s.yPos -= overshoot;
            s.vVel = -s.vVel * 0.7;
            s.hVel *= 0.75;
        }
        // Right wall
        if (s.xPos + s.width >= width) {
            double overshoot = (s.xPos + s.width) - width;
            s.xPos -= overshoot;
            s.hVel = -s.hVel * 0.75;
        } else if (s.xPos <= 0) {
            double overshoot = -s.xPos;
            s.xPos += overshoot;
            s.hVel = -s.hVel * 0.75;
        }
    }

    private void handleShapeCollisions() {
        int n = shapes.size();
        for (int i = 0; i < n; i++) {
            Shape A = shapes.get(i);
            Point2D.Double[] meshA = A.getCoords();
            for (int j = i + 1; j < n; j++) {
                Shape B = shapes.get(j);
                Point2D.Double[] meshB = B.getCoords();
                if (shapesCollideSAT(meshA, meshB)) {
                    // Simple response: swap velocities
                    double hx = A.hVel, vy = A.vVel;
                    A.hVel = B.hVel; A.vVel = B.vVel;
                    B.hVel = hx;    B.vVel = vy;
                }
            }
        }
    }

    private boolean shapesCollideSAT(Point2D.Double[] A, Point2D.Double[] B) {
        if (!boundingBoxesOverlap(A, B)) return false;
        List<Point2D.Double> axes = new ArrayList<>();
        collectAxes(A, axes);
        collectAxes(B, axes);
        for (Point2D.Double axis : axes) {
            double[] pA = projectMesh(A, axis);
            double[] pB = projectMesh(B, axis);
            if (pA[1] < pB[0] || pB[1] < pA[0]) return false;
        }
        return true;
    }

    private void collectAxes(Point2D.Double[] mesh, List<Point2D.Double> axes) {
        for (int t = 0; t < mesh.length; t += 3) {
            addAxis(mesh[t], mesh[t+1], axes);
            addAxis(mesh[t+1], mesh[t+2], axes);
            addAxis(mesh[t+2], mesh[t], axes);
        }
    }

    private void addAxis(Point2D.Double p1, Point2D.Double p2, List<Point2D.Double> axes) {
        double dx = p2.x - p1.x, dy = p2.y - p1.y;
        Point2D.Double axis = new Point2D.Double(-dy, dx);
        double len = Math.hypot(axis.x, axis.y);
        if (len > 0) {
            axis.x /= len; axis.y /= len;
            axes.add(axis);
        }
    }

    private double[] projectMesh(Point2D.Double[] mesh, Point2D.Double axis) {
        double min = dot(mesh[0], axis), max = min;
        for (Point2D.Double p : mesh) {
            double proj = dot(p, axis);
            if (proj < min) min = proj;
            if (proj > max) max = proj;
        }
        return new double[]{min, max};
    }

    private double dot(Point2D.Double p, Point2D.Double a) {
        return p.x * a.x + p.y * a.y;
    }

    private boolean boundingBoxesOverlap(Point2D.Double[] A, Point2D.Double[] B) {
        double minAx = Double.POSITIVE_INFINITY, minAy = Double.POSITIVE_INFINITY;
        double maxAx = Double.NEGATIVE_INFINITY, maxAy = Double.NEGATIVE_INFINITY;
        for (Point2D.Double p : A) {
            minAx = Math.min(minAx, p.x); minAy = Math.min(minAy, p.y);
            maxAx = Math.max(maxAx, p.x); maxAy = Math.max(maxAy, p.y);
        }
        double minBx = Double.POSITIVE_INFINITY, minBy = Double.POSITIVE_INFINITY;
        double maxBx = Double.NEGATIVE_INFINITY, maxBy = Double.NEGATIVE_INFINITY;
        for (Point2D.Double p : B) {
            minBx = Math.min(minBx, p.x); minBy = Math.min(minBy, p.y);
            maxBx = Math.max(maxBx, p.x); maxBy = Math.max(maxBy, p.y);
        }
        return !(maxAx < minBx || maxBx < minAx || maxAy < minBy || maxBy < minAy);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SAT Collision Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MyPanel panel = new MyPanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

