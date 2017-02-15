package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;
import javax.inject.*;

import javax.persistence.*;

@Singleton
public class InventoryDAO {
    
     @Inject JPAApi jpaApi;
     
    
    
    /**
     * Create an Inventory
     *
     * @param Inventory model
     *
     * @return Inventory
     */
    public Inventory create (Inventory model) {
        model.emptyToDefaultValue();
	
	    jpaApi.em().persist(model);
        // Flush and refresh for check
        jpaApi.em().flush();
        jpaApi.em().refresh(model);
        return model;
    }

    /**
     * Find an Inventory by id
     *
     * @param Integer id
     *
     * @return Inventory
     */
    public Inventory find(Integer id) {
	
        return jpaApi.em().find(Inventory.class, id);
    }

     /**
     * Find Inventorys by name or name/quantity pair
     *
     * @param String name
     *
     * @return List<Inventory>
     */
    public List<Inventory> find(String name, Integer quantity) {
	
	    if(quantity == null){
	        Query query= jpaApi.em().createQuery("SELECT m FROM " + Inventory.TABLE + " m WHERE name=:name");
	        query.setParameter("name",name);
	        return(List<Inventory>) query.getResultList();
	    }
	    else{
		    Query query= jpaApi.em().createQuery("SELECT m FROM " + Inventory.TABLE + " m WHERE name=:name and quantity=:quantity");
		    query.setParameter("name",name);
		    query.setParameter("quantity",quantity);
		    return(List<Inventory>) query.getResultList();
	    }
	
    }

    /**
     * Update an Inventory by Id
     *
     * @param Inventory model
     *
     * @return Inventory
     */
    public Inventory update(Inventory model) {
        Inventory inventory = (Inventory)find(model.id);
	    if(inventory == null)
	       return null;
	    else{
              if(model.quantity == null){
		        model.quantity = inventory.quantity;
		      }
		    return jpaApi.em().merge(model);

	    }
    }
    
    

    /**
     * Delete an Inventory by id
     *
     * @param Integer id
     */
    public void delete(Integer id) {
        Inventory model = jpaApi.em().getReference(Inventory.class, id);
        jpaApi.em().remove(model);
    }

    /**
     * Get all Inventorys
     *
     * @return List<Inventory>
     */
    public List<Inventory> all() {
        Query query = jpaApi.em().createQuery("SELECT m FROM " + Inventory.TABLE + " m ORDER BY id");
        List<Inventory> listInventory = query.getResultList();
        return listInventory; 
    }

    
    /**
     * Get the number of total row
     *
     * @return Long
     */
    public Long count() {
        return (Long) jpaApi.em().createQuery("SELECT count(m) FROM " + Inventory.TABLE + " m").getSingleResult();
    }

     /**
     * Check if a product already exists
     *
     * @param Inventory
     */
     public List<Inventory> getProductDetails(Inventory model)
	{
	    Query query= jpaApi.em().createQuery("SELECT m FROM " + Inventory.TABLE + " m WHERE name=:name");
	    query.setParameter("name",model.name);
	    List<Inventory> products =  query.getResultList();
	    return products; 
	}

	


}