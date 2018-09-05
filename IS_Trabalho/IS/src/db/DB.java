package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;

import apis.Scopus;
import apis.Work;

import static apis.Json.createJsonComponent;

public class DB 
{
	
	public static void insertAllDataBase(Map<Work, Scopus> worksScopus)
	{
		Connection conn =  null;
		try
		{
			conn = Connect.connect();
			//ir à bd buscar os que já lá estão
			
			
			Set<Work> works = worksScopus.keySet();
			List<Scopus> scopus = new ArrayList<Scopus>(worksScopus.values());
			for(Scopus sco: scopus)
				if(sco != null)
					insereScopus(sco, conn);
			
			int numWorks = getNumberOfWorksDB();
			
			insereWorksDB(works, numWorks, conn);
			
		}
		catch(SQLException ex)
		{
			//ex.printStackTrace();
		}
	}
	
	private static void insereScopus(Scopus scopus, Connection conn)
	{
		long eid = scopus.getEid();
		float sjr = -1;
		int numCit = scopus.getNumCitacoes();
		int numCit3 = scopus.getNumCitacoes3anos();
		Set<String> authors = scopus.getAuthors();
		PreparedStatement sco;
		try
		{
			for(String au : authors)
			{
				sco = conn.prepareStatement("insert ignore into scopus values (?,?,?,?,?)");
				sco.setLong(1, eid);
				sco.setFloat(2, sjr);
				sco.setInt(3, numCit);
				sco.setInt(4, numCit3);
				sco.setString(5, au);
				sco.executeUpdate();
			}
		} catch (SQLException ex) {
            //ex.printStackTrace();
        }
	}
	
	private static void insereWorksDB(Set<Work> works, int numWorks, Connection conn)
    {
        int i = 0;
        boolean exists = false;
        List<Integer> workids;
        String orcid = "nulo";
        for(Work work : works)
        {
        	workids = new ArrayList<>();
        	exists= false;
        	
	        	PreparedStatement ps;
				try 
				{
					if(work.getOrcID() != orcid)
		        	{
						orcid = work.getOrcID();
						ps = conn.prepareStatement("select count(*) from work inner join author_has_work on workid= work_idWorks inner join author on author_orcId = orcid where author.orcid = ?");
						ps.setString(1, work.getOrcID());
			        	ResultSet rs = ps.executeQuery();
			        	
			        	if(rs.next())
			                if(rs.getInt("count(*)") != 0)
			                	exists = true;
		        	}
				
			
	        	if(!exists)
	        	{
	        		//System.out.println("Não existe");
		        	insereAutor(work.getAuthor(), work.getOrcID(), conn);
		            //insere na work
		            insereWork(work, conn);
		            
		            //insere na WORK_HAS_AUTHO         
		            insereWorkAuthor(numWorks + i++, work.getOrcID() , conn);
	        	}
	        	else
	        	{
	        		orcid = "nulidade";
	        		//System.out.println("Existe");
	        		ps = conn.prepareStatement("select * from work inner join author_has_work on workid= work_idWorks inner join author on author_orcId = orcid where author.orcid = ?");
					ps.setString(1, work.getOrcID());
		        	ResultSet rs = ps.executeQuery();
		        	
		        	while(rs.next())
		        	{
		        		if( !checkIfWorkIsNowVoid(works, rs.getString(2), rs.getInt(3), rs.getString(4)))
		        			workids.add(rs.getInt(1));
		        	}
		        	
		        	//se o workids tiver cenas, há que remover essas da base de dados
		        	for(Integer id : workids)
		        	{
		        		ps = conn.prepareStatement("delete from work where workid = ?");
		        		ps.setInt(1, id);
		        		ps = conn.prepareStatement("delete from author_has_work where work_idWorks = ?");
		        		ps.setInt(1, id);
		        	}
		        	
	        	}
			} catch (SQLException e) {
				//e.printStackTrace();
			}
			
        
        }
    }
	
	private static boolean checkIfWorkIsNowVoid(Set<Work> works, String titulo, int ano, String publicacao)
	{
		boolean workState = false;
		for(Work work : works)
			if(work.getAno() == ano && work.getTitle().equals(titulo) && work.getType().equals(publicacao))
			{
				workState = true;
				break;
			}
		
		return workState;
	}
	
