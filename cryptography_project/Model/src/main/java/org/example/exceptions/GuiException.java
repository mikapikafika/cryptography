package org.example.exceptions;

import java.io.IOException;

public class GuiException extends IOException {
    public GuiException(Throwable cause) {
        super(cause);
    }
    public GuiException(String message) {super(message);
    }
    public GuiException(String message, Throwable cause) {
        super(message, cause);
    }
}
