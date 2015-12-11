package com.todolist.service;

import com.todolist.model.User;
import com.todolist.model.UserRole;
import com.todolist.repositories.UserDao;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vano on 09.12.15.
 */
@Service
public class VkImpl implements Vk {


    private final String USER_AGENT = "Mozilla/5.0";



    @Autowired
    UserDao userDao;


    //for registration
    //https://oauth.vk.com/authorize?client_id=5181063&display=page&redirect_uri=http://localhost&scope=friends&response_type=code&v=5.40

    String redirect_uri = "http://192.168.50.113:8080";



    @Transactional
    public String getToken (String code)throws Exception {

        String url = "https://oauth.vk.com/access_token?client_id=5181063&client_secret=sDAvKlcVb54De5ffT9ho&redirect_uri="
                + redirect_uri + "&code=" + code ;


        URL obj = new URL(url);
        HttpsURLConnection con= (HttpsURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonObj = new JSONObject(response.toString());

        final String userId = jsonObj.getInt("user_id") + "";
        String token = jsonObj.getString("access_token");



        User user = userDao.findByUserName(userId);

        if (user == null) {
            user = new User();
            user.setUsername(userId);
            user.setPassword("ddddd");
            user.setEmail("vano@vano.com");
            user.setEnabled(true);

            Set<UserRole> userRoles = new HashSet<UserRole>();
            UserRole userRole = new UserRole();
            userRole.setRole("ROLE_USER");
            userRoles.add(userRole);
            user.setUserRole(userRoles);
            userDao.addUser(user);
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final Authentication authentication2 = authentication;

        final MyUserDetailsService userDetailsService = new MyUserDetailsService();

        final org.springframework.security.core.userdetails.UserDetails u =  userDetailsService.getUser(user);



        Authentication trustedAuthentication = new Authentication () {
            private String name = userId;
            private Object details = authentication2.getDetails();


            private UserDetails user = u;
            private boolean authenticated = true;
            private Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

            @ Override
            public String getName() {
                return name;
            }
            @ Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }
            @ Override
            public Object getCredentials() {
                return user.getPassword();
            }
            @ Override
            public Object getDetails() {
                return details;
            }
            @ Override
            public Object getPrincipal() {
                return user;
            }
            @ Override
            public boolean isAuthenticated() {
                return authenticated;
            }
            @ Override
            public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
                this.authenticated = authenticated;
            }
        };

        //(user, authentication.getDetails())
        authentication = trustedAuthentication;
        SecurityContextHolder.getContext().setAuthentication(authentication);





        return token;
    }



}
