package co.id.bcafinance.finalproject.controller;

import co.id.bcafinance.finalproject.dto.auth.*;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth0")
public class AuthController {

    @Autowired
    UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * @desc    Register akun user
     * @route   POST /api/auth/regis
     * @access  Public
     */

    @PostMapping("/regis")
    public ResponseEntity<Object> doRegis(@Valid @RequestBody RegisterDTO regisDTO, HttpServletRequest request) {
        User user = modelMapper.map(regisDTO, new TypeToken<User>() {}.getType());

        return userService.checkRegis(user,request);
    }

    /**
     * @desc    Login akun user
     * @route   POST /api/auth/login
     * @access  Public
     */

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        User user = modelMapper.map(loginDTO,new TypeToken<User>(){}.getType());
        return userService.doLogin(user,request);
    }

}

