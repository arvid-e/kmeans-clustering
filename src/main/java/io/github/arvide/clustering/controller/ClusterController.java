package io.github.arvide.clustering.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.arvide.clustering.dto.Cluster;
import io.github.arvide.clustering.service.ClusterService;

@RestController
@RequestMapping("/api/v1/clusters")
public class ClusterController {
    private final ClusterService clusterService;

    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GetMapping("")
    public List<Cluster> getClusters(@RequestParam(defaultValue = "6") int numberOfClusters,
            @RequestParam(defaultValue = "100") int maxIterations) {
        return clusterService.cluster(Integer.valueOf(numberOfClusters), Integer.valueOf(maxIterations));

    }
}
