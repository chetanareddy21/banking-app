package org.xyonsoft.bankingapp.config;



import org.xyonsoft.bankingapp.Entity.Admin;
import org.xyonsoft.bankingapp.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = Admin.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // CHANGE this before any real deployment
                    .build();
            adminRepository.save(admin);
            System.out.println("Default admin created — username: admin / password: admin123");
        }
    }
}