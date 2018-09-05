package pack;


import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("pack")
public class MyRESTServices extends ResourceConfig {

    public MyRESTServices()
    {
    	packages("pack");
    }

}
