import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Bombs extends JFrame implements ActionListener
{
    private Board gameBoard;
    public JLabel timerLabel, bombsLeftLabel;
    private JPanel boardView, labelView;

    private int gameTime = 0;
    private Timer gameTimer;
    private ClassLoader loader = getClass().getClassLoader();
    private int toWin, height, width, numBombs;

    private boolean gameWon, gameLost;
    private boolean bombsChosen, sizeChosen;

    private Icon smiley = new ImageIcon(loader.getResource("res/facesmile.png"));
    private JRadioButtonMenuItem setupItems[];
    private ButtonGroup setupButtonGroup;
    public JButton restart;


    public Bombs() {
        Bombs M = new Bombs(3, 3, (3*3)/2);
    }

    public Bombs(int h, int w, int n)
    {
        super("Minesweeper");

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        createMenus();
        //start off by initializing important board information
        height = h;
        width = w;
        numBombs = n;
        toWin = h * w - n;
        //other important info like clock and restart
        bombsLeftLabel = new JLabel("Bombs: " + numBombs);
        restart = new JButton(smiley);
        restart.addActionListener(this);
        timerLabel = new JLabel("Timer: 0");
        //important initialization for time clock
        ActionListener seconds = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameTime += 1;
                timerLabel.setText("Timer:  " + gameTime);
            }
        };
        gameTimer = new Timer(1000, seconds);

        labelView = new JPanel();
        boardView = new JPanel();
        labelView.setLayout(new GridLayout(1, 3, 0, 0));
        gameBoard = new Board(height, width, numBombs, this, bombsLeftLabel);

        labelView.add(bombsLeftLabel);
        labelView.add(restart);
        labelView.add(timerLabel);
        boardView.setLayout(new GridLayout(height, width, 2, 2));

        gameBoard.bombsLeft = numBombs;
        gameBoard.fillBoardView(boardView);

        Container c = getContentPane();
        c.add(labelView, BorderLayout.NORTH);
        c.add(boardView, BorderLayout.SOUTH);

        setSize(50*width+50, 50*height+125);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void createMenus(){
        JMenu gameMenu = new JMenu( "Game" );
        JMenu setupMenu = new JMenu( "Setup" );
        JMenuItem newItem = new JMenuItem( "New" );
        gameMenu.add( newItem );
        newItem.addActionListener(this);

        String difficulty[] = { "Beginner", "Intermediate", "Expert", "Custom" };

        setupItems = new JRadioButtonMenuItem[ difficulty.length ];
        setupButtonGroup = new ButtonGroup();

        for ( int count = 0; count < difficulty.length; count++ )
        {
            setupItems[ count ] = new JRadioButtonMenuItem( difficulty[ count ] );
            setupMenu.add( setupItems[ count ] );
            setupButtonGroup.add( setupItems[ count ] );
            setupItems[ count ].addActionListener( this );
        }

        gameMenu.add( setupMenu );
        gameMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem( "Exit" );
        JMenuItem helpItem = new JMenuItem( "Help" );
        gameMenu.add( exitItem );

        exitItem.addActionListener(this);
        helpItem.addActionListener(this);

        JMenuBar mBar = new JMenuBar();
        setJMenuBar( mBar );
        mBar.add( gameMenu );
        mBar.add( helpItem );
    }

    public void customMenu(){
        JFrame popup = new JFrame();
        JLabel topLabel = new JLabel("Choose a size and # of bombs");
        popup.setSize(400, 190);
        popup.setLocationRelativeTo(null);
        setResizable(false);
        popup.setVisible( true );
        //Sliders are used to determine the size of the board and numbers of bombs
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL,3,12,3);
        JSlider bombsSlider = new JSlider(JSlider.HORIZONTAL,2,(height*width)/2,2);
        //------------------------ Bug 2 is in this code -----------------------------------------
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setMajorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setBorder(new TitledBorder("Size(N x N)"));
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if(!source.getValueIsAdjusting()){
                    height = (int)source.getValue();
                    width = (int)source.getValue();
                    bombsSlider.setMaximum((height*width)/2);
                    //the bomb slider dynamically changes based upon the
                    //average of the height and width
                    if(height < bombsSlider.getValue()){
                        bombsSlider.setValue((height+width)/2);
                    }
                    sizeChosen = true;
                    bombsChosen = false;
                }
            }
        });

        bombsSlider.setMajorTickSpacing((height*width)/2);
        bombsSlider.setPaintTicks(true);
        bombsSlider.setPaintLabels(true);
        bombsSlider.setBorder(new TitledBorder("Bombs"));
        bombsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if(!source.getValueIsAdjusting()){
                    bombsChosen = true;
                    numBombs = bombsSlider.getValue();
                    if(sizeChosen == true && bombsChosen == true){
                        reset(height,width,numBombs);
                    }
                }
            }
        });
        //-------------------------------------------------------------------------------
        popup.add(topLabel, BorderLayout.NORTH);
        popup.add(sizeSlider, BorderLayout.CENTER);
        popup.add(bombsSlider,BorderLayout.SOUTH);
    }

    public void helpMenu(){
        JOptionPane.showMessageDialog( Bombs.this,
                "Start by clicking any square on the board.\n" +
                        "The number on the square tells you how many\nbombs are next to the square.\n" +
                        "Your goal is to clear all of the squares that are not bombs.\n" +
                        "Right clicking 'flags' a square and makes it easier to track the bombs.\n" +
                        "If you flag a square, you cannot click it until you un-flag it.",
                "Help", JOptionPane.PLAIN_MESSAGE );
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Exit")){ System.exit(0); }

        else if (e.getActionCommand().equals("New") || (e.getSource().equals(restart))){ reset(height, width, numBombs);}
        else if (e.getActionCommand().equals("Beginner")){ reset(4,4,4); }
        else if (e.getActionCommand().equals("Intermediate")){ reset(8, 8, 15);}
        else if (e.getActionCommand().equals("Expert")){ reset(12, 12, 40);}

        else if (e.getActionCommand().equals("Custom")){
            customMenu();
            if(bombsChosen == true && sizeChosen == true){
                reset(height,width,numBombs);
            }
        }
        else if (e.getActionCommand().equals("Help")){ helpMenu(); }

        else{

            FlippableButton currCard = (FlippableButton)e.getSource();
            int x = currCard.getI();
            int y = currCard.getJ();
            gameTimer.start();
            //Bug 1 is in here, cant get this function to work
            if (gameBoard.toWin == ((height*width)-numBombs) &&  gameLost == false) {
                win();
            }
            else if (currCard.id() == -1 && gameWon == false){
                lose();
            }
            else if (currCard.id() > 0 && currCard.id() < 9){
                currCard.showFront();
                gameBoard.toWin++;
                currCard.removeActionListener(this);
            }
            else if (currCard.id() == 0){
                gameBoard.reveal(x, y, this);
            }

        }
    }

    public void reset(int h, int w, int n){
        dispose();
        new Bombs(h, w, n);
    }

    public void win(){
        gameWon = true;
        gameBoard.revealBoard(this);
        gameTimer.stop();
        JOptionPane.showMessageDialog( Bombs.this,"YOU WIN!","Game Complete", JOptionPane.PLAIN_MESSAGE );
    }

    public void lose(){
        gameLost = true;
        //gameBoard.revealBombs(this);
        gameBoard.revealBoard(this);
        gameTimer.stop();
        JOptionPane.showMessageDialog( Bombs.this,"YOU LOSE!","Game Over", JOptionPane.PLAIN_MESSAGE );
    }


    public static void main(String args[])
    {
        Bombs M = new Bombs();
    }
}
