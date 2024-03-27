import java.util.*;

public class Astar {
    public static Node astar(Objet[] objets, Sac[] sacs, Node initial_node){
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        List<Node> closedSet = new ArrayList<>();
        int val = 0;
        for (Objet obj: objets){
            val += obj.val;
        }

        initial_node.h = val;
        initial_node.f = initial_node.h;
        int profMax = 0;

        openSet.add(initial_node);
       // closedSet.add(initial_node);
        while (!openSet.isEmpty()){
            Node current_node = openSet.poll();
            closedSet.add(current_node);
            if (current_node.profondeur_max > profMax){
                profMax = current_node.profondeur_max;
            }

            if (current_node.f <= initial_node.f) {
                //System.out.println("matrix:" + Arrays.deepToString(current_node.matrice));
                //System.out.println(current_node.f);
                //System.out.println(initial_node.f);
                //System.out.println("remplissage!");
                for (Sac sac : sacs) {
                    for (Objet obj : objets) {
                        initial_node.matrice[sac.id][obj.id] = current_node.matrice[sac.id][obj.id];
                        //System.out.print(initial_node.matrice[sac.id][obj.id] + " ");
                    }
                    //System.out.println();
                }
                initial_node.h = current_node.h;
                initial_node.g = current_node.g;
                initial_node.f = current_node.f;

                for (Sac sac : sacs) {
                    for (Objet obj : objets) {
                        if (!visited(current_node, obj.id)) {
                            Node succ = new Node(sacs.length, objets.length);
                            for (int i = 0; i < sacs.length; i++) {
                                for (int j = 0; j < objets.length; j++) {
                                    succ.matrice[i][j] = current_node.matrice[i][j];
                                }
                            }
                            succ.matrice[sac.id][obj.id] = 1;
                            if (calculer_poids(succ, sac, objets) <= sac.poid && !isClosed(succ, closedSet)) {
                                succ.g = current_node.g + obj.poid;
                                succ.h = current_node.h - obj.val;
                                succ.f = (double) 1 / succ.g + succ.h;
                                succ.profondeur_max = current_node.profondeur_max + 1;
                                openSet.add(succ);
                            }
                        }
                    }
                }
            }
        }
        initial_node.profondeur_max = profMax;
        return initial_node;
    }
    public static boolean visited(Node node, int objet){
        for (int i = 0; i < node.matrice.length; i++) {
            if (node.matrice[i][objet] == 1){
                return true;
            }
        }
        return false;
    }
    public static int calculer_poids(Node noeud, Sac sac, Objet[] objets){
        int poid = 0;
        for (int i = 0; i < noeud.matrice[sac.id].length; i++) {
            if (noeud.matrice[sac.id][i] == 1){
                poid += objets[i].poid;
            }
        }
        return poid;
    }
    public static int calculer_val(Node noeud, Sac[] sacs, Objet[] objets){
        int val = 0;
        for (Sac sac: sacs){
            for (int i = 0; i < noeud.matrice[sac.id].length; i++) {
                if (noeud.matrice[sac.id][i] == 1){
                    val += objets[i].val;
                }
            }
        }
        return val;
    }

    public static boolean isClosed(Node node1, List<Node> closed) {
        int[][] matrice1 = node1.matrice;

        for (Node node2 : closed) {
            int[][] matrice2 = node2.matrice;

            if (matrice1.length == matrice2.length && matrice1[0].length == matrice2[0].length) {
                boolean identical = true;
                for (int i = 0; i < matrice1.length; i++) {
                    for (int j = 0; j < matrice1[0].length; j++) {
                        if (matrice1[i][j] != matrice2[i][j]) {
                            identical = false;
                            break; // sortir de la boucle interne dès qu'un élément différent est trouvé
                        }
                    }
                    if (!identical) {
                        break; // sortir de la boucle externe dès qu'une matrice différente est trouvée
                    }
                }
                if (identical) {
                    // System.out.println("chemin identique trouvé");
                    return true; // retourner true si une matrice identique est trouvée
                }
            }
        }
        return false; // aucune matrice identique trouvée dans la liste
    }
}
