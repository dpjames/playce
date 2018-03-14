package cz.makub;

import java.io.InputStream;
import java.net.URL;

public class WeatherGrab {
    static boolean isOutdoor() {
     	boolean ret = false;
 		URL website;
       double val = 0;
 		try {
 			website = new URL("http://api.wunderground.com/api/eff6f60613b31fc1/conditions/q/CA/San_Luis_Obispo.json");
 			byte[] bytes = new byte[200000];
     		try (InputStream in = website.openStream()) {
     	    	in.read(bytes, 0, 200000);
     		}
     		
            //new Scanner(System.in).next();

     	  System.out.println(new String(bytes));
          String rec = new String(bytes);
          //System.out.println(rec);
     	  int index = rec.indexOf("precip_today_in"); // 15
          int col = rec.indexOf(':', index);
          int fq = rec.indexOf('"', col);
          int lq = rec.indexOf('"', fq+1);          
          System.out.println(col);
          System.out.println(fq);
          System.out.println(lq);
          String sval = rec.substring(fq+1,lq);
          System.out.println("sval is " + sval);
          val = Double.parseDouble(sval); 
 		} catch (Exception e) {
 			e.printStackTrace();
 		}   
       System.out.println(val);
     	return !(val > 0);
     }
}
