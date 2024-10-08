package efub.gift_u.domain.user.repository;

import efub.gift_u.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<Object> findByNickname(String nickname);
    User findByUserId(Long userId);

}
