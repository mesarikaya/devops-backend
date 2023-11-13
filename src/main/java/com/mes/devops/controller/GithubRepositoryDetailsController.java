package com.mes.devops.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/github")
public class GithubRepositoryDetailsController {

    @GetMapping("")
    public String getRepositories() throws IOException {
        log.info("Log inside POD: Request for getRepositories Endpoint");
        return "Simple Test github endpoint - Get github repositories";
    }
}
