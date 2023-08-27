package com.example.ussd.repository;


import com.example.ussd.model.UserCore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserCore, String> {

    UserCore findByUsername(String userName);

}
