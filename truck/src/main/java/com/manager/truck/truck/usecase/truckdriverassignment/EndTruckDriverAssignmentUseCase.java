package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentEndRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EndTruckDriverAssignmentUseCase {

    private final TruckDriverAssignmentService assignmentService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckDriverAssignmentResponse execute(Long assignmentId,
                                                 TruckDriverAssignmentEndRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        TruckDriverAssignment assignment = assignmentService.findById(assignmentId);
        if (assignment.getAssignedUntil() != null) {
            throw new ConflictException("La asignación " + assignmentId + " ya fue finalizada");
        }
        if (!request.getAssignedUntil().isAfter(assignment.getAssignedFrom())) {
            throw new BadRequestException("assignedUntil debe ser posterior a assignedFrom ("
                    + assignment.getAssignedFrom() + ")");
        }
        assignment.setAssignedUntil(request.getAssignedUntil());
        TruckDriverAssignment saved = assignmentService.update(assignment);
        return TruckDriverAssignmentResponse.from(saved);
    }
}
