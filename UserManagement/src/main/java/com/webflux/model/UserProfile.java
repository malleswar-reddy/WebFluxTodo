package com.webflux.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @Column("user_id")
    private Long userId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("role")
    private String role = "customer";

    @Column("picture")
    private String picture;

    @Column("phone")
    private Long phone;

    @Column("alternate_phone")
    private Long alternatePhone;

    @Column("exp")
    private String exp;
}