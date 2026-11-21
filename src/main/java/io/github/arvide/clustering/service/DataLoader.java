package io.github.arvide.clustering.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import io.github.arvide.clustering.entity.Blog;

@Service 
public class DataLoader {
    private List<String> words = new ArrayList<>();
    private List<Blog> blogs = new ArrayList<>();

    private void loadData() throws IOException {
        ClassPathResource resource = new ClassPathResource("blogdata.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String[] header = reader.readLine().split("\t");
            words = Arrays.asList(header).subList(1, header.length);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                List<Integer> wordCounts = new ArrayList<>();

                for (int i = 1; i < columns.length; i++) {
                    wordCounts.add(Integer.valueOf(columns[i]));
                }
                
                blogs.add(new Blog(columns[0], wordCounts));
            }
        }
    }

    public DataLoader() throws IOException {
        loadData();
    }

    public List<String> getWords() {
        return words;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

}
