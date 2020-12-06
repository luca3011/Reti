#include <stdio.h>
#include <rpc/rpc.h>
#include "echo.h"

#define DIM 100

int main(int argc, char *argv[]){
    CLIENT *cl;
    char **echo_msg;
    char *server;
    char *msg;
    
    if (argc < 2){
        fprintf(stderr, "uso: %s serverhost\n", argv[0]); 
        exit(1);
    }
    server = argv[1];
    cl = clnt_create(server, ECHOPROG, ECHOVERS, "udp");
    if (cl == NULL){ 
        clnt_pcreateerror(server);
        exit(1);
    }
    msg = (char*)malloc(DIM);
    printf("Qualsiasi tasto per procedere, EOF per terminare:\n"); 
    printf("Messaggio (max 100 caratteri)? "); 
    /* lettura della stringa da inviare o fine file */
    while(gets(msg)){
        echo_msg = echo_1(&msg, cl);
        if(echo_msg == NULL){ /* controllo errore RPC */
            fprintf(stderr, "%s: %s fallisce la rpc\n", argv[0], server);
            clnt_perror(cl, server);
            exit(1);
        }
        if (*echo_msg == NULL){ /* controllo errore risultato*/
            fprintf(stderr, "%s: %s risultato non valido (NULL)\n",   argv[0], server);
            clnt_perror(cl, server);
            exit(1);
        }
        printf("Messaggio consegnato a %s: %s\n", server, msg);
        printf("Messaggio ricevuto da %s: %s\n\n", server, *echo_msg);
        printf("Qualsiasi tasto per procedere, EOF per terminare:\n");
        printf("Messaggio (max 100 caratteri)? ");
    }// while 
    gets(msg);
    free(msg);
    clnt_destroy(cl);
    // Libero risorse: malloc e gestore di trasporto
    printf("Termino...\n");  
    exit(0); 
} // main