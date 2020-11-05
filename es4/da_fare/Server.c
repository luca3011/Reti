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
#include <dirent.h>
#include <sys/wait.h>

#define DIM_BUFF 4096
#define DIRNAME_SIZE 64

// Funzione di invio dei file nel sottodirettorio
void invia_nomi_file(DIR *dir, int connfd){ 
    struct dirent *subdd;
    while ((subdd = readdir(dir)) != NULL){
        //leggo ogni file e lo scrivo (separando ogni nome da ';')
        if(strcmp(subdd->d_name, ".") != 0 && strcmp(subdd->d_name, "..") != 0){
            char tmp[65];
            strcpy(tmp, subdd->d_name);
            strcat(tmp, ";");
            write(connfd, tmp, strlen(subdd->d_name)+1);    
            // bufferizzare la scrittura dei nomi file per eseguire meno system call
        }
    }
}

void scan_subdir(DIR *dir, char *basePath, int connfd){
    struct dirent *dd;
    while((dd = readdir(dir)) != NULL){
        DIR *subdir;
        char subdir_name[2*DIRNAME_SIZE];
        if(strcmp(dd->d_name, ".") != 0 && strcmp(dd->d_name, "..") != 0){   
            //evito di scorrere la dir corrente o quella sovrastante
            //costruisco il path della sottocartella
            strcpy(subdir_name, basePath);
            strcat(subdir_name, "/");
            strcat(subdir_name, dd->d_name);

            if((subdir = opendir(subdir_name)) != NULL){ 
                // != NULL --> è una directory che posso aprire (e non un file)
                invia_nomi_file(subdir, connfd);
                closedir(subdir);
            }
        }
    }
}


int elimina_parola(char *name)
{
    char *nome_file;
    char *parola;
    char buff[DIM_BUFF], goodtxt[DIM_BUFF];
    int let_trov = 0, k = 0, i = 0;
    int count = 0, fd_file, nread, lengpar = -1, fd_temp;
    nome_file = strtok(name, ";"); //libero il token
    parola = strtok(NULL, ";");

    if (nome_file == NULL || parola == NULL)
    {
        printf("errore con i dati in input\n");
        printf("Qualsiasi tasto per procedere, EOF per fine:");
        return -1;
    }
    fd_file = open(nome_file, O_RDONLY);
    if (fd_file < 0)
    {
        perror("open file");
        printf("Qualsiasi tasto per procedere, EOF per fine:");
        return -2;
    }

    lengpar = strlen(parola);
    printf("size: %d, parola: %s.\n", lengpar, parola);
    // Creazione file ordinato
    if ((fd_temp = open("temp.txt", O_CREAT | O_WRONLY | O_TRUNC, 0644)) < 0){
        perror("open file temp");
        printf("Qualsiasi tasto per procedere, EOF per fine:");
        return -3;
    }
    while ((nread = read(fd_file, buff, sizeof(buff))) > 0){
        for (i = 0; i < nread; i++){
            if (buff[i] == parola[let_trov]) //c'è una corrispondenza tra buff e parola
                let_trov++;
            else{
                for (int t = 0; t < let_trov; t++){         //copio la parte di buffer scambiata per buona
                    goodtxt[k] = buff[(i - let_trov) + t]; //i-let_trov è l'inizio del falso positivo
                    k++;
                }
                let_trov = 0;
                if (buff[i] == parola[let_trov]) let_trov++; //se la lettera non è valida a seguito delle altre ma è la prima
                else { goodtxt[k] = buff[i]; k++; }
            }
            if (let_trov == lengpar){ //trovata parola completa
                count++;
                let_trov = 0;
            }
        }
        if (let_trov > 0){ //fine del file, le ultime lettere potrebbero essere l'inizio della parola
            for (int t = 0; t < let_trov; t++){
                goodtxt[k] = buff[(i - let_trov) + t]; //i-let_trov è l'inizio del falso positivo
                k++;
            }
        }
        if ((nread = write(fd_temp, &goodtxt, (k) * sizeof(char))) < 0)
        {
            perror("write\n");
            printf("Qualsiasi tasto per procedere, EOF per fine:");
            return -3;
        }
        write(1, &goodtxt, (k) * sizeof(char)); //debug
    }//while
    close(fd_file);
    close(fd_temp);

    if (remove(nome_file) != 0)
    {
        perror("remove");
        printf("Qualsiasi tasto per procedere, EOF per fine:");
        return -3;
    }
    if (rename("temp.txt", nome_file) != 0)
    {
        perror("rename");
        printf("Qualsiasi tasto per procedere, EOF per fine:");
        return -3;
    }
    return count;
}

