package main;
import java.util.*;

public class Triangle extends Geometry {
    Triangle(LinkedList<Point> list) {
        points = new LinkedList<>();
        if (list.size() != 3) {
            throw new IllegalArgumentException("Expected 3 Points, found" + list.size());
        }
        setOfPoints = new HashSet<>();
        commonErrorHandler(list);
        fixDirection(points);
    }

    public boolean equals(Triangle o){
        int same = 0;
        for(Point pr: points){
            for(Point sc: o.points){
                if(pr.equals(sc)){
                    same++;
                }
            }
        }
        return same >=3;
    }
    @Override
    public String toString(){
        String result = "[";
        for(Point el: points){
            result+=" "+el;
        }
        result =result.trim();
        return result+" ]";
    }



}
