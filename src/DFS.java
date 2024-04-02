import java.util.HashSet;
import java.util.Stack;

public class DFS {
    public static Node recherche(Sac[] sacs, Objet[] objets) {
        Node but = new Node(sacs.length, objets.length);
        for (Sac sac : sacs) {
            but.poid_total += sac.poid;
        }
        long p = but.poid_total;
        but = dfs(but, sacs, objets);
        but.poid_total = p - but.poid_total;
        System.out.println("dfs");
        System.out.println("solution but");

        return but;
    }


    public static Node dfs(Node but, Sac[] sacs, Objet[] objets) {
        Stack<Node> stack = new Stack<>();
        HashSet<Node> visitedNodes = new HashSet<>();
        int prof_max = 0;
        stack.push(but);
        //long nb = 0;
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            //nb++;
            //System.out.println(nb);
            visitedNodes.add(n);
            if (n.profondeur_max > prof_max){
                prof_max = n.profondeur_max;
            }
            if ((n.val_total>but.val_total) || (n.val_total == but.val_total && n.poid_total > but.poid_total)) {                //System.out.println("remplissage!");
                for (Sac sac : sacs) {
                    for (Objet obj : objets) {
                        but.matrice[sac.id][obj.id] = n.matrice[sac.id][obj.id];
                        //System.out.print(but.matrice[sac.id][obj.id] + " ");
                    }
                }
                but.poid_total = n.poid_total;
                but.val_total = n.val_total;
                //System.out.println("-----------------");
            }

            for (Sac sac : sacs) {
                for (Objet obj : objets) {
                    if (!visited(n, obj.id)) {
                        Node succ = new Node(sacs.length, objets.length);
                        for (int i = 0; i < sacs.length; i++) {
                            System.arraycopy(n.matrice[i], 0, succ.matrice[i], 0, objets.length);
                        }
                        succ.poid_total = n.poid_total;
                        succ.val_total = n.val_total;
                        succ.matrice[sac.id][obj.id] = 1;
                        succ.poid_total -= obj.poid;
                        succ.val_total += obj.val;
                        succ.profondeur_max = n.profondeur_max + 1;
                        succ.updateConfiguration();
                        if (calculer_poids(succ, sac, objets) <= sac.poid) {
                            if (!visitedNodes.contains(succ)) {
                                stack.push(succ);
                            }
                        }
                    }
                }
            }
        }

        System.out.println();
        but.profondeur_max = prof_max;
        return but;
    }

    public static boolean visited(Node node, int objet) {
        for (int i = 0; i < node.matrice.length; i++) {
            if (node.matrice[i][objet] == 1) {
                return true;
            }
        }
        return false;
    }

    public static int calculer_poids(Node noeud, Sac sac, Objet[] objets) {
        int poid = 0;
        for (int i = 0; i < noeud.matrice[sac.id].length; i++) {
            if (noeud.matrice[sac.id][i] == 1) {
                poid += objets[i].poid;
            }
        }
        return poid;
    }

    public static int calculer_val(Node noeud, Sac[] sacs, Objet[] objets) {
        int val = 0;
        for (Sac sac : sacs) {
            for (int i = 0; i < noeud.matrice[sac.id].length; i++) {
                if (noeud.matrice[sac.id][i] == 1) {
                    val += objets[i].val;
                }
            }
        }
        return val;
    }

    public static int calculer_poid_total(Node noeud, Sac[] sacs, Objet[] objets) {
        int val = 0;
        for (Sac sac : sacs) {
            for (Objet obj : objets) {
                if (noeud.matrice[sac.id][obj.id] == 1) {
                    val += obj.poid;
                }
            }
        }
        return val;
    }
}