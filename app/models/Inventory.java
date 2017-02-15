package models;

import javax.persistence.*;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.data.format.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.validation.*;
import java.util.List;
import java.util.ArrayList;


@Entity
public class Inventory {
    public static String TABLE = Inventory.class.getSimpleName();

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

    @Constraints.Required
    public String name;

    @Constraints.Min(0)   
    public Integer quantity;

    public Inventory() {}
    public Inventory( String name, Integer quantity) { 
	this.name = name;
	this.quantity = quantity;
   }

 
	
   /**
     * Set all empty values to default
     */
    public void emptyToDefaultValue() {
        if (name != null && name.isEmpty()) name = null;
        if (quantity == null) quantity = 0;
	}


}