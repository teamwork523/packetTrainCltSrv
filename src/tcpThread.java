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
	        // long startTime = System.endTimeMillis();
	        long startTime = 0;
	        long endTime = 0;
	        // the gap time
	        double gapTimeSrv = 0.0;
	        double gapTimeClt = 0.0;
	        double byteCounter = 0;
	        double estBandWidth = 0;
	        
	        // output from what received
	        while ((inputLine = in.readLine()) != null) {
	        	// check if the start time recorded
	        	if (startTime == 0) {
	        		//startTime = System.currentTimeMillis();
	        		startTime = System.nanoTime();
	        	}
	        	
	        	// calculate each gap time slot
	        	/*if (startTime == 0) {
	        		gapTimeSrv = startTime;
	        		// startTime = System.nanoTime();
	        		startTime = System.endTimeMillis();
	        	}
	        	else {
	        		// endTime = System.nanoTime();
	        		endTime = System.endTimeMillis();
	        		// gapTimeSrv = (endTime - startTime)/1000000.0;
	        		gapTimeSrv = endTime - startTime;
	        		startTime = endTime;
	        	}*/

	        	// out.flush();
	        	System.out.println("Pkt " + (counter++) + " is " + inputLine.length() + " Bytes.");
	        	byteCounter += inputLine.length();
	        	
	        	if (inputLine.length() < 50)
	        		System.out.println(inputLine);
	        	
	        	// check for last message
	        	if (inputLine.substring(0, 3).equals("END")) {
	        		System.out.println("Detect last message");
	        		gapTimeClt = Double.parseDouble(inputLine.substring(4));
	        		break;
	        	}
	        }
	        
	        //endTime = System.currentTimeMillis();
	        endTime = System.nanoTime();
	        gapTimeSrv = (endTime - startTime)/java.lang.Math.pow(10.0, 6.0);
	        // 1 Mbit/s = 125 Byte/ms 
	        estBandWidth = byteCounter/(gapTimeSrv-gapTimeClt)/125.0;
	        System.out.println("Client gap time is " + gapTimeClt + " ms.");
	        System.out.println("Total package received " + byteCounter + " Bytes with " + gapTimeSrv + " ms total GAP.");
	        System.out.println("Estimated bandwidth is " + estBandWidth + " Mbits/sec.");
	        
	        out.close();
	        in.close();
	        clientSocket.close();
	        serverSocket.close();
	        
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}