package crossword.web;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * Filter that adds standard headers to every response.
 */
public class HeadersFilter extends Filter {
    
    private final Headers headers = new Headers();
    
    /**
     * Make a headers filter that adds the given headers.
     * @param headers standard headers to add to every response
     */
    public HeadersFilter(Map<String, String> headers) {
        headers.forEach(this.headers::add);
    }
    
    @Override public String description() { return "Add headers to all responses"; }
    
    @Override public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        exchange.getResponseHeaders().putAll(headers);
        chain.doFilter(exchange);
    }
}
