#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

#define DIM_BUFF 256
#define LINE_LENGTH 64
int main(int argc, char **argv)
{
    int sd, fd_sorg, fd_dest, nread, riga, num1, port;
    char *rigastringa;
    char okstr[LINE_LENGTH];
    char buff[DIM_BUFF];
    char nome_sorg[FILENAME_MAX + 1], nome_dest[FILENAME_MAX + 1];
    struct hostent *host;
    struct sockaddr_in servaddr;

    // Controllo argomenti
    if (argc != 3)
    {
        printf("Error:%s serverAddress serverPort\n", argv[0]);
        exit(1);
    }
    num1 = 0;
    while( argv[2][num1]!= '\0' )
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
        printf("Port scorretta...");
        exit(2); 
    }
    // Inizializzazione indirizzo server
    memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);

    if (host == NULL)
    {
        printf("%s not found in /etc/hosts\n", argv[1]);
        exit(1);
    }
    
    servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
    servaddr.sin_port = htons(atoi(argv[2]));

    // Corpo del client
    printf("Eliminiamo una riga da un file!\n");
    printf("Nome del file, EOF per terminare: ");

    while (gets(nome_sorg))
    {
        printf("File da aprire: __%s__\n", nome_sorg);
        /*
        if(sizeof(nome_sorg)>FILENAME_MAX)
        {
            printf("nome del direttorio troppo lungo");
            continue;
        }
        */
        if ((fd_sorg = open(nome_sorg, O_RDONLY)) < 0)
        {
            perror("open"); // in caso che il file da ordinare non esista
            printf("Qualsiasi tasto per procedere, EOF per fine: ");
            continue;
        }
     
        printf("Inserisci riga da eliminare: ");
        scanf("%d", &riga);
        gets(okstr);

        if (riga < 0)
        {
            printf("Qualsiasi tasto per procedere, EOF per fine: ");
            continue;
        }

        sd = socket(AF_INET, SOCK_STREAM, 0);
        // Creazione socket
        if (sd < 0)
        {
            perror("apertura socket");
            exit(1);
        }
        printf("Client: creata la socket sd=%d\n", sd);
        if (connect(sd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr)) < 0)
        {
            perror("connect");
            exit(1);
        }
        
        //scrittura della numero della riga
        riga = htonl(riga);
        write(sd, &riga, sizeof(int));

        while ((nread = read(fd_sorg, buff, DIM_BUFF)) > 0)
        {
            // write(1, buff, nread);
            // Stampa su console
            write(sd, buff, nread); // Invio
        }
        shutdown(sd, SHUT_WR);
        close(fd_sorg);
        if(remove(nome_sorg) != 0){
            perror("remove");
        }
        // Creazione file ordinato
        if ((fd_dest = open(nome_sorg, O_WRONLY | O_CREAT | O_TRUNC, 0644)) < 0)
        {
            perror("open");
            printf("Qualsiasi tasto per procedere, EOF per fine:” ");
            continue;
        }
        while ((nread = read(sd, buff, DIM_BUFF)) > 0)
        {
            write(fd_dest, buff, nread);
            //write(1, buff, nread);
        }
        shutdown(sd, SHUT_RD);
        close(fd_dest);

        close(sd);
        printf("Nome del file da ordinare, EOF per terminare:");
    }
    exit(0);
}