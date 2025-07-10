import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Circle implemented as a fan of triangles for SAT collision detection.
 */
public class Circle extends Shape {
    private final int segments = 20;
    private Point2D.Double[] coords;

    public Circle(double xPos, double yPos, double width, double height, double hVel, double vVel) {
        super(xPos, yPos, width, height, hVel, vVel);
        coords = new Point2D.Double[segments * 3];
    }

    @Override
    public Point2D.Double[] getCoords() {
        double angleStep = 2 * Math.PI / segments;
        double radiusX = width / 2;
        double radiusY = height / 2;
        for (int i = 0; i < segments; i++) {
            double angle1 = i * angleStep;
            double angle2 = (i + 1) * angleStep;
            // Center point
            coords[i * 3] = new Point2D.Double(xPos, yPos);
            // First outer point
            coords[i * 3 + 1] = new Point2D.Double(
                xPos + radiusX * Math.cos(angle1),
                yPos + radiusY * Math.sin(angle1)
            );
            // Second outer point
            coords[i * 3 + 2] = new Point2D.Double(
                xPos + radiusX * Math.cos(angle2),
                yPos + radiusY * Math.sin(angle2)
            );
        }
        return coords;
    }

    @Override
    public void draw(Graphics2D g) {
        Point2D.Double[] mesh = getCoords();
        for (int t = 0; t < mesh.length; t += 3) {
            Path2D.Double tri = new Path2D.Double();
            tri.moveTo(mesh[t].x, mesh[t].y);
            tri.lineTo(mesh[t + 1].x, mesh[t + 1].y);
            tri.lineTo(mesh[t + 2].x, mesh[t + 2].y);
            tri.closePath();
            g.fill(tri);
        }
    }
}

