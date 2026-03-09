package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.TicketCreationRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.Ticket;
import com.devteria.identity_service.enums.TicketStatus;
import com.devteria.identity_service.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    TicketService ticketService;
    @PostMapping
    @Operation(summary = "Create a new ticket (Type 1,3) - Requires approver and assigned staff")
    public ApiResponse<Ticket> createTicket(@RequestBody @Valid TicketCreationRequest request) {
        Ticket ticket = ticketService.createTicket(request);
        return ApiResponse.<Ticket>builder()
                .data(ticket)
                .message("Ticket created successfully")
                .code(1000)
                .build();

    }
    @PostMapping("/ticket_type2")
    @Operation(summary = "Create a new ticket (Type 2,4) - No approver or assigned staff needed, auto-assign to admin")
    public ApiResponse<Ticket> createTicketType2(@RequestBody @Valid TicketCreationRequest request) {
        Ticket ticket = ticketService.createTicketType24(request);
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
    @PostMapping("/{ticketID}/assign/{staffID}")
    @Operation(summary = "Assign a ticket to a staff member")
    public ApiResponse<Ticket> assignTicket(@PathVariable String ticketID, @PathVariable String staffID) {
        Ticket ticket = ticketService.assignTicket(ticketID, staffID);
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
}