	private static void insereAutor(String author, String orcId, Connection conn)
    {
        PreparedStatement get, ps;
        try 
        {
        	//verificar se existe e se o nome é ""
        	get = conn.prepareStatement("select * from author where orcid = ?");
        	get.setString(1, orcId);
        	ResultSet rs = get.executeQuery();
        	if(rs.next())
        	{
        		if(!"".equals(author))
        		{
        			ps = conn.prepareStatement("update author set nome = ? where orcId=?");
        			ps.setString(1, author);
        			ps.setString(2, orcId);
		            ps.executeUpdate();
        		}
        	}
        	else
        	{
	            ps = conn.prepareStatement("insert ignore into author values(?,?)");
	            ps.setString(1, orcId);
	            ps.setString(2, author);
	            ps.executeUpdate();
        	}
            
        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
        
    }
    
    private static int getNumberOfWorksDB()
    {
        int num = -1;
        try {
            Connection conn = Connect.connect();
            PreparedStatement ps =  conn.prepareStatement("select count(*) from work");
            ResultSet rs = ps.executeQuery();
            
            if(rs.next())
                num = rs.getInt("count(*)");
            
        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
        
        return num + 1;
    }
    
    
    
    private static void insereWork(Work work, Connection conn)
    {
        
        try {
            String type = work.getType();
            int ano = work.getAno();
            long eid = work.getEid();
            String wos = work.getWos();
            String dblp = work.getDblp();
            
            String insert = "insert into work(titulo, ano, local_publicacao, eId, WOS, DBLP) values(?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setString(1, work.getTitle());
            
            if(ano != -1)
                ps.setInt(2, ano);
            else
                ps.setObject(2, null);
            
            if(!"".equals(type))
                ps.setString(3, type);
            else
                ps.setString(3, null);
            
            if(eid != -1)
                ps.setLong(4, eid);
            else
                ps.setObject(4, null);
            
            if(!"".equals(wos))
                ps.setString(5, wos);
            else
                ps.setString(5, null);
            
            if(!"".equals(dblp))
                ps.setString(6, dblp);
            else
                ps.setString(6, null);
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
    }
    
    private static void insereWorkAuthor(int workId, String orcId, Connection conn )
    {
        try 
        {
            PreparedStatement ps =  conn.prepareStatement("insert into author_has_work values(?,?)");
            ps.setString(1, orcId);
            ps.setInt(2, workId);
            ps.executeUpdate();
           

        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
    }
    //orcId, w.getTitle(), w.getType(), w.getDblp(), w.getWos(), w.getAno(), s.getEid(), s.getNumCitacoes(), s.getNumCitacoes3anos(), s.getAuthors());
    public static String dataToJson(String orcId)
    {
    	String jsonComp = "";
    	String json = "";
    	Connection conn = null;
    	String title = "";
    	String type = "";
    	String dblp = "";
    	String wos = "";
    	int ano = -1;
    	long eid = -1;
    	int numCitacoes = -1;
    	int numCitacoes3anos = -1;
    	Set<String> auths = new HashSet<>();
        try 
        {
        	conn = Connect.connect();
        	
            PreparedStatement ps =  
            		conn.prepareStatement("select titulo, local_publicacao, dblp, wos, ano, eid from author inner join author_has_work on orcId = author_orcid inner join work on work_idworks = workid");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
            	title = rs.getString(1);
            	type = rs.getString(2);
            	dblp = rs.getString(3);
            	wos = rs.getString(4);
            	ano = rs.getInt(5);
            	eid = rs.getLong(6);
            	
            	if(eid != -1)
            	{
            		PreparedStatement eidData = conn.prepareStatement("select num_citacoes, num_citacoes_3anos from scopus where eid = ?");
                	eidData.setLong(1,eid);
	            	ResultSet dataEid = eidData.executeQuery();
	            	if(dataEid.next())
	            	{
	            		 numCitacoes = dataEid.getInt(1);
	            		 numCitacoes3anos = dataEid.getInt(2);
	            	}
	            	
		            PreparedStatement authors =
		            		conn.prepareStatement("select author from scopus where eid = ?");
		            authors.setLong(1,eid);
		            ResultSet aus = authors.executeQuery();
		            while(aus.next())
		            {
		            	auths.add(aus.getString(1));
		            }
		            
		            jsonComp = createJsonComponent(orcId, title, type, dblp, wos, ano, eid, numCitacoes, numCitacoes3anos, auths);
            	}
            	else
            		jsonComp = createJsonComponent(orcId, title, type, dblp, wos, ano, -1, -1, -1, new HashSet<>());
            		
	            
	            json = json + jsonComp + ",";
            }
            

        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
        
        return json;
    }    
    
}
