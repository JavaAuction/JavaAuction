package com.javaauction.user.infrastructure.external.event;

import com.javaauction.user.infrastructure.external.dto.ResInternalBidsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidGetResponseEvent {
    private String correlationId;
    private ResInternalBidsDto bids;
}

