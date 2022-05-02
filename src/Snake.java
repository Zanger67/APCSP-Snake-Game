import javax.swing.*;
import java.io.*;
import java.awt.*;

public class Snake extends JFrame {
    public int sizeOfBoard, snakeGrowthPerEat, timeBetweenFrames, pixelsPerSquare, borderGap;
    public int[][] map;
    public JPanel[][] mapPanels;
    public Color bodyColor, headColor, backgroundColor, appleColor;

    public int sizeOfSnake;
    public char currentDirection;
    public int currentX, currentY;


    public Snake() throws IOException, InterruptedException {
        init();
        startGame();
    }

    public void startGame() throws InterruptedException {
        currentDirection = 'r';
        boolean alive = true;
        while(alive){
            move();
            updateFrame();

            Thread.sleep(timeBetweenFrames);

            alive = !willDie();
        }
    }

    public void move(){
        switch (currentDirection) {
            case 'r' :
                currentX++;
                break;
            case 'l' :
                currentX--;
                break;
            case 'u' :
                currentY++;
                break;
            case 'd' :
                currentY--;
                break;
        }

    }

    public boolean willDie(){
        switch (currentDirection) {
            case 'r' :
                if (currentX + 1 >= sizeOfBoard || map[currentX + 1][currentY] > 0) {
                    return true;
                } else return false;
            case 'l' :
                if (currentX - 1 < 0 || map[currentX - 1][currentY] > 0) {
                    return true;
                } else return false;
            case 'u' :
                if (currentY + 1 >= sizeOfBoard || map[currentX][currentY + 1] > 0) {
                    return true;
                } else return false;
            case 'd' :
                if (currentY - 1 < 0 || map[currentX][currentY - 1] > 0) {
                    return true;
                } else return false;
            default : return false;
        }
    }

    public void init() throws IOException {
        //Collects game specifics from the relevant file.
        initParameters();

//        panel = new JPanel();
//        panel.setBounds(borderGap, borderGap, sizeOfBoard * pixelsPerSquare, sizeOfBoard * pixelsPerSquare);

        //JFrame initial parameters
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(sizeOfBoard,sizeOfBoard));

        setSize(sizeOfBoard * pixelsPerSquare + 2 * borderGap, sizeOfBoard * pixelsPerSquare + 2 * borderGap); // 40 extra for border purposes
        setTitle("Simple Snake Game");

        //sets the frame's position to the center of the screen
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        //Snake game init
        map = new int[sizeOfBoard][sizeOfBoard];
        mapPanels = new JPanel[sizeOfBoard][sizeOfBoard];
        sizeOfSnake = 5;
        currentX = sizeOfBoard / 2;
        currentY = currentX;
        map[currentX][currentY] = sizeOfSnake;

        for(int y = 0; y < sizeOfBoard; y++){
            for (int x = 0; x < sizeOfBoard; x++){
                mapPanels[x][y] = new JPanel();
            }
        }

        //add adding the initial snake
        updateFrame();


        //making the application visible to the user
        this.setVisible(true);
    }

    public void updateFrame(){
        for(int y = sizeOfBoard - 1; y >= 0; y--){ //note: printing is from the top left down to bottom right
            for(int x = 0; x < sizeOfBoard; x++){

                int valueAtCoord = map[x][y];

                JPanel unit = new JPanel();
                unit.setSize(pixelsPerSquare, pixelsPerSquare);

                if (valueAtCoord == sizeOfSnake) {
                    mapPanels[x][y].setBackground(headColor);
                } else if (valueAtCoord > 0) {
                    mapPanels[x][y].setBackground(bodyColor);
                } else if (valueAtCoord == -1) { // Denotes the APPLE
                    mapPanels[x][y].setBackground(appleColor);
                } else {
                    mapPanels[x][y].setBackground(backgroundColor);
                }

                unit.setBounds(x * pixelsPerSquare + borderGap, y * pixelsPerSquare + borderGap, pixelsPerSquare, pixelsPerSquare);
                unit.setBorder(BorderFactory.createEtchedBorder());
//                this.add(unit);
            }
        }
        this.revalidate();
//        this.add(panel);
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


}
