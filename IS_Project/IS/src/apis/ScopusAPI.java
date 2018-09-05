package apis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.net.URL;


import static apis.XMLParser.parserXMLSecondAPI;

public class ScopusAPI 
{
	private static String apiKey = "8aa83e2111104976fd449a651aca9c37";
	
	public static Map<Work, Scopus> retrieveData2ndAPI(List<Work> works)
	{
		Map<Work, Scopus> ws = new HashMap<>();
		try 
		{
			URL oracle;
			URLConnection yc;
			BufferedReader in;
			String inputLine;
			PrintWriter pw;
			for(Work work: works)
            {
				long eId = work.getEid();
                if(eId != -1 )
                {
                    //Aqui tem de se ir buscar o ficheiro ao url com o eid respetivo
                	/*
                	oracle = new URL("https://api.elsevier.com/content/abstract/citations?scopus_id="+ eId +"&apiKey=" + apiKey +"&httpAccept=application%2Fxml");
                    yc = oracle.openConnection();
                    in = new BufferedReader(new InputStreamReader(
                            yc.getInputStream()));
                   
                    pw = new PrintWriter("scopus.xml");
                    while ((inputLine = in.readLine()) != null)
                        pw.write(inputLine + "\n");
                    pw.flush();
                    pw.close();
                    in.close();
                	*/
                	
                    Scopus scopus = parserXMLSecondAPI(eId,"C://eclipse//IS//scopus.xml");
                    /*System.out.println("--------------------------------");
                    System.out.println(scopus.toString());
                    System.out.println("--------------------------------");*/
                    //aqui tem de se ir buscar o ficheiro ao url com o issn respetivo
                    if(!"".equals(scopus.getISSN()))
                    {
                    	
                    }
                    
                    ws.put(work, scopus); 
                }
                else
                	ws.put(work, null);
            }
		}
		catch (Exception ex) {
            ex.printStackTrace();
        }
		return ws;
	}
	
	
	
}
