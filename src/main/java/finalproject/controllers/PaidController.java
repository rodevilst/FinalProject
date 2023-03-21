package finalproject.controllers;

import finalproject.Filter.PaidFilter;
import finalproject.models.Comment;
import finalproject.models.Group;
import finalproject.models.Paid;
import finalproject.models.User;
import finalproject.pojo.JwtResponse;
import finalproject.repository.CommentRepository;
import finalproject.repository.GroupRepository;
import finalproject.repository.PaidRepository;
import finalproject.repository.UserRepository;
import finalproject.service.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "Paid controller")

@RestController
@RequestMapping("/api/paid")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaidController {
    @Autowired
    PaidRepository paidRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    CommentRepository commentRepository;

    @Operation(summary = "get all paid",
            operationId = "getAllPaid",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Paid.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("")
    @PreAuthorize("#user.is_active")
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
    public ResponseEntity<?> getAllPaid(@RequestParam(defaultValue = "1", required = false) int page,
                                        @RequestParam(required = false) String order,
                                        @RequestParam(required = false) String My,
                                        @Parameter(hidden = true) @ModelAttribute PaidFilter filter, Authentication authentication,@AuthenticationPrincipal UserDetailsImpl user) throws IOException {
        int pageSize = 50;
        if (order == null) {
            order = "id";
        }
        Sort sort = order.startsWith("-") ? Sort.by(order.substring(1)).descending() : Sort.by(order).ascending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Paid> cq = cb.createQuery(Paid.class);
        Root<Paid> root = cq.from(Paid.class);
        List<Predicate> predicates = new ArrayList<>();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String currentUser = ((UserDetailsImpl) principal).getEmail();
            User byEmail = userRepository.findByEmail(currentUser).orElseThrow(SecurityException::new);


            if (My != null) {
                predicates.add(cb.equal(root.get("user").get("email"), currentUser));
            }


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
        return null;

    }


    @Operation(summary = "getpaidbyid",
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

    @Operation(summary = "get all paid",
            operationId = "getAllPaid",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Paid.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @PatchMapping("")
    @PreAuthorize("#user.is_active")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<?> setPaidParam(
            @RequestParam long id,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String courseFormat,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) Integer sum,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) Integer alreadyPaid, Authentication authentication,@AuthenticationPrincipal UserDetailsImpl user) {

        Paid paid = paidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("paid not found"));
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String currentUser = ((UserDetailsImpl) principal).getEmail();
            User byEmail = userRepository.findByEmail(currentUser).orElseThrow(SecurityException::new);

            if (paid == null) {
                return ResponseEntity.badRequest().build();
            }
            if (course != null) {
                paid.setCourse(course);
                checkUserAndSave(paid, byEmail);
            }
            if (group != null) {
                Group byName = groupRepository.findByName(group);

                if (paid.getGroup() == null) {
                    paid.setGroup(byName);
                }
                if (paid.getGroup() != null) {
                    paid.setGroup(byName);
                }
                checkUserAndSave(paid, byEmail);
            }
            if (name != null) {
                paid.setName(name);
                checkUserAndSave(paid, byEmail);
            }
            if (surname != null) {
                paid.setSurname(surname);
                checkUserAndSave(paid, byEmail);
            }
            if (email != null) {
                paid.setEmail(email);
                checkUserAndSave(paid, byEmail);
            }
            if (phone != null) {
                paid.setPhone(phone);
                checkUserAndSave(paid, byEmail);
            }
            if (age != null) {
                paid.setAge(age);
                checkUserAndSave(paid, byEmail);
            }
            if (courseFormat != null) {
                paid.setCourseFormat(courseFormat);
                checkUserAndSave(paid, byEmail);
            }
            if (courseType != null) {
                paid.setCourseType(courseType);
                checkUserAndSave(paid, byEmail);
            }
            if (sum != null) {
                paid.setSum(sum);
                checkUserAndSave(paid, byEmail);
            }
            if (status != null) {
                paid.setStatus(status);
                checkUserAndSave(paid, byEmail);
                if (status.equals("New")) {
                    paid.setUser(null);
                }
            }
            if (alreadyPaid != null) {
                paid.setAlreadyPaid(alreadyPaid);
                checkUserAndSave(paid, byEmail);
            }
            if (comment != null) {
                Comment comment1 = new Comment();
                comment1.setComment(comment);
                comment1.setCreated_at(new Date());
                comment1.setPaid(paid);
                commentRepository.save(comment1);
                checkUserAndSave(paid, byEmail);

            }
            paidRepository.save(paid);
            return new ResponseEntity(paid, HttpStatus.OK);
        }
        return null;
    }
UserDetailsImpl userDetails;
    @Operation(summary = "Create a new group",
            operationId = "CreateGroup",
            parameters = {
                    @Parameter(name = "name", description = "The name of the group", required = true, example = "My Group")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Group created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request - group name is missing or empty"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PreAuthorize("#user.is_active")
    @PostMapping("/group")
    public ResponseEntity<?> createGroup(@RequestParam(required = false) String name, @AuthenticationPrincipal UserDetailsImpl user) {

        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Group name is required");
        }
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setId(groupRepository.count());
        Group savedGroup = groupRepository.save(newGroup);

        return ResponseEntity.ok(savedGroup);
    }
    @Operation(summary = "Get all groups",
            operationId = "GetAllGroups",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all groups returned successfully", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PreAuthorize("#user.is_active")
    @GetMapping("/group")
    public ResponseEntity<?> getAllGroup(@AuthenticationPrincipal UserDetailsImpl user) {
        List<Group> all = groupRepository.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }


    private void checkUserAndSave(Paid paid, User byEmail) {
        if (byEmail != paid.getUser() && paid.getUser() != null) {
            throw new IllegalArgumentException("you can't do that");
        } else if (paid.getUser() == null) {
            paid.setUser(byEmail);
            paidRepository.save(paid);
        }
    }


}
