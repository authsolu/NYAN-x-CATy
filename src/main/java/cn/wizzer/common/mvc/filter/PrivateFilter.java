package cn.wizzer.common.mvc.filter;

import cn.wizzer.common.util.StringUtils;
import cn.wizzer.modules.sys.bean.Sys_user;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

import java.util.Map;

/**
 * 后台拦截器
 * Created by Wizzer.cn on 2015/7/10.
 */
public class PrivateFilter implements ActionFilter {

    public View match(ActionContext context) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            Sys_user user = (Sys_user) currentUser.getPrincipal();
            if (user != null) {
                context.getRequest().setAttribute("app_path", getMenu(StringUtils.getPath(context.getPath()), user.getIdMenus()));
            }
        }
        return null;
    }

    /**
     * 得到当前路径或上级路径的栏目ID
     * @param path
     * @param map
     * @return
     */
    private String getMenu(String path, Map map) {
        String p = Strings.sNull(map.get(path));
        System.out.println("p:::"+p);
        System.out.println("path:::"+path);
        if (!Strings.isEmpty(p)) {
            return Strings.sNull(p);
        } else if (path.indexOf("/")>0) {
            return getMenu(path.substring(0, path.lastIndexOf("/")), map);
        } else {
            return "";
        }
    }
}
