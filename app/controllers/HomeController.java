package controllers;

import play.mvc.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import models.Element;
import play.db.Database;
import play.libs.Json;

public class HomeController extends Controller {

	private static final Element defaultElement = new Element("Search again", -1, "-", "-");
	
	private Database db;
	
	@Inject
	public HomeController(Database db) {
		this.db = db;
	}
	
    public Result index() throws SQLException {
        String searchTerm = request().getQueryString("element");
        //TODO - query database
        Connection conn = db.getConnection();
        
        String SQLQuery = "Select * from PERIODIC_ELEMENTS WHERE LOWER(ELEMENT) LIKE ?";
        PreparedStatement getAnElementRow = conn.prepareStatement(SQLQuery);
        
        String queryParam = String.format("%%%s%%", searchTerm.toLowerCase());
        getAnElementRow.setString(1, queryParam);
        ResultSet theElementSet = getAnElementRow.executeQuery();
        
        //TODO - bind result to model
        Element element = (theElementSet.next()) ? mapToElement(theElementSet) : defaultElement;
        //TODO - convert model toJson
        return ok(Json.toJson(element));
    }
    
    private Element mapToElement(ResultSet theElementSet) throws SQLException {
    	String element = theElementSet.getString("ELEMENT");
    	int atomicNumber = theElementSet.getInt("ATOMIC_NUMBER");
    	String symbol = theElementSet.getString("SYMBOL");
    	String metalGroup = theElementSet.getString("METAL_GROUP");
    	return new Element(element, atomicNumber, symbol, metalGroup);
    }

}
