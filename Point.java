package main;

public class Point {
    public final double x;
    public final double y;
    public Point(double x, double y){
        this.x=x;
        this.y=y;
    }
    @Override
    public String toString(){
        return String.format("x: %-5.2f y: %-5.2f",x,y);
    }
    public Point copy(){
        return new Point(this.x,this.y);
    }
    public boolean equals(Point b){
        return Math.abs(this.x - b.x) < 1e-3 && Math.abs(this.y - b.y) < 1e-3;
    }
}
