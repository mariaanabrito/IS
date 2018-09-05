package apis;

import static apis.ScopusAPI.retrieveData2ndAPI;
import static db.DB.insertAllDataBase;
import static db.DB.dataToJson;
import static apis.Json.createJsonComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ORCData 
{
	public static String retrieveDataFromORCID(String orcId)
	{
		String json;
		List<Work> works;
		try{
			works = WorkAPI.retrieveData1stAPI(orcId);
			Map<Work, Scopus> worksScopus = retrieveData2ndAPI(works);
	    	
	    	json = convertMapToJson(worksScopus, orcId);
		}
		catch (Exception e) {
			System.out.println("ORC não existe");
			json = "";
		}

    	return json;
	}
	
	public static String retrieveDataFromORCIDDB(String orcId)
	{
		String json;
		List<Work> works;
		try{
			works = WorkAPI.retrieveData1stAPI(orcId);
			Map<Work, Scopus> worksScopus = retrieveData2ndAPI(works);
	    	
			insertAllDataBase(worksScopus);
	    	json = convertDBDataToJson(orcId);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("ORC não existe");
			json = "";
		}

    	return json;
	}
	
	private static String convertDBDataToJson(String orcId)
	{
		return dataToJson(orcId);
		
	}
	
	private static String convertMapToJson(Map<Work, Scopus> ws, String orcId)
	{
		String jsonComp;
		String json = "";
		
		for(Map.Entry<Work, Scopus> entry : ws.entrySet())
		{
			Work w = entry.getKey();
			Scopus s = entry.getValue();
			if(s != null)
				jsonComp = createJsonComponent(orcId, w.getTitle(), w.getType(), w.getDblp(), w.getWos(), w.getAno(), s.getEid(), s.getNumCitacoes(), s.getNumCitacoes3anos(), s.getAuthors());
			else
				jsonComp = createJsonComponent(orcId, w.getTitle(), w.getType(), w.getDblp(), w.getWos(), w.getAno(), -1, -1, -1, new HashSet<String>());
			
			json = json + jsonComp + ",";
		}
		if(ws.isEmpty())
			json = "";
		
		return json;
	}
}
