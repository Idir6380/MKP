import javax.swing.*;
import java.awt.*;

public class ResultatInterface extends JFrame {
    private JTextArea resultatTextArea;

    public ResultatInterface(String resultat, String algorithme) {
        super("Résultat de l'algorithme " + algorithme);

        resultatTextArea = new JTextArea(resultat);
        resultatTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultatTextArea);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); 
        setVisible(true);
    }
}
