package io.github.arvide.clustering.entity;

import java.util.List;

public record Cluster(Centroid centroid, List<Blog> blogs) {

}
