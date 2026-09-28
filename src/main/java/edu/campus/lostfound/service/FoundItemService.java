package edu.campus.lostfound.service;

import edu.campus.lostfound.model.FoundItem;
import edu.campus.lostfound.model.FoundStatus;
import edu.campus.lostfound.model.Category;
import edu.campus.lostfound.model.User;
import edu.campus.lostfound.model.UserRole;
import edu.campus.lostfound.repository.CategoryRepository;
import edu.campus.lostfound.repository.FoundItemRepository;
import edu.campus.lostfound.web.dto.FoundItemRequest;
import edu.campus.lostfound.web.dto.FoundItemResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FoundItemService {
    private final FoundItemRepository foundItemRepository;
    private final CategoryRepository categoryRepository;

    public FoundItemService(FoundItemRepository foundItemRepository, CategoryRepository categoryRepository) {
        this.foundItemRepository = foundItemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public FoundItemResponse create(FoundItemRequest request, User actor) {
        if (actor.getRole() != UserRole.STAFF && actor.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only staff or administrators can log found items");
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category does not exist"));
        FoundItem item = new FoundItem(actor, category, request.description().trim(), request.location().trim(),
                request.foundDate() == null ? Instant.now() : request.foundDate());
        return FoundItemResponse.from(foundItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<FoundItemResponse> list() {
        return foundItemRepository.findAll().stream().map(FoundItemResponse::from).toList();
    }

    @Transactional
    public FoundItemResponse changeStatus(long itemId, FoundStatus status, User actor) {
        FoundItem item = foundItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Found item not found"));
        if (actor.getRole() != UserRole.ADMIN
            && (actor.getId() == null || item.getStaff().getId() == null
                || !item.getStaff().getId().equals(actor.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only an administrator or the staff member who logged this item can change its status");
        }
        if (status == FoundStatus.RETURNED && item.getStatus() != FoundStatus.CLAIMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A found item must be CLAIMED before it can be marked RETURNED");
        }
        item.setStatus(status);
        return FoundItemResponse.from(foundItemRepository.save(item));
    }
}