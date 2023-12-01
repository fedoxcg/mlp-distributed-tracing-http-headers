package com.fedox.study.tracing.checkout.resolvers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationPropertiesResolver {
    @Value("${services.order.creationEndpoint}")
    private String orderServiceEndpoint;

    @Value("${services.billing.paymentEndpoint}")
    private String billingServicePaymentEndpoint;

    @Value("${services.delivery.arrangeDeliveryEndpoint}")
    public String arrangeDeliveryServiceEndpoint;
}
