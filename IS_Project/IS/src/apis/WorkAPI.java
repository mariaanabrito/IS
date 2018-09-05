package apis;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.net.URL;
import java.util.List;


import static apis.XMLParser.parserXMLFirstAPI;

public class WorkAPI 
{
	//para teste ---> orcId="0000-0003-4121-6169"
	public static List<Work> retrieveData1stAPI(String orcId) throws Exception
	{
		List<Work> works = null;
		//try {
            URL oracle = new URL("https://pub.orcid.org/v2.1/" + orcId +"/works");
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            PrintWriter pw = new PrintWriter("C://eclipse//IS//works.xml");
            while ((inputLine = in.readLine()) != null)
                pw.write(inputLine + "\n");
            pw.flush();
            pw.close();
            in.close();
            
            
            works = parserXMLFirstAPI(orcId,"C://eclipse//IS//works.xml"/*, getNumberOfWorksDB()*/);
		//} 
		//catch (Exception ex) {
          //  ex.printStackTrace();
            //System.out.println("entrei aqui");
        //}
		return works;
	}
	
	
	
}
