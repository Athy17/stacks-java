package com.xxAMIDOxx.xxSTACKSxx.exception;

import com.xxAMIDOxx.xxSTACKSxx.cqrs.commands.MenuCommand;

import java.util.UUID;

public class ItemAlreadyExistsException extends MenuApiException {

    public ItemAlreadyExistsException(MenuCommand command, UUID categoryId, String name) {
        super(String.format(
                "An item with the name '%s' already exists for the category '%s' in menu with " +
                        "id '%s'.", name,
                categoryId,
                command.getMenuId()),
                ExceptionCode.MENUITEMALREADY_EXISTS,
                command);
    }
}
