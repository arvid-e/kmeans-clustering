package io.github.arvide.clustering.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClusterState {
    private List<Double> centroid;
    private List<Blog> assignments;
    private List<Blog> previousAssignments;

    public ClusterState(List<Double> centroid, List<Blog> assignments, List<Blog> previousAssignments) {
        this.centroid = new ArrayList<>(centroid);
        this.assignments = new ArrayList<>(assignments);
        this.previousAssignments = new ArrayList<>(previousAssignments);
    }

    public boolean assignmentsChanged() {
        return !new HashSet<>(assignments).equals(new HashSet<>(previousAssignments));
    }

    public List<Blog> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Blog> assignments) {
        this.assignments = assignments;
    }

    public void assignBlog(Blog blog) {
        this.assignments.add(blog);
    }

    public List<Blog> getPreviousAssignments() {
        return previousAssignments;
    }

    public void setPreviousAssignments(List<Blog> previousAssignments) {
        this.previousAssignments = previousAssignments;
    }

    public List<Double> getCentroid() {
        return centroid;
    }

    public void setCentroid(List<Double> centroid) {
        this.centroid = centroid;
    }
}
