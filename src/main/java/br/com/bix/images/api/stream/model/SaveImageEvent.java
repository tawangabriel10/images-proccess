package br.com.bix.images.api.stream.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveImageEvent {

    private String name;
    private String type;
    private String userId;
    private String token;
    private ImageData imageData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {

        private String data;
    }
}
