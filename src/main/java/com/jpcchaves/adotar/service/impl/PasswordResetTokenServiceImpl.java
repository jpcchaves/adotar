package com.jpcchaves.adotar.service.impl;

import com.jpcchaves.adotar.domain.entities.PasswordResetToken;
import com.jpcchaves.adotar.domain.entities.User;
import com.jpcchaves.adotar.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
public class PasswordResetTokenServiceImpl {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void createPasswordResetTokenForUser(User user,
                                                String passwordToken) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(passwordToken, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public String validatePasswordResetToken(String theToken) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(theToken);

        if (passwordResetToken.isEmpty()) {
            return "Código de verificação inválido";
        }

        if (isTokenExpired(passwordResetToken.get())) {
            return "O link de verificação expirou, reenviar link";
        }

        return "Valid";
    }

    public Optional<User> findUserByPasswordToken(String passwordToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordToken).get().getUser());
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        Calendar calendar = Calendar.getInstance();
        return ((passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0);
    }
}
