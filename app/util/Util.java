package util;


public class Util {
    
   public static boolean isPositiveInteger(String input) {
    try {
        int inputNumber = Integer.parseInt(input);
	    if(inputNumber < 0 ){ return false; }
      return true;
    }
    catch( Exception e ) {
        return false;
    }
   }
}