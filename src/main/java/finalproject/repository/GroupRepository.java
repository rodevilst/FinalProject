package finalproject.repository;

import finalproject.models.Group;
import finalproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group,String> {
    Group findByName(String name);


}
