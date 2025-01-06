package com.green.smarty.dto;

import com.green.smarty.vo.UserVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@ToString
public class SecurityUserDTO implements UserDetails {

    // UserVO를 래핑하는 별도의 Security 객체 구성
    // UserDetails 상속 : 유연성을 위해 메서드 직접 구현
    private final UserVO userVO;

    public SecurityUserDTO(UserVO userVO) {
        this.userVO = userVO;
    }

    //----------------------------Spring Security 필수 요구 메서드-----------------------//
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // String Security의 getAuthorities() 메서드는 다수의 권한을 가질 수 있다는 전제로 설계됨
        // Collection<? extends GrantedAuthority> 타입을 반환하도록 정의
        if (userVO.getLevel().equals("admin")) return List.of(() -> "ROLE_USER", () -> "ROLE_ADMIN");
        else return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return userVO.getPassword();
    }

    @Override
    public String getUsername() {
        // 식별자 지정
        return userVO.getUser_id();
    }
    //--------------------------------------------------------------------------------//

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부
        return userVO.isUser_status();
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        List<String> roleList = new ArrayList<>();
        dataMap.put("id", userVO.getUser_id());
        dataMap.put("email", userVO.getEmail());
        dataMap.put("level", userVO.getLevel());
        if(userVO.getLevel().equals("admin")) {
            roleList.add(0, "USER");
            roleList.add(0, "ADMIN");
            dataMap.put("role", roleList);
        } else {
            roleList.add(0, "USER");
            dataMap.put("role", roleList);
        }
        return dataMap;
    }
}
