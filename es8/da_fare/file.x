struct Output{
    int caratteri;
    int parole;
    int linee;
};

struct Input{
    string nome <50>;
    int soglia;
};

program SCANPROG{
    version SCANVERS{
        Output FILE_SCAN(string) = 1;
        int DIR_SCAN(Input) = 2;
    } = 1;
} = 0x20000013;