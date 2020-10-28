#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

#define MAX_WORD_LENGTH 200

typedef struct
{
    char nomefile[20];
} Request;

int main(int argc, char **argv)
{

    int sd, port, len, max_lenght = 0;
    char line[200];
    FILE *file;
    const int on = 1;
    struct sockaddr_in cliaddr, servaddr;
    struct hostent *clienthost;
    Request *req = (Request *)malloc(sizeof(Request));

    if (argc != 2)
    { // Controllo argomenti
        printf("Error: %s port\n", argv[0]);
        exit(1);
    }
    else
    { // Verifica intero...
        port = atoi(argv[1]);
        if (port < 1024 || port > 65535)
        { // Porta nel rangeporte disponibili
            printf("Error: %s port\n", argv[0]);
            exit(2);
        }
    }
    memset((char *)&servaddr, 0, sizeof(servaddr)); // Inizializzazione indirizzo
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(port);
    sd = socket(AF_INET, SOCK_DGRAM, 0); // Creazione, bind e settaggio socket
    if (sd < 0)
    {
        perror("creazione socket ");
        exit(1);
    }
    printf("Server: creata la socket, sd=%d\n", sd);
    if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("set opzioni socket ");
        exit(1);
    }
    if (bind(sd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket ");
        exit(1);
    }
    printf("Server: bind socket ok\n");
    for (;;)
    { // Ciclo infinito di ricezione e servizio
        len = sizeof(struct sockaddr_in);
        if (recvfrom(sd, req, sizeof(Request), 0, (struct sockaddr *)&cliaddr, &len) < 0)
        {
            perror("recvfrom");
            continue;
        }

        printf("file: %s\n", req);
        if ((file = fopen(req->nomefile,"r")) == NULL)
        {
            max_lenght = -1;
            if (sendto(sd, &max_lenght, sizeof(max_lenght), 0, (struct sockaddr *)&cliaddr, len) < 0)
            {
                perror("sendto1");
                continue;
            }
            perror("open");
            continue;
        }
        else
        {
            while (fgets(line, 60, file) != NULL)
            {
                //printf("verifica di line: %s \n", line); //debug
                char *temp = strtok(line, " ");
                while (temp != NULL)
                {
                    //printf("verifica di temp: %s \n", temp);
                    if (strlen(temp) > max_lenght)
                    {
                        max_lenght = strlen(temp);
                        //printf("strlen: %d\n", max_lenght);
                    }
                    temp = strtok(NULL, " ");
                }
            }
            fclose(file);
        }
        max_lenght = htonl(max_lenght);
        if (sendto(sd, &max_lenght, sizeof(max_lenght), 0, (struct sockaddr *)&cliaddr, len) < 0)
        {
            perror("sendto");
            continue;
        }
    }
}