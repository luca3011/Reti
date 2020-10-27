#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
/*****************************************/
typedef struct{
    int op1;
    int op2;
    char tipoOp;
}Request;
/****************************************/
int main(int argc, char **argv){
    int sd, port, len, num1, num2, ris;
    const int on = 1;
    struct sockaddr_in cliaddr, servaddr;
    struct hostent *clienthost;
    Request* req = (Request*)malloc(sizeof(Request));
    if(argc!=2){ // Controllo argomenti
        printf("Error: %s port\n", argv[0]); 
        exit(1); 
    }
    else{  // Verifica intero...
        port = atoi(argv[1]);
        if(port< 1024 || port> 65535){ // Porta nel rangeporte disponibili
            printf("Error: %s port\n", argv[0]); 
            exit(2); 
        }
    }
    memset((char*)&servaddr, 0, sizeof(servaddr)); // Inizializzazione indirizzo
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(port);  
    sd=socket(AF_INET, SOCK_DGRAM, 0); // Creazione, bind e settaggio socket
    if(sd<0){
        perror("creazione socket "); 
        exit(1);
    }
    printf("Server: creata la socket, sd=%d\n", sd);
    if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0){
        perror("set opzioni socket "); 
        exit(1);
    }  
    if(bind(sd,(struct sockaddr*) &servaddr, sizeof(servaddr))<0){
        perror("bind socket "); 
        exit(1);
    }
    printf("Server: bind socket ok\n");
    for(;;){ // Ciclo infinito di ricezione e servizio
        len = sizeof(struct sockaddr_in);
        if (recvfrom(sd, req, sizeof(Request), 0, (struct sockaddr*)&cliaddr, &len) < 0){
            perror("recvfrom"); 
            continue;
        }
        num1 = ntohl(req->op1);// Trattiamo conversioni possibili
        num2 = ntohl(req->op2);
        printf("Operazione richiesta: %i %c %i\n",num1, req->tipoOp, num2);
        clienthost = gethostbyaddr( (char *)&cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
        if (clienthost == NULL) 
            printf("client host not found\n");
        else 
            printf("Operazione richiesta da: %s %i\n", clienthost->h_name, (unsigned)ntohs(cliaddr.sin_port)); 
        if(req->tipoOp == '+')
            ris = num1+num2;
        else if(req->tipoOp == '-')
            ris = num1-num2;
        else if(req->tipoOp == '*')
            ris = num1*num2;
        else if(req->tipoOp =='/'){
            if (num2!=0)  ris = num1/num2;
        }
        /* Risultato di default, in caso di errore. Sarebbe piu'corretto avere messaggi di errore, farlo per esercizio */
        else 
            ris=0;
        ris = htonl(ris);
        if (sendto(sd, &ris, sizeof(ris), 0, (struct sockaddr*)&cliaddr, len)<0){
            perror("sendto"); 
            continue;
        }
    }  // while
}  // main