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

/**************************/
void gestore(int signo){ 
    int stato;
    printf("esecuzione gestore di SIGCHLD\n");
    wait(&stato);
}
/**************************/
int main(int argc, char **argv){ 
    int listen_sd, conn_sd;
    int port, len; 
    const int on = 1;
    struct sockaddr_in cliaddr, servaddr;
    struct hostent *host;
    if(argc!=2){ // Controllo argomenti
        printf("Error: %s port\n", argv[0]); 
        exit(1); 
    }
    else 
        port = atoi(argv[1]); // Verifica intero...  Controllo porta????
    // Inizializzazione indirizzo server
    memset((char*)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(port);
    // Creazione, bind e settaggio opzioni socket ascolto
    listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    if(listen_sd<0){
        perror("creazione socket "); 
        exit(1);
    }
    if(setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0){
        perror("setsockopt"); 
        exit(1);
    }
    printf("Server: set ok\n");  
    if(bind(listen_sd, (struct sockaddr*) &servaddr, sizeof(servaddr)) < 0){
        perror("bind socket d'ascolto"); 
        exit(1);
    }
    printf("Server: bind socket d'ascolto ok\n");
    if (listen(listen_sd, 5) < 0){
        perror("listen"); 
        exit(1);
    }
    /* Aggancio gestore per evitare figli zombie. Quali altre primitive potrei usare?
    * E' portabile su tutti i sistemi? Pregi/Difetti?  */
    signal(SIGCHLD, gestore);
    for(;;){ // Ciclo di ricezione richieste
        if((conn_sd = accept(listen_sd,(struct sockaddr*)&cliaddr,&len)) < 0){
            /* La accept può essere interrotta dai segnali inviati dai figli alla loro teminazione.
            * Tale situazione va gestita opportunamente. Vedere nel man a cosa corrisponde la costante EINTR!*/
            if(errno == EINTR){ 
               perror("Forzo la continuazione della accept");
               continue;
            }
            else exit(1);
        }
        if (fork()==0){ //  Figlio
            // Chiusura file descriptor non utilizzati e ridirezione di stdin e stdout 
            close (listen_sd);
            close(1); 
            close(0);
            dup(conn_sd); 
            dup(conn_sd);
            close(conn_sd);
            // Esecuzione ordinamento
            execl("/bin/sort", "sort", (char*)0);
        } 
        // come se ci fosse un:else
        // PADRE: chiusura socket di connessione  (NON di ascolto)
        close(conn_sd);
    }
}