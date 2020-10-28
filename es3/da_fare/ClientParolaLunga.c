#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#define MAX_WORD_LENGTH 200

typedef struct
{
    char nomefile[20];
} Request;

int main(int argc, char **argv)
{
    int port, sd, len, num1, max_lenght = -12;
    struct hostent *host;
    struct sockaddr_in clientaddr, servaddr;
    Request req;

    // Controllo argomenti
    if (argc != 3)
    {
        printf("Error:%s serverAddress serverPort\n", argv[0]);
        exit(1);
    }
    num1 = 0;
    //  Verifica correttezza porta e host: farla al meglio e fine controllo argomenti
    while (argv[2][num1] != '\0')
    {
        if ((argv[2][num1] < '0') || (argv[2][num1] > '9'))
        {
            printf("Secondo argomento non intero\n");
            exit(2);
        }
        num1++;
    }
    port = atoi(argv[2]);

    if (port < 1024 || port > 65535)
    {
        printf("Port scorretta...\n");
        exit(2);
    }
    if (host == NULL)
    {
        printf("Host not found ...\n", argv[1]);
        exit(2);
    }
    //Inizializzazione indirizzo client e server
    memset((char *)&clientaddr, 0, sizeof(struct sockaddr_in));
    clientaddr.sin_family = AF_INET;
    clientaddr.sin_addr.s_addr = INADDR_ANY;
    clientaddr.sin_port = 0;
    memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);
    {
        servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
        servaddr.sin_port = htons(port);
    }

    sd = socket(AF_INET, SOCK_DGRAM, 0); // Creazione socket
    if (sd < 0)
    {
        perror("apertura socket");
        exit(1);
    }
    // Binda una porta scelta dal sistema
    if (bind(sd, (struct sockaddr *)&clientaddr, sizeof(clientaddr)) < 0)
    {
        perror("bind socket ");
        exit(1);
    }
    printf("Inserisci il nome del file, EOF per terminare: ");
    while ((gets(req.nomefile)) != EOF)
    {
        //printf("nomefile: %s\n", req.nomefile);
        if (sizeof(req.nomefile) > MAX_WORD_LENGTH)
        {
            printf("nome file di testo troppo lungo");
            continue;
        }
        len = sizeof(servaddr);
        if (sendto(sd, &req, sizeof(Request), 0, (struct sockaddr *)&servaddr, len) < 0)
        {
            perror("sendto");
            continue;
        }
        /* ricezione del risultato */
        printf("Attesa del risultato...\n");
        if (recvfrom(sd, &max_lenght, sizeof(max_lenght), 0, (struct sockaddr *)&servaddr, &len) < 0)
        {
            perror("recvfrom");
            continue;
        }
        printf("Esito dell'operazione: %i. EOF per terminare ", ntohl(max_lenght));
    }
    close(sd); // Libero le risorse: chiusura socket
    printf("\nClient: termino...\n");
    exit(0);
}