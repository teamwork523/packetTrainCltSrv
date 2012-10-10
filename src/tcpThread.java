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
import java.util.concurrent.locks.LockSupport;

public class tcpThread extends Thread {
	// server socket listener
    private ServerSocket serverSocket = null;
    // target client socket
    private Socket clientSocket = null;
    // socket stream
    private PrintWriter out = null;
    private BufferedReader in = null;
    // downlink bandwidth test variable
    private long myGapSize = 0;
	private int myPktSize = 0;
	private int myTrainLength = 0;
    
    // class constructor
    tcpThread (double gap, int pkt, int train) {
		if (gap != 0)
			// convert from ms to ns
			myGapSize = (long) (gap*java.lang.Math.pow(10.0, 6.0));
		else
			myGapSize = constantSrv.pktGapNS;
		if (pkt != 0)
			myPktSize = pkt;
		else
			myPktSize = constantSrv.pktSize;
		if (train != 0)
			myTrainLength = train;
		else
			myTrainLength = constantSrv.pktTrainLength;
	}
    
	// setup a server Socket
	public void createServerSocket() {
		try {
            serverSocket = new ServerSocket(constantSrv.portNumber);
            
            // receive buffer size
            /*System.out.println("Before setting, receive buffer is " + serverSocket.getReceiveBufferSize());
            serverSocket.setReceiveBufferSize(10);
            System.out.println("After setting, receive buffer is " + serverSocket.getReceiveBufferSize());*/
            
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + constantSrv.portNumber + ".");
            System.exit(1);
        }
        
        System.out.println("Start listen on port: " + constantSrv.portNumber + ".");
	}
	
	// close all the socket and stream
	public void closeServerSocket() {
		try {
	        serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// main thread function
	public void run() {  
		// continuously listening on the port
		while (true) {
	        try {
	            clientSocket = serverSocket.accept();
	            
	            // set no delay on the server side
	            clientSocket.setTcpNoDelay(true);
	            
	        } catch (IOException e) {
	            System.err.println("Accept failed.");
	            System.exit(1);
	        }
	        
	        System.out.println("*****************************************************************");
	        System.out.println("********* Find client and set up a socket. **********************");
	        System.out.println("*****************************************************************");
	        
	        try {
		        out = new PrintWriter(clientSocket.getOutputStream(), true);
		        in = new BufferedReader(
						new InputStreamReader(
						clientSocket.getInputStream()));
		        
		        System.out.println("*****************************************************************");
		        System.out.println("************************ Uplink BW Test *************************");
		        System.out.println("*****************************************************************");
		        
		        // uplink test
		        upLinkBandwidthtest();
		        
		        System.out.println("*****************************************************************");
		        System.out.println("********************** Downlink BW Test *************************");
		        System.out.println("*****************************************************************");
		        
		        // downlink test
		        downLinkBandwidthtest();
		        
		        // close the current client socket
		        clientSocket.close();
		        out.close();
		        in.close();
	        
	        } catch (IOException e) {
	        	e.printStackTrace();
	        	// close the server socket
	        	closeServerSocket();
	        }
		}
    }
	
	// client side upload link test
	private void upLinkBandwidthtest() throws IOException {
        String inputLine = "";
        int counter = 0;
        int singlePktSize = 0;
        
        // timer to record packet arrived
        // long startTime = System.endTimeMillis();
        long startTime = 0;
        long endTime = 0;
        // the gap time
        double gapTimeSrv = 0.0;
        double gapTimeClt = 0.0;
        double byteCounter = 0.0;
        double estTotalUpBandWidth = 0.0;
        double estAvailiableUpBandWidth = 0.0;
        double availableBWFraction = 1.0;
        
        // output from what received
        while ((inputLine = in.readLine()) != null) {
        	// check if the start time recorded for first received packet
        	if (startTime == 0) {
        		//startTime = System.currentTimeMillis();
        		startTime = System.nanoTime();
        		singlePktSize = inputLine.length();
        	}

        	// out.flush();
        	byteCounter += inputLine.length();
        	
        	// check for last message
        	if (inputLine.substring(0, 3).equals("END")) {
        		System.out.println("Detect last upload link message");
        		gapTimeClt = Double.parseDouble(inputLine.substring(4));
        		break;
        	}
        	
        	// increase the counter
        	counter++;
        }
        
        
        //endTime = System.currentTimeMillis();
        endTime = System.nanoTime();
        gapTimeSrv = (endTime - startTime)/java.lang.Math.pow(10.0, 6.0);
        
        // Bandwidth calculation
        // 1 Mbit/s = 125 Byte/ms 
        estTotalUpBandWidth = byteCounter/gapTimeSrv/125.0;
        availableBWFraction = gapTimeClt/gapTimeSrv;
        estAvailiableUpBandWidth = estTotalUpBandWidth * availableBWFraction;
        
        // Display information at the server side
        System.out.println("Receive single Pkt size is " + singlePktSize + " Bytes.");
        System.out.println("Total receiving " + counter + " packets.");
        System.out.println("Client gap time is " + gapTimeClt + " ms.");
        System.out.println("Total package received " + byteCounter + " Bytes with " + gapTimeSrv + " ms total GAP.");
        System.out.println("Estimated Total upload bandwidth is " + estTotalUpBandWidth + " Mbits/sec.");
        System.out.println("Availabe fraction is " + availableBWFraction);
        System.out.println("Estimated Available upload bandwidth is " + estAvailiableUpBandWidth + " Mbits/sec.");
	}
	
	// client side download link test
	private void downLinkBandwidthtest() throws IOException {
		// create payload for the packet train
    	StringBuilder payload = new StringBuilder();
    		
    	// Create a zero string
    	for (int i = 0; i < myPktSize; i++) {
    		payload.append('0');
    	}
    		
    	// assign special characters
    	payload.setCharAt(0, 's');
    	payload.setCharAt(payload.length()-1, 'e');
			
	    // create a counter for packet train
    	int counter = 0;
    	long beforeTime = 0;
    	long afterTime = 0;
    	double diffTime = 0;
    	
		while (counter < myTrainLength) {
			// start recording the first packet send time
			if (beforeTime == 0) {
				beforeTime = System.nanoTime();
				//beforeTime = System.currentTimeMillis();
			}
			
			// send packet with constant gap
			out.println(payload);
			out.flush();
			
			// create train gap in nanoseconds
			/*try {
				Thread.sleep(constant.pktGapMS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			LockSupport.parkNanos(myGapSize);		
			counter++;
		}
		
		// record finish transmission time
		afterTime = System.nanoTime();
		//afterTime = System.currentTimeMillis();
				
		System.out.println("Single Packet size is " + payload.length() + " Bytes.");
		System.out.println("Single GAP is " + myGapSize/java.lang.Math.pow(10.0, 6.0) + " ms.");
		System.out.println("Total number of packet is " + counter);
		
		String lastMSG;
		// Total GAP calculation
		diffTime = (afterTime - beforeTime)/java.lang.Math.pow(10.0, 6.0);
		// diffTime = myTrainLength*myGapSize/java.lang.Math.pow(10.0, 6.0);
		// diffTime = myTrainLength*constant.pktGapMS;
		lastMSG = "END:" + diffTime;
		// send the last message
		out.println(lastMSG);
		out.flush();
		
		double test = Double.parseDouble(lastMSG.substring(4));
		
		System.out.println("Server side takes " + test + " ms.");
	}
}









