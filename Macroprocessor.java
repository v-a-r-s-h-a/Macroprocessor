// Macroprocessor using Two Pass Algorithm -

import java.io.*;
import java.util.*;
class Macroprocessor
{
    public static void main(String args[])throws IOException
    {
        //Provide Input code in the Input.txt file
        Input();
        //Calling Pass 1
        Pass1();
        //Calling Pass 2
        Pass2();
    }

    public static void Input()throws IOException
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(isr);
        FileWriter fw =new FileWriter("Input.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        System.out.println("Enter data for input file :");
        String s;
        do
        {
            s=br.readLine();
            s=s.toUpperCase();
            pw.println(s);
        }while(s.equals("END")==false);   
        pw.close();
        bw.close();
        fw.close();
    }

    public static void Pass1()throws IOException
    {
        FileReader fr =new FileReader("Input.txt");
        BufferedReader fbr = new BufferedReader(fr);

        FileWriter fw2 =new FileWriter("MDT.txt");
        BufferedWriter bw2 = new BufferedWriter(fw2);
        PrintWriter pw2 = new PrintWriter(bw2);

        FileWriter fw3 =new FileWriter("MNT.txt");
        BufferedWriter bw3 = new BufferedWriter(fw3);
        PrintWriter pw3 = new PrintWriter(bw3);

        FileWriter fw4 =new FileWriter("Pass1.txt");
        BufferedWriter bw4 = new BufferedWriter(fw4);
        PrintWriter pw4 = new PrintWriter(bw4);

        String l;
        int mdtc=0,mntc=0;
        //Read line from Input code
        while((l=fbr.readLine())!=null)
        {
            //If MACRO pseudo op found
            if(l.compareTo("MACRO")==0)
            {   
                String mn=fbr.readLine();
                //Entering macro name in MNT with current value of mdtc and updating value mntc
                pw3.println((++mntc)+" "+mn+" "+(mdtc+1));

                //Preparing argument list array
                char ala[]=new char[50];
                int na = Pass1ala(ala,mn);

                //Entering macro name in MDT and updating value of mdtc
                pw2.println((++mdtc)+" "+mn);

                while((l=fbr.readLine())!=null)
                {
                    ++mdtc;
                    //Stop if MEND pseude op is found
                    if(l.equals("MEND")==true)
                    {
                        pw2.println(mdtc+" "+l);
                        break;
                    }
                    else
                    {
                        //Substuting index notation for dummy arguments
                        String k="";
                        for(int i=0;i<l.length()-1;i++)
                        {
                            char c=l.charAt(i);
                            if(c=='&')
                            {
                                char c2=l.charAt(i+1);
                                for(int j=0;j<na;j++)
                                {
                                    if(c2==ala[j])
                                        k=k+"#"+Integer.toString(j);
                                }
                            }
                            else
                                k=k+c;
                        }
                        pw2.println(mdtc+" "+k);
                    }
                }
            }  
            else
                pw4.println(l);
        }
        pw4.close();
        bw4.close();
        fw4.close();
        pw3.close();
        bw3.close();
        fw3.close();
        pw2.close();
        bw2.close();
        fw2.close();       
        fbr.close();
        fr.close();
    }

    public static int Pass1ala(char ala[],String l)throws IOException
    {
        int m=0;
        for(int i=0;i<l.length()-1;i++)
        {
            char c=l.charAt(i);
            if(c=='&')
                ala[m++]=l.charAt(i+1);
        }
        return m;
    }

    public static void Pass2()throws IOException
    {
        FileReader fr5 =new FileReader("Pass1.txt");
        BufferedReader fbr5 = new BufferedReader(fr5);

        FileWriter fw5 =new FileWriter("OUTPUT.txt");
        BufferedWriter bw5 = new BufferedWriter(fw5);
        PrintWriter pw5 = new PrintWriter(bw5);

        System.out.println("Output:");
        String l;
        while((l=fbr5.readLine())!=null)
        {
            StringTokenizer st=new StringTokenizer(l," ");
            String mn=st.nextToken();

            //Searching MNT for match
            String mntl=FindinMNT(mn);
            //If macro name is not found enter the line in OUTPUT file else process the macro call
            if(mntl==null)
            {
                System.out.println(l); 
                pw5.println(l);              
            }                
            else
            {
                //Finding value of mdtp from MNT entry
                char c=mntl.charAt(mntl.length()-1);
                int mdtp=Integer.parseInt(String.valueOf(c));

                //Set up argument list array
                String ala2[]=new String[50];
                int na=Pass2ala(ala2,l);

                FileReader fr6 =new FileReader("MDT.txt");
                BufferedReader fbr6 = new BufferedReader(fr6);
                //Updating value of mdtp
                ++mdtp;
                
                String l2;
                while((l2=fbr6.readLine())!=null)
                { 
                    char c1=l2.charAt(0);
                    int index=Integer.parseInt(String.valueOf(c1));
                    String sub=l2.substring(2);
                    if(index==mdtp)
                    {
                        // If "MEND" is found we assign mdtp to -1 so that it does not perform further substitution
                        if(sub.compareTo("MEND")==0)
                            mdtp=-1;

                        if(mdtp!=-1)
                        {
                            l2=l2.substring(2);
                            String k="";
                            for(int i=0;i<l2.length()-1;i++)
                            {
                                char c2=l2.charAt(i);
                                if(c2=='#')
                                {
                                    char c3=l2.charAt(i+1);
                                    int in=Integer.parseInt(String.valueOf(c3));
                                    for(int j=0;j<na;j++)
                                    {
                                        if(in==j)
                                        {
                                            k=k+ala2[j];
                                        }
                                    }	
                                }
                                else
                                    k=k+c2;
                            }

                            System.out.println(k); 
                            pw5.println(k);
                            //Incrementing value of mdtp by one
                            ++mdtp;
                        }                        
                    } 
                }
                fbr6.close();
                fr6.close();
            }            
        }

        pw5.close();
        bw5.close();
        fw5.close();

        fbr5.close();
        fr5.close();
    }

    public static String FindinMNT(String mn)throws IOException
    {
        FileReader fr4 =new FileReader("MNT.txt");
        BufferedReader fbr4 = new BufferedReader(fr4);
        String r="";
        String l;
        while((l=fbr4.readLine())!=null)    
        {
            StringTokenizer st=new StringTokenizer(l," ");
            String m=st.nextToken();
            String mntmn=st.nextToken();
            if(mntmn.compareTo(mn)==0)
            {
                r=l;
                break;
            }
            else
                r=null;
        }
        fbr4.close();
        fr4.close(); 

        return r;
    }

    public static int Pass2ala(String ala2[],String l)throws IOException
    {
        int m=0;
        StringTokenizer st2=new StringTokenizer(l," ");
        String t1=st2.nextToken();
        String t2=st2.nextToken(); 

        StringTokenizer st3=new StringTokenizer(t2,",");
        int nT=st3.countTokens();
        for(int i=0;i<nT;i++)
            ala2[m++]=st3.nextToken();
        return m;
    }
}
