#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"

int *somma_1_svc( Operandi *op,struct svc_req*rp){
    static int ris;
    
    printf("Operandi ricevuti: %i e %i\n", op->op1, op->op2);
    ris = (op->op1) + (op->op2);
    printf("Somma: %i\n", ris);
    
    return (&ris);
}

int *moltiplicazione_1_svc(Operandi *op, struct svc_req* rp){
    static int ris;
    printf("Operandi ricevuti: %i e %i\n", op->op1, op->op2);
    ris = (op->op1) * (op->op2);
    printf("Moltiplicazione: %i\n", ris);
    return (&ris);
}