import java.util.*;
public class Driver {
   public static void main(String[] args){
      Hashtable<String, Integer> pref = getPref();
   }

   private static Hashtable<String, Integer>  getPref(){
      Hashtable<String, Integer> pref = new Hashtable<String, Integer>();

      String temp;
      boolean b;
      getInput("How many people (including you?): ", "numPeople", pref); 
      getInput("How much money? (0-4): ", "price", pref); 
      getInput("Is this a date? (0 || 1): ", "isDate", pref); 
      getInput("Are you hungry (0 || 1): ", "isHungry", pref); 
      getInput("Are you feeling active? (0 || 1): ", "isActive", pref); 
      getInput("Are you feeling like being around people? (0 || 1): ", "isSocial", pref); 
      getInput("Are you (and everyone in your group) 21+? (0 || 1): ", "is21", pref); 
      //pref.put("key",0); 
      return pref;
   }
   private static void getInput(String question, String key,Hashtable<String, Integer> pref){
      Scanner input = new Scanner(System.in);
      boolean b;
      String temp;
      do{
         System.out.print(question); 
         temp = input.nextLine();
         try{
            b = false;
            int val = Integer.parseInt(temp);
            pref.put(key, val);
         }catch(NumberFormatException e){
            b = true;
            System.out.println("bad input");
         }
      } while(b);  
   }
}
