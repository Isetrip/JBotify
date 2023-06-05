package com.isetrip.jbotify.data;

import com.isetrip.jbotify.lang.Lang;
import com.isetrip.jbotify.utils.LangUtils;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JBUser {

    @Id
    @Getter
    @Setter
    @Column(name = "userId")
    private String userId;
    @Getter
    @Setter
    @Column(name = "userName")
    private String userName;
    @Setter
    @Column(name = "userLang")
    private String userLang;
    @Getter
    @Setter
    @Column(name = "chatId")
    private String chatId;

    public Lang getUserLang() {
        return LangUtils.of(this.userLang);
    }
}
