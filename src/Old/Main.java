package Old;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        snake s = new snake();

        s.startGame();

    }

    public static class snake implements KeyListener {
        public File FL;
        public String filePath;
        public int[][] map;
        public final int sizeOfMap = 15;
        public final int timeBetweenFrames = 1000; //mil secs
        public int xCord, yCord;
        public boolean alive = false;
        public final int howMuchGainedFromEat = 3; // how much length gained from eating a thing
        public int score;
        public int lengthOfSnake;


        public char movement; // wsad used for directions reference (w = up, s = down, etc.)


        public snake() throws FileNotFoundException {
            filePath = System.getProperty("user.dir") + "\\src\\Old\\HighScores.txt";
            FL = new File(filePath);
        }

        public void startGame() throws InterruptedException {
            map = new int[sizeOfMap][sizeOfMap];

            xCord = sizeOfMap / 2;
            yCord = xCord;

            movement = 'd';
            alive = true;

            score = 0;
            lengthOfSnake = 5;

            while(alive){
                progress2NextFrame(); // essentially just print frame

                Thread.sleep(timeBetweenFrames);

                //process of checking next move
                switch(movement){
                    case 'w' :
                        if (map[xCord][(yCord + 1) % sizeOfMap] > 0) { // -1 represents the food that the snake wants to eat
                            alive = false;
                        }
                    case 's' :
                        if (map[xCord][(yCord - 1 + sizeOfMap) % sizeOfMap] > 0) { // -1 represents the food that the snake wants to eat
                            alive = false;
                        }
                    case 'a' :
                        if (map[(xCord - 1 + sizeOfMap) % sizeOfMap][yCord] == -1) { // -1 represents the food that the snake wants to eat
                            alive = false;
                        }
                    case 'd' :
                        if (map[(xCord + 1) % sizeOfMap][yCord] == -1) { // -1 represents the food that the snake wants to eat
                            alive = false;
                        }
                }

                /////CHECKING IF IT HITS ITSELF

                //checking if snake will eat something
                switch(movement){
                    case 'w' :
                        if (map[xCord][(yCord + 1) % sizeOfMap] == -1) { // -1 represents the food that the snake wants to eat
                            ateAThing();
                            lengthOfSnake++;
                        }
                    case 's' :
                        if (map[xCord][(yCord - 1 + sizeOfMap) % sizeOfMap] == -1) { // -1 represents the food that the snake wants to eat
                            ateAThing();
                            lengthOfSnake++;
                        }
                    case 'a' :
                        if (map[(xCord - 1 + sizeOfMap) % sizeOfMap][yCord] == -1) { // -1 represents the food that the snake wants to eat
                            ateAThing();
                            lengthOfSnake++;
                        }
                    case 'd' :
                        if (map[(xCord + 1) % sizeOfMap][yCord] == -1) { // -1 represents the food that the snake wants to eat
                            ateAThing();
                            lengthOfSnake++;
                        }
                }

                //actual action of adding new 'pixel' of snake
                switch(movement){
                    case 'w' :
                        map[xCord][(yCord + 1) % sizeOfMap] = lengthOfSnake;
                    case 's' :
                        map[xCord][(yCord - 1 + sizeOfMap) % sizeOfMap] = lengthOfSnake;
                    case 'a' :
                        map[(xCord - 1 + sizeOfMap) % sizeOfMap][yCord] = lengthOfSnake;
                    case 'd' :
                        map[(xCord + 1) % sizeOfMap][yCord] = lengthOfSnake;
                }

                subtractFromAll(1);


            }

        }

        public void progress2NextFrame(){
            blankCons();
            for(int i = 0; i < sizeOfMap; i++){
                for(int j = 0; j < sizeOfMap; j++){
                    if(map[i][j] > 0) {
                        if (map[i][j] == lengthOfSnake)
                            System.out.print(" O ");
                        else
                            System.out.print(" # ");
                    } else { System.out.print("   "); }
                }
                System.out.println();
            }
        }

        public void subtractFromAll(int x){
            for(int i = 0; i < sizeOfMap; i++){
                for(int j = 0; j < sizeOfMap; j++){
                    if(map[i][j] > 0) {
                        map[i][j] -= x;
                    }
                }
            }
        }

        public void ateAThing(){
            for(int i = 0; i < sizeOfMap; i++){
                for(int j = 0; j < sizeOfMap; j++){
                    if(map[i][j] > 0) {
                        map[i][j] += howMuchGainedFromEat;
                    }
                }
            }
        }

        public void blankCons(){
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        }

        public void printHighscores() throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(FL));

            for(int i = 0; i < 10; i++){
                System.out.println((i+1) + ".\t" + br.readLine());
            }

            br.close();
        }
        public void addScoreToLeaderboard(int score) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(FL));

            int indx = -1;
            Queue<String> copy = new LinkedList<>();

            for(int i = 10; i > 0; i--){
                int temp = Integer.parseInt(br.readLine());
                copy.add("" + temp);

                if(indx == -1 && score > temp){
                    indx = i;
                }
            }

            br.close();

            if(indx != -1){
                BufferedWriter bw = new BufferedWriter(new FileWriter(FL));
                for(int i = 10; i > 0; i--){
                    if(i == indx){
                        bw.write(score + "\n");
                    } else {
                        bw.write(copy.poll() + "\n");
                    }
                }
                bw.close();
            } // else do nothing
        }
        public void resetHighScores() throws IOException {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FL));
            for(int i = 10; i >= 1; i--){
                bw.write(Integer.toString((i * 100)) + "\n");
            }

            bw.close();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            int key = e.getKeyCode();

            switch(key){
                case KeyEvent.VK_A :
                    movement = 'A';
                case KeyEvent.VK_D :
                    movement = 'D';
                case KeyEvent.VK_W :
                    movement = 'W';
                case KeyEvent.VK_S :
                    movement = 'S';
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            switch(key){
                case KeyEvent.VK_A :
                    movement = 'A';
                case KeyEvent.VK_D :
                    movement = 'D';
                case KeyEvent.VK_W :
                    movement = 'W';
                case KeyEvent.VK_S :
                    movement = 'S';
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}
