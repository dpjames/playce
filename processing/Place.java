import java.util.*;
public class Place{
   public String name;
   public String address;
   public String phone;
   public String[] hours;
   public String price;
   public String rating;
   public ArrayList<String> types;
   public String website;
   public Review[] reviews;
   public Place(Scanner scan){
      scan.nextLine();
      this.name = scan.nextLine();
      scan.nextLine();
      this.address = scan.nextLine();
      scan.nextLine();
      this.phone = scan.nextLine();
      scan.nextLine();
      this.hours = parseHours(scan.nextLine());
      scan.nextLine();
      this.price = scan.nextLine();
      scan.nextLine();
      this.rating = scan.nextLine();
      scan.nextLine();
      this.types = parseTypes(scan.nextLine());
      scan.nextLine();
      this.website = scan.nextLine();
      scan.nextLine();
      scan.nextLine();
      this.reviews = parseReviews(scan);
      System.out.println(this.toString());
   }
   public String toString(){
      String ret = "";
      ret+=this.name + "\n";
      ret+=this.address + "\n";
      ret+=this.phone + "\n";
      for(int i = 0; i < 7 ;i ++){
         ret+=this.hours[i] + " ";
      }
      ret+="\n";
      ret+=this.price + "\n";
      ret+=this.rating + "\n";
      ret+=this.types.toString();
      ret+=this.website+"\n";
      return ret;
   }
   private String[] parseHours(String l){
      String[] ret = new String[7];
      String[] splitup = l.split(",");
      for(int i = 0; i < splitup.length; i++){
         String hours = splitup[i].split(":", 2)[1];
         String[] tmp = hours.split(" ");
         tmp[5] = tmp[5].split("'")[0];
         hours = tmp[1] + tmp[2] + " " + tmp[4] + tmp[5];
         ret[i] = hours;
      }
      return ret;
   }
   private ArrayList<String> parseTypes(String l){
      ArrayList<String> ret = new ArrayList<String>();
      String cleaned = l.replace("'","").replace("[","").replace("]","");
      String[] splitup = cleaned.split(",");
      for(String s : splitup){
         ret.add(s);
      }
      return ret;

   }
   private Review[] parseReviews(Scanner s){
      return null;

   }
   public class Review{
      String text;
      String rating;

   }
}


