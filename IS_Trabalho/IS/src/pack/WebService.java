package pack;


import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;


import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


import static apis.ORCData.retrieveDataFromORCID;
import static apis.ORCData.retrieveDataFromORCIDDB;
import static apis.XMLParser.retrieveOrcIdsFromXML;

import java.util.List;

@Path("webservice")
public class WebService {

    @Context
    private UriInfo context;

    
    public WebService() {
        
    }

   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        return "{}";
    }
  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orcid/{varX}")
    public String getDataOrcId(@PathParam("varX") String orcId) 
    {	
    	String json = "";
    	String comp = retrieveDataFromORCID(orcId);
    	
    	if(comp.equals(""))
    	{
    		json = "[]";
    	}
    	else
    	{
    		json = "[";
    		comp = comp.substring(0, comp.length() - 1);
    		json = json + comp + "]";
    	}
    	
    	return json;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("orcids")
    public String orcIds(String xml)
    {
    	long startTime = System.nanoTime();    
    	
    	
    	String json = "[";
    	List<String> orcIds =  retrieveOrcIdsFromXML(xml);
    	orcIds.forEach(x->System.out.println(x));
    	for(String orcId : orcIds)
    		json = json + retrieveDataFromORCID(orcId);
    	if("[".equals(json))
    		json = "[]";
    	else
    	{
    		json = json.substring(0, json.length() - 1);
    		json = json + "]";
    	}
    	//System.out.println(json);
    	long estimatedTime = System.nanoTime() - startTime;
    	System.out.println("Tempo decorrido:" + estimatedTime);
    	return json;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("orcidsdb")
    public String orcIdsDB(String xml)
    {
    	long startTime = System.nanoTime(); 
    	
    	String json = "[";
    	List<String> orcIds =  retrieveOrcIdsFromXML(xml);
    	orcIds.forEach(x->System.out.println(x));
    	for(String orcId : orcIds)
    		json = json + retrieveDataFromORCIDDB(orcId);
    	if("[".equals(json))
    		json = "[]";
    	else
    	{
    		json = json.substring(0, json.length() - 1);
    		json = json + "]";
    	}
    	//System.out.println(json);
    	long estimatedTime = System.nanoTime() - startTime;
    	System.out.println("Tempo decorrido:" + estimatedTime);
    	return json;
    }

}
