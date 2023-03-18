package finalproject.repository;

import finalproject.models.Paid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PaidRepository extends JpaRepository<Paid,Long> {
    @Query("SELECT p FROM Paid p WHERE "
            + "(:id IS NULL OR p.id = :id) AND "
            + "(:course IS NULL OR p.course LIKE %:course%) AND "
            + "(:name IS NULL OR p.name LIKE %:name%) AND "
            + "(:surname IS NULL OR p.surname LIKE %:surname%) AND "
            + "(:email IS NULL OR p.email LIKE %:email%) AND "
            + "(:phone IS NULL OR p.phone LIKE %:phone%) AND "
            + "(:age IS NULL OR p.age = :age) AND "
            + "(:courseFormat IS NULL OR p.courseFormat LIKE %:courseFormat%) AND "
            + "(:courseType IS NULL OR p.courseType LIKE %:courseType%) AND "
            + "(:createdAt IS NULL OR p.createdAt = :createdAt) AND "
            + "(:status IS NULL OR p.status LIKE %:status%)")
    List<Paid> findByCriteria(
            @Param("id") Long id,
            @Param("course") String course,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("age") Integer age,
            @Param("courseFormat") String courseFormat,
            @Param("courseType") String courseType,
            @Param("createdAt") Date createdAt,
            @Param("status") String status);

    Page<Paid> findAll(Specification<Paid> spec, Pageable pageable);
   List<Paid> findByUserEmail(String email);


}
