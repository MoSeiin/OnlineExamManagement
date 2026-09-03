package ir.maktabsharif.maktab_finalProject.util;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import ir.maktabsharif.maktab_finalProject.repository.PersonRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final PersonRepository personRepository;

    public CustomUserDetailService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Person person = personRepository.findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Person not found: " + username));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + person.getRole().name())
        );


        return CustomUserDetails.fromPerson(person, authorities);
    }
}
