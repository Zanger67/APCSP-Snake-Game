import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EnterNewHighScore extends JFrame implements ActionListener {
    public JTextField nameInput;
    public int newScore;

    public EnterNewHighScore(int score) {
        newScore = score;
        this.setTitle("New High Score!");

        JPanel input = new JPanel();
        JLabel lb[] = new JLabel[3];
        lb[0] = new JLabel("Congratulations!");
        lb[0].setFont(lb[0].getFont().deriveFont(15F));

        lb[1] = new JLabel("You achieved a high score of " + score + "!");
        lb[1].setFont(lb[0].getFont());

        lb[2] = new JLabel("Please enter your name below.");


        nameInput = new JTextField();
        nameInput.setFont(nameInput.getFont().deriveFont(15f));
        JButton enterName = new JButton("Submit");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(300,200);

//        input.setLayout(new BoxLayout());

        enterName.addActionListener(this);

        input.add(lb[0]);
        input.add(lb[1]);
        input.add(lb[2]);
        input.add(nameInput);
        input.add(enterName);
//
//        nameInput.setLocation(getWidth()/2,250);
//        enterName.setLocation(getWidth()/2,350);

        nameInput.setPreferredSize(new Dimension(240,30));

        this.add(input);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Queue<Integer> scores = new LinkedList<>();
        Queue<String> names = new LinkedList<>();

        try {
            String nameTaken = nameInput.getText();

            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\HighScores.txt")));


            boolean added = false;
            for (int i = 0; i < 10; i++) {
                String temp = br.readLine();
                int thisScore = Integer.parseInt(temp.substring(0, temp.indexOf(" ")));
                String thisName = temp.substring(temp.indexOf(" ") + 1);

                if (!added && thisScore < newScore){
                    scores.add(newScore);
                    names.add(nameTaken);
                    added = true;
                }

                scores.add(thisScore);
                names.add(thisName);
            }
            br.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + "\\src\\HighScores.txt")));

            for(int i = 0; i < 10; i++){
                String addThis = Integer.toString(scores.poll()) + " " + names.poll();
                bw.write(addThis);
                bw.write("\n");
            }

            bw.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


        this.dispose();

        try {
            new StartMenu();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }
}
