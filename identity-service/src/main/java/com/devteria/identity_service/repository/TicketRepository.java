package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Ticket;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.RequestType;
import com.devteria.identity_service.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    List<Ticket> findByRequesterAndStatus(User requester, TicketStatus status);
    List<Ticket> findByApproverAndStatus(User approver, TicketStatus status);
    @Query("SELECT t FROM Ticket t WHERE t.assigned_staff = :staff AND t.status = :status")
    List<Ticket> findByAssignedStaffAndStatus( @Param("staff")User staff, @Param("status")TicketStatus status);
    List<Ticket> findByRequester(User requester);
    @Query("SELECT t FROM Ticket t WHERE t.assigned_staff = :staff")
    List<Ticket> findByAssignedStaff(@Param("staff")User assignedStaff);
    List<Ticket> findByApprover(User approver);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByType(RequestType type);
}
