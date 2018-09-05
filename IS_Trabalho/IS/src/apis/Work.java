package apis;

public class Work 
{
    private String orcID, author, title, type, dblp, wos;
    private int ano;
    private long eid ;
    
    public Work(){}
    
    public Work(String orcId, String author, String title, String type, int ano, long eid, String wos, String dblp) {
        this.orcID = orcId;
        this.author = author;
    	this.title = title;
        this.type = type;
        this.ano = ano;
        this.eid = eid;
        this.wos = wos;
        this.dblp = dblp;
    }

    public String getOrcID() {
		return orcID;
	}

	public void setOrcID(String orcID) {
		this.orcID = orcID;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setEid(long eid) {
        this.eid = eid;
    }

    public void setWos(String wos) {
        this.wos = wos;
    }

    public void setDblp(String dblp) {
        this.dblp = dblp;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getAno() {
        return ano;
    }

    public long getEid() {
        return eid;
    }

    public String getWos() {
        return wos;
    }

    public String getDblp() {
        return dblp;
    }
    
    
}
