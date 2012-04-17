/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package checkersagent;

import java.io.*;
import java.net.*;

/**
 *
 * @author james
 */
public class client {
    private final String _machine  = "icarus2.engr.uconn.edu"; /*change here*/
    private int _port = 3499;
    private Socket _socket = null;
    private PrintWriter _out = null;
    private BufferedReader _in = null;

    private String _gameID;
    private String _myColor;

    public client(){
	_socket = openSocket();
    }

    public Socket getSocket(){
	return _socket;
    }

    public PrintWriter getOut(){
	return _out;
    }

    public BufferedReader getIn(){
	return _in;
    }

    public void setGameID(String id){
	_gameID = id;
    }

    public String getGameID() {
	return _gameID;
    }

    public void setColor(String color){
	_myColor = color;
    }

    public String getColor() {
	return _myColor;
    }

    public String readAndEcho() throws IOException
    {
	String readMessage = _in.readLine();
	System.out.println("read: "+readMessage);
	return readMessage;
    }

    public void writeMessage(String message) throws IOException
    {
	_out.print(message+"\r\n");
	_out.flush();
    }

    public void writeMessageAndEcho(String message) throws IOException
    {
	_out.print(message+"\r\n");
	_out.flush();
	System.out.println("sent: "+ message);
    }

    public  Socket openSocket(){
	//Create socket connection, adapted from Sun example
	try{
            _socket = new Socket(_machine, _port);
            _out = new PrintWriter(_socket.getOutputStream(), true);
            _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + _machine);
            System.exit(1);
        } catch  (IOException e) {
            System.out.println("No I/O");
            System.exit(1);
        }
        return _socket;
    }
}

