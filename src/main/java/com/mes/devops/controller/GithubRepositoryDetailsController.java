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

/*    @Value("${github.username}")
    private String username;

    @Value("${github.password}")
    private String password;*/

    @GetMapping("")
    public String getRepositories() throws IOException {
        GitHub github = new GitHubBuilder().withPassword(username, password).build();
        GHRepositorySearchBuilder builder = github.searchRepositories();
        return "Simple Test github endpoint - Get github repositories";
    }
}
