package com.PBO.TaleSwipe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pageId;

    private int pageNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    // Konstruktor yang diperlukan untuk StoryServiceImpl
    public Page(String content, Story story) {
        this.content = content;
        this.story = story;
    }
}
