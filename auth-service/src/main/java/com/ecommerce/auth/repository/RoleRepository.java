package com.ecommerce.auth.repository;

import com.ecommerce.auth.domain.entity.Role;
import com.ecommerce.auth.domain.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 * Provides CRUD operations for role management.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find a role by its name.
     * 
     * @param name The role name to search for
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(RoleType name);
    
    /**
     * Check if a role exists by name.
     * 
     * @param name The role name to check
     * @return True if role exists, false otherwise
     */
    Boolean existsByName(RoleType name);
}






