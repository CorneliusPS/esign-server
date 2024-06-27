package co.id.bcafinance.finalproject.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;



public class RegisterDTO {
    @NotNull(message = "Email Tidak Boleh NULL")
    @NotBlank(message = "Email Tidak Boleh Blank")
    @NotEmpty(message = "Email Tidak Boleh Kosong")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Format email tidak valid")
    private String email;

    @NotNull(message = "Nama Lengkap Tidak Boleh NULL")
    @NotBlank(message = "Nama Lengkap Tidak Boleh Blank")
    @NotEmpty(message = "Nama Lengkap Tidak Boleh Kosong")
    private String fullName;

    @NotNull(message = "Username Tidak Boleh NULL")
    @NotBlank(message = "Username Tidak Boleh Blank")
    @NotEmpty(message = "Username Tidak Boleh Kosong")
//    @Pattern(regexp = "^[a-z]{7,15}$", message = "Username harus terdiri dari 7-15 huruf kecil saja")
    private String username;

    @NotNull(message = "Password Tidak Boleh NULL")
    @NotBlank(message = "Password Tidak Boleh Blank")
    @NotEmpty(message = "Password Tidak Boleh Kosong")
//    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[_#\\-$])(?!.*?[^A-Za-z0-9_#\\-$]).{8,}$", message = "Password harus memiliki minimal satu huruf besar, huruf kecil, angka, dan hanya satu digit spesial karakter (_ \"Underscore\", - \"Hyphen\", # \"Hash\", atau $ \"Dollar\")")
    private String password;

    private String checkPassword;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }
}


