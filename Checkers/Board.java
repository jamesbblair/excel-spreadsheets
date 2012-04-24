/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;

import java.util.LinkedList;

/**
 *
 * @author iandardik
 */
public class Board {
    private Checker[] board;
    private Checker.Color turn;
    private Move lastMove;
    private Evaluation eval = null;
    
    public static Board nextBoard(Move move, Board b) {
        Board newBoard = new Board();
        newBoard.board = new Checker[36];
        for (int i = 1; i < 36; i++)
            if (b.isCheckerAt(i))
                newBoard.board[i] = Checker.copy(b.board[i]);
        newBoard.turn = b.turn;
        
        Checker c = newBoard.removeCheckerFrom(move.from());
        for (Jump j : move.jumps())
            newBoard.removeCheckerFrom(j.over());
        newBoard.placeCheckerAt(move.to(), c);
        newBoard.setLastMove(move);
        newBoard.switchTurn();
        if (move.to() >= 32 && c.color().equals(Checker.Color.black)) // king me
            c.setKing();
        else if (move.to() <= 4 && c.color().equals(Checker.Color.white))
            c.setKing();
        
        return newBoard;
    }
    
    public Board() {
        board = new Checker[36];
        turn = Checker.Color.black;
        lastMove = null;
        initCheckers();
    }
    
    public Checker.Color turn() {
        return turn;
    }
    
    public Evaluation getEval() {
        return eval;
    }
    
    public void setEval(Evaluation e) {
        if (eval != null)
            throw new RuntimeException("reevaluation");
        eval = e;
    }
    
    public double pieceRatio(Checker.Color c) {
        double us = 0;
        double them = 0;
        for (int i = 1; i <= 35; i++)
            if (isCheckerAt(i))
                if (checkerAt(i).isColor(c))
                    us++;
                else
                    them++;
        return us / them;
    }
    
    public int enemyTerritory(Checker.Color c) {
        int score = 0;
        for (int i = 1; i <= 35; i++)
            if (isCheckerAt(i) && checkerAt(i).isColor(c))
                if (c.equals(Checker.Color.black))
                    score += i / 9;
                else
                    score += (35-i) / 9;
        return score;
    }
    
    public int pawnAdvancement(Checker.Color c) {
        int score = 0;
        for (int i = 1; i <= 35; i++)
            if (isCheckerAt(i) && checkerAt(i).isColor(c))
                if (checkerAt(i).isKing()) // kings are our goal
                    score += 35;
                else
                    if (c.equals(Checker.Color.black))
                        score += i / 9;
                    else
                        score += (35-i) / 9;
        return score;
    }
    
    public double kingAttack(Checker.Color c) {
        double maxScore = 0;
        double ndScore = 0; // second to highest
        
        for (int i = 1; i <= 35; i++) // for each king
            if (isCheckerAt(i) && checkerAt(i).isColor(c) && checkerAt(i).isKing()) {
//                int moveCount = 0;
                
                for (int j = 1; j <= 35; j++) // how close is he to each opponenet? 
                    if (isCheckerAt(j) && checkerAt(j).isColor(Checker.Color.opposite(c))) {
                        double score = 35 - distance(i, j); // based on closseness
                        
                        if (score > maxScore)
                            maxScore = score;
                        else if (score > ndScore)
                            ndScore = score;
                    }
            }
        return maxScore + ndScore;
    }
    
    public int pullThrough(Checker.Color c) {
        for (int i = 1; i <= 35; i++)
            if (isCheckerAt(i) && checkerAt(i).isColor(c))
                if (pullThroughLeft(i, c) || pullThroughRight(i, c))
                    return 1;
        return 0;
    }
    
    public int noPieces() {
        return noPieces(Checker.Color.black) + noPieces(Checker.Color.white);
    }
    
