package be.fabricTout.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter extends HttpFilter implements Filter {

    private static final long serialVersionUID = 1L;
	private FilterConfig filterConfig;

    public AuthenticationFilter() {
        super();
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        super.init(fConfig);
        this.filterConfig = fConfig;
        System.out.println("Initializing filter: " + this.filterConfig.getFilterName());
    }

    @Override
    public void destroy() {
        System.out.println("Destroying filter: " + (filterConfig != null ? filterConfig.getFilterName() : "unknown"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false); 
        boolean isLoggedIn = (session != null && session.getAttribute("idEmployee") != null);

        String path = httpRequest.getRequestURI();

        System.out.println("---------- REQUEST ---------");
        System.out.println("Path accessed: " + path);
        System.out.println("Encoding: " + request.getCharacterEncoding());
        System.out.println("Content type: " + request.getContentType());
        System.out.println("Content length: " + request.getContentLength());
        System.out.println("Remote host name: " + request.getRemoteHost());
        System.out.println("Remote host IP address: " + request.getRemoteAddr());

        if (!isLoggedIn && !isPublicPath(path)) {
            System.out.println("Unauthorized access attempt to: " + path);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/Login");
            return;
        }

        chain.doFilter(request, response);

        System.out.println("---------- RESPONSE ---------");
        System.out.println("Encoding: " + response.getCharacterEncoding());
        System.out.println("Content type: " + response.getContentType());
    }


    private boolean isPublicPath(String path) {
        return path.contains("/Login") || path.contains("/resources/");
    }
}
