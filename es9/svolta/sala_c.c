#include <stdio.h>

#include "sala.h"

int main(int argc, char *argv[])
{
    char *host;
    CLIENT *cl;
    int *ris, *start_ok;
    void *in;
    Sala *sala;
    Input input;
    char str[5];
    char c, ok[256];
    int i, j, fila, col;

    if (argc != 2)
    {
        printf("usage: %s server_host\n", argv[0]);
        exit(1);
    }
    host = argv[1];
    cl = clnt_create(host, SALA, SALAVERS, "udp");
    if (cl == NULL)
    {
        clnt_pcreateerror(host);
        exit(1);
    }
    printf("Inserire:\nV) per vederela sala\tP) per prenotare la postazione\t^Dper terminare: ");
    while (gets(ok))
    {
        if (strcmp(ok, "P") == 0)
        {
            gets(ok);
            // Leggo e controllo il tipo
            while ((strcmp(ok, "P") != 0) && (strcmp(ok, "D") != 0) && (strcmp(ok, "B") != 0))
            {
                printf("Lettera sbagliata! Inserisci P, D o B: \n");
                gets(ok);
            }
            input.tipo = ok[0];
            fila = -1;
            // Leggo la fila
            while (fila < 0 || fila > (NUMFILE - 1))
            {
                printf("Inserisci la fila (da 0 a %i): \n", (NUMFILE - 1));
                while (scanf("%i", &fila) != 1)
                {
                    do
                    {
                        c = getchar();
                        printf("%c ", c);
                    } while (c != '\n');
                    printf("Fila: ");
                }
            }
            gets(ok); //Consumo fine linea
            input.fila = fila;
            col = -1; // Leggo la colonna
            while (col < 0 || col > (LUNGHFILA - 1))
            {
                printf("Inserisci la colonna(0 -%i):\n", (LUNGHFILA - 1));
                while (scanf("%i", &col) != 1)
                {
                    do
                    {
                        c = getchar();
                        printf("%c ", c);
                    } while (c != '\n');
                    printf("Colonna: ");
                }
            }
            gets(ok); //  Consumo fine linea
            input.colonna = col;
            ris = prenota_postazione_1(&input, cl); // Invocazione remota
            if (ris == NULL)
            {
                clnt_perror(cl, host);
                exit(1);
            }
            if (*ris < 0)
                printf("Problemi...\n");
            else
                printf("Prenotazione effettuata con successo\n");

            /* ad ogni invocazione controlliamo sempre che non ci sia stato un errore di RPC (risultato NULL) 
            e poi di logica (a secondo del valore atteso e dalla logica del programma */
        } // if P
        else if (strcmp(ok, "V") == 0)
        {
            // Invocazione remota
            sala = visualizza_stato_1(in, cl);
            if (sala == NULL)
            {
                clnt_perror(cl, host);
                exit(1);
            }
            printf("Stato di occupazione della sala:\n");
            for (i = 0; i < NUMFILE; i++)
            {
                for (j = 0; j < LUNGHFILA; j++)
                    printf("%c\t", sala->fila[i].posto[j]);
                printf("\n");
            }
        } // if V
        else
            printf("Argomento di ingresso errato!!\n");

        printf("Inserire:\nV) per vederela sala\tP) per prenotare la postazione\t^Dper terminare: ");
    } // while
    // Libero le risorse, distruggendo il gestore di trasporto
    clnt_destroy(cl);
    exit(0);
} // main