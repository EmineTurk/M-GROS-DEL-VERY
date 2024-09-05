import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Antcolonyalgorithm {
    public int maxIterations;
    public int antCount;
    public double degradationFactor;
    public double alpha;
    public double beta;
    public double initialPheromone;
    public double QValue;
    public double[][] pheromones;
    public double[][] distances;
    public int numNodes;
    public  Antcolonyalgorithm(double[][] distances) {//this is a constructor which initializes pheromons list and some variables.
        this.distances = distances;
        this.numNodes = distances.length;
        this.pheromones = new double[numNodes][numNodes];
        initializePheromones();
        this.maxIterations = 100;
        this.antCount = 50;
        this.degradationFactor = 0.9;
        this.alpha = 0.8;
        this.beta = 1.5;
        this.initialPheromone = 0.1;
        this.QValue = 0.0001;


    }
    public void initializePheromones() {//this sets all initial pheromone values to 0.1
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if(i!=j) {
                    pheromones[i][j] = initialPheromone;
                }
            }
        }

    }
    public List<Integer> findshortestpath() {//this makes free 50 ants 100 times and for all of them probabilistically finds ways adds tihs ways to a list then finds shortest one and returns it.
        List<Integer> bestTour = null;
        double bestTourLength = Double.MAX_VALUE;

        for (int iter = 0; iter < maxIterations; iter++) {
            List<List<Integer>> antTours = new ArrayList<>();

            for (int ant = 0; ant < antCount; ant++) {
                List<Integer> tour = generateAntTour();
                antTours.add(tour);

                double tourLength = calculateTourLength(tour);
                if (tourLength < bestTourLength) {
                    bestTourLength = tourLength;
                    bestTour = new ArrayList<>(tour);
                }

                updatePheromones(tour, tourLength);

            }

            evaporatePheromones();
        }

        return bestTour;
    }
    private List<Integer> generateAntTour() {//this calculates probabilities for nodes which were not visited yet according to current node and select one of them then sets it as current node until visited list has length equals numbers of nodes.
        List<Integer> tour = new ArrayList<>();
        ArrayList<Integer> visited =new ArrayList<>();
        Random random = new Random();

        int startNode = 0;  // Start from node 0
        int currentNode = startNode;
        tour.add(currentNode);
        visited.add(currentNode);

        while (visited.size() < numNodes) {
            double[] probabilities = calculateProbabilities(currentNode, visited);
            int nextNode = chooseNextNode(probabilities, random);
            tour.add(nextNode);
            visited.add(nextNode);
            currentNode = nextNode;
        }

        tour.add(startNode);  // Add start node back to complete the cycle
        return tour;
    }
    private double[] calculateProbabilities(int currentNode, List<Integer> visited) {//this calculates probabilities according to current node but ignores visited nodes.Then divides total probability to acquire real probability value.
        double[] probabilities = new double[numNodes];
        double total = 0.0;

        for (int i = 0; i < numNodes; i++) {
            if (!visited.contains(i)) {
                double pheromone = Math.pow(pheromones[currentNode][i], alpha);



                if (distances[currentNode][i] != 0.0) {
                    double distance = Math.pow(distances[currentNode][i], beta);
                    probabilities[i] = pheromone / distance;
                    total += probabilities[i];

                }
            }






        }



        for (int i = 0; i < numNodes; i++) {

                probabilities[i] = probabilities[i]/total;



        }


        return probabilities;
    }
    private int chooseNextNode(double[] probabilities, Random random) {//this selects next node with the way which determines a random number between 0-1 and sums probability values until it reaches random number and choose the node at this index.
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];


//
            if (rand <= cumulativeProbability) {
                return i;
            }
        }
        return probabilities.length-1;



    }
    public double calculateTourLength(List<Integer> tour) {//this calculates a tour length according to distances list.
        double length = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int from = tour.get(i);
            int to = tour.get(i + 1);
            length += distances[from][to];
        }
        return length;
    }
    private void updatePheromones(List<Integer> tour, double tourLength) {//end of the each ants road it updates pheromones which are at this way.
        if(tourLength>0){
            double delta = QValue / tourLength;
            for (int i = 0; i < tour.size() - 1; i++) {
                int from = tour.get(i);
                int to = tour.get(i + 1);
                pheromones[from][to] += delta;
                pheromones[to][from] += delta;
            }
        }
    }
    private void evaporatePheromones() {//End of the each iteration this method decreases pheromone levels gradually.
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                pheromones[i][j] *= degradationFactor;
            }
        }
    }



}
