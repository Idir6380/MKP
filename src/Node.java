public class Node {
    int[][] matrice;
    int g = 0;
    int h = 0;
    double f = 0;
    long poid_total = 0;
    int val_total = 0;
    int profondeur_max = 0;
    String configuration;

    public Node(int nbr_sac, int nbr_objets) {
        this.matrice = new int[nbr_sac][nbr_objets];
        this.updateConfiguration();
    }

    public void updateConfiguration() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[i].length; j++) {
                if (matrice[i][j] == 1) {
                    sb.append(i).append(j);
                }
            }
        }
        this.configuration = sb.toString();
    }

    @Override
    public int hashCode() {
        return configuration.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        return configuration.equals(other.configuration);
    }
}