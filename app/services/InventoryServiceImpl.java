package services;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import models.InventoryDAO;
import models.Inventory;

import javax.inject.*;

@Singleton
public class InventoryServiceImpl implements InventoryService {
    
    InventoryDAO inventoryDAO;

    @Inject
    InventoryServiceImpl(InventoryDAO dao){
        this.inventoryDAO = dao;
    }
    
    /**
     * Create an Inventory
     *
     * @param Inventory data
     *
     * @return Inventory
     */
    @Override
    public Inventory create(Inventory data) {
        return inventoryDAO.create(data);
    }

    /**
     * Update an Inventory
     *
     * @param Inventory data
     *
     * @return Inventory
     */
    @Override
    public Inventory update(Inventory data) {
        return inventoryDAO.update(data);
    }

   
    /**
     * Find  Inventorys by name
     *
     * @param String name
     *
     * @return List<Inventory>
     */
    @Override
    public List<Inventory> find(String name) {
        return inventoryDAO.find(name, null);
    }
    
    /**
     * Find  Inventorys by name and quantity
     *
     * @param String name
     *
     * @return List<Inventory>
     */
    @Override
    public List<Inventory> find(String name, Integer quantity) {
        return inventoryDAO.find(name, quantity);
    }

    /**
     * Delete an Inventory by id
     *
     * @param Integer id
     */
    @Override
    public Boolean delete(Integer id) {
        Inventory Inventory = inventoryDAO.find(id);
        if (Inventory != null) {
            inventoryDAO.delete(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all Inventorys
     *
     * @return List<Inventory>
     */
    @Override
    public List<Inventory> all() {
        return inventoryDAO.all();
    }

    

    /**
     * Get the number of total of Inventorys
     *
     * @return Long
     */
    @Override
    public Long count() {
        return inventoryDAO.count();
    }

    /**
     * Check if a product already exists
     *
     * @param Inventory
     */
    @Override
    public List<Inventory> getProductDetails(Inventory data) {
       return inventoryDAO.getProductDetails(data);
        
    }

}