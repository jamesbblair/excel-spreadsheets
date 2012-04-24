/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package checkersagent;

import java.io.*;
import Checkers.*;
import Checkers.Checker.Color;
import Checkers.Move;

/**
 *
 * @author james
 */
public class Main {
    private static Color oppColor;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] argv){
        String _user = "";  // need legit id here
        String _password = "";  // need password here
        String _opponent = "";
	String readMessage;
	client myClient = new client();

	try{
            Console c = System.console();
            if (c == null) {
                System.err.println("No console.");
                System.exit(1);
            }
            _user = c.readLine("Enter user ID: ");
            _password = c.readLine("Enter user PASSWORD: ");
            _opponent = c.readLine("Enter opponent ID: ");

	    myClient.readAndEcho(); // start message
	    myClient.readAndEcho(); // ID query
	    myClient.writeMessageAndEcho(_user); // user ID

	    myClient.readAndEcho(); // password query
	    myClient.writeMessage(_password);  // password

	    myClient.readAndEcho(); // opponent query
	    myClient.writeMessageAndEcho(_opponent);  // opponent

	    myClient.setGameID(myClient.readAndEcho().substring(5)); // game
	    myClient.setColor(myClient.readAndEcho().substring(6,11));  // color
	    System.out.println("I am playing as "+myClient.getColor()+ " in game number "+ myClient.getGameID());

            Board board = new Board();

            //readMessage = myClient.readAndEcho();
	    // depends on color--a black move if i am white, Move:Black:i:j
	    // otherwise a query to move, ?Move(time):
            String strMove;
            Move myMove;
            Player me = null;

	    if (myClient.getColor().equals("White")) {
                me = new Player(Checker.Color.white);
                oppColor=Color.black;

                readMessage = myClient.readAndEcho();  // black move
                strMove = readMessage.substring(11);
                System.out.println(strMove);
                Move oppMove = new Move(strMove);
                board = Board.nextBoard(oppMove,board);
                readMessage = myClient.readAndEcho();
	    }
	    else {
                me = new Player(Checker.Color.black);
                oppColor=Color.white;

                readMessage = myClient.readAndEcho(); //the first request
            }

            while(!readMessage.contains("Result") || !readMessage.contains("Error") || !readMessage.contains("Draw")){
                    board = me.nextBoard(board);
                    myMove = board.getLastMove();
                    myClient.writeMessageAndEcho(myMove.outputForm());
                    myClient.readAndEcho();
                    readMessage = myClient.readAndEcho();
                    if(readMessage.contains("Result") || readMessage.contains("Error") || readMessage.contains("Draw")){
                        break;
                    }
                    strMove = readMessage.substring(11); // ...possibly +-1
                    Move oppMove = new Move(strMove);
                    board = Board.nextBoard(oppMove,board);
                    readMessage = myClient.readAndEcho();
            }

            if(readMessage.contains("Result") || readMessage.contains("Error") || readMessage.contains("Draw")){
                System.out.println(readMessage+" ...I'm done.");
                myClient.getSocket().close();
                System.exit(0);
            }

	    myClient.getSocket().close();
	} catch  (IOException e) {
	    System.out.println("Failed in read/close");
	    System.exit(1);
	}
        //testing unexpected exit
        System.out.println("Client left game unexpectedly");
        System.exit(1);
    }

}
