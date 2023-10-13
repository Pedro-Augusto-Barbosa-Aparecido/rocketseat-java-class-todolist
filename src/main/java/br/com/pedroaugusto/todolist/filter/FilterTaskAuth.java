package br.com.pedroaugusto.todolist.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.pedroaugusto.todolist.users.IUserRepository;
import br.com.pedroaugusto.todolist.users.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
  @Autowired
  IUserRepository userRepository;

  private String[] whiteListRoutes = {"/users"};

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        var servletPath = request.getServletPath();
        Boolean isWhiteListRoute = Arrays.asList(this.whiteListRoutes).stream().anyMatch(route -> servletPath.startsWith(route));
        if (isWhiteListRoute) {
          filterChain.doFilter(request, response);
          return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
          response.sendError(401);
          return;
        }

        String authEncoded = authorization.substring("Basic".length()).trim();

        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
        String authString = new String(authDecoded);

        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];

        UserModel user = this.userRepository.findByUsername(username);
        if (user == null) {
          response.sendError(401);
          return;
        }

        var result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!result.verified) {
          response.sendError(401);
          return;
        }

        request.setAttribute("userId", user.getId());
        filterChain.doFilter(request, response);        
  }
}
