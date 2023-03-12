package finalproject.controllers;

import finalproject.models.Paid;
import finalproject.repository.PaidRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Api(value = "Paid controller")

@RestController
@RequestMapping("/api/paid")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaidController {
    @Autowired
    PaidRepository paidRepository;
    @PersistenceContext
    private EntityManager em;
    @Operation(summary = "get all paid",
            operationId = "getAllPaid",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Paid.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<Paid>> getAllPaid(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(required = false) Long id,
                                                 @RequestParam(required = false) String course,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String surname,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String phone,
                                                 @RequestParam(required = false) Integer age,
                                                 @RequestParam(required = false) String courseFormat,
                                                 @RequestParam(required = false) String courseType,
                                                 @RequestParam(required = false) Date createdAt,
                                                 @RequestParam(required = false) String status) {
        int pageSize = 50;
        Pageable pageable = PageRequest.of(page, pageSize);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Paid> cq = cb.createQuery(Paid.class);
        Root<Paid> root = cq.from(Paid.class);
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(cb.equal(root.get("id"), id));
        }
        if (course != null) {
            predicates.add(cb.like(root.get("course"), "%" + course + "%"));
        }
        if (name != null) {
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }
        if (surname != null) {
            predicates.add(cb.like(root.get("surname"), "%" + surname + "%"));
        }
        if (email != null) {
            predicates.add(cb.like(root.get("email"), "%" + email + "%"));
        }
        if (phone != null) {
            predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
        }
        if (age != null) {
            predicates.add(cb.equal(root.get("age"), age));
        }
        if (courseFormat != null) {
            predicates.add(cb.equal(root.get("courseFormat"), courseFormat));
        }
        if (courseType != null) {
            predicates.add(cb.equal(root.get("courseType"), courseType));
        }
        if (createdAt != null) {
            predicates.add(cb.equal(root.get("createdAt"), createdAt));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Paid> query = em.createQuery(cq);
        List<Paid> results = query.getResultList();

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @Operation(summary = "get all paid",
            operationId = "getAllPaid",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Paid.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("/{id}")
    @SecurityRequirement(name = "JWT")

    public ResponseEntity<?> getPaidById(@PathVariable long id) {
        Optional<Paid> one = paidRepository.findById(id);
        return new ResponseEntity<>(one, HttpStatus.OK);
    }
}
