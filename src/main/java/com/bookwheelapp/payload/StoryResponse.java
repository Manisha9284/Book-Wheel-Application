package com.bookwheelapp.payload;

import java.util.List;

import com.bookwheelapp.entities.Story;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StoryResponse {

	private List<Story> content;
	private int pageNumber;
	private int pageSize;
	private long totalElement;
	private int totalPages;
	private boolean lastPage;
}
