package main;

import java.util.*;


public class Polygon extends Geometry {
    public Polygon(LinkedList<Point> list){
        points = new LinkedList<>();
        setOfPoints = new HashSet<>();
        commonErrorHandler(list);
        fixDirection(points);
    }

    @Override
    public String toString(){
        String result="[";
        int piv =0;
        for(Point point : points){
            result+= point.toString()+" -> ";
        }
        result = result.trim();
        result+=" "+ points.getFirst().toString()+"]";
        return result;
    }

    //Sometimes points.indexOf(Point a) didn't work as expected, hence I wrote identical method that works. */
    public int findPointIndex(Point a){
        int piv =0;
        for(Point el: points){
            if(el.equals(a)){
                return piv;
            }
            piv++;
        }
        return -1;
    }

    /* Helper method designed to navigate through Maps easier by turning triangles into proper strings */
    public String simplify(int a, int b, int c){
        LinkedList<Integer> list = new LinkedList<>();
        list.add(a);list.add(b);list.add(c);
        Collections.sort(list);
        String result ="";
        for(Integer el: list){
            result+=" "+el;
        }
        return result;
    }

    /* The following explains triangulation process:
        1. Retrieve all possible triangles in a single polygon using a hashmap to avoid same triangles
        2. adjacentTriangles uses HashMap such that we can find all adjacent triangles of a given triangle.
        3. triangulate method iterates through this map and chooses 'base' triangle as a starter.
        It calls robustTriangulate till it finds a proper triangle list.
        4. robustTriangulate iterates through adjacents' of base and after properly checking them, recursively calls them
        by changing the base triangle to the next triangle in the parameters.
        5. When the amount of triangles exceeds a certain number, the list itself is given.
        6. If it takes too long to process, an empty linkedlist is returned.
        7. Second time algorithm is run, the algorithm allows thin triangles as well.
        8. If the algorithm still returns null or empty list, that polygon is skipped.
     */
    public HashMap<String, LinkedList<Triangle>> adjacentTriangles(Collection<Triangle> triangles){
        HashMap<String, LinkedList<Triangle>> adjacents = new HashMap<>();
        for(Triangle el: triangles){
            int firstIndex = findPointIndex(el.points.getFirst());
            int secondIndex = findPointIndex(el.points.get(1));
            int thirdIndex = findPointIndex(el.points.getLast());
            LinkedList<Triangle> firstList = adjacents.getOrDefault(simplify(0, secondIndex, thirdIndex).trim(), new LinkedList<>());
            firstList.add(el);
            if(firstList.size()==1){
                adjacents.put(simplify(0, secondIndex, thirdIndex).trim(),firstList);
            }
            LinkedList<Triangle> secondList = adjacents.getOrDefault(simplify(firstIndex, 0, thirdIndex).trim(), new LinkedList<>());
            secondList.add(el);
            if(secondList.size()==1){
                adjacents.put(simplify(firstIndex, 0, thirdIndex).trim(),firstList);
            }
            LinkedList<Triangle> thirdList = adjacents.getOrDefault(simplify(firstIndex, secondIndex, 0).trim(), new LinkedList<>());
            thirdList.add(el);
            if(thirdList.size()==1){
                adjacents.put(simplify(firstIndex, secondIndex, 0).trim(),firstList);
            }
        }
        return adjacents;
    }
    public HashMap<String,Triangle> allPossibleTriangles(boolean allowThin){
        HashMap<String, Triangle> map = new HashMap<>();
        for(Point a: points){
            for(Point b: points){
                for(Point c: points){
                    if(a==b || b==c || a==c){
                        continue;
                    }
                    LinkedList<Point> list = new LinkedList<>();
                    list.add(a);list.add(b);list.add(c);
                    if(Math.abs(Check.getArea(list))<1e-3){
                        continue;
                    }
                    String ref = simplify(findPointIndex(a),findPointIndex(b),findPointIndex(c));
                    if(map.get(ref)==null){
                        Triangle triangle = new Triangle(list);
                        if(Check.eligible(this,triangle)){
                            if((!allowThin&&Check.thin(triangle))){
                                continue;
                            }
                            map.put(ref,triangle);
                        }
                    }

                }
            }
        }

        return map;
    }
    public LinkedList<Triangle> triangulate(boolean allowThin) throws InterruptedException{
        HashMap<String, Triangle> map = this.allPossibleTriangles(allowThin);
        HashMap<String, LinkedList<Triangle>> adjacents = this.adjacentTriangles(map.values());
        for(Triangle el: map.values()){
            LinkedList<Triangle> result = robustTriangulate(el, adjacents, new LinkedList<>(),0);
            if(result!=null){
                double area = 0;
                for(Triangle tri: result){
                    area+= Math.abs(Check.getArea(tri.points));
                }
                if(Math.abs(area-(Math.abs(Check.getArea(this.points))))<1e-3) return result;
            }
        }
        return null;
    }
    public LinkedList<Triangle> robustTriangulate(Triangle base, HashMap<String, LinkedList<Triangle>> adjacents, LinkedList<Triangle> triangles, int n){
        if(n==points.size()-2){
            return triangles;
        }
        int firstIndex = findPointIndex(base.points.getFirst());
        int secondIndex = findPointIndex(base.points.get(1));
        int thirdIndex = findPointIndex(base.points.getLast());
        LinkedList<Triangle> firstList = adjacents.getOrDefault(simplify(0, secondIndex, thirdIndex).trim(), new LinkedList<>());
        LinkedList<Triangle> secondList = adjacents.getOrDefault(simplify(firstIndex, 0, thirdIndex).trim(), new LinkedList<>());
        LinkedList<Triangle> thirdList = adjacents.getOrDefault(simplify(firstIndex, secondIndex, 0).trim(), new LinkedList<>());
        LinkedList<Triangle> completeList = new LinkedList<>();
        completeList.addAll(firstList);completeList.addAll(secondList);completeList.addAll(thirdList);
        for(Triangle el: completeList){
            if(Check.appropriateTriangle(triangles,el)){
                triangles.add(el);
                LinkedList<Triangle> result = robustTriangulate(el,adjacents,triangles,n+1);
                if(result!=null){
                    return result;
                }
                triangles.remove(el);
            }
        }
        return null;
    }

}

