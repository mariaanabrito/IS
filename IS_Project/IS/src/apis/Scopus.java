package apis;

import java.util.Set;

public class Scopus 
{
    private  long eid;
    private  String issn;
    private  Set<String> authors;
    private  int numCitacoes, numCitacoes3anos;

    public Scopus()
    {
    	eid = -1;
    	issn = "";
    	authors = null;
    	numCitacoes = numCitacoes3anos = -1;
    }
    
    public Scopus(long e, String i,Set<String> as, int nc, int nc3) 
    {
        eid = e;
        authors = as;
        issn = i;
        numCitacoes = nc;
        numCitacoes3anos = nc3;
    }
    
    
    public  long getEid() {
        return eid;
    }
    
    public  String getISSN() {
        return issn;
    }
    
    public  void setISSN(String i) {
        issn  = i;
    }
    
    public  void setEid(long eid) {
        this.eid = eid;
    }

    public  void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public  void setNumCitacoes(int numCitacoes) {
        this.numCitacoes = numCitacoes;
    }

    public  void setNumCitacoes3anos(int numCitacoes3anos) {
        this.numCitacoes3anos = numCitacoes3anos;
    }

    public  Set<String> getAuthors() {
        return authors;
    }

    public  int getNumCitacoes() {
        return numCitacoes;
    }

    public  int getNumCitacoes3anos() {
        return numCitacoes3anos;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb =  new StringBuilder();
        sb.append("eid---> ").append(eid).append("\n");
        sb.append("issn---> ").append(issn).append("\n");
        sb.append("authors:\n");
        authors.stream().forEach((a) -> {
            sb.append("->").append(a).append("\n");
        });
        sb.append("numCitacoes--->").append(numCitacoes).append("\n");
        sb.append("numCitacoes3anos--->").append(numCitacoes3anos).append("\n");
        return sb.toString();
    }
    
}