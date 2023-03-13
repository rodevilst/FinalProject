package finalproject.controllers;

import finalproject.Filter.PaidFilter;
import finalproject.models.Paid;
import finalproject.repository.PaidRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

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
    @Parameters({
            @Parameter(name = "page", description = "A page number within the paginated result set.", in = ParameterIn.QUERY),
            @Parameter(name = "id", in = ParameterIn.QUERY),
            @Parameter(name = "course", in = ParameterIn.QUERY),
            @Parameter(name = "name", in = ParameterIn.QUERY),
            @Parameter(name = "surname", in = ParameterIn.QUERY),
            @Parameter(name = "email", in = ParameterIn.QUERY),
            @Parameter(name = "phone", in = ParameterIn.QUERY),
            @Parameter(name = "age", in = ParameterIn.QUERY),
            @Parameter(name = "courseFormat", in = ParameterIn.QUERY),
            @Parameter(name = "courseType", in = ParameterIn.QUERY),
            @Parameter(name = "createdAt", in = ParameterIn.QUERY),
            @Parameter(name = "status", in = ParameterIn.QUERY),
            @Parameter(name = "order", description = "Sort order (-name for descending)", in = ParameterIn.QUERY)
    })
    public ResponseEntity<Page<Paid>> getAllPaid(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam String order,
                                                 @Parameter(hidden = true) @ModelAttribute PaidFilter filter) {
        int pageSize = 50;
        Sort sort = order.startsWith("-") ? Sort.by(order.substring(1)).descending() : Sort.by(order).ascending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Paid> cq = cb.createQuery(Paid.class);
        Root<Paid> root = cq.from(Paid.class);
        List<Predicate> predicates = new ArrayList<>();


        if (filter.getId() != null) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        }
        if (filter.getCourse() != null) {
            predicates.add(cb.like(root.get("course"), "%" + filter.getCourse() + "%"));
        }
        if (filter.getName() != null) {
            predicates.add(cb.like(root.get("name"), "%" + filter.getName() + "%"));
        }
        if (filter.getSurname() != null) {
            predicates.add(cb.like(root.get("surname"), "%" + filter.getSurname() + "%"));
        }
        if (filter.getEmail() != null) {
            predicates.add(cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
        }
        if (filter.getPhone() != null) {
            predicates.add(cb.like(root.get("phone"), "%" + filter.getPhone() + "%"));
        }
        if (filter.getAge() != null) {
            predicates.add(cb.equal(root.get("age"), filter.getAge()));
        }
        if (filter.getCourseFormat() != null) {
            predicates.add(cb.equal(root.get("courseFormat"), filter.getCourseFormat()));
        }
        if (filter.getCourseType() != null) {
            predicates.add(cb.equal(root.get("courseType"), filter.getCourseType()));
        }
        if (filter.getCreatedAt() != null) {
            predicates.add(cb.equal(root.get("createdAt"), filter.getCreatedAt()));
        }
        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        if (order.startsWith("-")) {
            cq.orderBy(cb.desc(root.get(order.substring(1))));
        } else {
            cq.orderBy(cb.asc(root.get(order)));
        }

        TypedQuery<Paid> query = em.createQuery(cq);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<Paid> resultList = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Paid.class)));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();

        Page<Paid> results = new PageImpl<>(resultList, pageable, count);


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
