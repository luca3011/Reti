
import static java.lang.Integer.parseInt;

import java.io.*;

public class Consumatore2 {

    public static void main(String[] args) {
        Reader r = null;
        // fare controllo argomenti

        String stringa = args[0];

        if (args.length == 1) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            int res;
            String inputl = "";

            try {
                System.out.println("Quante righe vuoi inserire?");
                res = parseInt(in.readLine());
                for (int i =0; i<res; i++)
                { 
                    System.out.println("Inserisci la nuova riga");
                    inputl = inputl + in.readLine()+"\n";
                }
            }
            catch (NumberFormatException nfe) {nfe.printStackTrace(); System.exit(1);}
            catch (IOException e) {e.printStackTrace(); System.exit(2);}

            r = new StringReader(inputl);

            LettoreScrittore(r,stringa);
        }
        else if(args.length ==2)
        {
            try 
            { 
                r = new FileReader((args[1])); 
            }
            catch(FileNotFoundException e)
            { System.out.println("File non trovato"); System.exit(1);}
            
            LettoreScrittore(r,stringa);
            
        }
        else
        {
            System.out.println("Utilizzo: consumatore <prefixstring> <inputFilename>");
            System.exit(0);
        }

    }

   static  void LettoreScrittore(Reader r,String stringa)
    {
        int l = stringa.length();
        String appoggio="";

        try { 
            int snumber=0;
            char ch;
            int x;

           while ((x = r.read()) >=0)
            {
                ch = (char) x;

                if(ch == stringa.charAt(snumber))
                {        
                    appoggio += ch;
                    snumber++;
                    if(appoggio.length()==l)
                    {
                        appoggio="";
                        snumber=0;
                    }
                }
                else
                {   
                    appoggio += ch;
                    snumber++;
                    //appoggio[snumber] = '\0';
                    System.out.print(appoggio);
                    appoggio="";
                    snumber=0;
                }
            }
            r.close();
            }
        catch(IOException ex)
            {System.out.println("Errore di input"); System.exit(2);}
    }
  
}
