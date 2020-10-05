
//============================================================================
// Name        : SSLClient.cpp
// Compiling   : g++ -c -o SSLClient.o SSLClient.cpp
//               g++ -o SSLClient SSLClient.o -lssl -lcrypto
//============================================================================
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <string.h>
#include <netinet/tcp.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 

using namespace std;
    
SSL *ssl;
int sock;
int timeout = 1000;
    
int RecvPacket()
{
    int len=100;
    int finalLen = 0;
    char buf[1000000];

    /*
    while (len >= 0)
    {
        len=SSL_read(ssl, buf, 100);
        if (buf[0] == "\n")
        {
        }
        
        buf[len]=0;
        printf("%s\n",buf);
//         fprintf(fp, "%s",buf);
    }
    int bytes;
    int line_length = 0;
    int i = 0;
    int received;
    do {
        bytes = SSL_read(ssl, buf, 1);                // read 1 byte to c[0]
        if (bytes  <= 0) break;                     // read fall or connection closed
        if (buf[0] == '\n') {                         // if '\n'
            if (line_length == 0) break;            // empty line, so end header
            else line_length = 0;                   // else reset for new line
        } else if ( buf[0] != '\r') line_length++;    // inc length
        printf("%s",buf);
        received += bytes;                          // count
    } while (1);
   do
   {
        SSL_read(ssl, buf, 100);
        printf("%s",buf);
        memset(buf, 0, 100);
   } while (buf != "0\r\n");

    do {
        len=SSL_read(ssl, buf, 100);
        buf[len]=0;
        printf("%s",buf);
//        fprintf(fp, "%s",buf);
        printf("len je %i ",len);
    } while (len > 0);
    */
    int n;
    printf("going into read loop \n");
   while (n = read(sock,buf,255) > 0)
   {
       printf("ctu");
   }
   

   while (1)
   {
        len=SSL_read(ssl, buf, 100);
        buf[len]=0;
        //printf("%s",buf);
        printf("pending %i ",SSL_pending(ssl));
        printf("len je %i \n",len);
   }
   


    printf("finished reading");

    if (len < 0) {
        int err = SSL_get_error(ssl, len);
    if (err == SSL_ERROR_WANT_READ)
            return 0;
        if (err == SSL_ERROR_WANT_WRITE)
            return 0;
        if (err == SSL_ERROR_ZERO_RETURN || err == SSL_ERROR_SYSCALL || err == SSL_ERROR_SSL)
            return -1;
    }
}
    
int SendPacket(const char *buf)
{
	printf("writing to socket");
    int len = SSL_write(ssl, buf, strlen(buf));
	printf("finished writing to socket");
    if (len < 0) {
        int err = SSL_get_error(ssl, len);
        switch (err) {
        case SSL_ERROR_WANT_WRITE:
            return 0;
        case SSL_ERROR_WANT_READ:
            return 0;
        case SSL_ERROR_ZERO_RETURN:
        case SSL_ERROR_SYSCALL:
        case SSL_ERROR_SSL:
        default:
            return -1;
        }
    }
}
    
void log_ssl()
{
    int err;
    while (err = ERR_get_error()) {
        char *str = ERR_error_string(err, 0);
        if (!str)
            return;
        printf(str);
        printf("\n");
        fflush(stdout);
    }
}
    
int main(int argc, char *argv[])
{
    int s;
    s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0) {
        printf("Error creating socket.\n");
        return -1;
    }
    struct sockaddr_in sa;
    memset (&sa, 0, sizeof(sa));
    sa.sin_family      = AF_INET;
    sa.sin_addr.s_addr = inet_addr("162.159.138.232"); // discord ip
    sa.sin_port        = htons (443); 
    socklen_t socklen = sizeof(sa);
    if (connect(s, (struct sockaddr *)&sa, socklen)) {
        printf("Error connecting to server.\n");
        return -1;
    }
    SSL_library_init();
    SSLeay_add_ssl_algorithms();
    SSL_load_error_strings();
    const SSL_METHOD *meth = TLSv1_2_client_method();
    SSL_CTX *ctx = SSL_CTX_new (meth);
    ssl = SSL_new (ctx);


    if (!ssl) {
        printf("Error creating SSL.\n");
        log_ssl();
        return -1;
    }
    sock = SSL_get_fd(ssl);
    SSL_set_fd(ssl, s);
    int err = SSL_connect(ssl);
    if (err <= 0) {
        printf("Error creating SSL connection.  err=%x\n", err);
        log_ssl();
        fflush(stdout);
        return -1;
    }
    printf ("SSL connection using %s\n", SSL_get_cipher (ssl));

    while (1)
    {
        printf("in loop");  
        char input;
        scanf("%c", &input);
        if(input == 'n') {
            char *request = "GET /api/channels/720745314209890379/messages HTTP/1.1\r\nHost: discord.com\r\nAuthorization: Bot NzYyMTAyODE1MjQxNjY2NTkw.X3kRjg.9Ora6rgDp0jCTCZpdC3jDT-7Qoo\r\nUser-Agent: DiscordBot ($url, $versionNumber)\r\n\r\n"; 
            SendPacket(request);
            RecvPacket();
            printf("after if");
        }
    }
    


    return 0;
}