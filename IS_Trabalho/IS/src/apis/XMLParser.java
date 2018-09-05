package apis;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class XMLParser 
{
	public static List<Work> parserXMLFirstAPI(String orcId, String xml/*, int numWorks*/)
    {
        List<Work> works = new ArrayList<>();
        Work work;
        try 
        {
             
            File inputFile = new File(xml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("work:work-summary");
            
            for (int temp = 0; temp < nList.getLength(); temp++) 
            {
                work = new Work();
                Node nNode = nList.item(temp);
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    
                    if(eElement.getElementsByTagName("common:title").getLength() != 0)
                        work.setTitle(eElement.getElementsByTagName("common:title").item(0).getTextContent());
                    else
                        work.setTitle("");
                    
                    if(eElement.getElementsByTagName("work:type").getLength() != 0)
                        work.setType(eElement.getElementsByTagName("work:type").item(0).getTextContent());
                    else
                        work.setType("");
                    
                    if(eElement.getElementsByTagName("common:year").getLength() != 0)
                        work.setAno(Integer.parseInt(eElement.getElementsByTagName("common:year").item(0).getTextContent()));
                    else
                        work.setAno(-1);
                    
                    if(eElement.getElementsByTagName("common:path").getLength() != 0)
                    {
                        String path = eElement.getElementsByTagName("common:path").item(0).getTextContent();
                        if(orcId.equals(path))
                            work.setAuthor(eElement.getElementsByTagName("common:source-name").item(0).getTextContent());
                        else
                        	work.setAuthor("");
                    }
                    
                    NodeList nl = eElement.getElementsByTagName("common:external-ids");

                    for (int tem = 0; tem < nl.getLength(); tem++) 
                    {
                        Node node = nl.item(tem);

                        if (node.getNodeType() == Node.ELEMENT_NODE) 
                        {
                            Element element = (Element) node;
                            
                            if(element.getElementsByTagName("common:external-id-type").item(0).getTextContent().equals("eid")) 
                            {
                                String[] comps = element.getElementsByTagName("common:external-id-value").item(0).getTextContent().split("-");
                                work.setEid(Long.parseLong(comps[2]));
                            }
                            else
                                work.setEid(-1);
                            
                            if(element.getElementsByTagName("common:external-id-type").item(0).getTextContent().equals("wosuid"))
                            {
                                String[] comps = element.getElementsByTagName("common:external-id-value").item(0).getTextContent().split(":");
                                work.setWos(comps[1]);
                            }
                            else
                                work.setWos("");
                            
                            if(element.getElementsByTagName("common:external-id-type").item(0).getTextContent().equals("other-id"))
                            {
                            	String aux[] = element.getElementsByTagName("common:external-id-value").item(0).getTextContent().split(":"); 
                                work.setDblp(aux[1]);
                            }
                            else
                                work.setDblp("");
                            
                            work.setOrcID(orcId);
                        }
                    }
                    
                    
                }
                
                works.add(work);
                
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return works;
    }
	
	
	public static Scopus parserXMLSecondAPI(long eId, String xml)
    {
        Scopus scopus = null;
        Set<String> authors;
        try 
        {
            scopus = new Scopus();
            authors = new HashSet<>();
            
            File inputFile = new File(xml);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("abstract-citations-response");
            
            for (int temp = 0; temp < nList.getLength(); temp++) 
            {
                Node nNode = nList.item(temp);
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    
                    NodeList names = eElement.getElementsByTagName("index-name");
                    for (int i = 0; i < names.getLength(); i++) 
                        authors.add(names.item(i).getTextContent());

                    
                    if(eElement.getElementsByTagName("grandTotal").getLength() != 0)
                        scopus.setNumCitacoes(Integer.parseInt(eElement.getElementsByTagName("grandTotal").item(0).getTextContent()));
                    
                    if(eElement.getElementsByTagName("rangeColumnTotal").getLength() != 0)
                        scopus.setNumCitacoes3anos(Integer.parseInt(eElement.getElementsByTagName("rangeColumnTotal").item(0).getTextContent()));
                    
                    if(eElement.getElementsByTagName("prism:issn").getLength() != 0)
                        scopus.setISSN(eElement.getElementsByTagName("prism:issn").item(0).getTextContent());
                    
                }
            }
            scopus.setAuthors(authors);
            scopus.setEid(eId);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return scopus;
    }
	
	public static List<String> retrieveOrcIdsFromXML(String xml)
	{
		List<String> orcs =  new ArrayList<>();

		try 
        {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc =  builder.parse(new InputSource(new StringReader(xml)));
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("orcids");
            
            for (int temp = 0; temp < nList.getLength(); temp++) 
            {
                Node nNode = nList.item(temp);
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    
                    NodeList names = eElement.getElementsByTagName("orcid");
                    for (int i = 0; i < names.getLength(); i++) 
                        orcs.add(names.item(i).getTextContent());  
                }
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		
		return orcs;
	}
}
