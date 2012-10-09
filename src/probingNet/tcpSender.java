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
	private static Socket pkgTrainSocket = null;
	private static PrintWriter out = null;
	private static BufferedReader in = null;
	
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
            pkgTrainSocket.setSendBufferSize(constant.pktSize/10);
            System.out.println("Current send buffer size is " + pkgTrainSocket.getSendBufferSize());
            
            // set TCP receiving buffer size
            pkgTrainSocket.setReceiveBufferSize(constant.pktSize/10);
            System.out.println("Current send buffer size is " + pkgTrainSocket.getReceiveBufferSize());*/
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + constant.hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: taranis.");
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
	    // BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	    	
	    // create payload for the packet train
    	StringBuilder payload = new StringBuilder();
    		
    	// Create a zero string
    	for (int i = 0; i < constant.pktSize; i++) {
    		payload.append('0');
    	}
    		
    	// assign special characters
    	payload.setCharAt(0, 's');
    	payload.setCharAt(payload.length()-1, 'e');
			
	    // create a counter for packet train
    	int counter = 1;
    	//long beforeTime = 0;
    	//long afterTime = 0;
    	double diffTime = 0;
    	
		while (counter <= constant.pktTrainLength) {
			// record the time before transmission
			/*
			if (beforeTime == 0) {
				beforeTime = System.nanoTime();
				beforeTime = System.currentTimeMillis();
			}*/
			
			// send packet with constant gap
			out.println(payload);
			out.flush();
			
			// create train gap in nanoseconds
			/*try {
				Thread.sleep(constant.pktGapMS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			LockSupport.parkNanos(constant.pktGapNS);
						
			// display the payload
			System.out.println("Pkt " + (counter++) + " send with size " + payload.length() + " Bytes.");
			
		}
		
		String lastMSG;
		
		// record transmission time after
		//afterTime = System.nanoTime();
		//afterTime = System.currentTimeMillis();
		diffTime = constant.pktTrainLength*constant.pktGapNS/java.lang.Math.pow(10.0, 6.0);
		// diffTime = constant.pktTrainLength*constant.pktGapMS;
		lastMSG = "END:" + diffTime;
		// send the last message
		out.println(lastMSG);
		out.flush();
		
		double test = Double.parseDouble(lastMSG.substring(4));
		
		System.out.println("Client side takes " + test + " ms.");
		
		String feedback;
		
		// print feedback from the server side
		try {
			while ((feedback = in.readLine()) != null) {
				System.out.println(feedback);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}