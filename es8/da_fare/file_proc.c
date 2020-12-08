#include <stdio.h>
#include <rpc/rpc.h>
#include "file.h"

Output * file_scan_1_svc(char **nomefile, struct svc_req *rp){
    static Output result;
    int caratteri = 0;
    int parole = 1;
    int righe = 1;
    const int BUF_SIZE = 256;
    char buf[BUF_SIZE];

    FILE * infile;

    if((infile = fopen(*nomefile, "r")) != NULL){
        while( (fgets(buf, BUF_SIZE, infile)) != NULL){
            int i;
            for(i=0; i<strlen(buf); i++){
                if(buf[i] == ' ' || buf[i] == '\t'){
                    parole++;
                }
                else if(buf[i] == '\n'){
                    righe++;
                }
                else{
                    caratteri++;
                }
            }
        }
        if(!feof(infile)){
            //errore: EOF non raggiunta
            result.caratteri = result.parole = result.righe = -1;
        }
        else{
            result.caratteri = caratteri;
            result.parole = parole;
            result.righe = righe;
        }
        fclose(infile);
    }
    else{
        //errore: impossibile aprire il file
        result.caratteri = result.parole = result.righe = -1;
    }

    return &result;
}

int * dir_scan_1_svc(Input *input, struct svc_req *rp){

}