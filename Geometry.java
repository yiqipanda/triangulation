package main;

import main.CustomExceptions.InvalidGeometryException;

import java.util.*;

public abstract class Geometry {
    public LinkedList<Point> points;
    HashSet<String> setOfPoints;

    /* For the most common Errors we may catch, I assigned method to handle them */
    void commonErrorHandler(LinkedList<Point> list) {
        for (Point el : list) {
            Point point = el.copy();
            points.addFirst(point);
        }
        if(Check.duplicatePoints(points)){
            throw new InvalidGeometryException("duplicate points found.");
        }
        if(!Check.isSimple(points)){
            throw new InvalidGeometryException("non-simple geometry.");
        }

    }
    public int[] getXcoords(){
        int piv = 0;
        int[] result = new int[points.size()];
        for(Point point: points){
            result[piv++]=(int)point.x;
        }
        return result;
    }
    public int[] getYcoords(){
        int piv = 0;
        int[] result = new int[points.size()];
        for(Point point: points){
            result[piv++]=(int)point.y;
        }
        return result;
    }

    public static void fixDirection(LinkedList<Point> list){
        double area = Check.getArea(list);
        if(area>0){
            Collections.reverse(list);
        }
    }
}
