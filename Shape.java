import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * Base class for all shapes that can be drawn and participate in SAT collision detection.
 * Each shape must maintain a coords array where every consecutive triplet of Point2D.Double
 * represents a triangle in the mesh.
 */
public abstract class Shape {
    // Current and previous positions for collision response
    protected double xPos;
    protected double yPos;
    protected double xPosPrev;
    protected double yPosPrev;
    // Velocities
    protected double hVel;
    protected double vVel;
    // Dimensions
    protected double width;
    protected double height;
    // Whether the shape is currently held by the mouse
    protected boolean isHeld = false;

    // Mesh of triangles: flat array, every 3 points is one triangle
    protected Point2D.Double[] coords;

    /**
     * @param xPos initial x position
     * @param yPos initial y position
     * @param width width of bounding box or diameter-based size
     * @param height height of bounding box or diameter-based size
     * @param hVel initial horizontal velocity
     * @param vVel initial vertical velocity
     */
    public Shape(double xPos, double yPos, double width, double height, double hVel, double vVel) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.hVel = hVel;
        this.vVel = vVel;
    }

    /**
     * Draw the shape; must rebuild coords[] to current transformed mesh.
     */
    public abstract void draw(Graphics2D g);

    /**
     * Returns the flat array of Point2D.Double where every 3 consecutive points form one triangle.
     */
    public abstract Point2D.Double[] getCoords();

    // Basic accessors
    public double getX() { return xPos; }
    public double getY() { return yPos; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getHVel() { return hVel; }
    public double getVVel() { return vVel; }
    public boolean isHeld() { return isHeld; }
    public void setHeld(boolean held) { this.isHeld = held; }
}

