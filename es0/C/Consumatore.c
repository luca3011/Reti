#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main(int argc, char* argv[]){
    char *file_in, read_char, *prefixstring; int nread, fd, sNumber, sLength;
    
    // controllo argomenti
    if (argc != 3 && argc != 2) { perror(" numero di argomenti sbagliato"); exit(1);}

    if(argc == 3)	//se non c'è ridirezione dell'input
    	file_in = argv[2];

    sLength = strlen(argv[1]); //lunghezza della sequenza
    char temp[sLength+1];
    prefixstring = argv[1];

    // ciclo del programma
    if(argc == 3)
    	fd = open(file_in, O_RDONLY);
    else	//ridirezione dell'input
    	fd = 0;

    if (fd<0){ perror("P0: Impossibile aprire il file."); exit(2); }
    
    sNumber=0;	//numero di sequenza progressivo

    while(nread = read(fd, &read_char, sizeof(char)))
    /* un carattere alla volta fino ad EOF*/
    {
        if (nread > 0){
        	if(read_char == prefixstring[sNumber]){
        		temp[sNumber++] = read_char;

        		if(sNumber == sLength){
        			sNumber = 0;
        		}
        	}
        	else{
        		temp[sNumber++] = read_char;
        		temp[sNumber] = '\0';
        		printf("%s", temp);
        		sNumber = 0;
        	}
    	}
        else{
            printf("(PID %d) impossibile leggere dal file %s", getpid(), file_in);
            perror("Errore!"); close(fd); exit(3);
        }
    }
    close(fd);
}
