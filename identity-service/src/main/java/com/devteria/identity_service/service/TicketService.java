package com.devteria.identity_service.service;
import com.devteria.identity_service.dto.request.TicketCreationRequest;
import com.devteria.identity_service.entity.Ticket;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.RequestType;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.enums.TicketStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
    TicketRepository ticketRepository;
    UserService userService;
    SystemAuditLogService systemAuditLogService;
    @Transactional
    public Ticket createTicket(TicketCreationRequest request) {
        User loggedInUser = userService.getLoggedInUser();
        Ticket ticket = Ticket.builder()
                .requester(loggedInUser)
                .type(request.getType())
                .description(request.getDescription())
                .status(TicketStatus.CREATED)
                .approver(userService.getManagerByUserID(loggedInUser.getUser_id()))
                .createdAt(java.time.Instant.now())
                .build();
        ticketRepository.save(ticket);
        systemAuditLogService.logEvent(
                loggedInUser,
                com.devteria.identity_service.enums.EventLog.TICKET_CREATED,
                com.devteria.identity_service.enums.TargetEntity.TICKET,
                ticket.getTicket_id()
        );
        return ticket;
    }
    @Transactional
    public Ticket createTicketType24(TicketCreationRequest request) {
        User loggedInUser = userService.getLoggedInUser();
        Ticket ticket = Ticket.builder()
                .requester(loggedInUser)
                .type(request.getType())
                .description(request.getDescription())
                .status(TicketStatus.CREATED)
                .approver(userService.getAdminUser())
                .assigned_staff(userService.getAdminUser())
                .createdAt(java.time.Instant.now())
                .build();
        ticketRepository.save(ticket);
        systemAuditLogService.logEvent(
                loggedInUser,
                com.devteria.identity_service.enums.EventLog.TICKET_CREATED,
                com.devteria.identity_service.enums.TargetEntity.TICKET,
                ticket.getTicket_id()
        );
        return ticket;
    }
    @Transactional
    public Ticket deleteTicket(String ticketID) {
        Ticket ticket = getTicketByID(ticketID);
        ticketRepository.delete(ticket);
        systemAuditLogService.logEvent(
                userService.getLoggedInUser(),
                EventLog.TICKET_DELETED,
                TargetEntity.TICKET,
                ticketID
        );
        return ticket;
    }
    public Ticket getTicketByID(String ticketID) {
        return ticketRepository.findById(ticketID).orElseThrow( () -> new WebException(ErrorCode.TICKET_NOT_FOUND));
    }
    @Transactional
    public Ticket assignTicket(String ticketID, String staffID) {
        Ticket ticket = getTicketByID(ticketID);
        User staff = userService.getUserByID(staffID);
        Ticket oldTicketSnapshot = Ticket.builder()
                .status(ticket.getStatus())
                .assigned_staff(ticket.getAssigned_staff())
                .build();
        if (ticket.getStatus() == TicketStatus.APPROVED) {
            ticket.setAssigned_staff(staff);
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        } else {
            throw new WebException(ErrorCode.INVALID_TICKET_STATUS_UPDATE);
        }
        ticket.setUpdatedAt(Instant.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        systemAuditLogService.logEntityUpdate(
                userService.getLoggedInUser(),
                oldTicketSnapshot,
                updatedTicket,
                ticketID,
                TargetEntity.TICKET,
                EventLog.TICKET_ASSIGNED
        );
        return updatedTicket;
    }
    public List<Ticket> getTicketsByRequester(String requesterID, TicketStatus status) {
        return ticketRepository.findByRequesterAndStatus(userService.getUserByID(requesterID), status);
    }
    public List<Ticket> getTicketByApprover(String approverID, TicketStatus status) {
        return ticketRepository.findByApproverAndStatus(userService.getUserByID(approverID), status);
    }
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
    public List<Ticket> getTicketsByAssignedStaff(String staffID, TicketStatus status) {
        return ticketRepository.findByAssignedStaffAndStatus(userService.getUserByID(staffID), status);
    }
    @Transactional
    public Ticket updateTicketStatus(String ticketID, TicketStatus newStatus) {
        Ticket ticket = getTicketByID(ticketID);
        Ticket oldTicketSnapshot = Ticket.builder()
                .status(ticket.getStatus())
                .assigned_staff(ticket.getAssigned_staff())
                .build();
        TicketStatus currentStatus = ticket.getStatus();
        boolean isValidTransition = false;
        switch (currentStatus) {
            case CREATED:
                if (newStatus == TicketStatus.CANCELLED || newStatus == TicketStatus.APPROVED) {
                    isValidTransition = true;
                }
                break;
            case APPROVED:
                if (newStatus == TicketStatus.CANCELLED) {
                    isValidTransition = true;
                }
                break;
            case IN_PROGRESS:
                if (newStatus == TicketStatus.RESOLVED || newStatus == TicketStatus.CANCELLED) {
                    isValidTransition = true;
                }
                break;
            case RESOLVED:
                if (newStatus == TicketStatus.DONE) {
                    isValidTransition = true;
                }
                break;
            default:
                break;
        }
        if (!isValidTransition) {
            throw new WebException(ErrorCode.INVALID_TICKET_STATUS_UPDATE);
        }
        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(Instant.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        systemAuditLogService.logEntityUpdate(
                userService.getLoggedInUser(),
                oldTicketSnapshot,
                updatedTicket,
                ticketID,
                TargetEntity.TICKET,
                EventLog.TICKET_STATUS_UPDATED
        );
        return updatedTicket;
    }
    @Transactional
    public Ticket rejectTicket(String ticketID, String reason) {
        Ticket ticket = getTicketByID(ticketID);
        Ticket oldTicketSnapshot = Ticket.builder()
                .status(ticket.getStatus())
                .assigned_staff(ticket.getAssigned_staff())
                .build();
        if (ticket.getStatus() == TicketStatus.CREATED) {
            ticket.setStatus(TicketStatus.REJECTED);
            ticket.setReason(reason);
            ticket.setUpdatedAt(Instant.now());
            Ticket updatedTicket = ticketRepository.save(ticket);
            systemAuditLogService.logEntityUpdate(
                    userService.getLoggedInUser(),
                    oldTicketSnapshot,
                    updatedTicket,
                    ticketID,
                    TargetEntity.TICKET,
                    EventLog.TICKET_REJECTED
            );
            return updatedTicket;
        } else {
            throw new WebException(ErrorCode.INVALID_TICKET_STATUS_UPDATE);
        }
    }
}
