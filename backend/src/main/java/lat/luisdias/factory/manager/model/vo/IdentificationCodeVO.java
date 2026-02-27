package lat.luisdias.factory.manager.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class IdentificationCodeVO {

    private static final Pattern PATTERN = Pattern.compile("^[A-Z0-9_-]{3,50}$");

    @Column(name = "code", nullable = false, unique = true, updatable = false, length = 50)
    private String value;

    protected IdentificationCodeVO() {
    }

    public IdentificationCodeVO(String raw) {
        this.value = normalizeAndValidate(raw);
    }

    private String normalizeAndValidate(String raw) {
        if (raw == null) {
            throw new DomainInvariantViolationException(
                    "exception.domain.identification_code.invalid"
            );
        }

        String normalized = raw.trim().toUpperCase();

        if (!PATTERN.matcher(normalized).matches()) {
            throw new DomainInvariantViolationException(
                    "exception.domain.identification_code.invalid",
                    raw
            );
        }

        return normalized;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentificationCodeVO that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
