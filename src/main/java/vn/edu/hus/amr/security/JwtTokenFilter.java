package vn.edu.hus.amr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.edu.hus.amr.dto.ResponseDTO;
import vn.edu.hus.amr.exception.CustomException;
import vn.edu.hus.amr.util.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException ex) {
            SecurityContextHolder.clearContext();
            ResponseDTO responseDTO = new ResponseDTO(Constants.RESPONSE_STATUS.UN_AUTHENTICATE,
                    Constants.STATUS_CODE.UN_AUTHENTICATE, "Un authenticate", null);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responseDTO);

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);

            return;
        }

        filterChain.doFilter(request, response);
    }
}
