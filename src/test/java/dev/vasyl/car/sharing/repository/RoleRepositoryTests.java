package dev.vasyl.car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.vasyl.car.sharing.model.Role;
import dev.vasyl.car.sharing.util.TestUserUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Verify that method returns optional with role when search by correct name")
    public void findByName_returnOptionalPresented_whenRoleWithSuchNameExist() {
        Role expectedRole = TestUserUtil.getManagerRole();
        Optional<Role> actualOptional = roleRepository.findByName(expectedRole.getName());

        assertTrue(actualOptional.isPresent());
        assertEquals(expectedRole.getId(), actualOptional.get().getId());
        assertEquals(expectedRole.getName(), actualOptional.get().getName());
        assertEquals(expectedRole.getAuthority(), actualOptional.get().getAuthority());
    }
}
