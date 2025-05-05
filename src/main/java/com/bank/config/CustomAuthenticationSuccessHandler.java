package com.bank.config;

import com.bank.model.ActivityLog;
import com.bank.model.User;
import com.bank.repository.ActivityLogRepository;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow();
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction("Вошел в систему");
        log.setDate(LocalDateTime.now());
        activityLogRepository.save(log);

        response.sendRedirect(request.getContextPath() + "/home");
    }
}
