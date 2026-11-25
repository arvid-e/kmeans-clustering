package io.github.arvide.clustering.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.github.arvide.clustering.dto.Cluster;
import io.github.arvide.clustering.entity.Blog;
import io.github.arvide.clustering.entity.Centroid;

@Service
public class ClusterService {
    DataLoader dataLoader;
    Map<Centroid, List<Blog>> clusters = new HashMap<>();

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
    public List<Cluster> cluster(int k, int iterations) {
        List<Blog> blogs = this.dataLoader.getBlogs();
        List<String> words = this.dataLoader.getWords();

        // 1. Create K starting clusters.
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
            int[] totalWordCounts = new int[words.size()];
            for (Blog blog : cluster.getValue()) {
                for (int i = 0; i < blog.wordCounts().size(); i++) {
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

    /**
     * Calculates a similarity scores between the word counts of a blog vs the
     * average word counts in a centroid.
     * 
     * @param blogWordCounts
     * @param centroidWordCounts
     * @return - Similarity score.
     */
    public double pearson(List<Integer> blogWordCounts, List<Integer> centroidWordCounts) {
        int sumBlog = 0;
        for (Integer count : blogWordCounts) {
            sumBlog += count;
        }
        double meanBlog = sumBlog / blogWordCounts.size();

        int sumCentroid = 0;
        for (Integer count : centroidWordCounts) {
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
    public List<Cluster> presentData(Map<Centroid, List<Blog>> rawClusters) {
        List<Cluster> clusters = new ArrayList<>();

        int n = 1;
        for (Map.Entry<Centroid, List<Blog>> entry : rawClusters.entrySet()) {
            String name = "Cluster " + n;
            List<String> blogNames = new ArrayList<>();

            for (Blog blog : entry.getValue()) {
                blogNames.add(blog.name());
            }

            clusters.add(new Cluster(name, blogNames));
            n++;
        }

        return clusters;
    }
}
