package ir.maktabsharif.maktab_finalProject.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {

    ADMIN,
    PROFESSOR,
    STUDENT ;

    @JsonCreator
    public static Role from(String value) {
        return Role.valueOf(value.toUpperCase());
    }

}