    public int noPieces(Checker.Color color) {
        int count = 0;
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i) && checkerAt(i).color().equals(color))
                count++;
        return count;
    }
    
    public int noBackRow(Checker.Color color) {
        int count = 0;
        if (color.equals(Checker.Color.black)) {
            for (int i = 1; i <= 4; i++)
                if (isCheckerAt(i) && checkerAt(i).color().equals(color))
                    count++;
//            return 0; // disadvantage
        } else if (color.equals(Checker.Color.white)) {
            for (int i = 32; i <= 35; i++)
                if (isCheckerAt(i) && checkerAt(i).color().equals(color))
                    count++;
//            return 0; // disadvantage
        }
        return count;
    }
    
    public int pieceAdvantage(Checker.Color color) {
//        if (color.equals(Checker.Color.white))
//            return pieceAdvantage2(color);
        
        int sum = 0;
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i))
                if (checkerAt(i).color().equals(color))
                    sum += checkerAt(i).value();
                 else
                    sum -= checkerAt(i).value();
        return sum;
    }
    
    private int pieceAdvantage2(Checker.Color color) {
        int sum = 0;
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i))
                if (checkerAt(i).color().equals(color))
                    sum ++;
                 else
                    sum --;
        return sum;
    }
    
    public boolean noMoves(Checker.Color color) {
        Checker c;
        for (int i = 1; i < 36; i++)
            if (isCheckerAt(i) && (c = checkerAt(i)).color().equals(color))
                if (!getAllMoves(c, i, false).isEmpty())
                    return false;
        return true;
    }
    
    public int center2(Checker.Color color){
        int retv=0;
        for(int i=0;i<26;i++){
            if(i==13 || i==14 17 18 19)
                ;
        }
        return retv;
    }
    
    
    
    
    public LinkedList<Move> legalMoves(Checker.Color color) {
        LinkedList<Move> moves = new LinkedList<Move>();
        boolean jumps = false;
        
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i) && checkerAt(i).color().equals(color)) {
                LinkedList<Move> tempMoves = getAllMoves(checkerAt(i), i, jumps);
                if (!jumps && !tempMoves.isEmpty() && tempMoves.getFirst().isJump()) {
                    jumps = true;
                    moves = tempMoves;
                } else {
                    moves.addAll(tempMoves);
                }
            }
        return moves;
    }
    
    public boolean hasJump(int to, int over, Checker c) {
        return isValidLocation(to) && isEmpty(to) && isCheckerAt(over) && !checkerAt(over).color().equals(c.color());
    }
    
    public boolean isValidLocation(int loc) {
        if (loc % 9 == 0 || loc < 1 || loc > 35)
            return false;
        return true;
    }
    
    public boolean isEmpty(int loc) {
        return !isCheckerAt(loc);
    }
    
    public void setLastMove(Move m) {
        lastMove = m;
    }
    
    public Move getLastMove() {
        return lastMove;
    }
    
    public void switchTurn() {
        if (turn == Checker.Color.black || turn.equals(Checker.Color.black))
            turn = Checker.Color.white;
        else
            turn = Checker.Color.black;
    }
    
    public boolean isCheckerAt(int loc) {
        return board[loc] != null;
    }
    
    public Checker checkerAt(int loc) {
        return board[loc];
    }
    
    /**
     * generic example: don't always return ALL moves
     * 
     * @param c
     * @param from
     * @return 
     */
    private LinkedList<Move> getAttackMoves(Checker c, int from) {
        LinkedList<Move> moves = new LinkedList<Move>();
        int to;
        Checker.Color oppColor = Checker.Color.opposite(c.color());
        
        int uBound = 35; // we bound the opponenets pieces
        int lBound = 1;
        for (int i = 1; i < board.length; i++)
            if (isCheckerAt(i) && checkerAt(i).isColor(oppColor)) {
                if (i < uBound)  uBound = i;
                if (i > lBound)  lBound = i;
            }
        
        if (from < lBound) // attack down
            if (c.isColor(Checker.Color.black) || c.isColor(Checker.Color.white) && c.isKing()) { // only if the piece can move down
                if (isValidLocation(to = moveLeft(from, Checker.Color.black)) && isEmpty(to))
                    moves.add(new Move(from, to));
                if (isValidLocation(to = moveRight(from, Checker.Color.black)) && isEmpty(to))
                    moves.add((new Move(from, to)));
                moves.addAll(jumps(c, from, Checker.Color.black, this, null)); // we do forwards and backwards jumps cuz we have to..I think.  might not switch back to original color in the jump method...see that line
            }
        if (from > uBound) // attack up
            if (c.isColor(Checker.Color.white) || c.isColor(Checker.Color.black) && c.isKing()) { // only if the piece can move up
                if (isValidLocation(to = moveLeft(from, Checker.Color.white)) && isEmpty(to))
                    moves.add(new Move(from, to));
                if (isValidLocation(to = moveRight(from, Checker.Color.white)) && isEmpty(to))
                    moves.add((new Move(from, to)));
                moves.addAll(jumps(c, from, Checker.Color.white, this, null)); // we do forwards and backwards jumps cuz we have to..I think.  might not switch back to original color in the jump method...see that line
            }
        
        return moves;
    }
    
    public LinkedList<Move> getAllMoves(Checker c, int from, boolean jumps) {
        LinkedList<Move> moves = new LinkedList<Move>();
        moves.addAll(jumps(c, from, c.color(), this, null));
        if (jumps || !moves.isEmpty())
            return moves;
        
        // if there are no jumps
        int to;
        
        if (isValidLocation(to = moveLeft(from, c.color())) && isEmpty(to))
            moves.add(new Move(from, to));
        if (isValidLocation(to = moveRight(from, c.color())) && isEmpty(to))
            moves.add((new Move(from, to)));
        if (c.isKing()) {
            if (isValidLocation(to = moveLeft(from, Checker.Color.opposite(c.color()))) && isEmpty(to))
                moves.add(new Move(from, to));
            if (isValidLocation(to = moveRight(from, Checker.Color.opposite(c.color()))) && isEmpty(to))
                moves.add((new Move(from, to)));
        }
        
        return moves;
    }
    
    private LinkedList<Move> jumps(Checker c, int loc, Checker.Color color, Board b, Move midMove) {
        LinkedList<Move> moves = new LinkedList<Move>();
        int to;
        int over;
        
        if (b.hasJump(to = jumpLeft(loc, color), over = moveLeft(loc, color), c)) { // left moves
            Jump theJump = new Jump(loc, to, over);
            Move theNewMove = generateMove(midMove, theJump);
            
            LinkedList<Move> additionalJumps = jumps(c, to, c.color(), b.midMoveNextBoard(theJump), theNewMove);
            if (additionalJumps.isEmpty())
                moves.add(theNewMove);
            else
                moves.addAll(additionalJumps);
        }
        if (b.hasJump(to = jumpRight(loc, color), over = moveRight(loc, color), c)) { // right moves
            Jump theJump = new Jump(loc, to, over);
            Move theNewMove = generateMove(midMove, theJump);
            
            LinkedList<Move> additionalJumps = jumps(c, to, c.color(), b.midMoveNextBoard(theJump), theNewMove);
            if (additionalJumps.isEmpty())
                moves.add(theNewMove);
            else
                moves.addAll(additionalJumps);
        }
        
        if (c.isKing() && c.color().equals(color)) // kings backwards moves
            moves.addAll(jumps(c, loc, Checker.Color.opposite(color), b, midMove));
        
        return moves;
    }
    
    /**
     * operating on a board that is in the middle of a move, this adds another
     * jump
     * @param j
     * @return 
     */
    private Board midMoveNextBoard(Jump j) {
        Board newBoard = new Board();
        newBoard.board = new Checker[36];
        for (int i = 1; i < 36; i++)
            newBoard.board[i] = this.board[i];
        newBoard.turn = this.turn;
        
        Checker c = newBoard.removeCheckerFrom(j.from());
        newBoard.removeCheckerFrom(j.over());
        newBoard.placeCheckerAt(j.to(), c);
        
        return newBoard;
    }
    
    private boolean pullThroughLeft(int i, Checker.Color c) { // left diagnal
        boolean left = false;
        int leftSpot = moveLeft(i, Checker.Color.white);
        if (!isValidLocation(leftSpot) || isCheckerAt(leftSpot))
            left = true;
        
        boolean right = false;
        int rightSpot = moveRight(i, Checker.Color.black);
        if (!isValidLocation(rightSpot) || isCheckerAt(rightSpot))
            right = true;
        
        return left && right;
    }
    
    private boolean pullThroughRight(int i, Checker.Color c) { // right diagnal
        boolean left = false;
        int leftSpot = moveLeft(i, Checker.Color.black);
        if (!isValidLocation(leftSpot) || isCheckerAt(leftSpot))
            left = true;
        
        boolean right = false;
        int rightSpot = moveRight(i, Checker.Color.white);
        if (!isValidLocation(rightSpot) || isCheckerAt(rightSpot))
            right = true;
        
        return left && right;
    }
    
    private double distance(int i, int j) {
        int y1 = Move.y(i);
        int x1 = Move.x(i);
        int y2 = Move.y(j);
        int x2 = Move.x(j);
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    private void initCheckers() {
        for (int i = 1; i <= 13; i++)
            if (i % 9 != 0)
                board[i] = new Checker(Checker.Color.black);
        for (int i = 23; i <= 35; i++)
            if (i % 9 != 0)
                board[i] = new Checker(Checker.Color.white);
    }
    
    private Checker removeCheckerFrom(int loc) {
        Checker c = board[loc];
        board[loc] = null;
        return c;
    }
    
    private void placeCheckerAt(int loc, Checker c) {
        board[loc] = c;
    }
    
    private Move generateMove(Move midMove, Jump j) {
        Move nextMove;
        if (midMove == null)
            nextMove = new Move(j);
        else
            nextMove = Move.addJumpTo(midMove, j); // make sure this is a copy
        
        return nextMove;
    }
    
    private int moveLeft(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 4;
        return loc - 5;
    }
    
    private int moveRight(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 5;
        return loc - 4;
    }
    
    private int jumpLeft(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 8;
        return loc - 10;
    }
    
    private int jumpRight(int loc, Checker.Color c) {
        if (c.equals(Checker.Color.black))
            return loc + 10;
        return loc - 8;
    }
    
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        boolean shift = true;
//        int count = 0;
//        
//        for (int i = 1; i < board.length; i++)
//            if (i % 9 != 0) {
//                if (count % 4 == 0) {
//                    sb.append("\n");
//                    if (shift) {
//                        sb.append("   ");
//                        shift = false;
//                    } else
//                        shift = true;
//                }
//                count++;
//                if (isCheckerAt(i))
//                    sb.append("   ").append(checkerAt(i).toString()).append("   ");
//                else
//                    sb.append("   X   ");
//            }
//        sb.append("\n");
//        
//        return sb.toString();
//    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(turn()).append("'s turn\n");
        
        for (int i = 1; i < 36; i++)
            if (isEmpty(i))
                board[i] = new Checker(Checker.Color.test);
        
        sb.append("  | ").append(board[1]).append(" |   | ").append(board[2]).append(" |   | ").append(board[3]).append(" |   | ").append(board[4]).append("\n")
                .append("------------------------------\n")
                .append(board[5]).append(" |   | ").append(board[6]).append(" |   | ").append(board[7]).append(" |   | ").append(board[8]).append(" |\n")
                .append("------------------------------\n")
                .append("  | ").append(board[10]).append(" |   | ").append(board[11]).append(" |   | ").append(board[12]).append(" |   | ").append(board[13]).append("\n")
                .append("------------------------------\n")
                .append(board[14]).append(" |   | ").append(board[15]).append(" |   | ").append(board[16]).append(" |   | ").append(board[17]).append(" |\n")
                .append("------------------------------\n")
                .append("  | ").append(board[19]).append(" |   | ").append(board[20]).append(" |   | ").append(board[21]).append(" |   | ").append(board[22]).append("\n")
                .append("------------------------------\n")
                .append(board[23]).append(" |   | ").append(board[24]).append(" |   | ").append(board[25]).append(" |   | ").append(board[26]).append(" |\n")
                .append("------------------------------\n")
                .append("  | ").append(board[28]).append(" |   | ").append(board[29]).append(" |   | ").append(board[30]).append(" |   | ").append(board[31]).append("\n")
                .append("------------------------------\n")
                .append(board[32]).append(" |   | ").append(board[33]).append(" |   | ").append(board[34]).append(" |   | ").append(board[35]).append(" |\n");
        
        for (int i = 1; i < 36; i++)
            if (isCheckerAt(i) && checkerAt(i).color().equals(Checker.Color.test))
                board[i] = null;
        
        return sb.toString();
    }
}
