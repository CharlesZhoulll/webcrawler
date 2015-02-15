# The first webcrawler
[Project website](http://david.choffnes.com/classes/cs4700sp15/project2.php)

Introduction
------------
Webcrawler is a simple web crawler. It achieve basic function of automatical login and content fetching. Webcrawler will first try to login into http://cs5700sp15.ccs.neu.edu/fakebook/ with user name and password provided. The program will be terminated if login fails. After login the crawler will start fetching every link it can find inside the website. A filter is use to stop crawler from fetching outside link. The mission of crawler is to collect 5 secret flags hiding in the network. The crawer will stop as soon as all the secret flags hanve all been found.

Install and Usage
-----------------
1. Run `make` to install the program

2. Run `./client`  to run the program,the argument list are as followings: 

* username (required): username to log into the fakebook

* password (required): password to log into the fakebook

Implementation
--------------
The logical of this program is very simple:

Read argument - Create socket - Start conversation

<strong>Read argument</strong>: Here we only parse the input argument and initialize host, port, etc.  A pitfall here is that -p and -s are optional parameters, and their positions can be switched, so the idea here is to search for string ‘-p’ or ‘-s’ in argument string list. Also we would check the valid of port if it is assigned. The valid number of port should be any number between 1024 and 65535.

<strong>Create socket</strong>: Based on -s argument, we create normal socket or SSL socket. Then we try to connect to the remote host according to address and port. If any of them is not correct, an exception will be occur and program will stopped. A challenge here is how to create SSL socket, which we will discuss later. Also we use timeout option, if the socket cannot be connect with the address in 10s then an timeout exception will be thrown.

<strong>Start conversation</strong>: Here we call the socketApp function to start conversation between client and server. The idea is to keep receiving message sent from server, if it is a STATUS message, then solve the problem and sent SOLUTION back. This loop will be ended until receive a BYE message of a null message. Notice that if a null message received then it means either we sent the wrong message or server returns back a wrong message, and the program will terminate in either case. Besides, each message STATUS message will be checked to make sure it follows the format restricted. 

Challenge
---------
1. Create SSL socket 

The way to create SSL socket is more complex than I had expected. First thing I do is to download certification of remoter server. Then I had to create a local keystore file and import the certificate into keystore. Finally in the program we using the following line to create SSL socket:

	System.setProperty("javax.net.ssl.trustStore", "./kclient.keystore");
	System.setProperty("javax.net.ssl.trustStorePassword", "********");
	SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	client = (SSLSocket) sslsocketfactory.createSocket();

2. Code reuse

I want to reuse code for SSL and normal socket because I found after creating the socket instance, the next operations are exactly the same. I tried many methods to solve this problem, like using generic function. But after posting question on PIZZA, I got a surprisingly easy solution: cast SSLSockete to Socket. The code was like:

	private static Socket client = null;
	if (!SSL)
	{
		client = new Socket();
	}
	else 	
	{
		client = (SSLSocket) sslsocketfactory.createSocket();
	}`

3.Check format of parameters

This code is not hard to write, but there are many details in it that we have to watch out. The first problem is which parameters to check? 

The parameters used in problem can be generalized as: 

Arguments: port, SSL, host address, ID

Message: HELLO, SOLUTION, STATUS, BYE

Then for each parameter, our strategy is:

<strong>port</strong>: We do check port to make sure that it is an number between 1024 and 65535

<strong>SSL</strong>: We do not check it (there is no input)

<strong>host</strong>: We do not check it. But we do throw exception “cannot connect to server” if host is not right

<strong>ID</strong>: We do not check it, as the result will show “Unknown husky ID” if it is not correct

<strong>HELLO</strong>: We do not check it, as this is constructed by client

<strong>STATUS</strong>: We check it, making sure that it start with “cs5700spring2015 STATUS”, end with “\n” and the mathematical expression are valid

<strong>BYE</strong>: We check it, making sure that it start with cs5700spring2015, end with “BYE”

Test
----
Many tests are did to make sure the program runs well. Here we only generalize some of them:

1. Input argument test
We use different input argument, change the order of arguments, and add more arguments to test if program crashes.

2. Sending message test
We send server with messages that make no sense, to test what will the server do and how will client react.

3. Receive message test
We overwrite the message receive from the server, to test what will client do when receive wrong message.

4. Socket test
We use different port, host address to test if the performance of socket. 







