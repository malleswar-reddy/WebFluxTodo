package com.webflux.repository;

import com.webflux.exception.dto.UserDTO;
import com.webflux.model.UserAuth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserAuthRepository extends ReactiveCrudRepository<UserAuth, Long> {
    Mono<UserAuth> findByEmail(String email);

    @Query("""
        SELECT ua.user_id, ua.email, ua.password_hash, ua.email_verified, ua.created_at,
               up.first_name, up.last_name, up.role, up.picture, up.phone, up.alternate_phone, up.exp
        FROM user_auth ua
        LEFT JOIN user_profile up ON ua.user_id = up.user_id
        """)
    Flux<UserDTO> findAllWithProfile();
}
