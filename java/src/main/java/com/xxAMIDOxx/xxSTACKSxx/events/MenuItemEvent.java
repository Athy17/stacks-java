package com.xxAMIDOxx.xxSTACKSxx.events;

import com.xxAMIDOxx.xxSTACKSxx.cqrs.commands.MenuCommand;

import java.util.UUID;

public abstract class MenuItemEvent extends CategoryEvent {
    private UUID itemId;

    public MenuItemEvent(MenuCommand command, EventCode eventCode, UUID categoryId, UUID itemId) {
        super(command, eventCode, categoryId);
        this.itemId = itemId;
    }

    public UUID getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        return "MenuItemEvent{" +
                "itemId=" + itemId +
                "} " + super.toString();
    }
}
