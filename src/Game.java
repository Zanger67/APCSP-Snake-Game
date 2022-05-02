import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener, ActionListener {
    public int sizeOfBoard, snakeGrowthPerEat, timeBetweenFrames, pixelsPerSquare, borderGap;
    public Color bodyColor, headColor, backgroundColor, appleColor;

    public char direction;

    //New direction exists so that you can switch for example from W to S to D in one move before the program registers the new frame, thus bypassing the no going backwards rule
    public char newDirection;

    public int  lengthOfSnake, score;

    public int currentX, currentY;


    public int[][] mapInts;

    public boolean alive;

    public Game() throws IOException, InterruptedException {
        initParameters();

        this.setName("Snakey Snek");
        this.setSize(borderGap * 2 + sizeOfBoard * pixelsPerSquare,borderGap * 3 + sizeOfBoard * pixelsPerSquare);

        mapInts = new int[sizeOfBoard][sizeOfBoard];
        mapInts[sizeOfBoard/2][sizeOfBoard/2] = lengthOfSnake;
        addApple();

        currentX = sizeOfBoard / 2;
        currentY = sizeOfBoard / 2;

        score = 0;
        lengthOfSnake = 4;
        direction = 'r';
        newDirection = direction;

//        mainPanel = new JPanel();
//
//        mainPanel.setLayout(new GridLayout(sizeOfBoard, sizeOfBoard, 2,2));


        addKeyListener(this);

        this.setLocationRelativeTo(null);
        this.setSize(pixelsPerSquare * (sizeOfBoard + 1) + borderGap * 2, pixelsPerSquare * (sizeOfBoard + 1) + borderGap * 2); //the extra 1 is for spacing between squares and the 2x makes a buffer on the outside of the frame

        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.repaint();

//        this.add(mainPanel);
        this.setVisible(true);

        startCycles();

    }
    public Timer timer;
    public void startCycles() throws InterruptedException, IOException {
        alive = true;

        timer = new Timer(timeBetweenFrames,this);


        validate();
        repaint();
        timer.start();
//        while (alive){
//
//            System.out.println(currentX + "\t" + currentY + "\t" + direction);
//
//            Thread.sleep(timeBetweenFrames);
//            alive = nextMoveCalculations();
////            System.out.println("asdf");
//        }
//        died();
    }

    public boolean nextMoveCalculations(){
        switch (direction) {
            case 'r' :
                if (spotKillsSnakeOrEatsApple(currentX + 1, currentY)){
                    return false;
                } else {
                    remove1FromAll();
                    currentX++;
                    if (mapInts[currentX][currentY] == -1) { ateApple(); }
                    mapInts[currentX][currentY] = lengthOfSnake;

                    return true;
                }
            case 'l' :
                if (spotKillsSnakeOrEatsApple(currentX - 1, currentY)){
                    return false;
                } else {
                    remove1FromAll();
                    currentX--;
                    if (mapInts[currentX][currentY] == -1) { ateApple(); }
                    mapInts[currentX][currentY] = lengthOfSnake;
                    return true;
                }
            case 'u' :
                if (spotKillsSnakeOrEatsApple(currentX, currentY + 1)){
                    return false;
                } else {
                    remove1FromAll();
                    currentY++;
                    if (mapInts[currentX][currentY] == -1) { ateApple(); }
                    mapInts[currentX][currentY] = lengthOfSnake;
                    return true;
                }
            case 'd' :
                if (spotKillsSnakeOrEatsApple(currentX, currentY - 1)){
                    return false;
                } else {
                    remove1FromAll();
                    currentY--;
                    if (mapInts[currentX][currentY] == -1) { ateApple(); }
                    mapInts[currentX][currentY] = lengthOfSnake;
                    return true;
                }
            default :
                System.out.println("1234");
                return false;
        }
    }
    public void remove1FromAll(){
        for(int i = 0; i < sizeOfBoard; i++){for (int j = 0; j < sizeOfBoard; j++){
            if(mapInts[i][j] > 0) {
                mapInts[i][j]--;
            }
        }}
    }

    public boolean spotKillsSnakeOrEatsApple(int x, int y){
        if(x >= sizeOfBoard || y >= sizeOfBoard || x < 0 || y < 0){
            return true;
        } else if (mapInts[x][y] > 0){
            return true;
        }

        return false;
    }

    public void ateApple(){
        score += 100; // 100 pts per apple
        //adding length to snec
        for(int i = 0; i < sizeOfBoard; i++){
            for(int j = 0; j < sizeOfBoard; j++){
                if(mapInts[i][j] > 0) {
                    mapInts[i][j] += snakeGrowthPerEat;
                }
            }
        }
        lengthOfSnake += snakeGrowthPerEat;

        addApple();


    }

    public void addApple(){
        //adding new apple
        int numberOfFreeSquares = sizeOfBoard * sizeOfBoard - lengthOfSnake; // since the growth occurs after movement, there are still those spots to place apples on
        int chosenBox = (int) (Math.random() * numberOfFreeSquares);
        System.out.println(chosenBox + "\t" + numberOfFreeSquares);

        int counter = 0;
        for (int i = 0; i < sizeOfBoard; i++){
            for(int j = 0; j < sizeOfBoard; j++){
                if(mapInts[i][j] == 0){
                    if (counter == chosenBox){
                        mapInts[i][j] = -1;
                        j = sizeOfBoard + 1; // this breaks the nested loop
                        i = sizeOfBoard + 1;

//                        System.out.println(i + "\t" + j);
                    } else {
                        counter++;
                    }
                }
            }

        }
    }

    public void died() throws IOException, InterruptedException {
        boolean isHighScore = checkIfHighScore();

        if (isHighScore){
            Thread.sleep(200);
            new EnterNewHighScore(score);
            this.dispose();
        } else {
            Thread.sleep(500);
            this.dispose();
            new StartMenu();
        }
    }

    public boolean checkIfHighScore() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\HighScores.txt")));


        //getting old high scores
        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            String temp = br.readLine();
            scores.add(Integer.parseInt(temp.substring(0,temp.indexOf(" "))));
            names.add(temp.substring(temp.indexOf(" ") + 1));
        }
        br.close();

        //adding new score if applicable
        for(int i = 0; i < 10; i++){
            if (scores.get(i) < score) {
                return true;
            }
        }
        return false;
    }
    private String name2Add;



    public Image doubleBufferImage;
    public Graphics doubleBufferGraphics;
    @Override
    public void paint(Graphics g){
        doubleBufferImage = createImage(getWidth(), getHeight());
        doubleBufferGraphics = doubleBufferImage.getGraphics();

        paintComponent(doubleBufferGraphics);

        g.drawImage(doubleBufferImage,0,0,this);
    }

    public void paintComponent(Graphics g){
        super.paint(g);

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



    //This takes the parameters from the relivant txt file named "GameParameters.txt"
    public void initParameters() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\src\\GameParameters.txt")));

        String temp = br.readLine();

        while(!temp.equals("#")){
            String valueDesignation = temp.substring(0, temp.indexOf(":"));
            String value = temp.substring(temp.indexOf(":") + 1);

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

    @Override
    public void keyTyped(KeyEvent e) {
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
            default:
                break;
        }
    }

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
            case KeyEvent.VK_ESCAPE:
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

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer){
            direction = newDirection;


            if(!nextMoveCalculations()){
                try {
                    timer.stop();
                    died();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            validate();
            repaint();
            System.out.println(direction);
        }
    }
}