void gestore(int signo) // gestore del segnale per eliminare i processi figli
{
    int stato;
    printf("esecuzione gestore di SIGCHLD\n");
    wait(&stato);
}

int max(int a, int b){
    return (a > b) ? a : b;
}

int main(int argc, char **argv)
{
    int listenfd, connfd, udpfd, fd_file, nready, maxfdp1;
    char zero = 0, buff[DIM_BUFF], nome_dir[DIRNAME_SIZE], packet[20];
    const int on = 1;
    fd_set rset;
    int len, nread, nwrite, num, ris, port;
    struct sockaddr_in cliaddr, servaddr;
    if (argc != 2)
    {
        printf("Usage: serverSelect port");
        exit(EXIT_FAILURE);
    }
    else
    {
        port = atoi(argv[1]);
    } // Controllo argomenti
    // Inizializzazione indirizzo server
    memset((char *)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY); // no htonl
    servaddr.sin_port = htons(port);

    // NOTA: si possono usare lo stesso indirizzo e stesso numero di porta per le due socket
    // Creazione socket TCP di ascolto
    listenfd = socket(AF_INET, SOCK_STREAM, 0);
    if (listenfd < 0)
    {
        perror("aperturasocket TCP ");
        exit(1);
    }
    if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("set opzionisocket TCP");
        exit(2);
    }
    if (bind(listenfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket TCP");
        exit(3);
    }
    if (listen(listenfd, 5) < 0)
    {
        perror("listen");
        exit(4);
    }
    udpfd = socket(AF_INET, SOCK_DGRAM, 0); // Creazione socket UDP
    if (udpfd < 0)
    {
        perror("aperturasocket UDP");
        exit(5);
    }
    if (setsockopt(udpfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
    {
        perror("set opzionisocket UDP");
        exit(6);
    }
    if (bind(udpfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("bind socket UDP");
        exit(7);
    }
    signal(SIGCHLD, gestore);           // Aggancio gestore
    FD_ZERO(&rset);                     // Pulizia e settaggio maschera file descriptor
    maxfdp1 = max(listenfd, udpfd) + 1; // Prepara il primo parametro
    for (;;)
    { // Ciclo di ricezione eventi della select e preparazione maschera ad ogni giro
        FD_SET(listenfd, &rset);
        FD_SET(udpfd, &rset);
        if ((nready = select(maxfdp1, &rset, NULL, NULL, NULL)) < 0)
        {
            if (errno == EINTR)
                continue;
            else
            {
                perror("select");
                exit(8);
            }
        }
        if (FD_ISSET(udpfd, &rset))
        { // Gestione richieste conteggio
            len = sizeof(struct sockaddr_in);
            if (recvfrom(udpfd, &packet, sizeof(packet), 0, (struct sockaddr *)&cliaddr, &len) < 0)
            {
                perror("recvfrom");
                continue;
            }
            num = elimina_parola(packet);
            ris = htonl(num);
            if (sendto(udpfd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0)
            {
                perror("sendto");
                continue;
            }
        }
        if (FD_ISSET(listenfd, &rset))
        {
            printf("Ricevuta richiesta da un client directory\n");
            len = sizeof(struct sockaddr_in);
            if ((connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &len)) < 0){
                if (errno == EINTR)
                    continue;
                else{
                    perror("accept");
                    exit(9);
                }
            }
            if (fork() == 0){ /* FIGLIO */
                close(listenfd);
                printf("Dentro il figlio, pid=%i\n", getpid());
                while((nread = read(connfd, &nome_dir, sizeof(nome_dir))) != 0){ 
                    // Ciclo di gestione richieste con un’unica socket da parte del figlio
                    if (nread < 0){ // in caso di errore notifico il cliente
                        perror("read");
                        write(connfd, "N", 1);
                        continue;
                    }
                    printf("Richiesta directory %s\n", nome_dir);
                    DIR *dir = opendir(nome_dir);
                    if (dir == NULL){
                        write(connfd, "N", 1);
                    }
                    else{
                        write(connfd, "S", 1);
                        scan_subdir(dir, nome_dir, connfd); //effettua la scansione delle sottodirectory
                        closedir(dir);
                        write(connfd, &zero, 1); // Invio messaggio terminazione sequenza: zero binario
                    }                            
                }
                printf("Ricevuto EOF\n");                               
                close(connfd);      //Chiusura della connessione all’uscita dal ciclo
                exit(EXIT_SUCCESS);   
            } //figlio

            close(connfd); // Padre: chiusura socket di comunicazione e suo ciclo
        }                  
    }                      
} 