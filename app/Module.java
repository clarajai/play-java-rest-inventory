import com.google.inject.AbstractModule;


import services.InventoryService;
import services.InventoryServiceImpl;
import play.db.jpa.JPAApi;
import play.db.jpa.*;


public class Module extends AbstractModule {

    @Override
    public void configure() {
        
        // Set InventoryServiceImpl as the implementation for InventoryService.
        bind(InventoryService.class).to(InventoryServiceImpl.class);
        //bind(JPAApi.class).toProvider(DBProvider.class);
        bind(JPAApi.class).toProvider(DefaultJPAApi.JPAApiProvider.class);
    }

}
