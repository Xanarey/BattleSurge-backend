package com.example.battlesurgebackend.repositories;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
