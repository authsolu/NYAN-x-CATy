package cn.wizzer.modules.controllers.sys;

import cn.apiclub.captcha.Captcha;
import cn.wizzer.common.base.Message;
import cn.wizzer.common.services.log.SysLogService;
import cn.wizzer.common.shiro.exception.EmptyCaptchaException;
import cn.wizzer.common.shiro.exception.IncorrectCaptchaException;
import cn.wizzer.common.shiro.filter.CaptchaFormAuthenticationFilter;
import cn.wizzer.common.util.StringUtil;
import cn.wizzer.common.view.VelocityLayoutView;
import cn.wizzer.modules.models.sys.Sys_log;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.sys.UserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.view.ServerRedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/6/22.
 */
@IocBean // 声明为Ioc容器中的一个Bean
@At("/private/login") // 整个模块的路径前缀
@Ok("json:{locked:'password|createAt',ignoreNull:true}") // 忽略password和createAt属性,忽略空属性的json输出
public class LoginController {
    private static final Log log = Logs.get();
    @Inject
    UserService userService;
    @Inject
    SysLogService sysLogService;

    @At("")
    @Filters
    public View login() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.isRemembered()) {
            return new ServerRedirectView("/private/home");
        } else {
            return new VelocityLayoutView("views.private.login");
        }
    }

    /**
     * 切换样式，对登陆用户有效
     *
     * @param theme
     * @param req
     * @RequiresUser 记住我有效
     * @RequiresAuthentication 就算记住我也需要重新验证身份
     */
    @At("/theme")
    @RequiresUser
    public void theme(@Param("path") String theme, HttpServletRequest req) {
        if (!Strings.isEmpty(theme)) {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                Sys_user user = (Sys_user) subject.getPrincipal();
                user.setLoginTheme(theme);
                userService.update(Chain.make("loginTheme", theme), Cnd.where("id", "=", user.getId()));
            }
        }
    }

    /**
     * 切换布局，对登陆用户有效
     *
     * @param p
     * @param v
     * @param req
     * @RequiresUser 记住我有效
     * @RequiresAuthentication 就算记住我也需要重新验证身份
     */
    @At("/layout")
    @RequiresUser
    public void layout(@Param("p") String p, @Param("v") boolean v, HttpServletRequest req) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Sys_user user = (Sys_user) subject.getPrincipal();
            if ("sidebar".equals(p)) {
                userService.update(Chain.make("loginSidebar", v), Cnd.where("id", "=", user.getId()));
                user.setLoginSidebar(v);
            } else if ("boxed".equals(p)) {
                userService.update(Chain.make("loginBoxed", v), Cnd.where("id", "=", user.getId()));
                user.setLoginBoxed(v);
            } else if ("scroll".equals(p)) {
                userService.update(Chain.make("loginScroll", v), Cnd.where("id", "=", user.getId()));
                user.setLoginScroll(v);
            }
        }

    }

    /**
     * 登陆验证
     *
     * @param token
     * @param req
     * @return
     */
    @At("/doLogin")
    @Ok("json")
    @Filters(@By(type = CaptchaFormAuthenticationFilter.class))
    public Object doLogin(@Attr("loginToken") AuthenticationToken token, HttpServletRequest req, HttpSession session) {
        int errCount = 0;
        try {
            errCount = NumberUtils.toInt(Strings.sNull(SecurityUtils.getSubject().getSession(true).getAttribute("errCount")));
            Subject subject = SecurityUtils.getSubject();
            ThreadContext.bind(subject);
            subject.login(token);
            Sys_user user = (Sys_user) subject.getPrincipal();
            user.setMenus(userService.getMenus(user.getId()));
            user.setLoginIp(StringUtil.getRemoteAddr());
            //计算左侧菜单
            List<Sys_menu> firstMenus = new ArrayList<>();
            Map<String, List<Sys_menu>> secondMenus = new HashMap<>();
            for (Sys_menu menu : user.getMenus()) {
                if (menu.getPath().length() > 4) {
                    List<Sys_menu> s = secondMenus.get(StringUtil.getParentId(menu.getPath()));
                    if (s == null) s = new ArrayList<>();
                    s.add(menu);
                    secondMenus.put(StringUtil.getParentId(menu.getPath()), s);
                } else if (menu.getPath().length() == 4) {
                    firstMenus.add(menu);
                }
            }
            user.setFirstMenus(firstMenus);
            user.setSecondMenus(secondMenus);
            sysLogService.async(Sys_log.c("info", "用户登陆", "成功登录系统！"));
            userService.update(Chain.make("loginIp", user.getLoginIp()).add("loginAt", (int) System.currentTimeMillis() / 1000)
                    .add("loginCount", user.getLoginCount() + 1).add("online", true)
                    , Cnd.where("id", "=", user.getId()));
            return Message.success("login.success", req);
        } catch (IncorrectCaptchaException e) {
            //自定义的验证码错误异常,需shrio.ini 配置authcStrategy属性，加到对应的类中

            return Message.error(1, "login.error.captcha", req);
        } catch (EmptyCaptchaException e) {
            //验证码为空
            return Message.error(2, "login.error.captcha", req);
        } catch (LockedAccountException e) {
            return Message.error(3, "login.error.locked", req);
        } catch (AuthenticationException e) {
            errCount++;
            try {
                SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
            } catch (Exception e1) {
            }
            return Message.error(4, "login.error.user", req);
        } catch (Exception e) {
            errCount++;
            try {
                SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
            } catch (Exception e1) {
            }
            return Message.error(5, "login.error.system", req);
        }
    }

    @At("/captcha")
    @Ok("raw:png")
    public BufferedImage next(HttpSession session, @Param("w") int w, @Param("h") int h) {
        if (w * h < 1) { //长或宽为0?重置为默认长宽.
            w = 200;
            h = 60;
        }
        Captcha captcha = new Captcha.Builder(w, h)
                .addText()
//								.addBackground(new GradiatedBackgroundProducer())
//								.addNoise(new StraightLineNoiseProducer()).addBorder()
//								.gimp(new FishEyeGimpyRenderer())
                .build();
        String text = captcha.getAnswer();
        session.setAttribute("captcha", text);
        return captcha.getImage();
    }
}
