package com.xxAMIDOxx.xxSTACKSxx.cqrs.events;

import com.xxAMIDOxx.xxSTACKSxx.core.operations.OperationContext;

import java.util.UUID;

public class MenuItemCreatedEvent extends MenuItemEvent {
    public MenuItemCreatedEvent(int operationCode, UUID correlationId, UUID menuId, UUID itemId) {
        super(operationCode, correlationId, EventCode.MENU_ITEM_CREATED, menuId, itemId);
    }

    public MenuItemCreatedEvent(OperationContext operationContext, UUID menuId, UUID itemId) {
        super(operationContext, EventCode.MENU_ITEM_CREATED, menuId, itemId);
    }
}
