import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TestBSO {
    public static void main(String[] args) {
        String csv_file = "testBSO.csv";
        try (FileWriter fileWriter = new FileWriter(csv_file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter writer = new PrintWriter(bufferedWriter, true)){
            writer.println("Nb_sac, Nb_obj, Temps (s), MAX_ITER, NBR_BEE, FLIP, MAX_CHANCES, FITNESS, VALEUR_TOTAL");
            for (int nbs = 5; nbs < 200; nbs+=50) {
                for (int nbo = 100; nbo < 1000; nbo+=200) {

                    Sac[] sacs = new Sac[nbs];
                    Objet[] objets = new Objet[nbo];
                    Random rand = new Random();

                    int poidsMinSacs = Integer.MAX_VALUE;
                    int poidsMaxSacs = 0;
                    int poidsMinObjets = Integer.MAX_VALUE;
                    int poidsMaxObjets = 0;

                    for (int i = 0; i < nbs; i++){
                        int poid_sac = rand.nextInt(20000)+3000;
                        sacs[i] = new Sac(i, poid_sac);
                        poidsMinSacs = Math.min(poidsMinSacs, poid_sac);
                        poidsMaxSacs = Math.max(poidsMaxSacs, poid_sac);
                    }

                    for (int i = 0; i < nbo; i++){
                        int poids = rand.nextInt(10000) + 20;
                        int valeur = rand.nextInt(3000) + 5;
                        objets[i] = new Objet(i, valeur, poids);
                        poidsMinObjets = Math.min(poidsMinObjets, poids);
                        poidsMaxObjets = Math.max(poidsMaxObjets, poids);

                    }

                    for (int i = 0; i < nbs; i++){
                        System.out.println("sac"+sacs[i].id+": "+ sacs[i].poid+"g");
                    }

                    for (int i = 0; i < nbo; i++){
                        System.out.println("objet"+objets[i].id+": "+ objets[i].val+"$, "+objets[i].poid+"g");
                    }



                    long startTime, endTime;
                    double duration;
                    for(int MAX_ITER = 10; MAX_ITER < 101; MAX_ITER+=1){
                        for (int NBR_BEE = 5; NBR_BEE < 21; NBR_BEE+=1){
                            for (int FLIP = 1; FLIP <= 20; FLIP+=1){
                                for (int MAX_CHANCES = 1; MAX_CHANCES<= 10; MAX_CHANCES+=1){
                                    BSO bso = new BSO(sacs, objets, MAX_ITER, NBR_BEE, FLIP, MAX_CHANCES);
                                    Node b = new Node(nbs, nbo);
                                    startTime = System.nanoTime();
                                    b = bso.search();
                                    endTime = System.nanoTime();
                                    duration = (double) (endTime - startTime) /1_000_000_000.0;;
                                    writer.println(nbs +", "+ nbo + ", " + duration + ", " + MAX_ITER + ", " + NBR_BEE + ", " + FLIP +", " + MAX_CHANCES + ", " + b.f + ", " + b.val_total);
                                }
                            }

                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // Ajout de l'accolade fermante ici
}


