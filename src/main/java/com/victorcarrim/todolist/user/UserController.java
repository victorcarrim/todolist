package com.victorcarrim.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = this.userRepository.findByUsername(userModel.getUsername());
        if(user != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username não disponivel");
        var passwordHash = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHash);
        var userCreated =  this.userRepository.saveAndFlush(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body("");

    }
}
