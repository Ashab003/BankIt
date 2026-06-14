package com.project.BankIt_backend.controller;

import com.project.BankIt_backend.dto.DashboardResponseDTO;
import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.service.DashboardService;
import com.project.BankIt_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping
    public DashboardResponseDTO getDashboard() {

        User user = userService.getCurrentUser();
        return dashboardService.getDashboard(user.getUserId());

    }
}