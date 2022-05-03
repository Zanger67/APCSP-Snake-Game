package CompiledIntoOneFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class SnakeMainCompiled {
    public static void main(String[] args) throws IOException {
        new Snake();
    }

    public static class Snake {
        public Snake() throws IOException {
            new StartMenu();
        }
    }

    public static class StartMenu extends JFrame implements ActionListener {

        public StartMenu() throws IOException {
            //Separation of panels makes it easier to organize the positioning
            JPanel panel1 = new JPanel();
            JPanel panel2 = new JPanel();
            JPanel panel3 = new JPanel();


            //Setting parameters of start menu
            this.setSize(300,375);
            this.setLocationRelativeTo(null);
            this.setResizable(true);

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Start Game");

            this.setLayout(new FlowLayout());


            JLabel lb = new JLabel("Snake Game");
            JButton start = new JButton("Click to Start");

            //Button to proceed to game
            start.addActionListener(this);

            //Setting position and sizings
            lb.setLocation(this.getSize().width / 2,0);
            start.setLocation(this.getSize().width/2,50);

            panel1.add(lb);


            JTextArea jta = displayHighscores();
            panel2.add(jta);
            panel3.add(start);

            //Adding components to JFrame
            this.add(panel1);
            this.add(panel2);
            this.add(panel3);

            this.setVisible(true);
        }

        //Outputs the JTextArea that will be used to display the previous high scores. This method also reads the scores from the relivant file.
        public JTextArea displayHighscores() throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\HighScores.txt")));

            JTextArea jta = new JTextArea();
            jta.append("High Scores:\n");

            //Reading scores from BufferedReader and adding it to the JTextArea
            for(int i = 0; i < 10; i++){
                String temp = br.readLine();
                jta.append((i+1) + ": " + temp.substring(0,temp.indexOf(" ")) + "\t\t" + temp.substring(temp.indexOf(" ") + 1) + "\n");
            }

            jta.setEditable(false);
            br.close();

            return jta;
        }


        //This is for the JButton that starts the game.
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Game g = new Game();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }

            //dispose closes the "start window" and gets rid of it so it won't clutter, making it seem like a window switch.
            this.dispose();
        }
    }


    public static class Game extends JFrame implements KeyListener, ActionListener {

        //These variables are the ones that have preset parameters taken from the relivant parameter TXT file
        public int sizeOfBoard, snakeGrowthPerEat, timeBetweenFrames, pixelsPerSquare, borderGap;
        public Color bodyColor, headColor, backgroundColor, appleColor;

        //Denotes the direction the snake is moving in in the form of l=left, r=right, u=up, d=down.
        public char direction;

        //New direction exists so that you can switch for example from W to S to D in one move before the program registers the new frame, thus bypassing the no going backwards rule
        //That is, if the snake is moving right, and you click up, it will put the new direction that it will travel into this variable instead of "direction" so you can change your choice. The check to make sure you don't go "backwards" will be performed as a comparison with "direction" thus making it impossible to switch from l-->u-->r in one turn causing the snake to go backwards.
        public char newDirection;

        public int  lengthOfSnake, score;

        //Current snake head position
        public int currentX, currentY;


        //The array that stores all items on the "field" of play, including apple positions, snake positions, etc.
        public int[][] mapInts;

        //This timer controls the game's updating pattern and move calculation delay
        public Timer timer;

        public boolean alive;

        public Game() throws IOException, InterruptedException {
            //Initiating and collecting parameters from the relivant TXT file.
            initParameters();

            //Initing the JFrame information
            this.setName("Snakey Snek");

            //Size is based on the number of boxes times the number of pixels a side of the box has, plus all the added border gap desired.
            this.setSize(borderGap * 2 + sizeOfBoard * pixelsPerSquare,borderGap * 5 + sizeOfBoard * pixelsPerSquare);

            //More var initialization
            mapInts = new int[sizeOfBoard][sizeOfBoard];
            mapInts[sizeOfBoard/2][sizeOfBoard/2] = lengthOfSnake;

            //Adding the first apple
            addApple();

            //placing the snake in the middle of the board to start
            currentX = sizeOfBoard / 2;
            currentY = sizeOfBoard / 2;

            //Default start settings
            score = 0;
            lengthOfSnake = 4;
            direction = 'r';
            newDirection = direction;

            //Adding the controls (using WSAD or arrow keys to move)
            addKeyListener(this);

            //Positioning of the JFrame
            this.setLocationRelativeTo(null);
            this.setSize(pixelsPerSquare * (sizeOfBoard + 1) + borderGap * 2, pixelsPerSquare * (sizeOfBoard + 1) + borderGap * 2); //the extra 1 is for spacing between squares and the 2x makes a buffer on the outside of the frame

            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            this.setVisible(true);

            //Starting the game
            startCycles();
        }


        public void startCycles() throws InterruptedException, IOException {
            alive = true;
            timer = new Timer(timeBetweenFrames,this);

            validate();
            repaint();

            //Starts the game's internal timer to go
            timer.start();
        }

        //Calculates everything related to the next move / frame
        public boolean nextMoveCalculations(){
            //Switch case allows for modification of calculation based on direction character variable
            switch (direction) {
                case 'r' :
                    if (spotKillsSnake(currentX + 1, currentY)){
                        return false;
                    } else {
                        remove1FromAll();
                        currentX++;
                        if (mapInts[currentX][currentY] == -1) { ateApple(); }
                        mapInts[currentX][currentY] = lengthOfSnake;

                        return true;
                    }
                case 'l' :
                    if (spotKillsSnake(currentX - 1, currentY)){
                        return false;
                    } else {
                        remove1FromAll();
                        currentX--;
                        if (mapInts[currentX][currentY] == -1) { ateApple(); }
                        mapInts[currentX][currentY] = lengthOfSnake;
                        return true;
                    }
                case 'u' :
                    if (spotKillsSnake(currentX, currentY + 1)){
                        return false;
                    } else {
                        remove1FromAll();
                        currentY++;
                        if (mapInts[currentX][currentY] == -1) { ateApple(); }
                        mapInts[currentX][currentY] = lengthOfSnake;
                        return true;
                    }
                case 'd' :
                    if (spotKillsSnake(currentX, currentY - 1)){
                        return false;
                    } else {
                        remove1FromAll();
                        currentY--;
                        if (mapInts[currentX][currentY] == -1) { ateApple(); }
                        mapInts[currentX][currentY] = lengthOfSnake;
                        return true;
                    }
                default : //Unimportant addition that helps with the debugging process
                    return false;
            }
        }

        //This removes 1 "body value" from each of the snakes parts. That way, each cell of the map knows how long the snake will "remain on the square" as it ticks to 0
        public void remove1FromAll(){
            for(int i = 0; i < sizeOfBoard; i++){for (int j = 0; j < sizeOfBoard; j++){
                if(mapInts[i][j] > 0) {
                    mapInts[i][j]--;
                }
            }}
        }

        //Checks if the next move will kill the snake
        public boolean spotKillsSnake(int x, int y){
            //Hitting the wall aka going out of bounds
            if(x >= sizeOfBoard || y >= sizeOfBoard || x < 0 || y < 0){
                return true;
            } else if (mapInts[x][y] > 0){ //Hitting itself (any value greater than 0 is a part of the snake.
                return true;
            }

            //Default case of there being nothing there.
            return false;
        }

        //Called if the snake eats an apple
        public void ateApple(){
            score += 100; // 100 pts per apple

            //Adding length to the snake by adding the additional body length to all existing parts of the snake.
            //This makes the snake grow into the next few frames based on how many it's supposed to grow by.
            //This prevents uninentional conflicts
            for(int i = 0; i < sizeOfBoard; i++){
                for(int j = 0; j < sizeOfBoard; j++){
                    if(mapInts[i][j] > 0) {
                        mapInts[i][j] += snakeGrowthPerEat;
                    }
                }
            }
            lengthOfSnake += snakeGrowthPerEat; // For future reference as the snake moves

            //Adds a new apple to target
            addApple();

        }

        public void addApple(){
            //adding new apple
            int numberOfFreeSquares = sizeOfBoard * sizeOfBoard - lengthOfSnake; // This calculates the number of potential apple spots.
            int chosenBox = (int) (Math.random() * numberOfFreeSquares);

            //This essentially goes through each cell and finds the Nth cell, aka the chosen cell, and places the apple there. A cell is only counted as one of the 1,2,3,...n-1 cells if it is empty aka no snake there.
            int counter = 0;
            for (int i = 0; i < sizeOfBoard; i++){
                for(int j = 0; j < sizeOfBoard; j++){
                    if(mapInts[i][j] == 0){
                        if (counter == chosenBox){
                            mapInts[i][j] = -1;
                            j = sizeOfBoard + 1; // this breaks the nested loop
                            i = sizeOfBoard + 1;
                        } else {
                            counter++;
                        }
                    }
                }
            }

        }

        //Called when the snake hits itself or a boundary resulting in the end of the game
        public void died() throws IOException, InterruptedException {

            //Checks if a high score was achieved
            boolean isHighScore = checkIfHighScore();

            //Case where a new high score is found, requiring a name input
            if (isHighScore){
                Thread.sleep(200);
                new EnterNewHighScore(score);
                this.dispose();
            }
            //Default case where no high score was achieved (not top 10)
            else {
                Thread.sleep(500);
                this.dispose();
                new StartMenu();
            }
        }

        public boolean checkIfHighScore() throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\HighScores.txt")));

            //getting old high scores and storing them in ArrayLists
            ArrayList<Integer> scores = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();

            for(int i = 0; i < 10; i++){
                String temp = br.readLine();
                scores.add(Integer.parseInt(temp.substring(0,temp.indexOf(" "))));
                names.add(temp.substring(temp.indexOf(" ") + 1));
            }

            br.close();

            //Finding out if the high score fits in as one of the 1st to 10th places
            for(int i = 0; i < 10; i++){
                if (scores.get(i) < score) {
                    return true;
                }
            }
            return false;
        }
        private String name2Add;



        //Double buffering system to prevent flashes between repainting updates.
        public Image doubleBufferImage;
        public Graphics doubleBufferGraphics;

        @Override
        public void paint(Graphics g){
            //Creating new image to be painted
            doubleBufferImage = createImage(getWidth(), getHeight());
            doubleBufferGraphics = doubleBufferImage.getGraphics();

            paintComponent(doubleBufferGraphics);

            //Painting the newly created image
            g.drawImage(doubleBufferImage,0,0,this);
        }

        //Default process of painting the "field"
        public void paintComponent(Graphics g){
            super.paint(g);

            //Goes through each coordinate and determines if the value is an apple (-1), body (>0 but not equal to the snake length), head (equal to snake length), or empty space (0) then drawing the square
            for (int y = sizeOfBoard - 1; y >= 0; y--){
                for (int x = 0; x < sizeOfBoard; x++){
                    if(mapInts[x][y] == 0){
                        g.setColor(backgroundColor);
                        g.fillRect((x+1) * pixelsPerSquare, borderGap + (y+1) * pixelsPerSquare, pixelsPerSquare, pixelsPerSquare);
                    } else if (mapInts[x][y] == lengthOfSnake) {
                        g.setColor(headColor);
                        g.fillRect((x+1) * pixelsPerSquare, borderGap + (y+1) * pixelsPerSquare, pixelsPerSquare, pixelsPerSquare);
                    } else if (mapInts[x][y] == -1) {
                        g.setColor(appleColor);
                        g.fillRect((x+1) * pixelsPerSquare, borderGap + (y+1) * pixelsPerSquare, pixelsPerSquare, pixelsPerSquare);
                    } else {
                        g.setColor(bodyColor);
                        g.fillRect((x+1) * pixelsPerSquare, borderGap + (y+1) * pixelsPerSquare, pixelsPerSquare, pixelsPerSquare);
                    }
                }
            }


        }


        //This takes the parameters from the relevant txt file named "GameParameters.txt"
        public void initParameters() throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\GameParameters.txt")));

            String temp = br.readLine();

            // # case stops the checking, allowing for notes below
            while(!temp.equals("#")){
                String valueDesignation = temp.substring(0, temp.indexOf(":"));
                String value = temp.substring(temp.indexOf(":") + 1);

                //Checking which parameter is being read so that order doesn't matter
                switch (valueDesignation) {
                    case "sizeOfBoard" :
                        sizeOfBoard = Integer.parseInt(value);
                        break;
                    case "snakeGrowthPerEat" :
                        snakeGrowthPerEat = Integer.parseInt(value);
                        break;
                    case "timeBetweenFrames" :
                        timeBetweenFrames = Integer.parseInt(value);
                        break;
                    case "pixelsPerSquare" :
                        pixelsPerSquare = Integer.parseInt(value);
                        break;
                    case "borderGap" :
                        borderGap = Integer.parseInt(value);
                        break;

                    case "bodyColor" :
                        bodyColor = Color.decode(value);
                        break;
                    case "headColor" :
                        headColor = Color.decode(value);
                        break;
                    case "appleColor" :
                        appleColor = Color.decode(value);
                        break;
                    case "backgroundColor" :
                        backgroundColor = Color.decode(value);
                        break;
                    default :
                        break;
                }

                temp = br.readLine();
            }

            br.close();
        }

        //Inputs from keyboard for controlling the snake's direction
        @Override
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (direction != 'r') { // prevents going backwards --> only updates if the original direction is 90 degrees from the new typed direction
                        newDirection = 'l'; // assigns new value to a separate var so the reference direction is still intact
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (direction != 'l') { // prevents going backwards
                        newDirection = 'r';
                    }
                    break;
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (direction != 'u') { // prevents going backwards
                        newDirection = 'd';
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (direction != 'd') { // prevents going backwards
                        newDirection = 'u';
                    }
                    break;
                case KeyEvent.VK_ESCAPE: // For exiting the game prematurely
                    this.dispose();
                    timer.stop();
                    try {
                        new StartMenu();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

        //Same as the above method, but applies to another potential case of pressing (depending on the length of the press)
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (direction != 'r') { // prevents going backwards
                        newDirection = 'l';
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (direction != 'l') { // prevents going backwards
                        newDirection = 'r';
                    }
                    break;
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (direction != 'u') { // prevents going backwards
                        newDirection = 'd';
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (direction != 'd') { // prevents going backwards
                        newDirection = 'u';
                    }
                    break;
                case KeyEvent.VK_ESCAPE: // For exiting the game prematurely
                    this.dispose();
                    timer.stop();
                    try {
                        new StartMenu();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

        //Not relevant to this program
        @Override
        public void keyReleased(KeyEvent e) {}


        //This refers solely to the Timer function that repaints the JFrame and calculates the moves at regular intervals.
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == timer){
                //Sets direction to the newly chosen direction
                direction = newDirection;

                //If the move results in the snake's collision
                if(!nextMoveCalculations()){
                    try {
                        //Stop the timer process and proceed with protocol
                        timer.stop();
                        died();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                //Otherwise, continue on as usual
                validate();
                repaint();

                //Printing the direction to console for reference and proof of computer input
                System.out.println(direction);
            }
        }
    }

    //New high score achieved class JFrame
    public static class EnterNewHighScore extends JFrame implements ActionListener {
        public JTextField nameInput;
        public int newScore;

        public EnterNewHighScore(int score) {
            newScore = score;
            this.setTitle("New High Score!");

            JPanel input = new JPanel();
            JLabel lb[] = new JLabel[3];

            //Separated for font and alignment purposes
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

            enterName.addActionListener(this);

            input.add(lb[0]);
            input.add(lb[1]);
            input.add(lb[2]);
            input.add(nameInput);
            input.add(enterName);

            nameInput.setPreferredSize(new Dimension(240,30));

            this.add(input);
            this.setLocationRelativeTo(null);
            this.setResizable(false);
            this.setVisible(true);
        }

        //Button press activates this. The resulting code takes the name and inserts it into the highscores file
        @Override
        public void actionPerformed(ActionEvent e) {
            java.util.Queue<Integer> scores = new LinkedList<>();
            java.util.Queue<String> names = new LinkedList<>();

            //Reading the previous scores again
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

            //Writing the new set of top 10 scores to the same file.
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


            //Disposes of this JFrame and returns to the start menu
            this.dispose();

            try {
                new StartMenu();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        }
    }

}


/**
 *
 * Files that are used:
 *      High Scores File:

 1100 Holland
 1100 Val
 1000 John
 900 Cam
 800 Jessica
 700 Philip
 600 Lawrence
 500 Emily
 400 Daniel
 300 Bob


 *      Parameters File:

 sizeOfBoard:15
 snakeGrowthPerEat:3
 timeBetweenFrames:100
 pixelsPerSquare:30
 borderGap:20
 backgroundColor:#D3D3D3
 headColor:#006400
 bodyColor:#90EE90
 appleColor:#FF0000
 #

 Notes:
 sizeOfBoard refers to the side length (both height and width)
 snakeGrowthPerEat refers to how many units the snake will grow for eating an "apple"
 timeBetweenFrames refers to the number of milliseconds before the next "frame" registers, in other words the delay between updates

 Hashtag (#) denotes the end of parameters so these notes below are possible and won't cause errors.


 */