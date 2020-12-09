#include <stdio.h>
#include <rpc/rpc.h>
#include <dirent.h>

#include "file.h"

Output * file_scan_1_svc(char **nomefile, struct svc_req *rp){
    static Output result;
    const int BUF_SIZE = 256;

    int caratteri = 0;
    int parole = 1;
    int righe = 1;
    char buf[BUF_SIZE];

    FILE * infile;

    if((infile = fopen(*nomefile, "r")) != NULL){
        while( (fgets(buf, BUF_SIZE, infile)) != NULL){
            int i;
            for(i=0; i<strlen(buf); i++){
                if(buf[i] == ' ' || buf[i] == '\t')
                    parole++;
                
                else if(buf[i] == '\n')
                    righe++;
                
                caratteri++;
            }
        }
        if(!feof(infile)){//errore: EOF non raggiunta
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
    static int result;
    result = 0;
    DIR *dir = opendir(input->nome);
    if(dir == NULL)
        result = -1;

    else{
        struct dirent *dd;
        while( (dd = readdir(dir)) != NULL){
            if(dd->d_type == DT_REG){   // se entry è un file
                
                char buf[256];
                snprintf(buf, sizeof buf, "%s/%s", input->nome, dd->d_name);     
                FILE *entry = fopen(buf, "rb");
                if(entry != NULL){
                    fseek(entry, 0, SEEK_END);
                    if(ftell(entry) > input->soglia) //lunghezza del file supera soglia
                        result++;

                    fclose(entry);
                }
            }
        }

        closedir(dir);
    }

    return &result;
}