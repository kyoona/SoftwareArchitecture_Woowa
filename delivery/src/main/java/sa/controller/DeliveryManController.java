package sa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sa.dto.DeliveryManRequestDto;
import sa.dto.SimpleResDto;
import sa.service.DeliveryManService;

@RequestMapping("/delivery/users/{userId}/deliverymen")
@RequiredArgsConstructor
@RestController
public class DeliveryManController {
    private final DeliveryManService deliveryManService;

    @PostMapping("/request")
    public SimpleResDto requestDeliveryMan(@PathVariable Long userId,
                                           @RequestBody DeliveryManRequestDto deliveryManRequestDto) {
        Long deliveryManRequestInfoId = deliveryManService.requestDeliveryMan(userId, deliveryManRequestDto);

        return new SimpleResDto(deliveryManRequestInfoId);
    }

    @PostMapping("/{requestId}/accept")
    public SimpleResDto acceptDeliveryMan(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        Long deliveryManId = deliveryManService.acceptDeliveryMan(userId, requestId);

        return new SimpleResDto(deliveryManId);
    }

    @PostMapping("/{requestId}/deny")
    public SimpleResDto denyDeliveryMan(@PathVariable Long userId,
                                        @PathVariable Long requestId) {
        Long deliveryManRequestInfoId = deliveryManService.denyDeliveryMan(userId, requestId);

        return new SimpleResDto(deliveryManRequestInfoId);
    }
}
