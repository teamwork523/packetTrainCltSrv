/*
 * @author Haokun Luo, 09/27/2012
 * 
 * UDP transmission on the client side
 *  
 */

package probingNet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
//import java.net.MulticastSocket;

public class udpSender {
	// Unicast mechanism
	public void sendUDPPacket() {
		try {
			InetAddress uni = InetAddress.getByName(constant.hostName);
			int num = constant.udpPortNumber;
			
			// define UDP payload
			String s = "My first UDP Packet";
			byte[] b = s.getBytes();
			
			// setup UDP socket
			DatagramPacket  dp = new DatagramPacket(b, b.length, uni, num);
			DatagramSocket  sender = new DatagramSocket();
			sender.send(dp);
			
		} catch (UnknownHostException u) {
			System.err.println("Don't know about host: " + constant.hostName);
            System.exit(1);
            
		} catch (SocketException s) {
			s.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Open socket for UDP data transmission");
	}
	
	// Multicast mechanism (able to set TTL) not working for owl
	/*
	public void sendUDPMulticast() {
		try {
			 // join a Multicast group and send the group salutations
			 String msg = "Hello";
			 InetAddress group = InetAddress.getByName(constant.hostName);
			 MulticastSocket s = new MulticastSocket(constant.udpPortNumber);
			 
			 // output time to live
			 System.out.println("TTL value is " + s.getTimeToLive());
			 
			 s.joinGroup(group);
			 DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
			                             group, constant.udpPortNumber);
			 s.send(hi);
			 // get their responses!
			 byte[] buf = new byte[1000];
			 DatagramPacket recv = new DatagramPacket(buf, buf.length);
			 s.receive(recv);
			 // OK, I'm done talking - leave the group...
			 s.leaveGroup(group);
		} catch (UnknownHostException s) {
			s.printStackTrace();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Open socket for UDP multicast data transmission");
	}*/
}

