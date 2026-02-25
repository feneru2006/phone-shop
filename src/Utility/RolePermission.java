package Utility;

import DTO.accountDTO;
import java.util.HashSet;
import java.util.Set;

public class RolePermission {
    private static accountDTO currentUser;
    //Set luu danh sach ma chuc nang
    private static Set<String> permission = new HashSet<>();
    private RolePermission(){}
    public static void set(accountDTO user, Set<String> perms){
        currentUser = user;
        permission = (perms != null) ? perms : new HashSet<>();
    }

    public static boolean hasPerms(String permsCode){
        return permission.contains(permsCode);
    }

    public static accountDTO getCurrentUser(){
        return currentUser;
    }

    public static void clear(){
        permission.clear();
    }
}
