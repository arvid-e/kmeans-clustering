package io.github.arvide.clustering.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.github.arvide.clustering.entity.Blog;
import io.github.arvide.clustering.entity.Centroid;

@Service
public class ClusterService {
    DataLoader dataLoader;
    Map<Centroid, List<Blog>> clusters = new HashMap<>();

    public ClusterService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public Map<Centroid, List<Blog>> cluster(int k, int iterations) {
        List<Blog> blogs = this.dataLoader.getBlogs();
        List<String> words = this.dataLoader.getWords();

        // 1. Create K clusters with centroids with random word counts
        for (int i = 0; i < k; i++) {
            Centroid centroid = new Centroid(blogs.get(i).wordCounts());
            clusters.put(centroid, new ArrayList<>());
        }

        for (int i = 0; i < iterations; i++) {
            List<Centroid> centroids = new ArrayList<>(clusters.keySet());
            // 2. Add the blogs to the cluster with the most similar centroid
            findClosestClusterForEveryBlog(blogs, centroids);

            // 3. Centre all centroid by calculating the average word count for every blog
            // in the clusters
            centreCentroids(words);
        }

        List<Centroid> centroids = new ArrayList<>(clusters.keySet());
        findClosestClusterForEveryBlog(blogs, centroids);
        return clusters;

    }

    /**
     * Compare every blog's word counts with the centriods word counts using
     * pearson. Then add the blogs to the most similar cluster with the most similar
     * centroid.
     * 
     * @param blogs
     * @param centroids
     */
    public void findClosestClusterForEveryBlog(List<Blog> blogs, List<Centroid> centroids) {
        for (Blog blog : blogs) {
            Centroid closest = new Centroid(new ArrayList<>());
            double highestSimilarity = 0;

            for (Centroid centroid : centroids) {
                double similarity = pearson(blog.wordCounts(), centroid.averageWordCounts());
                if (similarity > highestSimilarity) {
                    closest = centroid;
                    highestSimilarity = similarity;
                }
            }

            clusters.putIfAbsent(closest, new ArrayList<>());
            clusters.get(closest).add(blog);
        }
    }

    /**
     * Calculate the average word count for all blogs in the clusters. Then set the
     * centroid's to that average value in order to centre it in the cluster.
     * 
     * @param words
     * @param amountOfBlogs
     */
    public void centreCentroids(List<String> words) {
        Map<Centroid, List<Blog>> movedClusters = new HashMap<>();

        for (Map.Entry<Centroid, List<Blog>> cluster : clusters.entrySet()) {
            // Get the total word counts for every word
            int[] totalWordCounts = new int[words.size() - 1];
            for (Blog blog : cluster.getValue()) {
                for (int i = 0; i < blog.wordCounts().size() - 1; i++) {
                    totalWordCounts[i] += blog.wordCounts().get(i);
                }
            }

            // Get the average word count for every word
            List<Integer> averageWordCounts = new ArrayList<>();
            for (int count : totalWordCounts) {
                int average = cluster.getValue().size() == 0 ? 0 : count / cluster.getValue().size();
                averageWordCounts.add(average);
            }

            Centroid centered = new Centroid(averageWordCounts);
            movedClusters.put(centered, new ArrayList<>());
        }
        clusters = movedClusters;
    }

    // Placeholder
    public double pearson(List<Integer> blogWordCounts, List<Integer> centroidWordCounts) {
        return 0.5;
    }
}
