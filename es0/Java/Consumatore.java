
import java.io.*;
public class Consumatore {
    
    public static void main(String[] args)
    {
        FileReader r = null;
        // fare controllo argomenti
        
        String stringa = args[0];
            String appoggio = "";
            int flag = 0;
            char nextChar = stringa.charAt(0);
            int l = stringa.length();


        if(args.length ==1)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            int res;
            String inputl = "";
            

            try
            {
                System.out.println("Quante righe vuoi inserire?");
                res = Integer.parseInt(in.readLine());
                for (int i =0; i<res; i++)
                { 
                    System.out.println("Inserisci la nuova riga");
                    inputl = inputl + in.readLine()+"\n";
                }
            }
            catch (NumberFormatException nfe) {nfe.printStackTrace(); System.exit(1);}
            catch (IOException e) {e.printStackTrace(); System.exit(2);}

            StringReader out = new StringReader(inputl);

            try { 
                int x; 
                char ch;
                while ((x = out.read()) >=0){   
                    ch = (char) x;
    
                    //filtro stringa
                    if(ch==nextChar)
                    {
                        do
                        {   
                            ch=(char) x;
                            flag++;
                            appoggio = appoggio+ch;
                            nextChar = stringa.charAt(flag);
                        }while(((x=out.read())>=0) && ((char)x==nextChar) && (flag < l-1) );
                        
                        if(flag < l-1)
                            System.out.print(appoggio);
                    
                        appoggio="";
                        flag=0;
                        nextChar = stringa.charAt(0);
                    } 
                    else
                    {
                        System.out.print(ch);
                    }
                }
                out.close();
                }
            catch(IOException ex)
                {System.out.println("Errore di input"); System.exit(2);}

        }
        else if(args.length ==2)
        {
            try { r = new FileReader(args[1]); }
            catch(FileNotFoundException e)
            { System.out.println("File non trovato"); System.exit(1);}
            
            try { 
                int x; 
                char ch;
                while ((x = r.read()) >=0){   
                    ch = (char) x;
    
                    //filtro stringa
                    if(ch==nextChar)
                    {
                        do
                        {   
                            ch=(char) x;
                            flag++;
                            appoggio = appoggio+ch;
                            nextChar = stringa.charAt(flag);
                        }while(((x=r.read())>=0) && ((char)x==nextChar) && (flag < l-1) );
                        
                        if(flag < l-1)
                            System.out.print(appoggio);
                    
                        appoggio="";
                        flag=0;
                        nextChar = stringa.charAt(0);
                    } 
                    else
                    {
                        System.out.print(ch);
                    }
                }
                r.close();
                }
            catch(IOException ex)
                {System.out.println("Errore di input"); System.exit(2);}
        }
        else
        {
            System.out.println("Utilizzo: consumatore <prefixstring> <inputFilename>");
            System.exit(0);
        }

    }


}
