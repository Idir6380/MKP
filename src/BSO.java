import java.util.*;

//cas non traiter nbr de sac superieur a celui des objet
public class BSO {
    // Definir les parametres de l algorithme BSO
    static  int MAX_ITER;
    //=10;
    static  int NBR_BEE;
    //=3;
    static  int FLIP;
    //=2;
    static  int MAX_CHANCES;
    //=5; // Nombre maximum de chances pour selectionner Sref

    // defenir les parametre de l instance
    static  int NUM_SACKS;
    //=3;
    static  int NUM_ITEMS;
    //=6;
    static  int[] MAX_CAPACITY;
    //= {5, 10, 15};
    static  int[] VALUES;
    //= {5, 10, 15,30,10,2};
    static  int[] WEIGHTS;
    //= {2, 5, 7,2,1,20};
    static Set<int[][]> Danse = new HashSet<>();
    public BSO(Sac[] sacs, Objet[] objets, int MAX_ITER, int NBR_BEE,  int FLIP, int  MAX_CHANCES){
        this.NUM_ITEMS = objets.length;;
        this.NUM_SACKS = sacs.length;
        this.WEIGHTS = new int[NUM_ITEMS];
        this.VALUES = new int[NUM_ITEMS];
        this.MAX_CAPACITY = new int[NUM_SACKS];
        this.MAX_ITER = MAX_ITER;
        this.NBR_BEE = NBR_BEE;
        this.FLIP = FLIP;
        this.MAX_CHANCES = MAX_CHANCES;
        for (int i = 0; i < NUM_ITEMS; i++) {
            WEIGHTS[i] = objets[i].poid;
            VALUES[i] = objets[i].val;
        }
        for (int i = 0; i < NUM_SACKS; i++) {
            MAX_CAPACITY[i] = sacs[i].poid;
        }

        //= new int[NUM_SACKS];
    }

    public static Node search() {

        //beeint
        Node but = new Node(NUM_SACKS, NUM_ITEMS) ;
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
        int[][] Sbest = new int[NUM_SACKS][NUM_ITEMS];
        int nbChances=MAX_CHANCES;
        for(int iter=0;iter < MAX_ITER; iter++){
            //insere sref lis taboo
            tabooList.add(Sref.clone());
            //determinate serach point
            System.out.println(iter);
            searchArea=generatesearcharea(Sref, NBR_BEE, FLIP);
            System.out.println("generated");
            //pour chaque abeille
            for (int[][] s : searchArea) {
                int[][] localSearchResult = localSearch(s);
                System.out.print("-");
                // Ajouter chaque solution à la liste Danse
                Danse.add(localSearchResult);


            }
            List<int[][]> DList = new ArrayList<>(Danse);
            // Code pour déterminer Sbest(t+1)

            if (!DList.isEmpty())
                Sbest=DList.get(0);
            for (int[][] solution : DList) {
                if (fitness(solution, VALUES, WEIGHTS) < fitness(Sbest, VALUES, WEIGHTS)) {
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
        but.matrice = tabooList.get(tabooList.size() - 1);
        but.val_total = valeur_totale;
        but.f = fitness(but.matrice, VALUES, WEIGHTS);
        return but;


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
        int h=0;
        while (searchArea.size() < B && h<FLIP) {
            int[][] s = new int[sRef.length][sRef[0].length];

            for (int i = 0; i < sRef.length; i++) {
                System.arraycopy(sRef[i], 0, s[i], 0, sRef[i].length);
            }
            int p = 0;
            while (searchArea.size() < B && p < FLIP) {
                copyrow(s, FLIP*p+h);
                p++;
            }
            h++;
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
        boolean[] objectsNotInBag = new boolean[VALUES.length]; // Initialiser un tableau pour suivre les objets non insérés
        Arrays.fill(objectsNotInBag, true); // Supposer initialement que tous les objets ne sont pas dans un sac


        // Calculer la somme des valeurs des objets restants
        for (int i = 0; i < s[0].length; i++) {
            boolean objectInBag = false;
            for (int[] bag : s) { // Parcourir tous les sacs
                if (bag[i] == 1) { // Si l'objet est dans ce sac
                    objectInBag = true;
                    break;
                }
            }
            if (!objectInBag) { // Si l'objet n'est dans aucun sac
                totalValue += VALUES[i];
            } else {
                objectsNotInBag[i] = false; // Marquer l'objet comme étant dans un sac
            }
        }
        // Calculer la somme des valeurs des objets restants
        for (int i = 0; i < objectsNotInBag.length; i++) {
            if (objectsNotInBag[i]) { // Si l'objet n'est dans aucun sac
                totalValue += VALUES[i]; // Ajouter sa valeur au fitness
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
        double rest = 0;
        if(totalValue != 0)
            rest = (1.0 / totalValue);

        // Calculer le fitness
        double fitness = rest + totalWeight;
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
        for (int sack = 0; sack < MAX_CAPACITY.length; sack++) {
            int totalWeight = 0;
            for (int item = 0; item < solution[sack].length; item++) {
                totalWeight += solution[sack][item] * WEIGHTS[item];
            }
            if (totalWeight > MAX_CAPACITY[sack]) {
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