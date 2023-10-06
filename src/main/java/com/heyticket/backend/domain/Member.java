package com.heyticket.backend.domain;

import com.heyticket.backend.service.enums.AuthProvider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private boolean allowKeywordPush;

    @Column
    private boolean allowMarketing;

    @Column
    private String fcmToken;

    @Column
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberGenre> memberGenres = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberArea> memberAreas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberKeyword> memberKeywords = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLike> memberLikes = new ArrayList<>();

    public void updatePassword(String password) {
        this.password = password;
    }

    public String getStrAuthorities() {
        return getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addMemberGenres(List<MemberGenre> memberGenres) {
        this.memberGenres.addAll(memberGenres);
        memberGenres.forEach(memberGenre -> memberGenre.setMember(this));
    }

    public void addMemberAreas(List<MemberArea> memberAreas) {
        this.memberAreas.addAll(memberAreas);
        memberAreas.forEach(memberArea -> memberArea.setMember(this));
    }

    public void addMemberKeywords(List<MemberKeyword> memberKeywords) {
        this.memberKeywords.addAll(memberKeywords);
        memberKeywords.forEach(memberKeyword -> memberKeyword.setMember(this));
    }

    public void setAllowKeywordPush(boolean allowKeywordPush) {
        this.allowKeywordPush = allowKeywordPush;
    }

    public void setAllowMarketing(boolean allowMarketing) {
        this.allowMarketing = allowMarketing;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
