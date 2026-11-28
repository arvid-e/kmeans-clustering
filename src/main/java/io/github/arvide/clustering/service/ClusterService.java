package io.github.arvide.clustering.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.arvide.clustering.dto.Cluster;
import io.github.arvide.clustering.entity.Blog;
import io.github.arvide.clustering.entity.ClusterState;

@Service
public class ClusterService {
    DataLoader dataLoader;

    public ClusterService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Creates clusters of related blogs using K-means clustering with Pearson
     * similarity.
     * 
     * @param k          - How many clusters to create.
     * @param iterations - How many times the clustering should run.
     * @return - Map with clusters of related blogs.
     */
    public List<Cluster> cluster(int numberOfClusters, int maxIterations) {
        List<Blog> blogs = this.dataLoader.getBlogs();
        List<ClusterState> clusters = new ArrayList<>();

        // Create K starting clusters.
        for (int i = 0; i < numberOfClusters; i++) {
            List<Double> centroid = blogs.get(i).wordCounts();
            clusters.add(new ClusterState(centroid, new ArrayList<>(), new ArrayList<>()));
        }

        int iteration = 0;
        boolean changed = true;
        while (changed && iteration < maxIterations) {
            for (ClusterState cluster : clusters) {
                cluster.setPreviousAssignments(cluster.getAssignments());
                cluster.setAssignments(new ArrayList<>());
            }
            clusters = assignBlogsToClusters(blogs, clusters);
            changed = clusters.stream().anyMatch(ClusterState::assignmentsChanged);
            clusters = recenter(clusters);
            iteration++;
        }

        return presentData(clusters);

    }

    /**
     * Compare every blog's word counts with the centriods word counts using
     * pearson. Then add the blogs to the most similar cluster with the most similar
     * centroid.
     * 
     * @param blogs
     * @param centroids
     */
    public List<ClusterState> assignBlogsToClusters(List<Blog> blogs, List<ClusterState> clusters) {
        for (Blog blog : blogs) {
            ClusterState closest = clusters.get(0);
            double highestSimilarity = Double.NEGATIVE_INFINITY;

            for (ClusterState cluster : clusters) {
                double similarity = pearson(blog.wordCounts(), cluster.getCentroid());
                if (similarity > highestSimilarity) {
                    closest = cluster;
                    highestSimilarity = similarity;
                }
            }

            closest.assignBlog(blog);
        }

        return clusters;
    }

    /**
     * Calculate the average word count for all blogs in the clusters. Then set the
     * centroid's to that average value in order to centre it in the cluster.
     * 
     * @param words
     * @param amountOfBlogs
     */
    public List<ClusterState> recenter(List<ClusterState> clusters) {
        List<String> words = this.dataLoader.getWords();
        List<ClusterState> centeredClusters = new ArrayList<>();

        for (ClusterState cluster : clusters) {
            List<Double> totalWordCounts = new ArrayList<>(Collections.nCopies(words.size(), 0.0));

            // get total word counts for all clusters
            for (Blog blog : cluster.getAssignments()) {
                for (int i = 0; i < blog.wordCounts().size(); i++) {
                    Double currentSum = totalWordCounts.get(i);
                    totalWordCounts.set(i, currentSum + blog.wordCounts().get(i));
                }
            }

            // get average word count for all clusters
            List<Double> averageWordCounts = new ArrayList<>();
            for (Double count : totalWordCounts) {
                Double average = cluster.getAssignments().size() == 0 ? 0 : count / cluster.getAssignments().size();
                averageWordCounts.add(average);
            }

            ClusterState centered = new ClusterState(averageWordCounts, cluster.getAssignments(),
                    cluster.getPreviousAssignments());
            centeredClusters.add(centered);

        }

        return centeredClusters;
    }

    /**
     * Calculates a similarity scores between the word counts of a blog vs the
     * average word counts in a centroid.
     * 
     * @param blogWordCounts
     * @param centroidWordCounts
     * @return - Similarity score.
     */
    public double pearson(List<Double> blogWordCounts, List<Double> centroidWordCounts) {
        double sumBlog = 0;
        for (Double count : blogWordCounts) {
            sumBlog += count;
        }
        double meanBlog = sumBlog / blogWordCounts.size();

        double sumCentroid = 0;
        for (Double count : centroidWordCounts) {
            sumCentroid += count;
        }
        double meanCentroid = sumCentroid / centroidWordCounts.size();

        double numerator = 0;
        double sumSqBlog = 0;
        double sumSqCentroid = 0;

        for (int i = 0; i < blogWordCounts.size(); i++) {
            double diffBlog = blogWordCounts.get(i) - meanBlog;
            double diffCentroid = centroidWordCounts.get(i) - meanCentroid;
            numerator += diffBlog * diffCentroid;
            sumSqBlog += diffBlog * diffBlog;
            sumSqCentroid += diffCentroid * diffCentroid;
        }

        double denominator = Math.sqrt(sumSqBlog) * Math.sqrt(sumSqCentroid);
        if (denominator == 0) {
            return 0;
        }
        return numerator / denominator;
    }

    /**
     * Converts the raw cluster data into presentable data in a list of clusters.
     * 
     * @param rawClusters - Raw cluster data.
     * @return - List of clusters and their blogs.
     */
    public List<Cluster> presentData(List<ClusterState> clusterStates) {
        List<Cluster> clusters = new ArrayList<>();

        int n = 1;
        for (ClusterState cluster : clusterStates) {
            String name = "Cluster " + n;
            List<String> blogNames = new ArrayList<>();

            for (Blog blog : cluster.getAssignments()) {
                blogNames.add(blog.name());
            }

            clusters.add(new Cluster(name, blogNames));
            n++;
        }

        return clusters;
    }
}
