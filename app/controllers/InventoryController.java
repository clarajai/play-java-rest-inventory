package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.libs.Json;
import play.libs.Json.*;
import play.data.FormFactory;
import play.data.Form;
import play.db.jpa.*;

import models.Inventory;
import services.InventoryService;
import views.html.*;
import javax.inject.*;

import util.Util;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class InventoryController extends Controller {
    
	@Inject FormFactory formFactory;

	private InventoryService service;

	@Inject
	public InventoryController(InventoryService service){
		this.service = service;
	}


    /**
     * Add the content-type json to response
     *
     * @param Result httpResponse
     *
     * @return Result
     */
    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=UTF-8");
        return httpResponse;
    }


    /**
     * Get the index page
     *
     * @return Result
     */
    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    /**
     * Get the Inventorys
     *
     * 
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list() {

        List models = service.all();
        Long count = service.count();

        ObjectNode result = Json.newObject();
        result.put("data", Json.toJson(models));
        result.put("total", count);
        
        return jsonResult(ok(result));
    }


    /**
     * Get Inventorys by name and quantity
     *
     * @param String name
     * @param Integer quantity
     *
     * @return Result
     */
    @Transactional(readOnly = true)
        public Result searchByCriteria(String name, String strQuantity) {

	    ObjectNode result = null;
	    Integer quantity = null;
	    if(strQuantity != null) 
	    {
	        if(Util.isPositiveInteger(strQuantity)){
		        quantity = Integer.parseInt(strQuantity);
		 
	        }
	        else{
		        result = Json.newObject();
	            result.put("error","Quantity should be a positive number");
	            return jsonResult(badRequest(result));
	        }
		
	    }
	    List<Inventory> models = service.find(name, quantity);
	
	    if (models != null && models.size() > 0) {
            result = Json.newObject();
            result.put("data", Json.toJson(models));
            return jsonResult(ok(result));
        }
        
        result = Json.newObject();
        result.put("error", "Record not found!!");
    return jsonResult(notFound(result));
	    
    }
    
   

    /**
     * Create an Inventory with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result create() {
        Form<Inventory> inventory = formFactory.form(Inventory.class).bindFromRequest();
        ObjectNode result;
	    if (inventory.hasErrors()) {
            return jsonResult(badRequest(inventory.errorsAsJson()));
        }
	    if(inventory.get().id != null){
		    result = Json.newObject();
	        result.put("error","ID field should not be included while adding a new product");
	     return jsonResult(badRequest(result));
	    }
        
        List<Inventory> models = service.getProductDetails(inventory.get());
        if( models != null && models.size() > 0 ){
		    result = Json.newObject();
	        result.put("error","Product already exists");
	        return jsonResult(badRequest(result));
	    }
	    Inventory newInventory = service.create(inventory.get());
    return jsonResult(created(Json.toJson(newInventory)));
    }

    /**
     * Update an Inventory with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result update() {
        Form<Inventory> inventory = formFactory.form(Inventory.class).bindFromRequest();
	    ObjectNode result;
        if (inventory.hasErrors()) {
            return jsonResult(badRequest(inventory.errorsAsJson()));
        }
	    
	    if(inventory.get().id == null){
		    result = Json.newObject();
	        result.put("error","ID field is required for update");
		    return jsonResult(badRequest(result));
	    }
        
        Inventory updatedInventory = service.update(inventory.get());
	    if(updatedInventory == null)
	    {
	        result = Json.newObject();
	        result.put("error","Record not found!!");
            return jsonResult(badRequest(result));

    	}
	
        return jsonResult(ok(Json.toJson(updatedInventory)));
    }

    /**
     * Delete an Inventory by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional
    public Result delete(Integer id) {
	    ObjectNode result;

        if (service.delete(id)) {
	        result = Json.newObject();
            result.put("msg", "Record Deleted " + id);
            return jsonResult(ok(result));
        }
        result = Json.newObject();
        result.put("error", "Record not found " + id);
        return jsonResult(notFound(result));
    }
}
