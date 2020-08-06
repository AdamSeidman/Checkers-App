package com.webcheckers.model;

import java.io.Serializable;

import static com.webcheckers.util.Attributes.MESSAGE_ILLEGAL_ARGUMENT_EXCEPTION_MSG;

/**
 * Generate various types of messages.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href='mailto:kkt7778@rit.edu'>Ketaki Tilak</a>
 */
public class Message implements Serializable {
    public enum Type {info, error} //whether the message is informational or an error msg.

    private Type type; //value from the enum
    public String text; //The text of the message from the server.

    public Message() {
        this.text = "";
    }

    public Message(String text, Type type) {
        if (type == null) {
            throw new IllegalArgumentException(MESSAGE_ILLEGAL_ARGUMENT_EXCEPTION_MSG);
        }
        this.text = text;
        this.type = type;
    }

    /**
     * gets text
     *
     * @return the text of the message from the server
     */
    public String getText() {
        return text;
    }

    /**
     * get the current type of msg
     *
     * @return type of error
     */
    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.text;
    }

}
