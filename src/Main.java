import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws IOException {
        snake s = new snake();
//        s.resetHighScores();
        s.resetHighScores();
        s.printHighscores();
        s.addScoreToLeaderboard(370);
        s.printHighscores();
    }

    public static class snake{
        public File FL;
        public String filePath;

        public snake() throws FileNotFoundException {
            filePath = System.getProperty("user.dir") + "\\src\\HighScores.txt";
            FL = new File(filePath);
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
    }

}
