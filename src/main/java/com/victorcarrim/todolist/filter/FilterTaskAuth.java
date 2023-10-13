package com.victorcarrim.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.victorcarrim.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPah = request.getServletPath();
         if(servletPah.startsWith("/tasks/")){
             // Pegar a autenticação (user and password)
             var auth = request.getHeader("Authorization");
             var authEncoded = auth.substring("Basic".length()).trim();

             byte[] authDecode = Base64.getDecoder().decode(authEncoded);
             var authString = new String(authDecode);
             String[] credentials = authString.split(":");
             String username = credentials[0];
             String password = credentials[1];
             // Valida se o usuário existe
             var user = this.userRepository.findByUsername(username);
             if(user == null){
                 response.sendError(401);
             } else {
                 // Valida senha
                 var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                 if(passwordVerify.verified){
                     request.setAttribute("idUser", user.getId());
                     filterChain.doFilter(request, response);
                 }else {
                     response.sendError(401);
                 }
                 // Se tudo ok, segue viagem. Se não, informa que não tem permissão
             }
         } else{
             filterChain.doFilter(request, response);
         }


    }
}
