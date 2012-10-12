/*
 * @author Haokun Luo, 10/09/2012
 * 
 * Define all the constant for server side package train
 * 
 */

public class constantSrv {
	// port number
	public static final int portNumber = 10100;
	
	// the number of package on the train
	public static final int pktTrainLength = 500;
	
	// the size of each packet on the train (in Byte)
	public static final int pktSize = 50*1024;
	
	// the gap of the size (in ns = 10^(-9)s)
	public static final double msParameter = 0.5;
	public static final long pktGapNS = (long)(msParameter*java.lang.Math.pow(10.0, 6.0));
	
	// special tag for message
	public static final String finalMSG = "END";
	public static final String ackMSG = "ACK";
	public static final String resultMSG = "RT";
}
