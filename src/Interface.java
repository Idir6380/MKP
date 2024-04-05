import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.text.DecimalFormat;

public class Interface extends JFrame {
    private JComboBox<String> algorithmeComboBox;
    private JButton selectionnerButton;
    private JTextArea resultatTextArea;
    private JButton genererInstancesButton;
    private int instanceCounter;

    public Interface() {
        super("Tester un algorithme avec des données CSV");

        instanceCounter = 1;

        JLabel algorithmeLabel = new JLabel("Sélectionnez un algorithme:");
        algorithmeComboBox = new JComboBox<>();
        algorithmeComboBox.addItem("DFS");
        algorithmeComboBox.addItem("BFS");
        algorithmeComboBox.addItem("A*");

        selectionnerButton = new JButton("Sélectionner un fichier CSV");
        selectionnerButton.addActionListener(new SelectionnerButtonListener());

        resultatTextArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(resultatTextArea);

        genererInstancesButton = new JButton("Générer des instances");
        genererInstancesButton.addActionListener(new GenererInstancesButtonListener());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        panel.add(algorithmeLabel);
        panel.add(algorithmeComboBox);
        panel.add(selectionnerButton);
        panel.add(genererInstancesButton);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);
    }

    private class SelectionnerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers CSV", "csv");
            fileChooser.setFileFilter(filter);
            int resultat = fileChooser.showOpenDialog(Interface.this);
            if (resultat == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                String algorithme = (String) algorithmeComboBox.getSelectedItem();
                testerAlgorithmeSurFichier(algorithme, fichier);
            }
        }
    }

    private class GenererInstancesButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String nbrInstancesStr = JOptionPane.showInputDialog(Interface.this, "Entrez le nombre d'instances à générer:");
            if (nbrInstancesStr != null) {
                try {
                    int nbrInstances = Integer.parseInt(nbrInstancesStr);
                    for (int i = 0; i < nbrInstances; i++) {
                        genererInstance();
                    }
                    JOptionPane.showMessageDialog(Interface.this, "Instances générées avec succès.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Interface.this, "Veuillez entrer un nombre valide.");
                }
            }
        }
    }

    public void genererInstance() {
        Random random= new Random();
        int nbrSacs= random.nextInt(10) + 1;
        int nbrObjets= random.nextInt(40) + 1;
        String fileName = "instance_" + instanceCounter + ".csv";
        genererInstances(fileName, nbrSacs, nbrObjets);
        instanceCounter++;
    }
    
    public void genererInstances(String fileName, int nbrSacs, int nbrObjets) {
        Random random= new Random();
        try (PrintWriter writer= new PrintWriter(new FileWriter(fileName))) {
            writer.println("Nombre de sacs,Nombre d'objets");
            writer.println(nbrSacs + "," + nbrObjets);
    
            writer.println("Poids de sacs");
            for (int i = 0; i < nbrSacs; i++) {
                int poidsSac= random.nextInt(20000)+3000;
                writer.println(poidsSac);
            }
    
            writer.println("Poids,Valeur");
            for (int i= 0; i < nbrObjets; i++) {
                int poidsObjet= random.nextInt(10000) + 20;
                int valeurObjet= random.nextInt(3000) + 5;
                writer.println(poidsObjet + "," + valeurObjet);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Node executerDFS(Sac[] sacs, Objet[] objets) {
        return DFS.recherche(sacs, objets);
    }

    public Node executerBFS(Sac[] sacs, Objet[] objets) {
        return BFS.recherche(sacs, objets);
    }

    public Node executerAStar(Sac[] sacs, Objet[] objets) {
        Node initialNode= new Node(sacs.length, objets.length);
        return Astar.astar(objets, sacs, initialNode);
    }

    private void testerAlgorithmeSurFichier(String algorithme, File fichier) {
        Scanner scanner= null;
        try {
            scanner= new Scanner(fichier);
            String premierLigne= scanner.nextLine(); 
            System.out.println("Première ligne : " + premierLigne);
    
            String[] ligneNbrSacs= scanner.nextLine().split(",");
            int nbrSacs= Integer.parseInt(ligneNbrSacs[0]);
            int nbrObjets= Integer.parseInt(ligneNbrSacs[1]);
            Sac[] sacs= new Sac[nbrSacs];
            Objet[] objets= new Objet[nbrObjets];
    
            
            String lignePoidsValeur= scanner.nextLine();
            System.out.println("Ligne 'Poids,Valeur' : " + lignePoidsValeur);
    
           
            System.out.println("Données des sacs :");
            for (int i = 0; i < nbrSacs; i++) {
                String[] ligneSac = scanner.nextLine().split(",");
                int poids = Integer.parseInt(ligneSac[0]);
                sacs[i] = new Sac(i, poids);
                System.out.println("Sac " + i + ": poids=" + poids);
            }
    
            
            scanner.nextLine();

           
            System.out.println("Données des objets :");
            for (int i= 0; i < nbrObjets; i++) {
                String[] ligneObjet= scanner.nextLine().split(",");
                int poids= Integer.parseInt(ligneObjet[0]);
                int valeur= Integer.parseInt(ligneObjet[1]);
                objets[i]= new Objet(i, valeur, poids);
                System.out.println("Objet " + i + ": poids=" + poids + ", valeur=" + valeur);
            }
            long startTime = System.currentTimeMillis();
            
            Node resultat= null;
            switch (algorithme) {
                case "DFS":
                    resultat= executerDFS(sacs, objets);
                    break;
                case "BFS":
                    resultat= executerBFS(sacs, objets);
                    break;
                case "A*":
                    resultat= executerAStar(sacs, objets);
                    break;
            }
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
             
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("pour l'instance ").append("' ").append(fichier.getName()).append(" '").append(" :\n");
            resultBuilder.append("Configuration actuelle :\n");
            resultBuilder.append("Poids total: ").append(resultat.poid_total).append(" gramme").append("\n");
            resultBuilder.append("Valeur totale: ").append(resultat.val_total).append("\n");
            resultBuilder.append("Profondeur maximale: ").append(resultat.profondeur_max).append("\n");
            resultBuilder.append("\n");
            resultBuilder.append("Details sur les sacs ").append(" :\n");

            for (Sac sac : sacs) {
                resultBuilder.append("Sac ").append(sac.id).append(" :\n");
                int poidsRempli= 0;
                for (Objet objet : objets) {
                    if (resultat.matrice[sac.id][objet.id] == 1) {
                        poidsRempli += objet.poid;
                    }
                }
                DecimalFormat df = new DecimalFormat("#.##");
                double pourcentageRemplissage= (double) poidsRempli / (double) sac.poid * 100.0;
                String pourcentageFormate = df.format(pourcentageRemplissage);
                resultBuilder.append("- Objets contenus : ");
                boolean premier = true;
                for (Objet objet : objets) {
                    if (resultat.matrice[sac.id][objet.id] == 1) {
                        if (!premier) {
                            resultBuilder.append(", ");
                        }
                        resultBuilder.append(objet.id);
                        premier = false;
                    }
                }
                resultBuilder.append("\n");
                
                resultBuilder.append("- Poids du sac : ").append(sac.poid).append("\n");
                resultBuilder.append("- Poids rempli : ").append(poidsRempli).append("\n");
                resultBuilder.append("- Pourcentage de remplissage : ").append(pourcentageFormate).append("%\n\n");
                
            }
            resultBuilder.append("Temps d'exécution : ").append(executionTime).append(" millisecondes\n\n");


            new ResultatInterface(resultBuilder.toString(), algorithme);


        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Interface.this, "Erreur: Fichier introuvable");
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
}
    public static void main(String[] args) {
        new Interface();
    }
}