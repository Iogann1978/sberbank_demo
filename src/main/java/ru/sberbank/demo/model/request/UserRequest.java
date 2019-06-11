package ru.sberbank.demo.model.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UserRequest {
    @NonNull
    private String firstName;
    private String lastName;
    @NonNull
    private String password;
}
