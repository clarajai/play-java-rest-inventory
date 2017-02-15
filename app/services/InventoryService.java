package services;

import java.util.List;
import models.Inventory;
import com.google.inject.ImplementedBy;

//Interface for inventorys
//@ImplementedBy(InventoryServiceImpl.class)
public interface InventoryService {
    
    Inventory create(Inventory data);

    
    Inventory update(Inventory data);
   
    
    List<Inventory> find(String name);

    List<Inventory> find(String name, Integer quantity);
	
    Boolean delete(Integer id);

    List<Inventory> all();
    
    Long count();

    List<Inventory> getProductDetails(Inventory data);
}