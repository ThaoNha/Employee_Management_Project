package vn.com.tma.training.ProjectTraining.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.tma.training.ProjectTraining.common.MessageResponse;
import vn.com.tma.training.ProjectTraining.dto.AdvanceDTO;
import vn.com.tma.training.ProjectTraining.service.AdvanceService;

@RestController
@RequestMapping("/api/advance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdvanceController {
    @Autowired
    private AdvanceService advanceService;

    @GetMapping("/get-all/{employee_id}")
    public ResponseEntity<?> listAdvance(@PathVariable Integer employee_id) {
        try {
            return ResponseEntity.ok(advanceService.listAdvance(employee_id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(e.getMessage()).build());

        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> addAdvance(@RequestBody AdvanceDTO advanceDTO) {
        try {

            return ResponseEntity.ok(advanceService.addAdvance(advanceDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(e.getMessage()).build());

        }

    }

    @DeleteMapping("/delete/{advance_id}")
    public ResponseEntity<?> deleteAdvance(@PathVariable Integer advance_id) {
        try {
            advanceService.deleteAdvance(advance_id);
            return ResponseEntity.ok().body("Delete Advance is successful!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(e.getMessage()).build());
        }


    }

}