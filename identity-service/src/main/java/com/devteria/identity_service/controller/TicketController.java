package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.AssignTicketRequest;
import com.devteria.identity_service.dto.request.TicketCreationRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.Ticket;
import com.devteria.identity_service.enums.RequestType;
import com.devteria.identity_service.enums.TicketStatus;
import com.devteria.identity_service.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    TicketService ticketService;
    @PostMapping
    @Operation(summary = "Create a new ticket (Type 1,3) - Requires approver and assigned staff")
    public ApiResponse<Ticket> createTicketType1Or3(@RequestBody @Valid TicketCreationRequest request) {
        Ticket ticket = ticketService.createTicket(request);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket created successfully")
                .code(1000)
                .build();

    }
    @PostMapping("/ticket_type2")
    @Operation(summary = "Create a new ticket (Type 2) - No approver or assigned staff needed, auto-assign to admin")
    public ApiResponse<Ticket> createTicketType2(@RequestBody @Valid TicketCreationRequest request) {
        Ticket ticket = ticketService.createTicketType2(request);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket created successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/{ticketID}")
    @Operation(summary = "Get ticket by ID")
    public ApiResponse<Ticket> getTicketByID(@PathVariable String ticketID) {
        Ticket ticket = ticketService.getTicketByID(ticketID);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket fetched successfully")
                .code(1000)
                .build();
    }
    @DeleteMapping("/{ticketID}")
    @Operation(summary = "Delete a ticket by ID")
    public ApiResponse<Ticket> deleteTicket(@PathVariable String ticketID) {
        Ticket ticket = ticketService.deleteTicket(ticketID);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket deleted successfully")
                .code(1000)
                .build();
    }
    @GetMapping
    @Operation(summary = "Get all tickets")
    public ApiResponse<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/requester/{requesterID}/status/{status}")
    @Operation(summary = "Get all tickets for a specific requester")
    public ApiResponse<List<Ticket>> getAllTicketsForRequester(@PathVariable String requesterID , @PathVariable TicketStatus status) {
        List<Ticket> tickets = ticketService.getTicketsByRequester(requesterID , status);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/approver/{approverID}/status/{status}")
    @Operation(summary = "Get all tickets for a specific approver")
    public ApiResponse<List<Ticket>> getAllTicketsForApprover(@PathVariable String approverID, @PathVariable TicketStatus status) {
        List<Ticket> tickets = ticketService.getTicketByApprover(approverID, status);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/assigned/{staffID}/status/{status}")
    @Operation(summary = "Get all tickets assigned to a specific staff")
    public ApiResponse<List<Ticket>> getAllTicketsForAssignedStaff(@PathVariable String staffID ,@PathVariable TicketStatus status) {
        List<Ticket> tickets = ticketService.getTicketsByAssignedStaff(staffID,status);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/assigned/{staffID}")
    @Operation(summary = "Get all tickets assigned to a specific staff regardless of status")
    public ApiResponse<List<Ticket>> getAllTicketsForAssignedStaff(@PathVariable String staffID) {
        List<Ticket> tickets = ticketService.getTicketByAssignedStaffID(staffID);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/approver/{approverID}")
    @Operation(summary = "Get all tickets for a specific approver regardless of status")
    public ApiResponse<List<Ticket>> getAllTicketsForApprover(@PathVariable String approverID) {
        List<Ticket> tickets = ticketService.getTicketByApproverID(approverID);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/requester/{requesterID}")
    @Operation(summary = "Get all tickets for a specific requester regardless of status")
    public ApiResponse<List<Ticket>> getAllTicketsForRequester(@PathVariable String requesterID) {
        List<Ticket> tickets = ticketService.getTicketByRequesterID(requesterID);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/status/{status}")
    @Operation(summary = "Get all tickets by status")
    public ApiResponse<List<Ticket>> getAllTicketsByStatus(@PathVariable TicketStatus status){
        List<Ticket> tickets = ticketService.getAllTicketsByStatus(status);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/type/{type}")
    @Operation(summary = "Get all tickets by type")
    public ApiResponse<List<Ticket>> getAllTicketsByType(@PathVariable RequestType type){
        List<Ticket> tickets = ticketService.getAllTicketsByType(type);
        return ApiResponse.<List<Ticket>>builder()
                .data(tickets)
                .message("Tickets fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PostMapping("/assign-ticket")
    @Operation(summary = "Assign a ticket to a BI member")
    public ApiResponse<Ticket> assignTicket(@RequestBody @Valid AssignTicketRequest request) {
        Ticket ticket = ticketService.assignTicket(request);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket assigned successfully")
                .code(1000)
                .build();
    }

    @PostMapping("/{ticketID}/status/{status}")
    @Operation(summary = "Update the status of a ticket")
    public ApiResponse<Ticket> updateTicketStatus(@PathVariable String ticketID, @PathVariable TicketStatus status) {
        Ticket ticket = ticketService.updateTicketStatus(ticketID, status);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket status updated successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('BI')")
    @PostMapping("/reject/{ticketID}")
    @Operation(summary = "Reject a ticket")
    public ApiResponse<Ticket> rejectTicket(@PathVariable String ticketID, @RequestParam String reason) {
        Ticket ticket = ticketService.rejectTicket(ticketID,reason);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket rejected successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PutMapping("/submit_result/{ticketID}/dashboard/{DashboardID}")
    @Operation(summary = "Submit the result of a ticket type 3")
    public ApiResponse<Ticket> submitTicketResult(@PathVariable String ticketID, @PathVariable String DashboardID) {
        Ticket ticket = ticketService.submitDashboardResult(ticketID, DashboardID);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket result submitted successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/approve-dashboard/{ticketID}")
    @Operation(summary = "Approve a dashboard access request (Ticket type 3)")
    public ApiResponse<Ticket> approveDashboardAccess(@PathVariable String ticketID) {
        Ticket ticket = ticketService.approveDashboardDraft(ticketID);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Dashboard access approved successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/reject-dashboard/{ticketID}")
    @Operation(summary = "Reject a dashboard access request (Ticket type 3)")
    public ApiResponse<Ticket> rejectDashboardAccess(@PathVariable String ticketID, @RequestParam String reason) {
        Ticket ticket = ticketService.rejectDashboardDraft(ticketID, reason);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Dashboard access rejected successfully")
                .code(1000)
                .build();
    }

}
