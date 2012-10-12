/*
 * @author Haokun Luo, 09/27/2012
 * 
 * Open TCP package for transfer
 * 
 */

package probingNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
// for accuracy of nanoseconds
import java.util.concurrent.locks.LockSupport;

public class tcpSender {
	// define all the variables
	private Socket pkgTrainSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private long myGapSize = 0;
	private int myPktSize = 0;
	private int myTrainLength = 0;
	
	// class constructor
	tcpSender(double gap, int pkt, int train) {
		if (gap != 0)
			// convert from ms to ns
			myGapSize = (long) (gap*java.lang.Math.pow(10.0, 6.0));
		else
			myGapSize = constant.pktGapNS;
		if (pkt != 0)
			myPktSize = pkt;
		else
			myPktSize = constant.pktSize;
		if (train != 0)
			myTrainLength = train;
		else
			myTrainLength = constant.pktTrainLength;
	}
	
	// set up all the package parameters
    public void openSocket() {
    	try {
    		pkgTrainSocket = new Socket(constant.hostName, constant.tcpPortNumber);
            out = new PrintWriter(pkgTrainSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
            		pkgTrainSocket.getInputStream()));
            
            // set the time out
            pkgTrainSocket.setSoTimeout(constant.tcpTimeOut);

            System.out.println("Current receive buffer size is " + pkgTrainSocket.getReceiveBufferSize());
            
            // set TCP no delay for packet transfer
            pkgTrainSocket.setTcpNoDelay(true);
            System.out.println("Current nodelay is " + pkgTrainSocket.getTcpNoDelay());
            
            /*
            // set TCP sending buffer size
            pkgTrainSocket.setSendBufferSize(myPktSize/10);
            System.out.println("Current send buffer size is " + pkgTrainSocket.getSendBufferSize());
            
            // set TCP receiving buffer size
            pkgTrainSocket.setReceiveBufferSize(myPktSize/10);
            System.out.println("Current send buffer size is " + pkgTrainSocket.getReceiveBufferSize());*/
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + constant.hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: " + constant.hostName );
            System.exit(1);
        }
    	
    	System.out.println("Open Socket for package train.");
    }
    
    // close all the socket and IO streams
    public void closeSocket() {
    	try {
	    	in.close();
	    	out.close();
	    	pkgTrainSocket.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("Close Socket for package train.");
    }
    
    // send TCP packet train
    public void runSocket() {
    	try {
    		 System.out.println("*****************************************************************");
		     System.out.println("************************ Uplink BW Test *************************");
		     System.out.println("*****************************************************************");
		     
	    	// upload link bandwidth test
	    	runUpLinkTask();
	    	
	    	 System.out.println("*****************************************************************");
		     System.out.println("********************** Downlink BW Test *************************");
		     System.out.println("*****************************************************************");
	    	
	    	// download link bandwidth test
	    	runDownLinkTask();
    	} catch (NumberFormatException n) {
    		n.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    // upload link test
    private void runUpLinkTask() throws IOException {
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
		lastMSG = constant.finalMSG + ':' + diffTime;
		// send the last message
		out.println(lastMSG);
		out.flush();
		
		double test = Double.parseDouble(lastMSG.substring(constant.resultMSG.length()+1));
		
		System.out.println("Client side takes " + test + " ms.");
		
		// waiting for the upload link result
		String uplinkBWResult;
		while ((uplinkBWResult = in.readLine()) != null) {
			if (uplinkBWResult.substring(0, constant.resultMSG.length()).equals(constant.resultMSG)) {
				// extra colon added
				System.out.println("Uplink Bandwidth result is " + uplinkBWResult.substring(constant.resultMSG.length()+1));
				// send back the ACK result
				out.println(constant.ackMSG);
				out.flush();
				break;
			}
		}
    }
    
    // download link test
    private void runDownLinkTask() throws NumberFormatException, IOException {
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
        double estTotalDownBandWidth = 0.0;
        double estAvailiableDownBandWidth = 0.0;
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
        	if (inputLine.substring(0, constant.finalMSG.length()).equals(constant.finalMSG)) {
        		System.out.println("Detect last download link message");
        		gapTimeSrv = Double.parseDouble(inputLine.substring(constant.finalMSG.length()+1));
        		break;
        	}
        	
        	// increase the counter
        	counter++;
        }
        
        
        //endTime = System.currentTimeMillis();
        endTime = System.nanoTime();
        gapTimeClt = (endTime - startTime)/java.lang.Math.pow(10.0, 6.0);
        
        // Bandwidth calculation
        // 1 Mbit/s = 125 Byte/ms 
        estTotalDownBandWidth = byteCounter/gapTimeClt/125.0;
        availableBWFraction = gapTimeSrv/gapTimeClt;
        estAvailiableDownBandWidth = estTotalDownBandWidth * availableBWFraction;
        
        // Display information at the server side
        System.out.println("Receive single Pkt size is " + singlePktSize + " Bytes.");
        System.out.println("Total receiving " + counter + " packets.");
        System.out.println("Server gap time is " + gapTimeSrv + " ms.");
        System.out.println("Total package received " + byteCounter + " Bytes with " + gapTimeClt + " ms total GAP.");
        System.out.println("Estimated Total download bandwidth is " + estTotalDownBandWidth + " Mbits/sec.");
        System.out.println("Availabe fraction is " + availableBWFraction);
        System.out.println("Estimated Available download bandwidth is " + estAvailiableDownBandWidth + " Mbits/sec.");
    }
}