package br.com.bix.images.api.stream.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessImageEvent {

    private String imageId;
    private ProcessAction action;
    private String path;
    private String userId;
    private String token;

    @Getter
    @AllArgsConstructor
    public enum ProcessAction {
        ORIGINAL, RESIZE, NEGATIVE, GRAY;
    }
}
