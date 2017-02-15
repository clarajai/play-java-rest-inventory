import org.junit.*;
import play.test.*;
import play.Application;
import play.mvc.*;
import static play.test.Helpers.*;
import static org.junit.Assert.*;
import play.db.jpa.*;
import java.util.List;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import java.util.HashMap;
import java.io.FileInputStream;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.ws.*;
import play.libs.Json;
import javax.inject.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class ControllerTest {

    int timeout = 20000;
    JndiDatabaseTester databaseTester;
    Application app;
    ObjectNode dataOk;
    ObjectNode dataError1;
    ObjectNode dataError2;


    public ControllerTest() {
        dataOk = Json.newObject();
        dataOk.put("name", "Towels");
        dataOk.put("quantity", new Integer(10));

        dataError1 = Json.newObject();
        dataError1.put("name", "");

        dataError2 = Json.newObject();
        dataError2.put("name", "Combs");
    }

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
    public void testFindAllInventory()  {
        running(testServer(3333, app),  ()  -> {
            
           try{
		        WSClient ws = WS.newClient(3333);                
		        WSRequest wsRequest = ws.url("http://localhost:9000/inventory");
                CompletionStage<WSResponse> stage = wsRequest.get();
                WSResponse response = stage.toCompletableFuture().get();
		        ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                JsonNode responseJson = response.asJson();
                assertTrue(responseJson.isObject());
                assertEquals(1,responseJson.get("data").get(0).get("id").asInt());
                assertEquals("Apple",responseJson.get("data").get(0).get("name").asText());
               
           }catch(InterruptedException e){
               e.printStackTrace();
           }catch(Exception e){
                e.printStackTrace();
           }
        });
    }
   
  @Test
    public void testFindInventoryByCriteria() {
        running(testServer(3333, app), () -> {
           
              try{
                    WSClient ws = WS.newClient(3333);
		            WSRequest wsRequest = ws.url("http://localhost:9000/inventory/search/banana");
                    CompletionStage<WSResponse> stage = wsRequest.get();
                    WSResponse response = stage.toCompletableFuture().get();  
			        ws.close();
                    assertEquals(OK, response.getStatus());
                    assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                    JsonNode responseJson = response.asJson();
                    assertTrue(responseJson.isObject());
                    assertEquals(2,responseJson.get("data").get(0).get("id").asInt());
                    assertEquals("Banana",responseJson.get("data").get(0).get("name").asText());
                    assertEquals(5,responseJson.get("data").get(0).get("quantity").asInt());
            
              }catch(InterruptedException e){
               e.printStackTrace();
              }catch(Exception e){
                e.printStackTrace();
              }
        });
    }


   
    @Test
    public void testFindInventoryNotFound() {
        running(testServer(3333, app), () -> {
           
                try{
			        WSClient ws = WS.newClient(3333);                    
			        WSRequest wsRequest = ws.url("http://localhost:9000/inventory/search/brush/15");
                    CompletionStage<WSResponse> stage = wsRequest.get();
                    WSResponse response = stage.toCompletableFuture().get();    
		            ws.close();
                    assertEquals(NOT_FOUND, response.getStatus());
                    assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                    JsonNode responseJson = response.asJson();
                    assertTrue(responseJson.isObject());
                    assertEquals("Record not found!!", responseJson.get("error").asText());
            
                }catch(InterruptedException e){
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }
        });
    }


    @Test
    public void testCreateInventory() {
        running(testServer(3333, app), () -> {
           
              try{
                WSClient ws = WS.newClient(3333);                
                WSRequest wsRequest = ws.url("http://localhost:9000/inventory/add");
                CompletionStage<WSResponse> stage = wsRequest.post(dataOk);
                WSResponse response = stage.toCompletableFuture().get();      
                ws.close();
                assertEquals(CREATED, response.getStatus());
                assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                JsonNode responseJson = response.asJson();
                assertTrue(responseJson.isObject());
                assertEquals(10,responseJson.get("quantity").asInt());
                assertEquals("Towels",responseJson.get("name").asText());
            
            }catch(InterruptedException e){
               e.printStackTrace();
           }catch(Exception e){
                e.printStackTrace();
           }
        });
    }

    @Test
    public void testCreateInventoryBadRequest1() {
        running(testServer(3333, app), () -> {
            
             try{
                WSClient ws = WS.newClient(3333);
                WSRequest wsRequest = ws.url("http://localhost:9000/inventory/add");
                CompletionStage<WSResponse> stage = wsRequest.post(dataError1);
                WSResponse response = stage.toCompletableFuture().get();        
                ws.close();
                assertEquals(BAD_REQUEST, response.getStatus());
                assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                JsonNode responseJson = response.asJson();
                assertTrue(responseJson.isObject());
                assertEquals("This field is required",responseJson.get("name").get(0).asText());
            }catch(InterruptedException e){
               e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
           }
        });
    }

   

    @Test
    public void testUpdateInventory() {
        running(testServer(3333, app), () -> {
            dataOk.put("id", 1);
            dataOk.put("name", "Paper");
            
            try{
                WSClient ws = WS.newClient(3333);
                WSRequest wsRequest = ws.url("http://localhost:9000/inventory/update");
                CompletionStage<WSResponse> stage = wsRequest.post(dataOk);
                WSResponse response = stage.toCompletableFuture().get();     
                ws.close();   
                assertEquals(OK, response.getStatus());
                assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                JsonNode responseJson = response.asJson();
                assertTrue(responseJson.isObject());
                assertEquals(10,responseJson.get("quantity").asInt());
                assertEquals("Paper",responseJson.get("name").asText());
            
             }catch(InterruptedException e){
               e.printStackTrace();
             }catch(Exception e){
                e.printStackTrace();
             }
        });
    }

    @Test
    public void testUpdateInventoryBadRequest1() {
        running(testServer(3333, app), () -> {
           
               try{
                    WSClient ws = WS.newClient(3333);
                    WSRequest wsRequest = ws.url("http://localhost:9000/inventory/update");
                    CompletionStage<WSResponse> stage = wsRequest.post(dataError2.put("id", 100));
                    WSResponse response = stage.toCompletableFuture().get();        
                    ws.close();
                    assertEquals(BAD_REQUEST, response.getStatus());
                    assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));

                    JsonNode responseJson = response.asJson();
                    assertTrue(responseJson.isObject());
                    assertEquals("Record not found!!",responseJson.get("error").asText());
            }catch(InterruptedException e){
               e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
           }
        });
    }



   
}
