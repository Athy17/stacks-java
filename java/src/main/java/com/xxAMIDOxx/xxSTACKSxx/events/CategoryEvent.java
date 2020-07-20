package com.xxAMIDOxx.xxSTACKSxx.events;

import com.xxAMIDOxx.xxSTACKSxx.cqrs.commands.MenuCommand;

import java.util.UUID;

public abstract class CategoryEvent extends MenuEvent {
    private UUID categoryId;

    public CategoryEvent(MenuCommand menuCommand, EventCode eventCode, UUID categoryId) {
        super(menuCommand, eventCode, menuCommand.getMenuId());
        this.categoryId = categoryId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    @Override
    public String toString() {
        return "CategoryEvent{" +
                "categoryId=" + categoryId +
                "} " + super.toString();
    }
}
