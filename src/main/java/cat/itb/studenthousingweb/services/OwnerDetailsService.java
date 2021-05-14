package cat.itb.studenthousingweb.services;


import cat.itb.studenthousingweb.models.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class OwnerDetailsService implements UserDetailsService {

    @Autowired
    private OwnerService ownerService;

    public static String currentOwnerId = "";


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {


        Owner user = ownerService.checkByEmail(s);
        User.UserBuilder builder = null;


        if (user != null) {
            builder = User.withUsername(s);
            builder.disabled(false);
            builder.password(user.getPassword());

            builder.roles("OWNER");
        } else {
            throw new UsernameNotFoundException("ERROR: Username not found");
        }

        currentOwnerId = user.getOwnerId();
        return builder.build();

    }
}
