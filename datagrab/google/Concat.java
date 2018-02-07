import java.io.*;
import java.util.*;
public class Concat{
   public static void main(String[] args) throws Exception{
      File f1 = new File("100.txt"); 
      File f2 = new File("200.txt"); 
      File f3 = new File("300.txt"); 
      File f4 = new File("400.txt"); 
      File f5 = new File("500.txt"); 
      File f6 = new File("600.txt"); 
      File f7 = new File("700.txt"); 
      File f8 = new File("800.txt"); 
      File f9 = new File("900.txt"); 
      File f10 = new File("1000.txt"); 
      File[] allFiles = new File[10];
      allFiles[0] = f1;
      allFiles[1] = f2;
      allFiles[2] = f3;
      allFiles[3] = f4;
      allFiles[4] = f5;
      allFiles[5] = f6;
      allFiles[6] = f7;
      allFiles[7] = f8;
      allFiles[8] = f9;
      allFiles[9] = f10;
      for(int i = 0; i < allFiles.length - 9; i++){
         doFile(allFiles[i], i);
      }
   }
   public static void doFile(File f, int j) throws Exception{
      Scanner scan = new Scanner(f);
      String res = "";
      int count = 0;
      while(scan.hasNext()){
         String word = scan.next();
         for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            if(c == '"'){
               if(i-1 >= 0 && word.charAt(i-1) == '\\'){

               }else{
                  res+=c;
               }
            }else if(c == '\''){
            }else{
               res+=c;
            }
         }
         res+=" ";
         System.err.println(count++);
      }
      System.out.println("var x"+j+" = " +res+";");
   }
}
