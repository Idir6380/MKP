import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Test {
        public static void main(String[] args) {
            String csv_file = "test.csv";
            try (FileWriter fileWriter = new FileWriter(csv_file);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                 PrintWriter writer = new PrintWriter(bufferedWriter, true)){
                 writer.println("Nb_sac, Nb_obj, Algorithme, Temps (s), Profondeur, Poids_min_sacs(g), Poids_max_sacs(g), Poids_min_objets(g), Poids_max_objets(g)");


                 for (int nbo = 5; nbo < 26; nbo+=5) {
                     for (int nbs = 1; nbs < 4; nbs++) {
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
                        Node but3 = new Node(nbs, nbo);

                        long startTime, endTime;
                        double duration1, duration2, duration3;


                        startTime = System.nanoTime();
                        but3 = Astar.astar(objets, sacs, but3);
                        endTime = System.nanoTime();
                        duration3 = (double) (endTime - startTime) /1_000_000_000.0;;
                        writer.println(nbs +", "+ nbo + ", Astar, " + duration3 + ", " + but3.profondeur_max + ", " + poidsMinSacs + ", " + poidsMaxSacs + ", " + + poidsMinObjets + ", " + poidsMaxObjets);

                        startTime = System.nanoTime();
                        but2 = DFS.recherche(sacs, objets);
                        endTime = System.nanoTime();
                        duration2 = (double) (endTime - startTime) /1_000_000_000.0;;
                        writer.println(nbs +", "+ nbo + ", DFS, " + duration2 + ", " + but2.profondeur_max + ", " + poidsMinSacs + ", " + poidsMaxSacs + ", " + + poidsMinObjets + ", " + poidsMaxObjets);

                        startTime = System.nanoTime();
                        but = BFS.recherche(sacs, objets);
                        endTime = System.nanoTime();
                        duration1 = (double) (endTime - startTime) /1_000_000_000.0;;
                        writer.println(nbs +", "+ nbo + ", BFS, " + duration1 + ", " + but.profondeur_max + ", " + poidsMinSacs + ", " + poidsMaxSacs + ", " + + poidsMinObjets + ", " + poidsMaxObjets);


                        System.out.println("resultats bfs:");
                        for (int i = 0; i < nbs; i++){
                            for (int j = 0; j < nbo; j++) {
                                System.out.print(but.matrice[i][j]+" ");
                            }
                            System.out.println();
                        }
                        System.out.println("temps d'execution: " + duration1);
                        System.out.println("profondeur max" + but.profondeur_max);

                        System.out.println("resultats Dfs:");
                        for (int i = 0; i < nbs; i++){
                            for (int j = 0; j < nbo; j++) {
                                System.out.print(but2.matrice[i][j]+" ");
                            }
                            System.out.println();
                        }
                        System.out.println("temps d'execution: " + duration2);
                        System.out.println("profondeur max" + but2.profondeur_max);

                        System.out.println("resultats A*:");
                        for (int i = 0; i < nbs; i++){
                            for (int j = 0; j < nbo; j++) {
                                System.out.print(but3.matrice[i][j]+" ");
                            }
                            System.out.println();
                        }
                        System.out.println("temps d'execution: " + duration3);

                        System.out.println("profondeur max" + but3.profondeur_max);
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