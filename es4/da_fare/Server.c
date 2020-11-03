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

/*int conta_file(char *name)
{ // Funzione di conteggio dei file nel direttorio
    DIR *dir;
    struct dirent *dd;
    int count = 0;
    dir = opendir(name);
    while ((dd = readdir(dir)) != NULL)
    {
        printf("Trovato il file %s\n", dd->d_name);
        count++;
    } /* conta anche il direttorio stesso e il padre e altri direttori! 
printf("Numero totale di file %d\n", count);
closedir(dir);
return count;
}
*/

int conta_parola(char *name)
{
    char *nome_file;
    char *parola;
    char buff[DIM_BUFF];
    int count = 0, fd_file, nread, lengpar=0,j=0;
    nome_file = strtok(name, ";");
    parola = strtok(NULL, ";");

    if (nome_file == NULL || parola == NULL)
    {
        printf("errore con i dati in input\n");
        return -1;
    }
    printf("ops\n");
    fd_file = open(nome_file, O_RDONLY);
    if (fd_file < 0)
    {
        printf("N");
        return -2;
    }
    printf("debug\n");
    printf("%s,%s.\n", nome_file, parola);

    lengpar=strlen(parola);
    printf("size: %d, lettera: %c\n",lengpar, parola[0]);
    while ((nread = read(fd_file, buff, sizeof(buff))) > 0){
        for(int i=0;i<sizeof(buff);i++){
            if(buff[i]==parola[j])
                j++;
            else
                j=0;
            if(j==lengpar){
                count++;j=0;}
        }
    }

    return count;
}

void gestore(int signo) // gestore del segnale per eliminare i processi figli
{
    int stato;
    printf("esecuzione gestore di SIGCHLD\n");
    wait(&stato);
}

int max(int a, int b)
{
    return (a > b) ? a : b;
}

int main(int argc, char **argv)
{
    int listenfd, connfd, udpfd, fd_file, nready, maxfdp1;
    char zero = 0, buff[DIM_BUFF], nome_file[20], packet[20];
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
            num = conta_parola(packet);
            ris = htonl(num);
            if (sendto(udpfd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0)
            {
                perror("sendto");
                continue;
            }
        }
        if (FD_ISSET(listenfd, &rset))
        {
            printf("Ricevuta richiesta di get di un file\n");
            len = sizeof(struct sockaddr_in);
            if ((connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &len)) < 0)
            {
                if (errno == EINTR)
                    continue;
                else
                {
                    perror("accept");
                    exit(9);
                }
            }
            if (fork() == 0)
            { /* FIGLIO */
                close(listenfd);
                printf("Dentro il figlio, pid=%i\n", getpid());
                for (;;)
                { // Ciclo di gestione richieste con un’unica socket da parte del figlio
                    if ((nread = read(connfd, &nome_file, sizeof(nome_file))) < 0)
                    {
                        perror("read");
                        break;
                    }
                    else if (nread == 0)
                    { // Quando il figlio riceve EOF esce dal ciclo
                        printf("Ricevuto EOF\n");
                        break;
                    }
                    printf("Richiesto file %s\n", nome_file);
                    fd_file = open(nome_file, O_RDONLY);
                    if (fd_file < 0)
                    {
                        write(connfd, "N", 1);
                    }
                    else
                    {
                        write(connfd, "S", 1); // lettura/scrittura file (a blocchi)
                        while ((nread = read(fd_file, buff, sizeof(buff))) > 0)
                        {
                            if (nwrite = write(connfd, buff, nread) < 0)
                            {
                                perror("write");
                                break;
                            }
                        }
                        write(connfd, &zero, 1); // Invio messaggio terminazione file: zero binario
                        close(fd_file);          // Libero la risorsa sessione file
                    }                            //else
                }                                //for
                close(connfd);
                exit(0);   //Chiusura della connessione all’uscita dal ciclo
            }              //figlio
            close(connfd); // Padre: chiusura socket di comunicazione e suo ciclo
        }                  //if
    }                      //for
} //main