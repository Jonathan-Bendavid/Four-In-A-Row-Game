import javax.swing.*;
import java.awt.*;
@SuppressWarnings("serial")
public class MyPanel4 extends JPanel {

    private char[][] board;
    
    MyPanel4(char[][] board) {
        this.board = board;
        this.setPreferredSize(new Dimension(1150, 1000));
        setDoubleBuffered(true);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        // Draw the board
        g2D.setPaint(Color.blue);
        g2D.setStroke(new BasicStroke(5));

        // Vertical lines
        for (int i = 0; i <= 7; i++) {
            int x = 50 + i * 150;
            g2D.drawLine(x, 50, x, 950);
        }

        // Horizontal lines
        for (int i = 0; i <= 6; i++) {
            int y = 50 + i * 150;
            g2D.drawLine(50, y, 1100, y);
        }

        // Draw X and O shapes
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                int x = 50 + j * 150;
                int y = 50 + i * 150;
                if (board[i][j] == 'X') {
                    drawO(g2D, x, y,true);
                } else if (board[i][j] == 'O') {
                    drawO(g2D, x, y,false);
                }
            }
        }
    }


    private void drawO(Graphics2D g2D, int x, int y,boolean ai) {
    	g2D.setColor(Color.black);
    	g2D.setStroke(new BasicStroke(10));
    	if(ai) g2D.setColor(Color.red);
    	else g2D.setColor(Color.yellow);
        g2D.drawOval(x + 20, y + 20, 110, 110);
        g2D.fillOval(x + 20, y + 20, 110, 110);
    }



    public void setBoard(char[][] board) {
        this.board = board;
        repaint();
    }
}

