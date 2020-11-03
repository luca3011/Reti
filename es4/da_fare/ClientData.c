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

int main(int argc, char **argv)
{
    struct hostent *host;
    struct sockaddr_in servaddr, clientaddr;
    int sd, len, num_file;
    char nome_file[20], parola[40];
    char *packet;
    if (argc != 3)
    { // Controllo argomenti
        printf("Error:%s server\n", argv[0]);
        exit(1);
    }

    clientaddr.sin_family = AF_INET; // Prepara indirizzo client e server
    clientaddr.sin_addr.s_addr = INADDR_ANY;
    clientaddr.sin_port = 0;
    memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);

    if (host == NULL)
    {
        printf("%s not found in /etc/hosts\n", argv[1]);
        exit(2);
    }
    else
    {
        servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
        servaddr.sin_port = htons(atoi(argv[2]));
    }
    sd = socket(AF_INET, SOCK_DGRAM, 0); // Creazione e connessione
    if (sd < 0)
    {
        perror("aperturasocket");
        exit(3);
    }

    bind(sd, (struct sockaddr *)&clientaddr, sizeof(clientaddr)); // Corpo del client: ciclo di accettazione di richieste di conteggio
    printf("Nome del file: ");

    while (gets(nome_file))
    {
        printf("parola: ");
        gets(parola);
        int finallength = strlen(nome_file) + strlen(parola) + 2;
        char *packet = (char *)malloc(finallength * sizeof(char));
        if (packet == NULL)
        {
            perror("errore nella malloc");
            printf("Nome del file: ");
            continue;
        }
        strcpy(packet, nome_file);
        strcat(packet, ";");
        strcat(packet, parola);

        len = sizeof(servaddr); // Invio richiesta
        if (sendto(sd, packet, (strlen(packet) + 1), 0, (struct sockaddr *)&servaddr, len) < 0)
        {
            perror("scrittura socket");
            printf("Nome del file: ");
            continue; // Se l’invio fallisce nuovo ciclo
        }
        // Ricezione del risultato
        if (recvfrom(sd, &num_file, sizeof(num_file), 0, (struct sockaddr *)&servaddr, &len) < 0)
        {
            perror("recvfrom");
            printf("Nome del file: ");
            continue; /* se la ricezione fallisce nuovo ciclo */
        }

        printf("Numero di occorrenze trovate: %i\n", ntohl(num_file));
        printf("Nome del file: ");
        free(packet);
    }

    printf("\nClient: termino...\n");
    close(sd);

    return 0;
} // main
