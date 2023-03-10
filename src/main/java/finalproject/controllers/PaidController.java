package finalproject.controllers;

import finalproject.models.Paid;
import finalproject.repository.PaidRepositrory;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Api(value = "Paid controller")

@RestController
@RequestMapping("/api/paid")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaidController {
    @Autowired
    PaidRepositrory paidRepositrory;
    @Operation(summary = "get all paid",
            operationId = "getAllPaid",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("")
    public ResponseEntity<?> getAllPaid() {
        List<Paid> paidList = paidRepositrory.findAll();
        return new ResponseEntity<>(paidList, HttpStatus.OK);
    }
    @Operation(summary = "get one paid",
            operationId = "getOnePaid",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaidById(@PathVariable long id) {
        Optional<Paid> one = paidRepositrory.findById(id);
        return new ResponseEntity<>(one, HttpStatus.OK);
    }
}
