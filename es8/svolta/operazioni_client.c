#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"


int main(int argc, char *argv[]){
    char *server; 
    Operandi op;  
    CLIENT *cl; 
    int*ris;
    if(argc!= 5){ //controllo argomenti
        fprintf(stderr, "uso: %s host tipo op1 op2\n", argv[0]); 
        exit(1);
    }
    if(argv[2][0] != 'm' && argv[2][0] != 's'){ 
        fprintf(stderr, "uso: %s host somma/moltiplicazione op1 op2\n", argv[0]);
        fprintf(stderr, "tipodeveiniziareper 's' o 'm'\n");
        exit(1);
    }
    server = argv[1];
    op.op1 = atoi(argv[3]);
    op.op2 = atoi(argv[4]);
    // creazione gestore di trasporto
    cl = clnt_create(server, OPERAZIONIPROG, OPERAZIONIVERS, "udp");
    if (cl == NULL){ 
        clnt_pcreateerror(server);
        exit(1);
    }

    if(argv[2][0] == 's') 
        ris = somma_1(&op, cl);
    if(argv[2][0] == 'm') 
        ris = moltiplicazione_1(&op, cl);
    /* errore RPC */
    if (ris== NULL){ 
        clnt_perror(cl, server); 
        exit(1); 
    }
    /* errore risultato: assumiamo che non si possa ottenere 0 */ 
    if (*ris == 0) { 
        fprintf(stderr, "%s:...", argv[0], server); 
        exit(1);
    }
    printf("Risultato da %s: %i\n", server, *ris);
    // libero la risorsa gestore di trasporto
    clnt_destroy(cl);
}