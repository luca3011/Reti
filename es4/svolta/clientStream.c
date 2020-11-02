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

#define DIM_BUFF 4096

int main(int argc, char *argv[])
{
    int sd, nread, nwrite;
    char c, ok, buff[DIM_BUFF], nome_file[15];
    struct hostent *host;
    struct sockaddr_in servaddr;
    const int on = 1;
    if (argc != 3)
    { // Controllo argomenti
        printf("Error:%s server port\n", argv[0]);
        exit(1);
    } // Preparazione indirizzo server
    memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);
    if (host == NULL)
    {
        printf("%s not found in /etc/hosts\n", argv[1]);
        exit(2);
    }
    servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
    servaddr.sin_port = htons(atoi(argv[2]));
    // Creazione socket e connessione PRIMA del ciclo
    sd = socket(AF_INET, SOCK_STREAM, 0);
    if (sd < 0)
    {
        perror("apertura socket ");
        exit(3);
    }
    printf("Creata la socket sd=%d\n", sd);
    if (connect(sd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr)) < 0)
    {
        perror("Errore in connect");
        exit(4);
    }
    // Corpo del client: accettazione richieste
    printf("Nome del file da richiedere: ");
    while (gets(nome_file))
    {
        if (write(sd, nome_file, (strlen(nome_file) + 1)) < 0)
        {
            perror("write"); /*...*/
            break;
        }
        if (read(sd, &ok, 1) < 0)
        {
            perror("read"); /*...*/
            break;
        }
        if (ok == 'S')
        {
            while ((nread = read(sd, &c, 1)) > 0)
                if (c != '\0')
                {
                    write(1, &c, 1);
                }
                // Stampo a video fino a EOF
                else
                    break;
            if (nread < 0)
            {
                perror("read"); /*...*/
                break;
            }
        }
        else if (ok == 'N')
            printf("File inesistente\n"); // Controllare sempre che il protocollo sia rispettato
        else
            printf("Errore di protocollo!!!\n");
        printf("Nome del file da richiedere: ");
    } //while
    // Chiusura FUORI dal while
    close(sd);
    printf("\nClient: termino...\n");

    return 0;
}