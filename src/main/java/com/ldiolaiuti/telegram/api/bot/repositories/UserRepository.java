package com.ldiolaiuti.telegram.api.bot.repositories;

import com.ldiolaiuti.telegram.api.bot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 15/04/23 by ldiolaiuti
 * <p>
 * JPA repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
