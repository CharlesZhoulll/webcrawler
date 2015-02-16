# The first webcrawler
[Project website](http://david.choffnes.com/classes/cs4700sp15/project2.php)

[Java Doc](http://charleszhoulll.github.io/webcrawler/)

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
There are six classes of this program. The reson I did this is to seperate the function of webcrawler to different classes, so that it would be much easier for future improvement. The six classes are:

<strong>WebCrawlerProj2:</strong> The start of the program. Read user name and password, initiate crawer and run crawler. After the program ends it will print all the secret flags found.

<strong>Crawler:</strong> Realize the basic function of cralwer. First it will try to automatically login into the start website using username and password provided. If login fails the program will terminate. If login successful it will start fetching content from the link in the website with host cs5700.ccs.neu.edu and search for secret flags. 

<strong>HTTPconnection:</strong> This class implements the function of socket. The main function of this class is processURL(). To use this function, user (crawler) must first setup the request command, request header and message body (if request is POST). To make the header setup more easily a function named setDefaultHeader is provided. It will set up values of some common headers. 

<strong>Page:</strong> Parse the content fetched from the website. The content consists of header and html body. The main function of this class is to separate header and body. Also to make crawler works easier it provided a lot of functions like getBody(), getCookies(), getCsrfCode(), etc. 

<strong>Url:</strong> Similar to Page, this function mainly parse the website address. The website address consists of different component, like host, string query and path. The class is responsible for extract these different components from website and store them in proper structure. 

<strong>Cookie:</strong> This class handle cookies. It is function is simple: get the name and value of cookie, and update cookie’s value when necessary. 

Challenge
---------
1. I always got request timeout. Then I realize that a blank line must be sent after sending request header, otherwise the server will think client has more commands to be sent and wait until timeout.

2. I try to compare HTTP1.1 and HTTP1.0. However bad request error will occur when I change HTTP1.0 to HTTP1.1. Then I realize that to use HTTP1.1, host name must be included, otherwise the bad request error will happen.

3. I found that if I use the HTTP1.1, the time it takes to fetching content is unbearable slow. Then I realize that it is because the connection status should be set to “Close” instead of “Keep Alive”, otherwise the connection will not be terminated after client has received all message from server. 

4. CSRF verification. This is the hardest part of this program. I got million times 403 forbidden error because I cannot pass the CSRF verification. Then I found that several things must be done to pass it. First the csrfmiddlewaretoken field must be send along with POST request. Second the CSRF token and sessionid must be sent in the cookie to server. Finally the header field must contains a content-length field.
5. Cookie management. I found that even if I login into the webiste, the host ask me to login in to again when fetching other pages. Then I realize that it is because I did not send session id to the server in cookie, that make server think it is a new connection.
6. Page movement status, like 302. I do not know how to handle this status in the first place. Then after observing the behavior of web browser I realize I should re fetch the content of page according to the location provided in the response. 

Test
----
Many tests are did to make sure the program runs well. Here we only generalize some of them:

1. Input argument test
We use different input argument, change the order of arguments, and add more arguments to test if program crashes.

2. Internet server error test
We send server with request with different headers to test how the client is going to react with different status. 

3. Different starting website test
We test client with different start website. For now, the client only support start website with address http://cs5700.ccs.neu.edu/fakebook/

4. Socket test
We use different port, host address to test if the performance of socket. 








