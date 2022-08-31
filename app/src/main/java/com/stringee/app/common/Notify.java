package com.stringee.app.common;

public enum Notify {
    CONNECTION_STATE("com.stringee.connection.state"),
    UPDATE_LOG("com.stringee.log.update"),
    END_CALL_FROM_DIAL("com.stringee.stringeex.call.end_dial"),
    CALL_SIGNAL_CHANGE("com.stringee.stringeex.call.signal.change"),
    CALL_MEDIA_CHANGE("com.stringee.stringeex.call.media.change"),
    CALL_HANDLED_ON_OTHER_DEVICE("com.stringee.stringeex.call.handled"),
    FINISH_SUBDOMAIN_ACTIVITY("com.stringee.stringeex.finish.subdomain_activity"),
    REFRESH_CONTACT("com.stringee.stringeex.contact.refresh"),
    UPDATE_CONTACT("com.stringee.stringeex.contact.update"),
    UPDATE_CONTACT_FIELD("com.stringee.stringeex.contact.field.update"),
    UPDATE_TICKETS("com.stringee.stringeex.tickets.update"),
    UPDATE_TICKET_NOTE("com.stringee.stringeex.ticket.note.update"),
    TELEPHONY_STATE_CHANGE("com.stringee.stringeex.telephony.state.change"),
    REQUEST_LOGOUT("com.stringee.stringeex.request.logout"),
    IN_CALL("com.stringee.stringeex.in.call"),
    REQUEST_PERMISSION("com.stringee.stringeex.request.permission"),
    UPDATE_SYSTEM_FIELD("com.stringee.stringeex.update.system.field"),
    // message
    ON_CHANGE_EVENT("com.stringee.stringeex.message.on.change.event"),
    ON_LIVE_CHAT_EVENT("com.stringee.stringeex.message.on.live.chat.event"),
    ON_TYPING_EVENT("com.stringee.stringeex.message.on.typing.event"),
    CONTINUE_CHAT("com.stringee.stringeex.continue.chat"),
    SHOW_IN_CHAT("com.stringee.stringeex.show.in.chat"),
    EDIT_MESSAGE("com.stringee.stringeex.edit.message");

    private String value;

    private Notify(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
