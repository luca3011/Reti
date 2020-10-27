#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#define LINE_LENGTH 256

/***************************************/
typedef struct{ 
    int op1; 
    int op2; 
    char tipoOp; 
 }Request;
/**************************************/

int main(int argc, char **argv){
    struct hostent* host;
    struct sockaddr_in clientaddr, servaddr;
    int port, sd, num1, num2, len, ris;
    char okstr[LINE_LENGTH];
    char c;  
    int ok;
    Request req; 
    if(argc!=3){ // Controllo argomenti
        printf("Error:%s serverAddress serverPort\n", argv[0]);
        exit(1);
    }
    num1 = 0; 
    //  Verifica correttezza porta e host: farla al meglio e fine controllo argomenti
    while( argv[2][num1]!= '\0'){ 
        if( (argv[2][num1] < '0') || (argv[2][num1] > '9') ){ 
            printf("Secondo argomento non intero\n"); 
            exit(2); 
        }
        num1++;
    }
    port = atoi(argv[2]);
    if (port < 1024 || port > 65535) {
        printf("Port scorretta..."); 
        exit(2); 
    }
    if (host == NULL){ 
        printf("Host not found ..."); 
        exit(2); 
    }
    // valoricorretti
    //Inizializzazione indirizzo client e server
    memset((char *)&clientaddr, 0, sizeof(struct sockaddr_in));
    clientaddr.sin_family = AF_INET;
    clientaddr.sin_addr.s_addr = INADDR_ANY;
    clientaddr.sin_port = 0;
    memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);
    { 
        servaddr.sin_addr.s_addr = ((struct in_addr*)(host->h_addr))->s_addr;
        servaddr.sin_port = htons(port);
    }
    sd = socket(AF_INET, SOCK_DGRAM, 0);    // Creazione socket
    if(sd<0) {
        perror("apertura socket"); 
        exit(1);
    }
    // Binda una porta scelta dal sistema
    if(bind(sd,(struct sockaddr*) &clientaddr,sizeof(clientaddr)) < 0){
        perror("bind socket "); 
        exit(1);
    }
    printf("Inserisci il primo operando (int), EOF per terminare:");
    while( (ok = scanf("%i", &num1)) != EOF){ // Filtro
        if(ok != 1){ // errore di formato
            /* Problema nell’implementazione della scanf. Se l’input contiene PRIMA dell’intero  
            * altri caratteri la testina di lettura si blocca sul primo carattere (non intero) letto. 
            * Ad esempio:  |ab1292\n|
            *               ^     La testina si blocca qui        
            * Bisogna quindi consumare tutto il buffer in modo da sbloccare la testina.  
            */
            do{
                c=getchar(); 
                printf("%c ", c);
            }while(c != '\n');

            printf("Inserisci il primo operando (int), EOF per terminare: ");
            continue;
        }
        req.op1 = htonl(num1);
        gets(okstr);  // Consumo il resto dellalinea
        printf("Secondo operando (intero): ");
        while(scanf("%i", &num2) != 1){
            do{
                c=getchar(); 
                printf("%c ", c);
            }while(c!= '\n');
            printf("Secondo operando (intero): ");
        }
        req.op2 = htonl(num2);
        gets(okstr);
        printf("Stringa letta: %s\n", okstr);    
        do{ 
            printf("Operazione (+ = addizione, - = sottrazione, ... ");
            c = getchar();
        } while (c!='+' && c !='-' && c!='*' && c  !='/');
        req.tipoOp = c;
        gets(okstr); // Consumo il resto della linea
        printf("Operazione richiesta: %u %c %u\n", ntohl(req.op1), req.tipoOp, ntohl(req.op2));
        len=sizeof(servaddr); // Richiesta operazione 
        if(sendto(sd, &req, sizeof(Request), 0,(struct sockaddr*)&servaddr, len) < 0){ 
            perror("sendto"); 
            continue; 
        }
        /* ricezione del risultato */
        printf("Attesa del risultato...\n");
        if (recvfrom(sd, &ris, sizeof(ris), 0, (struct sockaddr*)&servaddr, &len) < 0){
            perror("recvfrom"); 
            continue;
        }
        printf("Esito dell'operazione: %i\n", ntohl(ris));
        printf("Inserisci il primo operando (int), EOF per terminare:");
    } 
    close(sd);// Libero le risorse: chiusura socket
    printf("\nClient: termino...\n");  
    exit(0);
}