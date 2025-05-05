// src/main/java/com/bank/controller/ActivityLogController.java
package com.bank.controller;

import com.bank.model.ActivityLog;
import com.bank.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @GetMapping("/activity-logs")
    public String viewActivityLogs(Model model) {
        List<ActivityLog> logs = activityLogRepository.findAll();
        model.addAttribute("logs", logs);
        return "activity_logs";
    }
}
