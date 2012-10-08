/*
 * @author Haokun Luo, 10/03/2012
 * 
 * Server side TCP receiver
 * 
 */

public class pktTrainServer{
	
    public static void main(String[] args) {
    	// create a TCP thread
    	tcpThread srvThread = new tcpThread();
    	
    	// create a UDP thread
    	// udpThread srvThread = new udpThread();
    	
    	// run the thread
    	srvThread.start();
    }
}