#include <stdio.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <signal.h>
#include <errno.h>
#include <sys/wait.h>

#define DIM_BUFF 256

/**************************/
void gestore(int signo)
{
    int stato;
    printf("esecuzione gestore di SIGCHLD\n");
    wait(&stato);
}
/**************************/

void figlio(int conn_sd);

int main(int argc, char **argv)
{
    int listen_sd, conn_sd;
    int port, len;
    const int on = 1;
    struct sockaddr_in cliaddr, servaddr;
    struct hostent *host;
    if (argc != 2)
    { // Controllo argomenti
        printf("Error: %s port\n", argv[0]);
        exit(1);
    }
    else
        port = atoi(argv[1]);

    if (port < 1024 || port > 65535)
    {
        printf("Porta non valida");
        exit(1);
    }
    //Inizializzazione dell'address del server
    memset((char *)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(port);
    // Creazione, bind e settaggio opzioni socket ascolto
    listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    if (listen_sd < 0)
    {
        perror("creazione socket ");
        exit(1);
    }
    if (setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("setsockopt");
        exit(1);
    }
    printf("Server: set ok\n");
    if (bind(listen_sd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket d'ascolto");
        exit(1);
    }
    printf("Server: bind socket d'ascolto ok\n");
    if (listen(listen_sd, 5) < 0)
    {
        perror("listen");
        exit(1);
    }
    signal(SIGCHLD, gestore);
    for (;;)
    { // Ciclo di ricezione richieste
        if ((conn_sd = accept(listen_sd, (struct sockaddr *)&cliaddr, &len)) < 0)
        {
            if (errno == EINTR)
            {
                perror("Forzo la continuazione della accept");
                continue;
            }
            else
                exit(1);
        }
        int pid = fork();
        if (pid == 0)    //  Figlio
        { 
            // Chiusura file descriptor non utilizzati e ridirezione di stdin e stdout
            close(listen_sd);
            figlio(conn_sd);
        }
        else if(pid < 0){
            perror("fork error");
        }
        // PADRE: chiusura socket di connessione  (NON di ascolto)
        close(conn_sd);
    }
    return 0;
}

void figlio(int conn_sd){
    int numlinea, nread;
    char buffer[DIM_BUFF];

    if(read(conn_sd, &numlinea, sizeof(int)) <= 0){
        printf("[figlio n. %d] socket error (connessione chiusa dal client?)", getpid());
        exit(1);
    }
    //controllo sulla correttezza del numero di linea non necessario
    numlinea = ntohl(numlinea);
    printf("riga da eliminare: %d\n", numlinea);
    int riga = 1;
    while((nread = read(conn_sd, buffer, DIM_BUFF)) > 0){
        int i;
        char *start = buffer;
        char *end = buffer;
        for(i=0;i<nread && riga <= numlinea;i++){
            if(buffer[i] == '\n'){
                if(riga == numlinea){
                    //write(1, buffer, (end-buffer));
                    write(conn_sd, buffer, (end-buffer));
                    start = buffer+i+1;
                }
                end = buffer+i+1;
                riga++;
            }
        }
        //se la riga da cancellare non è l'ultima
        if(i!=nread){
            //write(1, start, nread - (start - buffer));
            write(conn_sd, start, nread - (start - buffer));
        }
        else if(buffer[nread-1] != '\n'){   //se la riga da cancellare è l'ultima
            //write(1, buffer,(end - buffer)+1);
            write(conn_sd, buffer, (end - buffer)+1);
        }
    
    }
    
    printf("[figlio n. %d] comunicazione conclusa, termino...", getpid());
    exit(0);
}