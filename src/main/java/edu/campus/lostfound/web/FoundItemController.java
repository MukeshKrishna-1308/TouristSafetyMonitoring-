package edu.campus.lostfound.web;

import edu.campus.lostfound.model.FoundStatus;
import edu.campus.lostfound.service.FoundItemService;
import edu.campus.lostfound.web.dto.FoundItemRequest;
import edu.campus.lostfound.web.dto.FoundItemResponse;
import edu.campus.lostfound.web.dto.StatusRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/found-items")
public class FoundItemController {
    private final FoundItemService foundItemService;

    public FoundItemController(FoundItemService foundItemService) { this.foundItemService = foundItemService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoundItemResponse create(@Valid @RequestBody FoundItemRequest request) {
        return foundItemService.create(request, CurrentUser.get());
    }

    @GetMapping
    public List<FoundItemResponse> list() { return foundItemService.list(); }

    @PatchMapping("/{id}/status")
    public FoundItemResponse changeStatus(@PathVariable long id, @Valid @RequestBody StatusRequest request) {
        FoundStatus status = request.status();
        return foundItemService.changeStatus(id, status, CurrentUser.get());
    }
}