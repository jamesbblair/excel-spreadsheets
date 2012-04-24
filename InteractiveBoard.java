/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author iandardik
 */
public class InteractiveBoard extends JFrame {
    private final int MY_WIDTH = 800;
    private final int MY_HEIGHT = 900;
    private Board gameBoard = null;
    private Player opp = null;
    private BoardPanel bp;
    private boolean reset = false;
    private boolean canMove;
    private LinkedList<Integer> selection = new LinkedList<Integer>();
    private Checker.Color color;
    private JLabel turnLabel;
    
    
    private void newGame(Checker.Color c) {
        gameBoard = new Board();
        opp = new Player(opposite(c));
        bp.repaint();
        canMove = true;
        turnLabel.setText("black's turn");

        if (color.equals(Checker.Color.white)) {
            canMove = false;
//            System.out.println("your turn: "+canMove);
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    gameBoard = opp.nextBoard(gameBoard);
                    repaint();
        
                    canMove = true;//false;
                    turnLabel.setText("white's turn");
//                    System.out.println("your turn: "+canMove);
                }
            }).start();
        }

        selection = new LinkedList<Integer>();
        bp.repaint();
    }
    
    private void makeMove() {
        Move m = new Move(selection);
        gameBoard = Board.nextBoard(m, gameBoard);
        repaint();
        canMove = false;
        if (color.equals(Checker.Color.white))
            turnLabel.setText("black's turn");
        else
            turnLabel.setText("white's turn");
        
//        System.out.println("your turn: "+canMove);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoard = opp.nextBoard(gameBoard);
                repaint();
        
                canMove = true;
                if (color.equals(Checker.Color.black))
                    turnLabel.setText("black's turn");
                else
                    turnLabel.setText("white's turn");
//                System.out.println("your turn: "+canMove);
            }
        }).start();
        
        selection = new LinkedList<Integer>();
    }
    
    
    
    
    public InteractiveBoard() { initGUI(); }
    
    private void initGUI() {
        setTitle("Interactive Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setResizable(true);
        setVisible(true);
        
        setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        setLayout(new BorderLayout());
        
        add(new TopPanel(), BorderLayout.NORTH);
        add(bp = new BoardPanel(), BorderLayout.CENTER);
        add(new BottomPanel(), BorderLayout.SOUTH);
        
        pack();
    }
    
    
    private class TopPanel extends JPanel {
        public TopPanel() { initGUI(); }
        private void initGUI() {
            setPreferredSize(new Dimension(MY_WIDTH-5, 100));
//            setBackground(Color.black);
            setLayout(new GridLayout(1, 3));
            add(new JPanel());
            add(turnLabel = new JLabel());
            add(new RepaintButton());
        }
        
        private class RepaintButton extends JButton implements ActionListener{
            public RepaintButton() {
                addActionListener(this);
                setText("Repaint");
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                bp.repaint();
            }
        }
    }
    
    private class BottomPanel extends JPanel {
        private ColorPanel clr;
    
        public BottomPanel() { initGUI(); }
        private void initGUI() {
            setPreferredSize(new Dimension(MY_WIDTH-5, 100));
//            setBackground(Color.black);
            setLayout(new GridLayout(1, 3));
            add(new NewGameButton());
            add(clr = new ColorPanel());
            add(new MoveButton());
        }
        
        private class NewGameButton extends JButton implements ActionListener{
            public NewGameButton() {
                addActionListener(this);
                setText("New Game");
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame(color = clr.getColor());
            }
        }
        
        private class ColorPanel extends JPanel {
            private JRadioButton wButton;
            
            public ColorPanel() { initGUI(); }
            private void initGUI() {
                ButtonGroup group = new ButtonGroup();
                wButton = new JRadioButton("white");
                JRadioButton bButton = new JRadioButton("black");
                wButton.setSelected(true);
                group.add(wButton);
                group.add(bButton);
                add(wButton);
                add(bButton);
            }
            public Checker.Color getColor() {
                if (wButton.isSelected())
                    return Checker.Color.white;
                return Checker.Color.black;
            }
        }
        
        private class MoveButton extends JButton implements ActionListener{
            public MoveButton() {
                addActionListener(this);
                setText("Move");
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canMove) {
                    makeMove();
                }
            }
        }
    }
    
    private class BoardPanel extends JPanel implements MouseListener {
        public BoardPanel() { initGUI(); }
        private void initGUI() {
            setPreferredSize(new Dimension(MY_WIDTH-5, 600));
            setBackground(Color.white);
            addMouseListener(this);
            
            repaint();
        }
        
        public void mouseClicked(MouseEvent me) {
            int w = (MY_WIDTH - 5)/8;
            int h = 600/8;
            int x = (me.getX()-5) / w;
            int y = 7 - (me.getY()-5) / h;
            
            if (selection.contains(Move.map(y, x))) {
                reset = true;
                repaint();
                return;
            }
            
            if (!canMove || !isValidClick(x, y))
                return;
            
            selection.add(Move.map(y, x));
            repaint();
        }
        
        public boolean isValidClick(int x, int y) {
//            System.out.println("X: "+x+", Y: "+y);
            boolean rv = false;
            int loc = Move.map(y, x);
            if (gameBoard.isValidLocation(loc) && canSelect(loc))
                rv = true;
            
            if (rv)// && noJumps())
                canMove = true;
            
//            System.out.println("can move: "+canMove+", rv: "+rv);
            
            return rv;
        }
        
        private boolean canSelect(int loc) {
            if (selection.isEmpty())
                return gameBoard.isCheckerAt(loc) && !gameBoard.getAllMoves(gameBoard.checkerAt(loc), loc, false).isEmpty();
            
            int fr = selection.getFirst();
            LinkedList<Move> moves = gameBoard.getAllMoves(gameBoard.checkerAt(fr), fr, false);
            for (Move m : moves) {
                for (Jump j : m.jumps())
                    if (!selection.contains(j.to()))
                        if (loc == j.to())
                            return true;
                
                if (loc == m.to())
                    return true;
            }
            
            return false;
        }
        
        

        public void mousePressed(MouseEvent me) {}
        public void mouseReleased(MouseEvent me) {}
        public void mouseEntered(MouseEvent me) {}
        public void mouseExited(MouseEvent me) {}
        
        @Override
        public void paint(Graphics g) {
            for (int i = 1; i < 8; i++)
                g.drawLine(i * (MY_WIDTH - 5)/8, 5, i * (MY_WIDTH-5)/8, 590);
            for (int i = 1; i < 8; i++)
                g.drawLine(5, i * 600/8, MY_WIDTH-10, i * 600/8);
            
            if (gameBoard == null)
                return;
            
            for (int i = 1; i <= 35; i++)
                if (gameBoard.isCheckerAt(i)) {
                    int y = 7-Move.y(i);
                    int x = Move.x(i);
                    int w = (MY_WIDTH - 5)/8;
                    int h = 600/8;
                    
                    if (gameBoard.checkerAt(i).isColor(Checker.Color.black)) {
                        g.fillOval(x * w + 5, y * h + 5, w - 10, h - 10);
                        if (gameBoard.checkerAt(i).isKing())
                            g.clearRect(x * w + w/4, y * h + h/4, w - w/2, h - h/2);
                    } else {
                        g.drawOval(x * w + 5, y * h + 5, w - 10, h - 10);
                        if (gameBoard.checkerAt(i).isKing())
                            g.fillRect(x * w + w/4, y * h + h/4, w - w/2, h - h/2);
                    }
                    
                }
            
            for (Integer i : selection) {
                int y = 7-Move.y(i);
                int x = Move.x(i);
                int w = (MY_WIDTH - 5)/8;
                int h = 600/8;
                
                if (reset)
                    g.clearRect(x * w + 5, y * h + 5, x * w + 6, y * h + 6);//g.clearRect(x * w + 5, y * h + 5, x * w + w - 5, y * h + 5);
                else
                    g.drawLine(x * w + 5, y * h + 5, x * w + w - 5, y * h + 5);
            }
            
            if (reset) {
                selection = new LinkedList<Integer>();
                reset = false;
                repaint();
            }
        }
    }
    
    private Checker.Color opposite(Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return Checker.Color.white;
        return Checker.Color.black;
    }
    
    public static void main(String[] args) {
        new InteractiveBoard();
    }
}
