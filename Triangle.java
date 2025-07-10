import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Triangle{
    Point p1;
    Point p2;
    Point p3;

    Triangle(Point p1,Point p2, Point p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
    public static List<int[]> triangulate(List<Point2D> verts) {
        List<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < verts.size(); i++) idxs.add(i);

        List<int[]> result = new ArrayList<>();
        while (idxs.size() > 3) {
            boolean clipped = false;
            int n = idxs.size();
            for (int i = 0; i < n; i++) {
                int i0 = idxs.get((i + n - 1) % n);
                int i1 = idxs.get(i);
                int i2 = idxs.get((i + 1) % n);

                Point2D p0 = verts.get(i0), p1 = verts.get(i1), p2 = verts.get(i2);
                if (!isConvex(p0, p1, p2)) continue;                          // reflex check :contentReference[oaicite:7]{index=7}
                if (containsAny(verts, idxs, p0, p1, p2)) continue;            // no other point inside :contentReference[oaicite:8]{index=8}

                // Clip the ear
                result.add(new int[]{i0, i1, i2});
                idxs.remove(i);
                clipped = true;
                break;
            }
            if (!clipped) throw new IllegalArgumentException("Polygon not simple or degenerate");
        }
        // Last remaining triangle
        result.add(new int[]{idxs.get(0), idxs.get(1), idxs.get(2)});
        return result;
    }
    private static boolean isConvex(Point2D a, Point2D b, Point2D c) {
        double cross = (b.getX() - a.getX()) * (c.getY() - a.getY())
                     - (b.getY() - a.getY()) * (c.getX() - a.getX());
        return cross > 0;                                                // positive for CCW :contentReference[oaicite:9]{index=9}
    }

    private static boolean containsAny(List<Point2D> verts, List<Integer> idxs,
                                       Point2D a, Point2D b, Point2D c) {
        for (int idx : idxs) {
            Point2D p = verts.get(idx);
            if (p.equals(a) || p.equals(b) || p.equals(c)) continue;
            if (pointInTriangle(p, a, b, c)) return true;                  // O(n) inside test :contentReference[oaicite:10]{index=10}
        }
        return false;
    }

    private static boolean pointInTriangle(Point2D p, Point2D a, Point2D b, Point2D c) {
        double area  = cross(b, c, a);
        double area1 = cross(a, b, p);
        double area2 = cross(b, c, p);
        double area3 = cross(c, a, p);
        return (area1 >= 0 && area2 >= 0 && area3 >= 0) ||             // same sign test :contentReference[oaicite:11]{index=11}
               (area1 <= 0 && area2 <= 0 && area3 <= 0);
    }

    private static double cross(Point2D p1, Point2D p2, Point2D p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY())
             - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }
}
