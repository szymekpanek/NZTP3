package panek.szymon.ztp.lab3.ztp_lab3.infrastructure.db;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import panek.szymon.ztp.lab3.ztp_lab3.domain.Cart;
import panek.szymon.ztp.lab3.ztp_lab3.domain.CartRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MongoCartRepository extends MongoRepository<Cart, String>, CartRepository {
    List<Cart> findAllByLastModifiedBefore(LocalDateTime dateTime);
}