import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class BSOvsGA {
    public static void main(String[] args) {
        String csv_file = "BSOvsGA.csv";
        try (FileWriter fileWriter = new FileWriter(csv_file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter writer = new PrintWriter(bufferedWriter, true)){
            writer.println("Nb_sac, Nb_obj, Algorithme, Temps (s), VALEUR_TOTAL");


            for (int nbo = 1000; nbo < 10001; nbo+=100) {
                for (int nbs = 1; nbs < 50; nbs+=2) {
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

                    Node but = new Node(nbs, nbo);
                    Node but2 = new Node(nbs, nbo);


                    long startTime, endTime;
                    double duration1, duration2, duration3;



                    startTime = System.nanoTime();
                    BSO bso = new BSO(sacs, objets, 10, 10, 1, 3);
                    but2 = bso.search();
                    endTime = System.nanoTime();
                    duration2 = (double) (endTime - startTime) /1_000_000_000.0;;
                    writer.println(nbs +", "+ nbo + ", BSO, " + duration2 + ", " + but2.val_total);

                System.out.println("ga");
                startTime = System.nanoTime();
                Genetic ga = new Genetic(sacs, objets, 50, 100, 0.5, 0.2);
                but = ga.search();
                endTime = System.nanoTime();
                duration1 = (double) (endTime - startTime) /1_000_000_000.0;;
                writer.println(nbs +", "+ nbo + ", GA, " + duration1 + ", " + but.val_total);

                System.out.println("resultats GA:");
                for (int i = 0; i < nbs; i++){
                    for (int j = 0; j < nbo; j++) {
                        System.out.print(but.matrice[i][j]+" ");
                    }
                    System.out.println();
                }
                System.out.println("temps d'execution: " + duration1);


                    System.out.println("resultats BSO:");
                    for (int i = 0; i < but2.matrice.length; i++){
                        for (int j = 0; j < but2.matrice[0].length; j++) {
                            System.out.print(but2.matrice[i][j]+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("temps d'execution: " + duration2);

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // for (int nbsc = 2; nbsc < 4; nbsc++) {
        //  for (int nbobj = 5; nbobj < 21 ; nbobj+=5) {

        //  }
        //}
    }

}
