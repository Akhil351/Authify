package com.akhil.authify.service.impl;

import com.akhil.authify.exception.ApiException;
import com.akhil.authify.model.UserEntity;
import com.akhil.authify.repo.UserRepo;
import com.akhil.authify.request.UserRequest;
import com.akhil.authify.response.UserResponse;
import com.akhil.authify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        userRepo.findByEmail(userRequest.getEmail()).ifPresent(userEntity -> {throw new ApiException("Email already exists");
        });
        UserEntity userEntity=convertToUserEntity(userRequest);
        userEntity=userRepo.save(userEntity);
        return convertToUserResponse(userEntity);
    }

    @Override
    public UserResponse getProfile(String email) {
        UserEntity userEntity=userRepo.findByEmail(email).orElseThrow(()->new ApiException("Email not found"));
        return convertToUserResponse(userEntity);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity userEntity=userRepo.findByEmail(email).orElseThrow(()->new ApiException("Email not found"));
        // Generate 6 digits otp
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        // calculate expiry time (current time+ 10 minutes in milliseconds)
        long expiryTime=System.currentTimeMillis()+(10*60*1000);
        // update the user

        userEntity.setResetOtp(otp);
        userEntity.setResetOtpExpiryAt(expiryTime);

        userRepo.save(userEntity);

        try{
          emailService.sendResetOtpEmail(userEntity.getEmail(),userEntity.getResetOtp());
        } catch(Exception ex){
            throw new ApiException("unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity userEntity=userRepo.findByEmail(email).orElseThrow(()->new ApiException("Email not found"));
        if(userEntity.getResetOtp()==null || !userEntity.getResetOtp().equals(otp)){
            throw new ApiException("Invalid otp");
        }
        if(userEntity.getResetOtpExpiryAt()<System.currentTimeMillis()){
            throw new ApiException("Invalid otp expiry");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setResetOtp(null);
        userEntity.setResetOtpExpiryAt(0L);
        userRepo.save(userEntity);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity userEntity=userRepo.findByEmail(email).orElseThrow(()->new ApiException("Email not found"));
        if(userEntity.getIsAccountVerified()!=null && userEntity.getIsAccountVerified()){
            return;
        }
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        long expiryTime=System.currentTimeMillis()+(10*60*1000);


        userEntity.setVerifyOtp(otp);
        userEntity.setVerifyOtpExpiryAt(expiryTime);

        userRepo.save(userEntity);

        try{
            emailService.sendOtpEmail(userEntity.getEmail(),userEntity.getVerifyOtp());
        } catch(Exception ex){
            throw new ApiException("unable to send email");
        }


    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity userEntity=userRepo.findByEmail(email).orElseThrow(()->new ApiException("Email not found"));
        if(userEntity.getVerifyOtp()==null || !userEntity.getVerifyOtp().equals(otp)){
            throw new ApiException("Invalid otp");
        }
        if(userEntity.getVerifyOtpExpiryAt()<System.currentTimeMillis()){
            throw new ApiException("Invalid otp expiry");
        }

        userEntity.setIsAccountVerified(true);
        userEntity.setVerifyOtp(null);
        userEntity.setVerifyOtpExpiryAt(0L);
        userRepo.save(userEntity);

    }

    @Override
    public String getLoggedInUserId(String email) {
        UserEntity userEntity=userRepo.findByEmail(email).orElseThrow(()->new ApiException("Email not found"));
        return userEntity.getUserId();
    }

    private UserResponse convertToUserResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .userId(userEntity.getUserId())
                .isAccountVerified(userEntity.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(UserRequest userRequest) {
       return  UserEntity.builder()
                .email(userRequest.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(userRequest.getName())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpiryAt(0L)
                .resetOtp(null)
                .resetOtpExpiryAt(0L)
                .build();

    }
}
