
import play.test.*;
import play.Application;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import play.mvc.*;
import static play.test.Helpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;
import java.util.List;
import services.InventoryService;
import services.InventoryServiceImpl;
import models.Inventory;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import java.util.HashMap;
import java.io.FileInputStream;

import javax.inject.*;
import play.libs.ws.*;

import play.inject.guice.GuiceInjectorBuilder;
import play.inject.Injector;

public class ModelTest extends Controller{
    
    JndiDatabaseTester databaseTester;
    Application app;
   @Inject
    private  JPAApi jpaApi;

   @Inject
    private InventoryService service;
   	
   
    
  

    // Data needed for create the fake
    private static HashMap<String, String> settings() {
        HashMap<String, String> settings = new HashMap<String, String>();
        settings.put("db.default.url", "jdbc:mysql://localhost:3306/mysql");
        settings.put("db.default.username", "root");
        settings.put("db.default.password", "%password%");
        settings.put("db.default.jndiName", "DefaultDS");
        settings.put("jpa.default", "mySqlPersistenceUnit");
        return(settings);
    }

   

    @Before
    public void initializeData() throws Exception {
        app = Helpers.fakeApplication(settings());
        
        jpaApi = app.injector().instanceOf(JPAApi.class);
        service = app.injector().instanceOf(InventoryService.class);

        databaseTester = new JndiDatabaseTester("DefaultDS");
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/Inventory_dataset_1.xml"));
        databaseTester.setDataSet(initialDataSet);
        databaseTester.onSetup();
        
    }

    @After
    public void closeDB() throws Exception {
        databaseTester.onTearDown();
    }

    @Test
    public void testFindInventory() {
        running (app, () -> {
          jpaApi.withTransaction(() -> {
                List<Inventory> eList = service.find("Apple",5);
                assertEquals(eList.get(0).name, "Apple");
                assertEquals(eList.get(0).quantity, new Integer(5));
                
                eList = service.find("Banana");
                assertEquals(eList.get(0).name, "Banana");
                assertEquals(eList.get(0).quantity, new Integer(5));
                assertEquals(5,5);
           });
        });
    }

 

    @Test
    public void testFindAllInventorys() {
        running (app, () -> {
            jpaApi.withTransaction(() -> {
                List<Inventory> e = service.all();
                long count = service.count();
                assertEquals(count, 4);

            });
        });
    }


    @Test
    public void testCreateInventory() {
        running (app, () -> {
            jpaApi.withTransaction(() -> {
                Inventory create = new Inventory("New test",5);
                Inventory e = service.create(create);
                assertEquals(e, create);
            });
        });
    }

    @Test
    public void testUpdateInventory() {
        running (app, () -> {
            jpaApi.withTransaction(() -> {
                Inventory create = new Inventory("New Fruit",4);
                Inventory e = service.create(create);
                e.name = "Update Fruit";
                Inventory update = service.update(e);
                assertEquals(update.name, "Update Fruit");
            });
        });
    }
    
 
    
}
