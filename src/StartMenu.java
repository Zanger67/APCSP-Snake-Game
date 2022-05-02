import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Flow;

public class StartMenu extends JFrame implements ActionListener {

    public StartMenu() throws IOException {
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();


        this.setSize(320,375);
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Start Game");

        this.setLayout(new FlowLayout());


        JLabel lb = new JLabel("Snake Game");
        JButton start = new JButton("Click to Start");

        start.addActionListener(this);

        lb.setLocation(this.getSize().width / 2,0);
        start.setLocation(this.getSize().width/2,50);

        panel1.add(lb);


        JTextArea jta = displayHighscores();
        panel2.add(jta);
        panel3.add(start);


        this.add(panel1);
        this.add(panel2);
        this.add(panel3);
        this.setVisible(true);
    }

    public JTextArea displayHighscores() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\HighScores.txt")));

        JTextArea jta = new JTextArea();
        jta.append("High Scores:\n");

        for(int i = 0; i < 10; i++){
            String temp = br.readLine();
            jta.append((i+1) + ": " + temp.substring(0,temp.indexOf(" ")) + "\t\t" + temp.substring(temp.indexOf(" ") + 1) + "\n");
        }

        jta.setEditable(false);
        br.close();

        return jta;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Game g = new Game();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
        this.dispose();
    }
}
