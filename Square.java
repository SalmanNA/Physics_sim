import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.util.Arrays;
//import java.awt.event.*;
//import java.util.ArrayList;
//import javax.swing.*;
public class Square extends Shape {
    Square(double xPos, double yPos,double width, double height, double hVel, double vVel) {
        super(xPos, yPos,width,height, hVel, vVel);
        
    }
    public void draw(Graphics2D g){
       // List<Point2D> verts = new ArrayList<>();
       // verts.add(new Point2D.Double(xPos-(width/2), yPos-(width/2)));
       // verts.add(new Point2D.Double(xPos-(width/2), width+yPos-(width/2)));
       // verts.add(new Point2D.Double(width+xPos-(width/2), width+yPos-(width/2)));
       // verts.add(new Point2D.Double(width+xPos-(width/2), yPos-(width/2)));
        List<Point2D> verts = Arrays.asList(
            new Point2D.Double(100, 50),
            new Point2D.Double(200, 80),
            new Point2D.Double(250, 200),
            new Point2D.Double(150, 250),
            new Point2D.Double(80, 180)
        );
          List<int[]> tris = Triangle.triangulate(verts);
        for (int[] tri : tris) {
        int[] xs = {
            (int) verts.get(tri[0]).getX(),
            (int) verts.get(tri[1]).getX(),
            (int) verts.get(tri[2]).getX()
        };
        int[] ys = {
            (int) verts.get(tri[0]).getY(),
            (int) verts.get(tri[1]).getY(),
            (int) verts.get(tri[2]).getY()
        };
        g.fillPolygon(xs, ys, 3);    // Render filled triangle
        g.drawPolygon(xs, ys, 3);    // Optional outline
    }

    }
}
