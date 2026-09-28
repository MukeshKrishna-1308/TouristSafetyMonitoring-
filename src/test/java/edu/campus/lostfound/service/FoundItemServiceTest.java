package edu.campus.lostfound.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.campus.lostfound.model.Category;
import edu.campus.lostfound.model.FoundItem;
import edu.campus.lostfound.model.FoundStatus;
import edu.campus.lostfound.model.User;
import edu.campus.lostfound.model.UserRole;
import edu.campus.lostfound.repository.CategoryRepository;
import edu.campus.lostfound.repository.FoundItemRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FoundItemServiceTest {
    @Mock
    private FoundItemRepository repository;

    @Mock
    private CategoryRepository categories;

    private FoundItemService service;
    private User staff;
    private FoundItem item;

    @BeforeEach
    void setUp() {
        service = new FoundItemService(repository, categories);
        staff = new User("staff@campus.edu", "hashed", "Staff Member", UserRole.STAFF);
        ReflectionTestUtils.setField(staff, "id", 10L);
        item = new FoundItem(staff, new Category("Electronics"), "Black charger", "Library", Instant.now());
        ReflectionTestUtils.setField(item, "id", 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
    }

    @Test
    void returningAnAvailableItemIsRejected() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.changeStatus(1L, FoundStatus.RETURNED, staff));

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("A found item must be CLAIMED before it can be marked RETURNED", exception.getReason());
    }

    @Test
    void anotherStaffMemberCannotChangeStatus() {
        User otherStaff = new User("other@campus.edu", "hashed", "Other Staff", UserRole.STAFF);
        ReflectionTestUtils.setField(otherStaff, "id", 11L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.changeStatus(1L, FoundStatus.CLAIMED, otherStaff));

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void claimedItemCanBeReturnedByItsStaffMember() {
        item.setStatus(FoundStatus.CLAIMED);
        when(repository.save(item)).thenReturn(item);

        var returned = service.changeStatus(1L, FoundStatus.RETURNED, staff);

        assertEquals(FoundStatus.RETURNED, returned.status());
        verify(repository).save(item);
    }

    @Test
    void administratorCanChangeAnotherStaffMembersItem() {
        User admin = new User("admin@campus.edu", "hashed", "Administrator", UserRole.ADMIN);
        when(repository.save(item)).thenReturn(item);

        var claimed = service.changeStatus(1L, FoundStatus.CLAIMED, admin);

        assertEquals(FoundStatus.CLAIMED, claimed.status());
        verify(repository).save(item);
    }
}