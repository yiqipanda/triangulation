package main.IO;
import main.Polygon;
import main.CustomExceptions.InsufficientSizeException;
import main.Point;
import main.Triangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;


public abstract class IOHandler {
    public static <T> T runWithTimeout(Callable<T> task, long timeout, TimeUnit unit)
            throws TimeoutException, ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);

        try {
            return future.get(timeout, unit);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } finally {
            executor.shutdownNow();
        }
    }
    /* I thought it would be best to use same colours over time for the sake of consistency. */
    static Color randomColor(){
        int r = (int)(Math.random()*8);
        Color[] a = {Color.RED, Color.GRAY, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.pink, Color.YELLOW};
        return a[r];
    }
    final static int SIZE = 100;
    final static int WIDTH = 600;
    final static int HEIGHT = 600;

    static Point getCoordinate(String t){
        t=t.trim();
        String[] vertices = t.split(" ");
        if(vertices.length!=2){
            throw new InsufficientSizeException("number of parameters must be 2.");
        }
        return new Point(Double.parseDouble(vertices[0]),Double.parseDouble(vertices[1]));
    }

    /* Ensures that given set of points indeed fit to the WIDTH-HEIGHT dimensions, no overflow. */
    static LinkedList<Point> fitSize(LinkedList<Point> points){
        double cx = 0, cy = 0;
        double left = Integer.MAX_VALUE;
        double right = Integer.MIN_VALUE;
        double top = Integer.MIN_VALUE;
        double bottom = Integer.MAX_VALUE;
        for (Point p : points) {
            if(p.x < left){
                left = p.x;
            }
            if(p.x > right){
                right = p.x;
            }
            if(p.y < bottom){
                bottom = p.y;
            }
            if(p.y > top){
                top = p.y;
            }
            cx += p.x;
            cy += p.y;
        }
        double scaleFactor = 300.0/Math.max(right-left,top-bottom);
        cx /= points.size();
        cy /= points.size();
        LinkedList<Point> scaled = new LinkedList<>();
        for (Point p : points) {
            double newX = cx + scaleFactor * (p.x - cx);
            double newY = cy + scaleFactor * (p.y - cy);
            scaled.add(new Point(newX+300-cx, newY+300-cy));
        }
        return scaled;
    }

    /* This method iterates through input file, writes a polygon and a triangulated one respectively. Any polygon that
    doesn't have a triangulated shape are still written. Also, each triangulation gets 10 seconds to work otherwise it
    returns null.
    */

    public static void generate(){
        Polygon[] polygons = new Polygon[SIZE+1];
        for(int i=1;i<=SIZE;i++) {
            try {
                File myObj = new File("src/main/java/main/IO/input/" + i + ".txt");
                if (!myObj.exists()) {
                    continue;
                }
                LinkedList<Point> pointList = new LinkedList<>();
                Scanner myReader = new Scanner(myObj);

                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    pointList.add(getCoordinate(data));
                }
                if (pointList.size() < 3) {
                    throw new InsufficientSizeException("vertices must be greater than 2.");
                }

                Polygon polygonn = new Polygon(fitSize(pointList));
                polygons[i] = polygonn;
                myReader.close();
            } catch (FileNotFoundException error) {
                throw new InsufficientSizeException("polygons must be " + SIZE + ".");
            }
        }
            for(int i=1;i<=SIZE;i++){
            Polygon polygonn = polygons[i];
            if(polygonn == null) continue;
            int width = WIDTH, height = HEIGHT;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.GRAY);
            int[] xPoints = polygonn.getXcoords();
            int[] yPoints = polygonn.getYcoords();
            g.fillPolygon(xPoints, yPoints, xPoints.length);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawPolygon(xPoints, yPoints, xPoints.length);
            try {
                ImageIO.write(img, "png", new File("src/main/java/main/IO/outputImages/"+ i +"_polygon"+".png"));
            } catch (IOException e) {
                System.err.println("Failed to save image: " + e.getMessage());
            }
            long start = System.currentTimeMillis();
            LinkedList<Triangle> triangles = null;
            try {
                triangles = runWithTimeout(() -> polygonn.triangulate(false), 5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                triangles = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(triangles==null){
                try {
                    triangles = runWithTimeout(() -> polygonn.triangulate(true), 5, TimeUnit.SECONDS);

                } catch (TimeoutException e) {
                    triangles = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long end = System.currentTimeMillis();
            if(triangles==null) {
                System.out.println("Could not generate triangle "+i+".");
                continue;
            }
            System.out.println("Time it took to generate triangle "+i+": "+(end-start)+" milliseconds.");
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for(Triangle triangle: triangles){
                g.setColor(randomColor());
                xPoints = triangle.getXcoords();
                yPoints = triangle.getYcoords();
                g.fillPolygon(xPoints, yPoints, xPoints.length);
                g.setStroke(new BasicStroke(1));
                g.setColor(Color.black);
                g.drawPolygon(xPoints, yPoints, xPoints.length);
            }
            try {
                ImageIO.write(img, "png", new File("src/main/java/main/IO/outputImages/"+ i +"_triangles"+ ".png"));
            } catch (IOException e) {
                System.err.println("Failed to save image: " + e.getMessage());
            }
        }


    }


}
