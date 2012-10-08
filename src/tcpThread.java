/*
 * @author Haokun Luo, 10/03/2012
 * 
 * Server side TCP thread
 * 
 */

// import java.lang.Long;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class tcpThread extends Thread {
	// constant variable
	public static final int portNumber = 10100;
	
	public void run() {
    	System.out.println("Start");
    	
    	// setup server side socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            
            // receive buffer size
            /*System.out.println("Before setting, receive buffer is " + serverSocket.getReceiveBufferSize());
            serverSocket.setReceiveBufferSize(10);
            System.out.println("After setting, receive buffer is " + serverSocket.getReceiveBufferSize());*/
            
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + portNumber + ".");
            System.exit(1);
        }
        
        System.out.println("Start listen on port: " + portNumber + ".");
        
        // create a listener for client
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        
        System.out.println("Find client and set up a socket.");
        
        try {
	        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(
					new InputStreamReader(
					clientSocket.getInputStream()));
	        
	        String inputLine = "";
	        int counter = 1;
	        
	        // timer to record packet arrived
	        // long startTime = System.currentTimeMillis();
	        long prvTime = 0;
	        long currentTime = 0;
	        double diffTime = 0.0;
	        
	        // output from what received
	        while ((inputLine = in.readLine()) != null) {
	        	// output the packet recived time to the client
	        	// currentTime = System.currentTimeMillis();
	        	if (prvTime == 0) {
	        		diffTime = prvTime;
	        		prvTime = System.nanoTime();
	        	}
	        	else {
	        		currentTime = System.nanoTime();
	        		diffTime = (currentTime - prvTime)/1000000.0;
	        		prvTime = currentTime;
	        	}
	        		        	
	        	System.out.println("Pkt " + (counter++) + " took " + diffTime + "ms.");
	        	// out.flush();
	        	System.out.println("Packet size is " + inputLine.length() + ".");
	        }
	        
	        out.close();
	        in.close();
	        clientSocket.close();
	        serverSocket.close();
	        
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}