package io.github.arvide.clustering.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.arvide.clustering.dto.Cluster;

@SpringBootTest
public class ClusterServiceTest {
    @Autowired
    ClusterService clusterService;

    @Test
    public void clusteringRuns() {
        // Arrange

        // Act
        List<Cluster> clusters = clusterService.cluster(5, 4);

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
