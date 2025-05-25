package com.planApiService.manage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ScheduleCreateRequest {

    @NotBlank(message = "할일 제목은 필수입니다.")
    private String title;

    private String content;  // 내용은 필수 아님
}
