package com.akhil.authify.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    @Builder.Default
    private String status="success";
    @Builder.Default
    private LocalDateTime timestamp=LocalDateTime.now();
    @Builder.Default
    private Object data=null;
    @Builder.Default
    private Object error=null;
}
