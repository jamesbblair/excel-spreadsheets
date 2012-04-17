/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package checkersagent;

import java.io.*;
import Checkers.*;

/**
 *
 * @author james
 */
public class Main {

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

	    myClient.setGameID(myClient.readAndEcho().substring(5,9)); // game
	    myClient.setColor(myClient.readAndEcho().substring(6,11));  // color
	    System.out.println("I am playing as "+myClient.getColor()+ " in game number "+ myClient.getGameID());

            Board our = new Board();

            readMessage = myClient.readAndEcho();
	    // depends on color--a black move if i am white, Move:Black:i:j
	    // otherwise a query to move, ?Move(time):
	    if (myClient.getColor().equals("White")) {
                Player me = new Player(Checker.Color.white);

                readMessage = myClient.readAndEcho();  // black move
		while(!readMessage.substring(0,5).equals("Result")){
                    our = me.nextBoard(our);
		}
	    }
	    else {
                Player me = new Player(Checker.Color.black);

                readMessage = myClient.readAndEcho(); //the first request

                while(!readMessage.substring(0,5).equals("Result")){


		}
	    }

	    myClient.getSocket().close();
	} catch  (IOException e) {
	    System.out.println("Failed in read/close");
	    System.exit(1);
	}
    }

}
