import javax.swing.*;


public class FlippableButton extends JButton
{
    private ClassLoader loader = getClass().getClassLoader();
    private Icon front;
    private Icon back = new ImageIcon(loader.getResource("res/blank.png"));
    private Icon flag = new ImageIcon(loader.getResource("res/flag.png"));
    private int id, i, j;
    boolean revealed;
    public static int revealCount;

    public FlippableButton() {
        super();
        super.setIcon(back);
        id = 0;
    }

    public FlippableButton(int x, int y, int k)
    {
        super();
        i = x;
        j = y;
        id = k;
        String imgPath = "res/" + id + ".png";
        ImageIcon img = new ImageIcon(loader.getResource(imgPath));
        front = img;

        super.setIcon(back);
    }

    public void showFront() {
        super.setIcon(front);
        revealed = true;
        revealCount++;
    }
    public void showFlag() {
        super.setIcon(flag);
    }
    public void showBack() {
        super.setIcon(back);
    }


    public int id() { return id; }
    public void setID(int i) { id = i; }
    public int getI() { return i; }
    public int getJ() { return j; }

}
