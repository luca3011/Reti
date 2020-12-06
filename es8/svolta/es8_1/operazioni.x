struct Operandi{ 
    int op1; 
    int op2; 
};

program OPERAZIONIPROG {
    version OPERAZIONIVERS {
        int SOMMA(Operandi) = 1;
        int MOLTIPLICAZIONE(Operandi) = 2;
    } = 1;
} = 0x20000013;