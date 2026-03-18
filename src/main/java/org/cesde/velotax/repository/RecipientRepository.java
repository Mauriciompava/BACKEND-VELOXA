package org.cesde.velotax.repository;

import org.cesde.velotax.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    List<Recipient> findByUserId(Long userId);
}
