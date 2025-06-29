package main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class Check {
    /* The following three methods that given shape is simple, not-linear and doesn't contain duplicate points.*/
    public static boolean isSimple(LinkedList<Point> list){
        Point pr = null;
        Point sc;
        list.add(list.getFirst());
        for(Point a: list){
            sc = null;
            if(pr==null){
                pr = a;
            } else {
                for(Point b: list){
                    if(sc==null){
                        sc = b;
                    } else {
                        if(doLinesIntersect(a,pr,sc,b)){
                            list.removeLast();
                            return false;
                        }
                        sc = b;
                    }
                }
                pr=a;
            }
        }
        list.removeLast();
        return true;
    }
    public static double getArea(LinkedList<Point> points){
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        int piv =0;
        for(Point obj: points){
            x[piv]=obj.x;
            y[piv++]=obj.y;
        }
        double area = 0;
        int j = points.size()-1;
        for (int i=0; i<x.length; i++)
        { area +=  (x[j]+x[i]) * (y[j]-y[i]);
            j = i;
        }
        return area/2.0;
    }
    public static boolean duplicatePoints(LinkedList<Point> points){
        HashSet<String> set = new HashSet<>();
        for(Point obj: points){
            if(set.contains(obj.toString())){
                return true;
            } else {
                set.add(obj.toString());
            }
        }
        return false;
    }

    /* The following three methods checks if a given triangle is thin or not. */
    public static double distance(Point a, Point b){
        return Math.sqrt(Math.pow((a.x-b.x),2)+Math.pow((a.y-b.y),2));
    }
    public static double angleAt(Point a, Point b, Point c) {
        double abx = a.x - b.x;
        double aby = a.y - b.y;
        double cbx = c.x - b.x;
        double cby = c.y - b.y;

        double dot = abx * cbx + aby * cby;
        double mag1 = Math.hypot(abx, aby);
        double mag2 = Math.hypot(cbx, cby);
        if (mag1 == 0 || mag2 == 0) return 0; // Avoid division by 0

        double cosTheta = dot / (mag1 * mag2);
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
        return Math.toDegrees(Math.acos(cosTheta));
    }
    public static boolean thin(Triangle triangle){
        LinkedList<Point> list = triangle.points;
        Point a = list.get(0);
        Point b = list.get(1);
        Point c = list.get(2);
        double val1 = angleAt(a,b,c);
        double val2 = angleAt(b,a,c);
        double val3  =angleAt(a,c,b);
        return val1<15 || val2<15 || val3<15;
    }

    //Eligible method checks polygon - obj eligibility.
    public static boolean eligible(Polygon polygon, Triangle obj){
        if(!isInside(obj,polygon)) return false;
        if(intruding(polygon,obj)) return false;
        return true;
    }

    //The eligible triangles of a polygon is then called for appropriateTriangle to ensure everything is eligible.
    public static boolean appropriateTriangle(LinkedList<Triangle> triangles, Triangle obj){
        for(Triangle piv: triangles){
            if(obj.equals(piv)) return false;
            if(Check.intruding(obj,piv)) return false;
            if(Check.isInside(obj,piv) || Check.isInside(piv,obj)) return false;
        }
        return true;
    }

    /* The following methods are helper methods that eligible and appropriateTriangle methods use */
    public static boolean intruding(Geometry a, Geometry b){
        LinkedList<Point> aTemp = new LinkedList<>(a.points);
        LinkedList<Point> bTemp = new LinkedList<>(b.points);
        aTemp.add(aTemp.getFirst());
        bTemp.add(bTemp.getFirst());
        Point pa = null;
        Point pb;
        for(Point ca: aTemp){
            if(pa == null){
                pa = ca;
            } else {
                pb = null;
                for(Point cb: bTemp){
                    if(pb == null){
                        pb = cb;
                    } else {
                        if(pa.equals(pb) || pa.equals(cb) || ca.equals(pb) ||ca.equals(cb)){
                            pb = cb;
                            continue;
                        }
                        if(doLinesIntersect(pa,ca,pb,cb)){
                            return true;
                        }
                        pb = cb;
                    }
                }
                pa = ca;
            }
        }
        return false;
    }
    public static boolean areAdjacentSegmentsOverlapping(Point p1, Point q1, Point p2, Point q2) {
        if (orientation(p1, q1, p2) != 0 || orientation(p1, q1, q2) != 0)
            return false;

        return onSegment(p1, p2, q1) || onSegment(p1, q2, q1) ||
                onSegment(p2, p1, q2) || onSegment(p2, q1, q2);
    }
    public static boolean doLinesIntersect(Point p1, Point q1, Point p2, Point q2) {
        if(p1.equals(p2) && q1.equals(q2)){
            return false;
        }
        if(p1.equals(q2) && q1.equals(p2)){
            return false;
        }
        if(p1.equals(p2) || p1.equals(q2) || q1.equals(p2) || q1.equals(q2)){
            return areAdjacentSegmentsOverlapping(p1,q1,p2,q2);
        }
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4) return true;

        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false;
    }
    public static int orientation(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);
        if (Math.abs(val) < 1e-3) return 0;
        return (val > 0) ? 1 : 2;
    }
    public static boolean onSegment(Point a, Point b, Point c) {
        // checks if point b lies on segment ac
        return Math.min(a.x, c.x) <= b.x && b.x <= Math.max(a.x, c.x) &&
                Math.min(a.y, c.y) <= b.y && b.y <= Math.max(a.y, c.y) &&
                Math.abs((c.x - a.x) * (b.y - a.y) - (b.x - a.x) * (c.y - a.y)) < 1e-9;
    }
    public static boolean isInside(Geometry triangle, Geometry polygon){
        double x = 0, y = 0;
        for (Point a : triangle.points) {
            x += a.x;
            y += a.y;
        }
        x /= 3.0;
        y /= 3.0;
        Point testPoint = new Point(x, y);

        LinkedList<Point> pts = new LinkedList<>(polygon.points);
        pts.add(pts.get(0)); // close the polygon

        boolean inside = false;
        for (int i = 0; i < pts.size() - 1; i++) {
            Point a = pts.get(i);
            Point b = pts.get(i + 1);

            if (onSegment(a, testPoint, b)) {
                return true; // point is on edge
            }

            if (Math.abs(a.y - b.y) < 1e-9) continue; // horizontal edge

            boolean intersect = ((a.y > y) != (b.y > y)) &&
                    (x < (b.x - a.x) * (y - a.y) / (b.y - a.y) + a.x);

            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }
}
