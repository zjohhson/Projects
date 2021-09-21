package crossword.web;

import java.io.IOException;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

/**
 * Logging filter that reports exceptions.
 */
public class ExceptionsFilter extends Filter {
    
    @Override public String description() { return "Log exceptions"; }
    
    @Override public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        try {
            chain.doFilter(exchange);
        } catch (IOException | RuntimeException e) {
            System.err.print(" !! ");
            e.printStackTrace();
            throw e; // after logging, let the exception continue
        }
    }
}
