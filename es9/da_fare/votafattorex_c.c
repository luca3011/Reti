#include "votafattorex.h"

#include <stdio.h>
#include <string.h>

int main(int argc, char **argv){

    int i;
    char *host;
    char in[256];
    CLIENT *cl;
    Output *result;

    if(argc != 2){
        printf("Usage: votafattorex_client servername");
        exit(1);
    }
    
    host = argv[1];
    cl = clnt_create(host, VOTAFATTOREX, VOTAFATTOREXVERS, "udp");
    if(cl == NULL){
        clnt_pcreateerror(host);
        exit(1);
    }

    printf("Inserire:\tC) per visualizzare la classifica;\tA) per aggiungere un voto ad un partecipante;\tS) per sottrarre un voto;\t^D per uscire: ");
    while(gets(in)){
        while(strcmp(in, "C") != 0 && strcmp(in, "A") != 0 && strcmp(in, "S") != 0){
            printf("Inserire:\tC) per visualizzare la classifica;\tA) per aggiungere un voto ad un partecipante;\tS) per sottrarre un voto;\t^D per uscire: ");
            gets(in);
        }

        if(!strcmp(in, "C")){ //operazione: classifica
            result = classifica_giudici_1(NULL, cl);
            if(result == NULL){
                clnt_perror(cl, host);
                exit(1);
            }

            for(i=0;i<NUMGIUDICI;i++){
                printf("Giudice %s, punteggio: %d\n", result->giudici[i].nome, result->giudici[i].punteggio);
            }
        }
        else{ //operazione: esprimi voto
            Input params;
            params.operazione = in[0];
            printf("Inserire il nome del candidato su cui esprimere il giudizio: ");
            if(!gets(in))
                break;
            params.candidato = in;
            void *dummy_result = esprimi_voto_1(&params, cl);
            if(dummy_result == NULL){
                clnt_perror(cl, host);
                exit(1);
            }
        }

        printf("Inserire:\tC) per visualizzare la classifica;\tA) per aggiungere un voto ad un partecipante;\tS) per sottrarre un voto;\t^D per uscire: ");
    }

    printf("Client: termino...\n");

    return 0;
}