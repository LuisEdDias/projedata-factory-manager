package lat.luisdias.factory.manager.exeption.exceptions;

public class DomainInvariantViolationException extends RuntimeException{
    private final String messageKey;
    private final Object[] args;

    public DomainInvariantViolationException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    public DomainInvariantViolationException(String messageKey, Throwable cause, Object... args) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() { return messageKey; }
    public Object[] getArgs() { return args; }
}
