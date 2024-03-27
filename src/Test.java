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
                 writer.println("Taille, Algoritme, Temps (s), Profondeur");


                 for (int nbo = 20; nbo < 26; nbo+=5) {
                     for (int nbs = 3; nbs < 4; nbs++) {
                        int nbr_sac = nbs;
                        int nbr_objet = nbo;

                        Sac[] sacs = new Sac[nbr_sac];
                        Objet[] objets = new Objet[nbr_objet];

                        Random rand = new Random();

                        for (int i = 0; i < nbr_sac; i++){
                            sacs[i] = new Sac(i, rand.nextInt(1000)+100);
                        }

                        for (int i = 0; i < nbr_objet; i++){
                            objets[i] = new Objet(i, rand.nextInt(300)+5, rand.nextInt(500)+2);
                        }

                        for (int i = 0; i < nbr_sac; i++){
                            System.out.println("sac"+sacs[i].id+": "+ sacs[i].poid*10+"g");
                        }

                        for (int i = 0; i < nbr_objet; i++){
                            System.out.println("objet"+objets[i].id+": "+ objets[i].val*10+"$, "+objets[i].poid*10+"g");
                        }

                        Node but = new Node(nbr_sac, nbr_objet);
                        Node but2 = new Node(nbr_sac, nbr_objet);
                        Node but3 = new Node(nbr_sac, nbr_objet);

                        long startTime, endTime;
                        double duration1, duration2, duration3;


                        startTime = System.nanoTime();
                        but3 = Astar.astar(objets, sacs, but3);
                        endTime = System.nanoTime();
                        duration3 = (double) (endTime - startTime) /1_000_000_000.0;;
                        writer.println("("+nbr_sac+","+nbr_objet+")" + ", Astar, " + duration3 + ", " + but3.profondeur_max);

                        startTime = System.nanoTime();
                        but2 = DFS.recherche(sacs, objets);
                        endTime = System.nanoTime();
                        duration2 = (double) (endTime - startTime) /1_000_000_000.0;;
                        writer.println("("+nbr_sac+","+nbr_objet+")" + ", DFS, " + duration2 + ", " + but2.profondeur_max);

                        startTime = System.nanoTime();
                        but = BFS.recherche(sacs, objets);
                        endTime = System.nanoTime();
                        duration1 = (double) (endTime - startTime) /1_000_000_000.0;;
                        writer.println("("+nbr_sac+","+nbr_objet+")" + ", BFS, " + duration1 + ", " + but.profondeur_max);


                        System.out.println("resultats bfs:");
                        for (int i = 0; i < nbr_sac; i++){
                            for (int j = 0; j < nbr_objet; j++) {
                                System.out.print(but.matrice[i][j]+" ");
                            }
                            System.out.println();
                        }
                        System.out.println("temps d'execution: " + duration1);
                        System.out.println("profondeur max" + but.profondeur_max);

                        System.out.println("resultats Dfs:");
                        for (int i = 0; i < nbr_sac; i++){
                            for (int j = 0; j < nbr_objet; j++) {
                                System.out.print(but2.matrice[i][j]+" ");
                            }
                            System.out.println();
                        }
                        System.out.println("temps d'execution: " + duration2);
                        System.out.println("profondeur max" + but2.profondeur_max);

                        System.out.println("resultats A*:");
                        for (int i = 0; i < nbr_sac; i++){
                            for (int j = 0; j < nbr_objet; j++) {
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