/*
 * @author Haokun Luo, 09/27/2012
 * 
 * This is a prototype of examining package train on Mobile
 * 
 */

package probingNet;

class packageTrain {
		
	// main thread
    public static void main(String[] args) {
  
    	// new TCP sender
    	tcpSender newTCPSender = new tcpSender();
    	
        // set up the socket
    	newTCPSender.openSocket();
    	
    	// wrap a package and send it to the server
    	newTCPSender.runSocket();
    	
    	// close the socket
    	newTCPSender.closeSocket();
    	
    	/*
    	// create a UDP sender
    	udpSender newUDPSender = new udpSender();
    	
    	// send a UDP package
    	newUDPSender.sendUDPPacket();*/
    	
    	// UDP multicast
    	// newUDPSender.sendUDPMulticast();
    }
}