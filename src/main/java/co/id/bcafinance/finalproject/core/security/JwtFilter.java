package co.id.bcafinance.finalproject.core.security;

import co.id.bcafinance.finalproject.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.id.bcafinance.finalproject.configuration.MyHttpServletRequestWrapper;
import co.id.bcafinance.finalproject.configuration.OtherConfig;
import co.id.bcafinance.finalproject.configuration.RawHttpServletRequestWrapper;
import co.id.bcafinance.finalproject.core.Crypto;
import co.id.bcafinance.finalproject.handler.RequestCapture;
import co.id.bcafinance.finalproject.handler.XSSAttackExcception;
import co.id.bcafinance.finalproject.handler.XSSParamException;
import co.id.bcafinance.finalproject.handler.XSSResponse;
import co.id.bcafinance.finalproject.util.LoggingFile;
import co.id.bcafinance.finalproject.util.XSSValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;


@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private UserService usersService;

    @Autowired
    private ObjectMapper objectMapper;
    private String [] strExceptionArr = new String[2];
    public JwtFilter() {
        strExceptionArr[0] = "JwtFilter";
    }
    private XSSResponse xssResponse = new XSSResponse();

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");//ambil header Authorization
        authorization = authorization == null ? "": authorization;
        String token = null;
        String userName = null;
        MyHttpServletRequestWrapper requestWrapper = null;
        RawHttpServletRequestWrapper rawRequest = null;
        try{
            /**
             validasi khusus memilah request dengan content type application/json
             */
            String strContentType = request.getContentType()==null?"":request.getContentType();
            if(!strContentType.startsWith("multipart/form-data") || "".equals(strContentType)){
                rawRequest = new RawHttpServletRequestWrapper(request);
                requestWrapper = new MyHttpServletRequestWrapper(request, OtherConfig.getKataTerlarang());
                if(!XSSFilteringManual(rawRequest,request,response)){
                    /**
                     * Saat masuk sudah otomatis dari boddy nya error
                     * jadi wrapper yang mentah nya di switch ke object request agar bisa di stop prosesing nya
                     * karena kombinasi hacker untuk memasukkan script bisa di path variable saja atau req param saja , atau body saja
                     * agar program tidak terkecoh oleh kombinasi tersebut , maka sediakan 1 object request mentah nya agar bisa didapat kan script hacker tersebut
                     * dan dimasukkan ke dalam log
                     */
                    request = rawRequest;
                    xssResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    xssResponse.setMessage("XSS attack error");
                    response.getWriter().write(convertObjectToJson(xssResponse));
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    strExceptionArr[1] = "doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) "+ RequestCapture.allRequest(request);
                    LoggingFile.exceptionStringz(strExceptionArr, new XSSAttackExcception("Serangan Hacker"), OtherConfig.getFlagLoging());
                    return;
                }
                request = rawRequest;
            }

            /**
             Langkah pertama otentikasi token
             */
            if(!"".equals(authorization) && authorization.startsWith("Bearer ") && authorization.length()>7)
            {
                token = authorization.substring(7);//memotong setelah kata Bearer+spasi = 7 digit

                /**
                 *  DECRYPT TOKEN DARI FRONT END
                 */
                token = Crypto.performDecrypt(token);
                userName = jwtUtility.getUsernameFromToken(token);
                if(userName != null &&
                        SecurityContextHolder.getContext().getAuthentication()==null)
                {
                    if(jwtUtility.validateToken(token))
                    {

                        /**
                         Disini dicek ulang token ke table user apakah valid atau tidak user tersebut.
                         Karena payload di JWT base64 , orang dapat merangkai nya secara manual
                         jadi kalau pilihan nya security maka perlu diverifikasi lagi ke database informasi yang ada di JWT itu valid atau tidak
                         secara performance menurun karena ada step harus membuka koneksi ke database
                         akan tetapi lebih aman kalau divalidasi 2 kali...
                         */
                        final UserDetails userDetails = usersService.loadUserByUsername(userName);
                        final UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null,
                                        userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        catch (XSSParamException e)
        {
            xssResponse.setStatus(HttpStatus.FORBIDDEN.value());
            xssResponse.setMessage("XSS attack error");
            response.getWriter().write(convertObjectToJson(xssResponse));
            response.setStatus(HttpStatus.FORBIDDEN.value());
            strExceptionArr[1] = "doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) "+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
        }
        catch (Exception ex)
        {
            strExceptionArr[1] = "doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) "+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
        }

        try {
            filterChain.doFilter(request,response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Boolean XSSFilteringManual(RawHttpServletRequestWrapper requestWrapper,
                                       HttpServletRequest request,
                                       HttpServletResponse response
    )throws IOException,ServletException{
        try {
            String uri = requestWrapper.getRequestURI();
            String decodedURI = URLDecoder.decode(uri, "UTF-8");

            // XSS:  Path Variable Validation
            if (!XSSValidationUtils.isValidURL(decodedURI, OtherConfig.getKataTerlarang())) {
                return false;
            }
            if (!StringUtils.isEmpty(requestWrapper.getBody())) {
                // XSS :  Request Body validation
                if (!XSSValidationUtils.isValidURLPattern(requestWrapper.getBody(), OtherConfig.getKataTerlarang())) {
                    return false;
                }
            }
        } catch (XSSAttackExcception ex) {
            strExceptionArr[1] = "XSSFilteringManual(MyHttpServletRequestWrapper requestWrapper, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)"+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
            return false;
        }  catch (Exception ex) {
            strExceptionArr[1] = "XSSFilteringManual(MyHttpServletRequestWrapper requestWrapper, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)"+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
            return false;
        }

        return true;
    }
    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}