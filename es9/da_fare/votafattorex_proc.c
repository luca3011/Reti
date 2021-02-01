#include "votafattorex.h"

#include <string.h>
#include <stdio.h>

#define NUMPARTECIPANTI 10

typedef struct Partecipante{
    char *candidato;
    char *giudice;
    char categoria;
    char *nomefile;
    char fase;
    int voto;
} Partecipante;

/*strutture dati*/
static Partecipante partecipanti[NUMPARTECIPANTI];
/*mantengo la classifica come struttura statica, la andrò ad aggiornare ogni volta che il punteggio di un partecipante viene modificato*/
static Output classifica;
static short inizializzato = 0;
static short classifica_uptodate = 0;

void inizializza(){
    if(inizializzato)
        return;

    int i;
    for(i=0; i<NUMPARTECIPANTI; i++){
        partecipanti[i].candidato = "L";
        partecipanti[i].giudice = "L";
        partecipanti[i].nomefile = "L";
        partecipanti[i].categoria = 'L';
        partecipanti[i].fase = 'L';
        partecipanti[i].voto = -1;
    }

    partecipanti[1].candidato = "Fanti";
    partecipanti[1].giudice = "Branda";
    partecipanti[1].categoria = 'D';
    partecipanti[1].nomefile = "FantiProf.txt";
    partecipanti[1].fase = 'S';
    partecipanti[1].voto = 78;

    partecipanti[3].candidato = "Magagni";
    partecipanti[3].giudice = "Branda";
    partecipanti[3].categoria = 'D';
    partecipanti[3].nomefile = "MagagniProf.txt";
    partecipanti[3].fase = 'S';
    partecipanti[3].voto = 87;

    partecipanti[5].candidato = "Guerra";
    partecipanti[5].giudice = "Caini";
    partecipanti[5].categoria = 'U';
    partecipanti[5].nomefile = "GuerraProf.txt";
    partecipanti[5].fase = 'A';
    partecipanti[5].voto = 63;

    classifica.giudici[0].nome = "Branda";
    classifica.giudici[0].punteggio = 0;
    classifica.giudici[1].nome = "Caini";
    classifica.giudici[1].punteggio = 0;
    classifica.giudici[2].nome = classifica.giudici[3].nome = classifica.giudici[4].nome = "L";
    classifica.giudici[2].punteggio = classifica.giudici[3].punteggio = classifica.giudici[4].punteggio = -1;

    inizializzato = 1;

    printf("Strutture dati inizializzate!\n");
}

Output * classifica_giudici_1_svc(void *in, struct svc_req* rqstp){
    inizializza();
    int i,j;
    if(!classifica_uptodate){
        printf("Classifica non aggiornata, ricalcolo...\n");
        for(i=0;i<NUMGIUDICI; i++)
            classifica.giudici[i].punteggio = 0;
        // Scorro la lista dei partecipanti, aggiungendo il loro punteggio al contatore del relativo giudice
        for(i=0;i<NUMPARTECIPANTI;i++){
            if(strcmp(partecipanti[i].giudice, "L") != 0){
                for(j=0;j<NUMGIUDICI;j++){
                    if(!strcmp(partecipanti[i].giudice, classifica.giudici[j].nome))
                        classifica.giudici[j].punteggio += partecipanti[i].voto;
                }
            }
        }
        printf("Classifica aggiornata!\n");
        classifica_uptodate = 1;
    }

    //restituisco infine la classifica di tutti i giudici, comprensiva dei relativi punteggi (per semplicità, la classifica non è ordinata...)
    return &classifica;
}

void * esprimi_voto_1_svc(Input *in, struct svc_req* rqstp){
    inizializza();
    int i, op;
    switch(in->operazione){
        case 'A':
            op = 1;
            break;
        case 'S':
            op = -1;
            break;
    }

    for(i=0;i<NUMPARTECIPANTI;i++){
        if(strcmp(partecipanti[i].candidato, in->candidato) == 0)
            partecipanti[i].voto = partecipanti[i].voto + op;       
    }

    printf("Un voto è stato espresso!\n");
    classifica_uptodate = 0;
}