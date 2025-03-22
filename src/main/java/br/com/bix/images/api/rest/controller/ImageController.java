package br.com.bix.images.api.rest.controller;

import br.com.bix.images.api.rest.model.ImageResponse;
import br.com.bix.images.service.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/image")
public class ImageController {

    private final ImageService imageService;

    @GetMapping
    public ResponseEntity<List<ImageResponse>> findAll(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "pageNumber") Integer pageNumber,
        @RequestParam(value = "pageSize") Integer pageSize) {
        List<ImageResponse> images = imageService.findAll(name, pageNumber, pageSize);
        return ResponseEntity.ok().body(images);
    }

}
