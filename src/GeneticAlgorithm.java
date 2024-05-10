import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GeneticAlgorithm {

    private static final int POPULATION_SIZE= 50;
    private static final int MAX_GENERATIONS= 1000;
    private static final double MUTATION_RATE= 0.1;

    // Donnees de l instance
    private static final int NUM_ITEMS= 5;
    private static final int NUM_SACKS= 3;
    private static final Random random= new Random();

    private static int[] ITEM_WEIGHTS= {2, 5, 7, 3, 1};;
    private static int[] ITEM_VALUES={5, 10, 15, 7, 3};
    private static int[] SACK_CAPACITIES={5, 10, 15};

    private static void printPopulation(List<int[][]> population) {
        System.out.println("Population:");
        for (int i = 0; i < population.size(); i++) {
            System.out.println("Individual " + (i + 1) + ":");
            printIndividual(population.get(i));
        }
    }

    private static void printIndividual(int[][] individual) {
        for (int i=0; i < NUM_SACKS; i++) {
            System.out.print("Sack " + (i + 1) + ": ");
            for (int j=0; j < NUM_ITEMS; j++) {
                System.out.print(individual[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // generation des donne du probleme aleatoitre
        generateRandomInstance();

        // Affichage des poids et des valeurs générés
        System.out.println("Poids des objets generé : ");
        for (int i = 0; i < NUM_ITEMS; i++) {
            System.out.println("Objet " + (i + 1) + ": " + ITEM_WEIGHTS[i]);
        }
        System.out.println("\nValeurs des objets generes : ");
        for (int i = 0; i < NUM_ITEMS; i++) {
            System.out.println("Objet " + (i + 1) + ": " + ITEM_VALUES[i]);
        }
        System.out.println("\nCapacités des sacs genere : ");
        for (int i=0; i< NUM_SACKS; i++) {
            System.out.println("Sac " + (i + 1) + ": " + SACK_CAPACITIES[i]);
        }

        // la population initiale
        List<int[][]> population=generateInitialPopulation();
        // Impression de la population initiale
        printPopulation(population);

        for (int generation=1; generation <= MAX_GENERATIONS; generation++) {
            // évaluation
            List<Double> fitnessValues=evaluatePopulation(population);

            // sélection
            List<int[][]> selectedParents=selectParents(population, fitnessValues);

            // croisement
            List<int[][]> offspring=crossover(selectedParents);

            // mutation
            mutate(offspring);

            // remplacement de la population précédente par la nouvelle
            population=offspring;
        }

        // recherche de la meilleure solution parmi toutes les solutions de la dernière generation
        int[][] bestFinalSolution= getBestSolution(population, evaluatePopulation(population));

        // affichage de la solution optimale finale
        double totalValue= evaluateIndividual(bestFinalSolution);
        System.out.println("Solution optimale finale:");
        printSolution(bestFinalSolution);
        System.out.println("Valeur totale finale: " + totalValue);
    }

    private static void generateRandomInstance() {
        ITEM_WEIGHTS= new int[NUM_ITEMS];
        ITEM_VALUES=new int[NUM_ITEMS];
        SACK_CAPACITIES= new int[NUM_SACKS];

        // generation des poids et des valeurs des objets aleatoirement
        /*for (int i = 0; i < NUM_ITEMS; i++) {
            ITEM_WEIGHTS[i]= random.nextInt(100) + 50;
            ITEM_VALUES[i]= random.nextInt(500) + 100;
        }*/

        // generation des capacite de sac aleatoirement
        /*for (int i= 0; i < NUM_SACKS; i++) {
            SACK_CAPACITIES[i]= random.nextInt(5000) + 1000;
        }*/
    }

    private static List<int[][]> generateInitialPopulation() {
        List<int[][]> population= new ArrayList<>();
        for (int i= 0; i < POPULATION_SIZE; i++) {
            int[][] individual= new int[NUM_SACKS][NUM_ITEMS];
            // generation d une une solution individuelle valide
            generateValidSolution(individual);
            population.add(individual);
        }
        return population;
    }

    private static void generateValidSolution(int[][] individual) {
        for (int i = 0; i < NUM_SACKS; i++) {
            int sackCapacity= SACK_CAPACITIES[i];
            int currentWeight=0;
            List<Integer> selectedItems= new ArrayList<>(); // Liste des objets deje selectionne
            for (int j= 0; j < NUM_ITEMS; j++) {
                // verifier que la capacite du sac n est pas depasse
                if (currentWeight + ITEM_WEIGHTS[j] <= sackCapacity) {
                    // Vérifier si l objet n a pas deja ete selectionne dans un autre sac
                    if (!selectedItems.contains(j)) {
                        individual[i][j]= 1;
                        currentWeight += ITEM_WEIGHTS[j];
                        selectedItems.add(j); 
                    }
                }
            }
        }
        // s assurer qu un objet n est selection que dans un seul sac
        for (int j= 0; j < NUM_ITEMS; j++) {
            int count= 0;
            for (int i= 0; i < NUM_SACKS; i++) {
                count += individual[i][j];
            }
            if (count > 1) {
                // Reinitialiser les selections si un objet est selectione dans plusieurs sacs
                for (int i= 0; i < NUM_SACKS; i++) {
                    individual[i][j]= 0;
                }
                // Selectionner l objet dans un sac aleatoire
                int randomSack= random.nextInt(NUM_SACKS);
                individual[randomSack][j]=1;
            }
        }
    }

    private static List<Double> evaluatePopulation(List<int[][]> population) {
        List<Double> fitnessValues= new ArrayList<>();
        for (int[][] individual : population) {
            double fitness= evaluateIndividual(individual);
            fitnessValues.add(fitness);
        }
        return fitnessValues;
    }

   private static double evaluateIndividual(int[][] individual) {
    int totalValue= 0;
    int totalWeight= 0;
    
    // Calcul de la valeur totale et du poids total des objets
    for (int i= 0; i < NUM_SACKS; i++) {
        int sackCapacity= SACK_CAPACITIES[i];
        int sackWeight= 0;
        int sackValue= 0;
        for (int j= 0; j < NUM_ITEMS; j++) {
            if (individual[i][j] == 1) {
                sackWeight += ITEM_WEIGHTS[j];
                sackValue += ITEM_VALUES[j];
            }
        }
        if (sackWeight <= sackCapacity) {
            totalValue += sackValue;
            totalWeight += sackWeight;
        }
    }
    
    // Calcul de la fitness selon la formule donnée

    double fitness= 1 / (totalValue - totalWeight) + totalWeight;
    
    return fitness;
}


    private static List<int[][]> selectParents(List<int[][]> population, List<Double> fitnessValues) {
        // S assurer que la population et les valeurs de fitness ne sont pas vides
        if (population.isEmpty() || fitnessValues.isEmpty()) {
            throw new IllegalArgumentException("La population ou les valeurs de fitness sont vides.");
        }

        // Utilisation de la méthode de la roulette
        double totalFitness = fitnessValues.stream().mapToDouble(Double::doubleValue).sum();
        List<int[][]> selectedParents = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE / 2; i++) {
            double randomFitness = random.nextDouble() * totalFitness;
            double cumulativeFitness = 0.0;
            for (int j = 0; j < POPULATION_SIZE; j++) {
                cumulativeFitness += fitnessValues.get(j);
                if (cumulativeFitness >= randomFitness) {
                    selectedParents.add(population.get(j));
                    break;
                }
            }
        }
        return selectedParents;
    }

    private static List<int[][]> crossover(List<int[][]> parents) {
        List<int[][]> offspring= new ArrayList<>();// pour stocker les nouveau individus
        // chaque deux parents consecutifs sont selectionne
        for (int i = 0; i < parents.size() - 1; i += 2) {
            int[][] parent1= parents.get(i);
            int[][] parent2= parents.get(i + 1);
            int[][] child1= new int[NUM_SACKS][NUM_ITEMS];
            int[][] child2= new int[NUM_SACKS][NUM_ITEMS];
            int crossoverPoint= random.nextInt(NUM_ITEMS); // Point de croisement
            for (int j= 0; j < NUM_SACKS; j++) {
                for (int k= 0; k < NUM_ITEMS; k++) {
                    if (k < crossoverPoint) {
                        child1[j][k]= parent1[j][k];
                        child2[j][k]= parent2[j][k];
                    } else {
                        child1[j][k]= parent2[j][k];
                        child2[j][k]= parent1[j][k];
                    }
                }
            }
            offspring.add(child1);
            offspring.add(child2);
        }
        return offspring;
    }

    private static void mutate(List<int[][]> population) {
        for (int[][] individual : population) {
            for (int i = 0; i < NUM_SACKS; i++) {
                for (int j = 0; j < NUM_ITEMS; j++) {
                    if (random.nextDouble() < MUTATION_RATE) {
                        individual[i][j] = 1 - individual[i][j]; 
                    }
                }
            }
            // Verifier la validite de la solution après la mutation
            if (!isValidSolution(individual)) {
                // Si la solution est invalide remplacer par une solution valide
                generateValidSolution(individual);
            }
        }
    }

    private static boolean isValidSolution(int[][] individual) {
        Set<Integer> selectedItems = new HashSet<>();
        for (int i= 0; i < NUM_SACKS; i++) {
            int sackCapacity= SACK_CAPACITIES[i];
            int sackWeight= 0;
            for (int j= 0; j < NUM_ITEMS; j++) {
                if (individual[i][j]== 1) {
                    // Verifier si l objet est deja selectionnee dans un autre sac
                    if (selectedItems.contains(j)) {
                        return false; // La solution est invalide
                    }
                    selectedItems.add(j);

                    // Maj le poids total du sac
                    sackWeight += ITEM_WEIGHTS[j];
                }
            }
            // Verifier si le poids total du sac depasse la capacite du sac
            if (sackWeight > sackCapacity) {
                return false; // La solution est invalide
            }
        }
        // Verifier si chaque objet est selectionne exactement une fois
        if (selectedItems.size() != NUM_ITEMS) {
            return false; // La solution est invalide
        }
        return true; // La solution est valide
    }

    private static int[][] getBestSolution(List<int[][]> population, List<Double> fitnessValues) {
        double maxFitness= Double.MIN_VALUE;
        int bestIndex= 0;
        for (int i= 0; i < population.size(); i++) {
            if (fitnessValues.get(i) > maxFitness) {
                maxFitness= fitnessValues.get(i);
                bestIndex=i;
            }
        }
        return population.get(bestIndex);
    }

    private static void printSolution(int[][] solution) {
        for (int i= 0; i < NUM_SACKS; i++) {
            System.out.print("Sac " + (i + 1) + ": ");
            for (int j= 0; j < NUM_ITEMS; j++) {
                System.out.print(solution[i][j] + " ");
            }
            System.out.println();
        }
    }
}
