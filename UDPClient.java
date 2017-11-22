/* 
Compilation: javac UDPClient.java
Execution  : java Client <port_number> [eg. port_number = 5000, where port_number is the UDP server port number]
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.StringBuilder;

class UDPClient{

	public static void main(String args[]) throws Exception{

		System.out.println("\n--UDP Client\n");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int sportno = Integer.parseInt(args[0]); /* UDP server port number */
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("cse01.cse.unt.edu"); /* UDP server's IP address */
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String lifetime = "3600";

		// <--
		//DHCP Discover - Sending initial contact to server
		// <--
		String DHCP_Discover = "0.0.0.0,100"; //send default request
		sendData = DHCP_Discover.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, sportno);
		clientSocket.send(sendPacket);
		System.out.println("--[DHCP Discover] Sent to Server\n");

		// -->
		//Receiving DHCP Offer from UDP Server
		// -->
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		System.out.println("--[DHCP Offer] Received from Server");
		String serverMsg = new String(receivePacket.getData());

		//parse server message
		List<String> DHCP_Offer = Arrays.asList(serverMsg.split(","));


		//System.out.println("DHP Offer Received:");
		System.out.println("\tyiaddr: " + DHCP_Offer.get(0));
		System.out.println("\tT_ID: " + DHCP_Offer.get(1));
		System.out.println("\tLifetime: " + lifetime +"\n");
		//System.out.println("\t2:" + DHCP_Offer.get(2));

		//transaction ID string turned to double
		double trans_id = Double.parseDouble(DHCP_Offer.get(1));
		
		String new_trans_id = Double.toString(trans_id);
		

		//transactionID =+ 1;
		String final_DHCP_Request = "";

		//if the received IP isn't the default
		if(!DHCP_Offer.get(0).equals("0.0.0.0")){


			//build a string out of the three pieces of information
			StringBuilder DHCP_Request = new StringBuilder();

			DHCP_Request.append(DHCP_Offer.get(0) + ",");	//yiaddr
			DHCP_Request.append(new_trans_id + ",");    	//transaction id
			DHCP_Request.append(lifetime);					//lifetime

			//convert string builder to string
			final_DHCP_Request = DHCP_Request.toString();

			//print final message
			//System.out.print("--Final Server Message:\n" + final_DHCP_Request);

		}

		// <--
		//Sending DHCP Request to UDP Server
		// <--
		System.out.println("--[DHCP Request] Sent with " + DHCP_Offer.get(0)+"\n");
		sendData = final_DHCP_Request.getBytes();
		DatagramPacket sendRequestPacket = new DatagramPacket(sendData, sendData.length, IPAddress, sportno);
		clientSocket.send(sendRequestPacket);


		// -->
		//Receive DHCP ACK FROM UDP Server
		// -->
		DatagramPacket receiveACKPacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receiveACKPacket);
		
		System.out.println("--[DHCP ACK] Received from Server");
		String DHCPACK = new String(receivePacket.getData());

		//parse server message
		List<String> DHCP_ACK = Arrays.asList(DHCPACK.split(","));

		//print attributes
		System.out.println("\tyiaddr: " + DHCP_ACK.get(0));
		System.out.println("\tT_ID: " + DHCP_ACK.get(1));
		System.out.println("\tLifetime: " + lifetime + "\n");

		clientSocket.close();	//client leaves the server
	}
}