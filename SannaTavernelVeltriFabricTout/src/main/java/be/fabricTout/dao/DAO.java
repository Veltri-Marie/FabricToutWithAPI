package be.fabricTout.dao;

import java.sql.Connection;
import java.util.List;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.*;

import javax.servlet.ServletContext;

public abstract class DAO<T> {
    private WebResource resource;

    public DAO(ServletContext context) {
    	ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        String baseUrl = context.getInitParameter("api.baseUrl");
        resource = client.resource(getBaseURI(baseUrl));
    }
    
    public WebResource getResource() {
        return resource;
    }

    private static URI getBaseURI(String baseUrl) {
        return UriBuilder.fromUri(baseUrl).build();
    }

    public abstract boolean createDAO(T obj); 
    public abstract boolean deleteDAO(T obj);
    public abstract boolean updateDAO(T obj);
    public abstract T findDAO(int id);
    public abstract List<T> findAllDAO(); 
}
