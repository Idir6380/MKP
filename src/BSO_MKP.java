import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
//cas non traiter nbr de sac superieur a celui des objet 
public class BSO_MKP {
    // Definir les parametres de l algorithme BSO
    private static final int MAX_ITER=10;
    private static final int NBR_BEE=3;
    private static final int FLIP=2;
    private static final int MAX_CHANCES=5; // Nombre maximum de chances pour selectionner Sref

    // defenir les parametre de l instance
    private static final int NUM_SACKS=3;
    private static final int NUM_ITEMS=6;
    private static final int[] MAX_CAPACITY = {5, 10, 15};
    private static final int[] VALUES = {5, 10, 15,30,10,2};
    private static final int[] WEIGHTS = {2, 5, 7,2,1,20};
    private static Set<int[][]> Danse = new HashSet<>();

    public static void main(String[] args) {
        //beeint
        int[][]Sref= generatesref();
        // Afficher la matrice sref initiale
        System.out.println("sref init:");
        for(int i=0; i<NUM_SACKS; i++) {

            for (int j=0; j<NUM_ITEMS; j++) {
                System.out.print(Sref[i][j] + " ");
            }
            System.out.println();
        }
        List<int[][]> tabooList=new ArrayList<>();
        Set<int[][]> searchArea;
        int[][] Sbest = null;
        int nbChances=5;
        for(int iter=0;iter < MAX_ITER; iter++){
            //insere sref lis taboo
            tabooList.add(Sref.clone());
            //determinate serach point
            searchArea=generatesearcharea(Sref, NBR_BEE, FLIP);

            //pour chaque abeille
            for (int[][] s : searchArea) {
                int[][] localSearchResult = localSearch(s);
                // Ajouter chaque solution à la liste Danse
                Danse.add(localSearchResult);

            }
            List<int[][]> DList = new ArrayList<>(Danse);
            // Code pour déterminer Sbest(t+1)


            Sbest=DList.get(0);
            for (int[][] solution : DList) {
                if (fitness(solution, VALUES, WEIGHTS) > fitness(Sbest, VALUES, WEIGHTS)) {
                    Sbest = solution;
                }
            }


            Sref=selectionSol(Sref, Sbest, tabooList, DList, nbChances, MAX_CHANCES, VALUES, WEIGHTS);


            /*System.out.println("\nsref final:");
            printMatrix(Sref);*/
        }
        int valeur_totale= evaluateSolution(tabooList.get(tabooList.size() - 1));
        System.out.println("Solution optimale :");
        printMatrix(tabooList.get(tabooList.size() - 1));
        System.out.println("valeur totale:"+ valeur_totale);



        // Afficher les solutions dans SearchArea
        /*for(int[][] s : searchArea) {
            System.out.println("searcharea init:");
            for (int[] row : s) {
                for (int item : row) {

                    System.out.print(item + " ");
                }
                System.out.println();
            }
        }*/
        //System.out.println("taille de danse: " +Danse.size());
        // Afficher les paires de sRef et de solution
        /*System.out.println("Solution Danse:");
        for (int[][] solution : Danse) {
            System.out.println("Solution:");
            printMatrix(solution);
        }
        System.out.println("Contenu de tabooList :");
        for (int i = 0; i < tabooList.size(); i++) {
            int[][] solution = tabooList.get(i);
            System.out.println("Solution " + (i + 1) + ":");
            printMatrix(solution);
        }*/

    }
    //squelette de l algo
    private static int [][]generatesref(){
        int[][]sRef= new int[NUM_SACKS][NUM_ITEMS];
        Random rand= new Random();
        int[] maxCapacity=MAX_CAPACITY;
        int[] weights=WEIGHTS;


        //voir si l objet est deja placé dans un sac
        boolean[]itemPlaced=new boolean[NUM_ITEMS];
        for (int i=0; i< NUM_SACKS; i++) {
            int sackWeight=0;

            for (int j=0; j<NUM_ITEMS; j++) {
                int itemIndex=rand.nextInt(NUM_ITEMS);

                if (!itemPlaced[itemIndex]&&sackWeight+weights[itemIndex]<=maxCapacity[i]) {
                    sRef[i][itemIndex]=1;
                    itemPlaced[itemIndex]=true;
                    sackWeight+= weights[itemIndex];
                }
            }
        }
        return sRef;
    }
    private static Set<int[][]> generatesearcharea(int[][] sRef, int B, int FLIP) {
        Set<int[][]> searchArea = new HashSet<>();
        Random rand = new Random();

        while (searchArea.size() < B) {
            int[][] s = new int[sRef.length][sRef[0].length];

            for (int i = 0; i < sRef.length; i++) {
                System.arraycopy(sRef[i], 0, s[i], 0, sRef[i].length);
            }
            int p = 0;
            while (searchArea.size() < B && p < FLIP) {
                copyrow(s, rand.nextInt(sRef[0].length));
                p++;
            }

            if (isValid(s)) {
                boolean isUnique = true;
                for (int[][] existing : searchArea) {
                    if (areMatricesEqual(existing, s)) {
                        isUnique = false;
                        break;
                    }
                }
                if (isUnique) {
                    searchArea.add(s);
                }
            }
        }

        return searchArea;
    }
    private static int[][] localSearch(int[][] searchPoint) {
        int[][] currentSolution = searchPoint.clone(); // copie de la solution actuelle pour éviter les modifications indésirables
        double currentScore = fitness(currentSolution, VALUES, WEIGHTS); // évaluer la solution actuelle

        // Boucle jusqu'à ce qu'aucune amélioration ne soit possible
        boolean improved = true;
        while (improved) {
            improved = false;

            // Parcourir tous les voisins de la solution actuelle
            for (int sack = 0; sack < currentSolution.length; sack++) {
                for (int item = 0; item < currentSolution[sack].length; item++) {
                    // Copie temporaire de la solution courante pour la modification
                    int[][] neighborSolution = matcopy(currentSolution);

                    neighborSolution[sack][item] = (neighborSolution[sack][item] == 0) ? 1 : 0;

                    if (isValid(neighborSolution)) {
                        double neighborScore = fitness(neighborSolution, VALUES, WEIGHTS);
                        if (neighborScore > currentScore && !isInDanse(neighborSolution)) {
                            currentSolution = neighborSolution;
                            currentScore = neighborScore;
                            improved = true;
                        }
                    }
                }
            }
        }

        return currentSolution;
    }
    private static int[][] selectionSol(int[][] sreft, int[][] sreft1, List<int[][]> tabooList, List<int[][]> dance,int nbChances,
                                        int maxChances, int[ ]VALUES, int[] WEIGHTS) {
        int[][] sref =new int[NUM_SACKS][NUM_ITEMS];
        double deltaF = fitness(sreft, VALUES, WEIGHTS) - fitness(sreft1, VALUES, WEIGHTS);
        if(deltaF>0){
            sref=dance.get(0);
            for(int i=1;i<Danse.size();i++){
                if (fitness(dance.get(i), VALUES, WEIGHTS) > fitness(sref, VALUES, WEIGHTS)) {
                    sref = dance.get(i);
                }
            }
            if (nbChances < maxChances) {
                nbChances = maxChances;
            }
        }
        else{
            nbChances--;
            if(nbChances > 0){
                sref=dance.get(0);
                for(int i=1;i<Danse.size();i++){
                    if (fitness(dance.get(i), VALUES, WEIGHTS) > fitness(sref, VALUES, WEIGHTS)) {
                        sref = dance.get(i);
                    }
                }
                if (nbChances < maxChances) {
                    nbChances = maxChances;
                }
            }
            else{
                sref = dance.get(0);
                for (int i = 1; i < Danse.size(); i++) {
                    if (diversity(dance.get(i), tabooList) > diversity(sref, tabooList)) {
                        sref = dance.get(i);
                    }

                }
                nbChances = maxChances;
            }

        }
        return sref;
    }
    //les operateur
    private static void copyrow(int[][] s, int index) {
        Random rand=new Random();
        int numRows=s.length;
        int numCols=s[0].length;

        if (index>=0 && index<numCols) {
            int randomRowIndex=rand.nextInt(numRows);
            int[] selectedRow=s[randomRowIndex].clone();

            // Copie la range selectionne a la position de l index
            if (numRows>index) {
                int premierindexselt=rand.nextInt(numCols);
                for (int i = 0; i < numRows && premierindexselt + i < numCols; i++) {
                    s[i][index]=selectedRow[premierindexselt + i];
                }
            } else if (numRows<index) {
                for (int i=0; i<numRows && i<numCols;i++) {
                    s[i][index]=selectedRow[i];
                }
            } else {
                for (int i=0; i<numRows && index>=0 && index<numCols; i++) {
                    s[i][index]=selectedRow[i];
                }
            }
        }
    }


