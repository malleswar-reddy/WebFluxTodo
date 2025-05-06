package com.webflux.repository;

import com.webflux.model.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Long> {
}
