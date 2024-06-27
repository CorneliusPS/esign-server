package co.id.bcafinance.finalproject.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class OtpDto {


    @NotNull(message = "OTP Tidak Boleh NULL")
    @NotBlank(message = "OTP Tidak Boleh Blank")
    @NotEmpty(message = "OTP Tidak Boleh Kosong")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}


