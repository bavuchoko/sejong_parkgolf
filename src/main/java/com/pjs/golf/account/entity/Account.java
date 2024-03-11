package com.pjs.golf.account.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pjs.golf.game.entity.Game;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column(unique = true)
    private String username;
    private String birth;
    @JsonIgnore
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String portrait;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
    @Transient
    private String token;

    public void overwritePassword(String password) {
        this.password = password;
    }

}
