const NUMGIUDICI = 5;

struct Giudice{
    string nome <50>;
    int punteggio;
};

struct Output{
    Giudice giudici[NUMGIUDICI];
};

struct Input{
    string candidato <50>;
    char operazione;
};

program VOTAFATTOREX{
    version VOTAFATTOREXVERS{
        Output classifica_giudici(void) = 1;
        void esprimi_voto(Input) = 2;
    } = 1;
} = 0x20000013;