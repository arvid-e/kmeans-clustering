package io.github.arvide.clustering.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.arvide.clustering.dto.Cluster;
import io.github.arvide.clustering.entity.Blog;
import io.github.arvide.clustering.entity.Centroid;

@SpringBootTest
public class ClusterServiceTest {
    @Autowired
    ClusterService clusterService;
    @Autowired
    DataLoader dataLoader;

    @Test
    public void clusteringRuns() {
        // Arrange

        // Act
        List<Cluster> clusters = clusterService.cluster(5, 4);
        System.out.println("Test run");
        for (Cluster cluster : clusters) {
            System.out.println("CENTROID");
            for (String blog : cluster.blogs()) {
                System.out.println(" " + blog);
            }
        }
        // Assert
        assertEquals(5, clusters.size());
    }
}
