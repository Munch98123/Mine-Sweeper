import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Random;
import java.awt.event.MouseEvent;


public class Board
{
    public int bombsLeft = 0;
    public int toWin = 0;
    private FlippableButton[][] buttons;
    private int[][] gridLayout;
    private int[][] bombLocations;

    private int[][] adj = { {-1, 1} , {1, 0} , {1, 0} , {0, -1} , {0, -1} , {-1, 0} , {-1, 0} , {0, 1} };
    private int height, width;

    private boolean[][] flagPresent;

    // Resource loader
    private ClassLoader loader = getClass().getClassLoader();

    public Board(int h, int w, int numBombs, ActionListener AL, JLabel bombsLeftLabel)
    {
        height = h;
        width = w;
        bombsLeft = numBombs;

        buttons = new FlippableButton[height][width];
        flagPresent = new boolean[height][width];
        gridLayout = new int[height][width];
        bombLocations = new int[numBombs][2];

        for(int k = 0; k < bombLocations.length; k++) { Arrays.fill(bombLocations[k], -1); }

        Random rand = new Random();

        for (int i = 0; i < numBombs; i++) {
            int pts[] = { rand.nextInt(height), rand.nextInt(width) };

            while (containsPt(pts)) {
                pts[0] = rand.nextInt(height);
                pts[1] = rand.nextInt(width);
            }

            bombLocations[i] = pts;

            int x = pts[0];
            int y = pts[1];

            gridLayout[x][y] = -1;

            for (int q = 0; q < adj.length; q++){
                x += adj[q][0];
                y += adj[q][1];
                if (!(x < 0 || x >= height || y < 0 || y >= width || gridLayout[x][y] == -1)) { gridLayout[x][y] += 1; }
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                FlippableButton b = new FlippableButton(i, j, gridLayout[i][j]);
                b.addActionListener(AL);
                mouse(b, AL, bombsLeftLabel);
                buttons[i][j] = b;
            }
        }
    }


    public void reveal(int x, int y, ActionListener AL) {
        if (x < 0 || x >= height || y < 0 || y >= width || gridLayout[x][y] == -1 || buttons[x][y].revealed)
            return;

        buttons[x][y].showFront();
        buttons[x][y].removeActionListener(AL);

        if ( gridLayout[x][y] == 0 ) {
            for (int q = 0; q < adj.length; q++){
                toWin++;
                x += adj[q][0];
                y += adj[q][1];
                reveal(x, y, AL);
            }
        }
    }


    public void revealBoard(ActionListener AL)
    {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                buttons[i][j].showFront();
                buttons[i][j].removeActionListener(AL);
            }
        }
    }

    public void revealBombs(ActionListener AL)
    {
        for (int[] i : bombLocations){
            buttons[i[0]][i[1]].showFront();
            buttons[i[0]][i[1]].removeActionListener(AL);
        }
    }


    public void fillBoardView(JPanel view)
    {

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                view.add(buttons[i][j]);
            }
        }
    }

    private void mouse(FlippableButton b, ActionListener AL,JLabel bombListener) {
        b.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }

            @Override
            public void mousePressed(MouseEvent e) {
                FlippableButton currCard = (FlippableButton)e.getSource();

                if (e.getButton() == MouseEvent.BUTTON3 && !flagPresent[currCard.getI()][currCard.getJ()]) {
                    currCard.showFlag();
                    currCard.removeActionListener(AL);
                    flagPresent[currCard.getI()][currCard.getJ()] = true;
                    bombsLeft--;
                    bombListener.setText("Bombs: " + bombsLeft);


                }
                else if (e.getButton() == MouseEvent.BUTTON3 && flagPresent[currCard.getI()][currCard.getJ()]) {
                    currCard.showBack();
                    currCard.addActionListener(AL);
                    flagPresent[currCard.getI()][currCard.getJ()] = false;
                    bombsLeft++;
                    bombListener.setText("Bombs: " + bombsLeft);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });
    }

    private boolean containsPt(int[] pt) {

        for (int i = 0; i < bombLocations.length; i++) {
            if (bombLocations[i][0] == pt[0] && bombLocations[i][1] == pt[1]) {
                return true; }
        }
        return false;
    }

}
