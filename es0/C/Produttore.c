#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256
int main(int argc, char *argv[])
{
    // preparazione programma
    int fd, readValues, bytes_to_write, written;
    char *file_out;
    char buf[MAX_STRING_LENGTH], ch;
    int righe, i;
    char riga[MAX_STRING_LENGTH];

    // fare controllo argomenti
    if (argc != 2)
    {
        perror(" numero di argomenti sbagliato …");
        exit(EXIT_FAILURE);
    }

    // lettura righe
    file_out = argv[1];

    fd = open(file_out, O_WRONLY | O_CREAT | O_TRUNC, 00640);

    if (fd < 0)
    {
        perror("P0: Impossibile creare/aprire il file");
        exit(EXIT_FAILURE);
    }

    // corpo produttore
    
    while (read(0,&ch,1))
    {
        written = write(fd, &ch, sizeof(ch)); // uso della primitiva

        if (written <= 0)
        {
            perror("P0: errore nella scrittura sul file");
            exit(EXIT_FAILURE);
        }
    }
    close(fd);
}