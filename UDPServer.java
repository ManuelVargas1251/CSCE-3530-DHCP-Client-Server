/*
Compilation: javac UDPServer.java
Execution  : java UDPServer [5000+]
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.StringBuilder;

class UDPServer{

    public static void main(String args[]) throws Exception{

        int sportno = Integer.parseInt(args[0]); /*UDP server port number */
        DatagramSocket serverSocket = new DatagramSocket(sportno);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        System.out.print("\n--UDP Server on port: " + sportno);
        String lifetime = "3600";

        //keep server running
        //to break out, CTRL+Z
        while(true){

            /* Waiting for client's message */
            System.out.println("\n******** Waiting For Next Client ********\n");

            // <--
            // DHCP Discover Received from Client
            // <--
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            System.out.println("--[DCHP Discover] Received from Client");
            String clientMsg= new String(receivePacket.getData());

            //Print DHCP Discover 
            List<String> Client_Msg = Arrays.asList(clientMsg.split(","));
            System.out.println("\tyiaddr: " + Client_Msg.get(0));
            System.out.println("\tT_ID: " + Client_Msg.get(1) + "\n");

            // --
            // Prepping DHCP Offer
            // --

            //transaction ID string turned to double
            double trans_id = Double.parseDouble(Client_Msg.get(1));

            //stores final message created below
            String final_DHCP_Offer = "";
            String currentIP = "";

            //if correct client init is received, grab IP address to send back
            if(Client_Msg.get(0).equals("0.0.0.0")){
                System.out.println("--Popping IP from IP file");

                //////*************************************************
                //Using custom method "popOneIP", defined below main
                ////pop ip address from file and saves it to variable currentIP
                //////*************************************************
                currentIP = popOneIP("IPaddress.txt");  //passing filename

                //build a string out of the three pieces of information
                StringBuilder DHCP_Offer = new StringBuilder();

                DHCP_Offer.append(currentIP + ",");			//yiaddr
                DHCP_Offer.append(Client_Msg.get(1) + ",");	//transaction id
                DHCP_Offer.append(lifetime);                  //lifetime

                //convert string builder to string
                final_DHCP_Offer = DHCP_Offer.toString();

                //print final message
                //System.out.println("\n--Final DHCP Offer:\n" + final_DHCP_Offer );


                /* Getting the IP address and port number of client */
                InetAddress IPAddress = receivePacket.getAddress(); /*UDP client IP address */
                int cportno = receivePacket.getPort(); /*UDP client port number */

                // -->
                //Sending DHCP Offer
                // -->
                System.out.println("--[DHCP Offer] Sent with " + currentIP + "\n");
                //String serverMsg = final_DHCP_Offer;
                //sendData = serverMsg.getBytes();
                sendData = final_DHCP_Offer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, cportno);
                serverSocket.send(sendPacket);
            }


            // <--
            //Receiving DHCP Request
            // <--
            DatagramPacket receiveRequestPacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receiveRequestPacket);
            System.out.println("--[DCHP Request] Received from Client");
            String DHCPRequest= new String(receivePacket.getData());

            //split string into array
            List<String> DHCP_Request = Arrays.asList(DHCPRequest.split(","));

            //transaction ID string turned to double
            double trans_request_id = Double.parseDouble(DHCP_Request.get(1));
            trans_request_id = trans_request_id + 1;
            String new_trans_request_id = Double.toString(trans_request_id);

            //print 
            System.out.println("\tyiaddr: " + DHCP_Request.get(0));
            System.out.println("\tT_ID: " + new_trans_request_id);
            System.out.println("\tLifetime: " + lifetime + "\n");


            //Prepping DHCP ACK
            String final_DHCP_ACK = "";

            //if the received IP isn't the default
            if(!DHCP_Request.get(0).equals("0.0.0.0")){


                //build a string out of the three pieces of information
                StringBuilder DHCP_ACK = new StringBuilder();

                DHCP_ACK.append(DHCP_Request.get(0) + ",");		//yiaddr
                DHCP_ACK.append(new_trans_request_id + ",");    //transaction id
                DHCP_ACK.append(lifetime);         				//lifetime

                //convert string builder to string
                final_DHCP_ACK = DHCP_ACK.toString();

                //print final version of ACK message 
                //System.out.print("--Final Server Message:\n" + final_DHCP_ACK);

                /* Getting the IP address and port number of client */
                InetAddress IPAddress = receivePacket.getAddress(); /*UDP client IP address */
                int cportno = receivePacket.getPort(); /*UDP client port number */


                // -->
                //Sending DHCP ACK
                // -->
                System.out.print("--[DHCP ACK] Sent to Client\n");
                sendData = final_DHCP_ACK.getBytes();
                DatagramPacket sendACKPacket = new DatagramPacket(sendData, sendData.length, IPAddress, cportno);
                serverSocket.send(sendACKPacket);

            }


            //loop back waiting for more clients
        }
    }


    //Loading the ips from the text file,
    //returning one IP and removing it from the file.
    public static String popOneIP(String fileName){

        // This will reference one line at a time
        String line = null;

        Vector<String> IPs = new Vector();
        String IP = "";

        try {
            // Prepare BufferedReader.
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                IPs.add(line);
            } 

            //after insertion, print
            //System.out.println(IPs.toString());
            //System.out.println("Count: " + IPs.size());

            //save the last element to var to be returned
            IP = IPs.lastElement().toString();

            //remove last element just used
            IPs.removeElement(IPs.lastElement());
            //IPs.trimToSize();   //trim capacity

            //print to check
            //System.out.println(IPs.toString());
            System.out.println("IP Count: " + IPs.size() + " -> " + (IPs.size()-1) + " left\n");

            //overwrite file with updated list
            File newIPs = new File(fileName);
            FileWriter IPWriter = new FileWriter(newIPs, false);// false to overwrite.

            //loop through vector to write all IPS
            for(int i = 0; i < IPs.size(); i++){
                IPWriter.write(IPs.get(i) + "\n");
            }
            IPWriter.close();

            // close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }

        return IP;
    }
}

