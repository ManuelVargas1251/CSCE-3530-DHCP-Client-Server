# DHCP Client/Server

Built for CSCE 3530, Programming Assignment 3

Dynamic Host Configuration Protocol is a standardized network protocol controlled by a DHCP server that dynamically distributes network configuration parameters, such as IP addresses, for interfaces and services. A DHCP server enables computers to request IP addresses and networking parameters automatically, reducing the need for a network administrator or a user to configure these settings manually. In the absence of a DHCP server, each computer or other device on the network needs to be manually assigned to an IP address.

This program runs both a server and client. The server distributes an IP address to the client when the client initiates the DORA operations. The server takes the IP addresses from the included `IPaddress.txt` file and updates the file as the addresses are removed. [More Info](https://en.wikipedia.org/wiki/Dynamic_Host_Configuration_Protocol)


## Compilation

	javac UDPServer.java
	javac UDPClient.java

Note: UDP Server gives two negligible warnings
	
	
## Execution
	
	java UDPServer <port_number>
	java UDPClient <port_number>
	
Note: change `<port_number>` when a port error occurs