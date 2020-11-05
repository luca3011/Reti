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
    int sd, len, num_file,result,port;
    char nome_file[136], parola[80];
    char *packet;
    if (argc != 3)
    { // Controllo argomenti
        printf("Error:%s, numero di argomenti sbagliato\n", argv[0]);
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
        int num1 = 0;
         while( argv[2][num1]!= '\0' )
        {
            if ((argv[2][num1] < '0') || (argv[2][num1] > '9'))
            {   
                printf("Secondo argomento non intero\n");
                exit(3);
            }
        num1++;
        }
        port = atoi(argv[2]);
         if (port < 1024 || port > 65535)
        {
            printf("Port scorretta...");
            exit(3); 
        }
 
        servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
        servaddr.sin_port = htons(port);
    }

    sd = socket(AF_INET, SOCK_DGRAM, 0); // Creazione e connessione
    if (sd < 0)
    {
        perror("aperturasocket");
        exit(4);
    }

    bind(sd, (struct sockaddr *)&clientaddr, sizeof(clientaddr)); // Corpo del client: ciclo di accettazione di richieste di eliminazione
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
            continue;
        }
        strcpy(packet, nome_file);
        strcat(packet, ";"); //tokenizzo
        strcat(packet, parola);

        len = sizeof(servaddr); // Invio richiesta
        if (sendto(sd, packet, (strlen(packet) + 1), 0, (struct sockaddr *)&servaddr, len) < 0)
        {
            perror("scrittura socket");
            continue; // Se l’invio fallisce nuovo ciclo
        }
        // Ricezione del risultato
        if (recvfrom(sd, &num_file, sizeof(num_file), 0, (struct sockaddr *)&servaddr, &len) < 0)
        {
            perror("recvfrom");
            printf("Nome del file: ");
            continue; /* se la ricezione fallisce nuovo ciclo */
        }
        result=ntohl(num_file);
        switch(result){
            case -1:
                printf("errore con i parametri passati\n");
                break;
            case -2:
                printf("file non trovato o non apribile\n");
                break;
            case -3:
                printf("errore nella gestione del file\n");
                break;
            default:
                printf("Numero di occorrenze: %i\n", result);
        }

        printf("Nome del file: ");
        free(packet);
    }

    printf("\nClient: termino...\n");
    close(sd);

    return 0;
} // main
