package cn.wizzer.modules.sys.service;

import cn.wizzer.common.service.core.BaseService;
import cn.wizzer.modules.sys.bean.Sys_role;
import cn.wizzer.modules.sys.bean.Sys_menu;
import cn.wizzer.modules.sys.bean.Sys_user;
import cn.wizzer.modules.sys.bean.Sys_user_profile;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizzer.cn on 2015/6/30.
 */
@IocBean(args = {"refer:dao"})
public class UserService extends BaseService<Sys_user> {
    public UserService(Dao dao) {
        super(dao);
    }

    public void update(Sys_user user) {
        dao().update(user);
    }

    public Sys_user view(Long id) {
        return dao().fetchLinks(fetch(id), "profile");
    }

    public Sys_user viewRoles(Long id) {
        return dao().fetchLinks(fetch(id), "roles");
    }

    public Sys_user fetchByUsername(String username) {
        return fetch(Cnd.where("username", "=", username));
    }

    public List<Sys_menu> getMenus(long uid) {
        Sql sql = Sqls.create("select distinct a.* from sys_menu a,sys_role_menu b where a.id=b.menu_id and" +
                " b.role_id in(select role_id from sys_user_role where user_id=@userId) and a.is_enabled=1 and a.is_show=1 order by a.location ASC,a.id asc");
        sql.params().set("userId", uid);
        Entity<Sys_menu> entity = dao().getEntity(Sys_menu.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);
        return sql.getList(Sys_menu.class);
    }

    public Sys_user_profile getProfile(long uid){
        return dao().fetch(Sys_user_profile.class,Cnd.where("user_id","=",uid));
    }

    public List<String> getRoleNameList(Sys_user user) {
        dao().fetchLinks(user, "roles");
        List<String> roleNameList = new ArrayList<String>();
        for (Sys_role role : user.getRoles()) {
            roleNameList.add(role.getName());
        }
        return roleNameList;
    }

    public Sys_user fetchByOpenID(String openid) {
        Sys_user user = fetch(Cnd.where("openid", "=", openid));
        if (!Lang.isEmpty(user) && !user.isLocked()) {
            dao().fetchLinks(user, "servers");
            dao().fetchLinks(user, "roles");
        }
        return user;
    }
}

