#include<stdio.h>
#include<rpc/rpc.h>
#include"echo.h"

char**echo_1_svc(char**msg, struct svc_req *rp){
    static char* echo_msg;
    free(echo_msg);
    echo_msg = (char*)malloc(strlen(*msg)+1);
    printf("Messaggio ricevuto:%s\n",*msg);
    strcpy(echo_msg,*msg);
    printf("Messaggio da rispedire:%s\n", echo_msg);
    return(&echo_msg);
}