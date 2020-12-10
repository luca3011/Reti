#include <stdio.h>
#include <rpc/rpc.h>
#include <ctype.h> //toupper()
#include "file.h"

#define DIM 100

int main(int argc, char *argv[]){
    CLIENT *cl;
    Input dir;
    Output *file_result;
    int *dir_num;
    char *server;
    char *msg;
    
    if (argc != 2){
        fprintf(stderr, "uso: %s serverhost\n", argv[0]); 
        exit(1);
    }
    server = argv[1];
    cl = clnt_create(server, SCANPROG, SCANVERS, "udp");
    if (cl == NULL){ 
        clnt_pcreateerror(server);
        exit(1);
    }

    msg = (char*)malloc(DIM);
    printf("Scansione: 'F' per conta in un file e 'D' per direttorio, EOF per terminare\n");
    /* lettura della stringa da inviare o fine file */
    while(gets(msg)){
        if(toupper(msg[0]) == 'F'){
            printf("Nome del file: (max 100 caratteri)? ");
            gets(msg);
            file_result = file_scan_1(&msg, cl);
            if(file_result == NULL){ /* controllo errore RPC */
                fprintf(stderr, "%s: %s fallisce la rpc\n", argv[0], server);
                clnt_perror(cl, server);
                exit(1);
            }
            if (file_result->caratteri == -1){ /* controllo errore risultato*/
                fprintf(stderr, "%s: %s risultato non valido (-1,-1,-1)\n",   argv[0], server);
                clnt_perror(cl, server);
                exit(1);
            }
            printf("Nel file %s sono stati trovati %d caratteri, %d parole e %d righe\n", 
                  msg, file_result->caratteri, file_result->parole, file_result->righe);
        }
        else if(toupper(msg[0])=='D'){
            printf("Nome del direttorio: (max 100 caratteri)? \n");
            char buff[100];
            gets(buff);
            dir.nome = buff;
            printf("Dimensione minima del file(bytes): (nomedir: %s)\n", dir.nome);
            scanf("%d",&(dir.soglia));
            dir_num = dir_scan_1(&dir, cl);
            if(dir_num == NULL){ /* controllo errore RPC */
                fprintf(stderr, "%s: %s fallisce la rpc\n", argv[0], server);
                clnt_perror(cl, server);
                exit(1);
            }
            if (*dir_num == -1){ /* controllo errore risultato*/
                fprintf(stderr, "%s: %s risultato non valido (-1)\n",   argv[0], server);
                  clnt_perror(cl, server);
                  exit(1);
            }
            printf("Nella directory %s ci sono %d files più grandi di %d bytes\n", 
                                                    dir.nome, *dir_num, dir.soglia);
        }
        else{
            continue;
        }
        
        printf("Scansione: 'F' per conta in un file e 'D' per direttorio, EOF per terminare\n");
    }

    gets(msg);
    free(msg);
    clnt_destroy(cl);

    printf("Termino...\n");  
    exit(0); 
}