    private static int evaluateSolution(int[][] solution) {
        int score=0;
        for (int sack=0; sack<solution.length;sack++) {
            for (int item=0; item<solution[sack].length;item++) {
                score+= solution[sack][item] * VALUES[item];
            }
        }
        return score;
    }
    private static double fitness(int[][] s, int[] VALUES, int[] WEIGHTS) {
        int totalValue = 0;
        int totalWeight = 0;

        // Calculer la somme des valeurs des objets restants
        for (int i = 0; i < s[0].length; i++) {
            if (s[0][i] == 0) {
                totalValue += VALUES[i];
            }
        }

        // Calculer la somme des poids des objets insérés
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[i].length; j++) {
                if (s[i][j] == 1) {
                    totalWeight += WEIGHTS[j];
                }
            }
        }

        // Calculer le fitness
        double fitness = (1.0 / totalValue) + totalWeight;
        return fitness;
    }
    private static boolean isValid(int[][] solution) {
        // verifier si aucun objet n est repete dans un autre sac
        for (int item=0; item<solution[0].length; item++) {
            int totalInSacks=0;
            for (int sack=0; sack<solution.length; sack++) {
                totalInSacks+= solution[sack][item];
            }
            if (totalInSacks>1) {
                return false;
            }
        }

        // verifier si le poids total de chaque sac ne depasse pas la capacite maximale
        for (int sack=0;sack<solution.length;sack++) {
            int totalWeight=0;
            for (int item=0; item<solution[sack].length;item++) {
                totalWeight+=solution[sack][item] * WEIGHTS[item];
            }
            if (totalWeight>MAX_CAPACITY[sack]) {
                return false;
            }
        }

        // La solution est valide
        return true;
    }
    private static boolean isInDanse(int[][] solution) {
        for (int[][] s : Danse) {
            if (areMatricesEqual(s, solution)) {
                return true;
            }
        }
        return false;
    }
    private static boolean areMatricesEqual(int[][] matrix1, int[][] matrix2) {
        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
            return false;
        }
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                if (matrix1[i][j] != matrix2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    private static int[][] matcopy(int[][] original) {
        int[][]copy=new int[original.length][];
        for (int i=0; i<original.length; i++) {
            copy[i]=original[i].clone();
        }
        return copy;
    }
    private static int diversity(int[][] sref, List<int[][]> tabooList){
        int minDiffrence = diffrence(sref, tabooList.get(0));
        int diffrence;

        for (int i = 1; i < tabooList.size(); i++) {
            diffrence = diffrence(sref, tabooList.get(i));
            if (diffrence < minDiffrence) {
                minDiffrence = diffrence;
            }
        }

        return minDiffrence;
    }
    public static int diffrence(int[][] mat1, int[][] mat2) {
        int d = 0;
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat1[i].length; j++) {

                if (mat1[i][j] != mat2[i][j]) {
                    d++;
                }
            }
        }
        return d;
    }
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int item : row) {
                System.out.print(item + " ");
            }
            System.out.println();
        }
    }



}