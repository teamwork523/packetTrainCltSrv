/*
 * @author Haokun Luo, 09/27/2012
 * 
 * Define all the constant for the package train project
 * 
 */

package probingNet;

public class constant {
	// define the host name for the destination server
	public static final String hostName = "owl.eecs.umich.edu";
	
	// define the IP address for the destination server
	//public static final String ipAddress = "74.125.225.210"; 	// Google IP
	
	// define the port number that set up the connection (tcp)
	public static final int tcpPortNumber = 10100;
	
	// define the port number that set up the connection (udp)
	public static final int udpPortNumber = 10009;
	
	// timeout for TCP connections (in ms)
	public static final int tcpTimeOut = 20000;
	
	// the number of package on the train
	public static final int pktTrainLength = 100;
	
	// the size of each packet on the train (in byte)
	public static final int pktSize = 5000;
	
	// the gap of the size (in ms = 10^(-3)s)
	public static final long pktGapMS = 1;
	
	// the gap of the size (in ns = 10^(-9)s)
	public static final long pktGapNS = (long)(2*java.lang.Math.pow(10.0, 6.0));
}