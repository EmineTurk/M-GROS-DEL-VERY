import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author EmineTurk student ıd:2022400228
 */
public class EmineTurk {
    private static double shortestDistance = Double.MAX_VALUE;
    private static List<Integer> shortestPath;//this initializes a list for brute force algorithm.
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();//this holds time.

        int chosenMethod = 2;
        boolean feromondisplay = true;


        ArrayList<ArrayList<Double>> locations = new ArrayList<>();
        try {

            File file = new File("input03.txt");//this reads txt file to access coordinates of market and homes.
            Scanner scanner = new Scanner(file);


            while (scanner.hasNextLine()) {


                String line = scanner.nextLine();
                String[] parts = line.split(",");
                ArrayList<Double> coordinates = new ArrayList<>();
                coordinates.add(Double.parseDouble(parts[0]));
                coordinates.add(Double.parseDouble(parts[1]));
                locations.add(coordinates);


            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
            return;

        }


        if (chosenMethod == 1) {
            double circleRadius = 0.02;

            ArrayList<Integer> nodes = new ArrayList<>();//it adds all node index numbers to the list.
            for (int i = 0; i < locations.size(); i++) {
                nodes.add(i);
            }
            permute(nodes, 1, locations);//tihs calculates all permutations along all nodes and finds shortest path and its distance.



            shortestPath.add(0);
            ArrayList<Integer> newshortestpath =new ArrayList<>();
            for(int i : shortestPath)
                newshortestpath.add(i+1);
            double finishtime= System.currentTimeMillis();
            System.out.println("Method: Brute-Force Method");
            System.out.println("Shortest Distance: " + shortestDistance);
            System.out.println("Shortest Path: " + newshortestpath);
            double timeLasted1 =(finishtime-startTime)/1000;
            System.out.printf("Time it takes to find the shortest path: %.2f seconds.", timeLasted1);

            for (int z = 0; z < shortestPath.size() - 1; z++) {//draws lines between locations which are on the road.
                double x1 = locations.get(shortestPath.get(z)).get(0);
                double x2 = locations.get(shortestPath.get(z + 1)).get(0);
                double y1 = locations.get(shortestPath.get(z)).get(1);
                double y2 = locations.get(shortestPath.get(z + 1)).get(1);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setPenRadius(0.005);
                StdDraw.line(x1, y1, x2, y2);
            }
            for (int i = 0; i < locations.size(); i++) {
                ArrayList<Double> location = locations.get(i);
                if(i!=0) {
                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);//draws gray circles at all locations except migros.It draws it with orange colour.
                    StdDraw.filledCircle(location.get(0), location.get(1), circleRadius);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.setPenRadius(0.01);
                    StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1));
                } else {
                    StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                    StdDraw.filledCircle(location.get(0), location.get(1), circleRadius);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.setPenRadius(0.01);
                    StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1));


                }


            }








        } else if (chosenMethod == 2) {

            double[][] pheremon1 = new double[locations.size()][locations.size()];//this initializes pheromone list values as 0.1
            for (int i = 0; i < locations.size(); i++) {
                for (int j = 0; j < locations.size(); j++) {
                    if (i != j) {
                        pheremon1[i][j] = 0.1;
                    }
                }
            }

            double[][] distances1 = new double[locations.size()][locations.size()];//this creates a distances list which holds all distances between all locations.
            for (int i = 0; i < locations.size(); i++) {
                for (int j = 0; j < locations.size(); j++) {
                    distances1[i][j] = calculatedistance(i, j, locations);

                }
            }
            Antcolonyalgorithm antcolony = new Antcolonyalgorithm(distances1);//this creates an antcolony object named antcolony.
            antcolony.pheromones = pheremon1;

            List<Integer> besttour = antcolony.findshortestpath();//assigns shortestpath to besttour.
            double besttourlength = antcolony.calculateTourLength(besttour);//assigns shortest tour length to besttourlength.
            ArrayList<Integer> newbesttour = new ArrayList<>();
            for (int i : besttour) {
                newbesttour.add(i + 1);
            }

            System.out.println("Method: Antcolony Method");
            System.out.println("Shortest Distance: " + besttourlength);
            System.out.println("Shortest Path: " + newbesttour);


            int canvasWidth = 800;
            int canvasHeight = 650;
            double circleRadius = 0.02;


            StdDraw.setCanvasSize(canvasWidth, canvasHeight);

            StdDraw.setXscale(0, 1);
            StdDraw.setYscale(0, 1);

            for (int z = 0; z < besttour.size() - 1; z++) {//draws lines between locations which are at shortest road.
                double x1 = locations.get(besttour.get(z)).get(0);
                double x2 = locations.get(besttour.get(z + 1)).get(0);
                double y1 = locations.get(besttour.get(z)).get(1);
                double y2 = locations.get(besttour.get(z + 1)).get(1);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setPenRadius(0.004);
                StdDraw.line(x1, y1, x2, y2);
            }
            if (feromondisplay == true) {//draws pheromone lines according to pheromone intensity at that road.
                for (int i = 0; i < pheremon1.length; i++) {
                    for (int j = 0; j < pheremon1.length; j++) {
                        if (i != j) {
                            double x1 = locations.get(i).get(0);
                            double x2 = locations.get(j).get(0);
                            double y1 = locations.get(i).get(1);
                            double y2 = locations.get(j).get(1);
                            StdDraw.setPenColor(StdDraw.BLACK);
                            StdDraw.setPenRadius(4 * (Math.pow(pheremon1[i][j],1.3)));
                            StdDraw.line(x1, y1, x2, y2);

                        }
                    }


                }

            }
            for (int i = 0; i < locations.size(); i++) {//draws gray circles and text location numbers at determined x,y coordinates.
                ArrayList<Double> location = locations.get(i);
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                StdDraw.filledCircle(location.get(0), location.get(1), circleRadius);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setPenRadius(0.01);
                StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1));

            }


        long FinishTime = System.currentTimeMillis();
        double timelasted = (double)(FinishTime - startTime) / 1000;
        System.out.printf("Time it takes to find the shortest path: %.2f seconds.", timelasted);//Calculates time.
        }




    } private static void permute(List<Integer> nodes, int start, ArrayList<ArrayList<Double>> locations) {//this finds all permutations of nodes and calculates their distances to find shortest path.
        if (start == nodes.size() - 1) {
            double currentDistance = calculatePathDistance(nodes, locations);
            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                shortestPath = new ArrayList<>(nodes);
            }
            return;
        }

        for (int i = start; i < nodes.size(); i++) {
            swap(nodes, start, i);
            permute(nodes, start + 1, locations);
            swap(nodes, start, i);
        }
    } private static double calculatePathDistance(List<Integer> nodes, ArrayList<ArrayList<Double>> locations) {//calculates road length between nodes at the road.
        double distance = 0;
        for (int i = 0; i < nodes.size() - 1; i++) {
            distance += distanceBetween(locations.get(nodes.get(i)), locations.get(nodes.get(i + 1)));
        }
        distance += distanceBetween(locations.get(nodes.get(nodes.size() - 1)), locations.get(0)); // return to Migros
        return distance;
    }

    private static double distanceBetween(ArrayList<Double> loc1, ArrayList<Double> loc2) {//this calculates distances according to given x,y values.
        double dx = loc1.get(0) - loc2.get(0);
        double dy = loc1.get(1) - loc2.get(1);
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static void swap(List<Integer> nodes, int i, int j) {//this method exchanges nodes at i and j indexes.
        int temp = nodes.get(i);
        nodes.set(i, nodes.get(j));
        nodes.set(j, temp);
    }


    private static double calculatedistance(int i,int j,ArrayList<ArrayList<Double>> locations){//this calculates distance according to given indexes at locations list.
        double distance=0;
        distance += Math.sqrt(Math.pow(locations.get(i).get(0) - locations.get(j).get(0), 2) +
                Math.pow(locations.get(i).get(1) - locations.get(j).get(1), 2));

        return distance;
    }






}