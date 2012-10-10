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
    	double gap_size = 0.0;
    	int pkt_size = 0;
    	int train_length = 0;
    	
    	// input handling
    	// > java probingNet gap_size pkt_size train_length
    	if (args.length > 0 && args.length < 4) {
    		try {
	    		if (args.length >= 1) {
	    			gap_size = Double.parseDouble(args[0]);
	    		}
	    		if (args.length >= 2) {
	    			pkt_size = Integer.parseInt(args[1]);
	    		}
	    		if (args.length >= 3) {
	    			train_length = Integer.parseInt(args[2]);
	    		}
    		} catch (NumberFormatException e) {
    			e.getStackTrace();
    		}
    	}
  
    	// new TCP sender
    	tcpSender newTCPSender = new tcpSender(gap_size, pkt_size, train_length);
    	
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