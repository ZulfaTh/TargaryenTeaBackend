package com.targaryentea.inventoryservice.service;

import com.targaryentea.inventoryservice.dto.InventoryRequest;
import com.targaryentea.inventoryservice.dto.InventoryResponse;
import com.targaryentea.inventoryservice.entity.Inventory;
import com.targaryentea.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<InventoryRequest> inventoryRequests) {
        // Extract SKU codes from inventoryRequests
        List<String> skuCodes = inventoryRequests.stream()
                .map(InventoryRequest::getSkuCode)
                .toList();

        // Fetch inventory for the given SKU codes
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);

        return inventoryRequests.stream()
                .map(inventoryRequest -> {
                    Inventory inventory = inventories.stream()
                            .filter(inv -> inv.getSkuCode().equals(inventoryRequest.getSkuCode()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("SKU Code not found: " + inventoryRequest.getSkuCode()));
                return InventoryResponse.builder()
                        .skuCode(inventoryRequest.getSkuCode())
                        .isInStock(inventory.getQuantity() >= inventoryRequest.getQuantity())
                        .build();
                })
                .toList();

//       return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
//               .map(inventory ->
//                   InventoryResponse.builder()
//                           .skuCode(inventory.getSkuCode())
//                           .isInStock(inventory.getQuantity() > inventoryRequests.getFirst().getQuantity())
//                           .build()
//               ).toList();
    }
}
