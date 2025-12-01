package com.javaauction.alertservice.presentation.dto.common;

import com.javaauction.alertservice.domain.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchParam {
    private String term;
    private AlertType alertType;
    private Boolean isRead;
}
