package com.cvberry.berrypim.data;

import javax.validation.constraints.NotNull;

/**
 * 
 *  US Phone number, plus optional long range indicator.
 *  examples:
 *  1-800-123-4567  longrange-area-number
 *  800-123-4567    area-numer
 *             
 */

public class PhoneNumber {

    @NotNull
    protected String value;
    protected String type;

    public PhoneNumber(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        if(value==null) {
            return;
        }
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String value) {
        this.type = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (!value.equals(that.value)) return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
