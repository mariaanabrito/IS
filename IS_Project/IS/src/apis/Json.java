package apis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Json 
{
	
	
	public static String createJsonComponent(String orcId,String title, String type, String dblp, String wos, int ano, long eid, int numCit, int numCit3anos, Set<String> authors)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"ORCID\":\"").append(orcId).append("\",\n");
		sb.append("\"Title\":\"").append(title).append("\",\n");
		sb.append("\"Type\":\"").append(type.replace("-", " ")).append("\",\n");
		sb.append("\"Year\":\"").append(ano).append("\",\n");
		sb.append("\"DBLP\":\"").append(dblp).append("\",\n");
		sb.append("\"WOS\":\"").append(wos).append("\",\n");
		sb.append("\"EID\":\"").append(eid).append("\",\n");
		sb.append("\"SJR\":\"").append(-1).append("\",\n");
		if(eid == 38349047757L)
		{
			sb.append("\"Number of Citations\":\"").append(numCit).append("\",\n");
			sb.append("\"Number of Citations in the last 3 years\":\"").append(numCit3anos).append("\",\n");
			sb.append("\"Authors\":[");
			for(String a : authors)
				sb.append("\"").append(a).append("\",");
			if(authors.size() > 0)
				sb.setLength(sb.length() - 1);
			sb.append("]\n}");
		}
		else
		{
			sb.append("\"Number of Citations\":\"").append(-1).append("\",\n");
			sb.append("\"Number of Citations in the last 3 years\":\"").append(-1).append("\",\n");
			sb.append("\"Authors\":[]\n}");
		}
		
			
		
		return sb.toString();
	}
}
