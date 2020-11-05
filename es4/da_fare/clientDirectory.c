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
#define DIRNAME_SIZE 64

int main(int argc, char **argv){
    int sd, nread, nwrite, port;
    char c, ok, buff[DIM_BUFF], nome_directory[DIRNAME_SIZE];
    struct hostent *host;
    struct sockaddr_in servaddr;
    if(argc != 3){
        printf("Usage: %s serverAddr serverPort", argv[0]);
        exit(EXIT_FAILURE);
    }
    port = atoi(argv[2]);
    if(port < 1024 || port > 65535){
        printf("Errore: porta non valida");
        exit(EXIT_FAILURE);
    }
    memset(&servaddr, 0, sizeof(struct sockaddr_in));
    servaddr.sin_port = htons(port);
    servaddr.sin_family = AF_INET;
    host = gethostbyname(argv[1]);
    if(host == NULL){
        printf("%s not found in /etc/hosts", argv[1]);
        exit(EXIT_FAILURE);
    }
    servaddr.sin_addr.s_addr = ((struct in_addr*) (host->h_addr))->s_addr;
    
    if((sd = socket(AF_INET, SOCK_STREAM, 0)) < 0){
        perror("creazione socket");
        exit(EXIT_FAILURE);
    }
    printf("Creata la socket sd=%d\n", sd);

    if (connect(sd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr)) < 0){
        perror("Errore in connect");
        exit(EXIT_FAILURE);
    }

    //Corpo del client

    printf("Inserire il nome del direttorio: ");
    while(gets(nome_directory)){
        
        if(write(sd, nome_directory, strlen(nome_directory) + 1) < 0){
            perror("write");
            exit(EXIT_FAILURE);
        }

        if(read(sd, &ok, sizeof(char)) < 0){
            perror("read status");
            exit(EXIT_FAILURE);
        }
        
        if(ok == 'S'){
            // ricevo la lista dei nomi
            short EOS = 0;  // flag per segnalare la fine della sequenza
            printf("File contenuti nelle sottodirectory:\n");
            while(!EOS && (nread = read(sd, buff, sizeof(buff))) > 0){
                int i;
                char *startptr = buff;
                for(i=0;i<nread;i++){
                    if(buff[i] == 0){
                        EOS = 1;
                        break;
                    }
                    else if(buff[i] == ';'){
                        write(1, startptr, (buff+i) - startptr);
                        printf("\n");
                        startptr = buff+i+1;
                    }
                }

                //scrivo l'ultimo nome file eventualmente rimasto nel buffer
                if(!EOS && buff[nread - 1] != ';'){
                    write(1, startptr, (buff+nread) - startptr);
                    printf("\n");
                }
            }
        }
        else if(ok == 'N'){
            printf("Errore del server (directory inesistente?)\n");
        }
        else{
            printf("Errore di protocollo!\n");
        }
        printf("Inserire il nome del direttorio o EOF per terminare (Ctrl+D): ");
    }

    close(sd);
    printf("Client: termino...\n");
    return 0;
}