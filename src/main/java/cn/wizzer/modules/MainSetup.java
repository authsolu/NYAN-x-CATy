package cn.wizzer.modules;

import cn.wizzer.common.base.Globals;
import cn.wizzer.common.util.StringUtil;
import cn.wizzer.modules.models.sys.*;
import net.sf.ehcache.CacheManager;
import org.apache.velocity.app.Velocity;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by wizzer on 2016/6/21.
 */
public class MainSetup implements Setup {
    private static final Log log = Logs.get();
    public void init(NutConfig config) {
        try {
            Ioc ioc = config.getIoc();
            Dao dao = ioc.get(Dao.class);
            // 初始化数据表
            initSysData(config, dao);
            // 初始化Velocity
            velocityInit(config);
            // 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
            ioc.get(NutQuartzCronJobFactory.class);
            // 检查一下Ehcache CacheManager 是否正常.
            CacheManager cacheManager = ioc.get(CacheManager.class);
            log.debug("Ehcache CacheManager = " + cacheManager);
            /* redis测试
            JedisPool jedisPool = ioc.get(JedisPool.class);
            try (Jedis jedis = jedisPool.getResource()) {
                String re = jedis.set("_big_fish", "Hello Word!!");
                log.debug("1.redis say : " + re);
                re = jedis.get("_big_fish");
                log.debug("2.redis say : " + re);
            } finally {}

            RedisService redis = ioc.get(RedisService.class);
            redis.set("hi", "wendal,rekoe hoho..");
            log.debug("redis say again : " + redis.get("hi"));
            */
            // 初始化系统变量
            initSysSetting(config, dao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化数据库
     *
     * @param config
     * @param dao
     */
    private void initSysData(NutConfig config, Dao dao) {
        Daos.createTablesInPackage(dao, "cn.wizzer.nutzwk", true);
        // 若必要的数据表不存在，则初始化数据库
        if (0 == dao.count(Sys_user.class)) {
//            执行SQL脚本
//            FileSqlManager fm = new FileSqlManager("init_sys_dict.sql");
//            List<Sql> sqlList = fm.createCombo(fm.keys());
//            dao.execute(sqlList.toArray(new Sql[sqlList.size()]));
            //初始化配置表
            Sys_config conf = new Sys_config();
            conf.setConfigKey("AppName");
            conf.setConfigValue("NutzWk 开发框架");
            conf.setNote("系统名称");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppShrotName");
            conf.setConfigValue("NutzWk架");
            conf.setNote("系统短名称");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppDomain");
            conf.setConfigValue("127.0.0.1");
            conf.setNote("系统域名");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppUploadPath");
            conf.setConfigValue("/upload");
            conf.setNote("文件上传文件夹");
            dao.insert(conf);
            //初始化单位
            Sys_unit unit = new Sys_unit();
            unit.setPath("0001");
            unit.setName("系统管理");
            unit.setAliasName("System");
            unit.setLocation(0);
            unit.setAddress("银河-太阳系-地球");
            unit.setEmail("wizzer@qq.com");
            unit.setTelephone("");
            unit.setHasChildren(true);
            unit.setParentId("");
            unit.setWebsite("http://www.wizzer.cn");
            Sys_unit dbunit = dao.insert(unit);
            //初始化菜单
            List<Sys_menu> menuList = new ArrayList<Sys_menu>();
            Sys_menu menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("0001");
            menu.setName("系统管理");
            menu.setNote("系统管理");
            menu.setAliasName("System");
            menu.setIcon("ti-settings");
            menu.setLocation(0);
            menu.setHref("");
            menu.setTarget("");
            menu.setIsShow(true);
            menu.setHasChildren(true);
            menu.setParentId("");
            menu.setType("menu");
            Sys_menu m1 = dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("00010001");
            menu.setName("单位管理");
            menu.setAliasName("Unit");
            menu.setLocation(0);
            menu.setHref("/private/sys/unit");
            menu.setTarget("data-pjax");
            menu.setIsShow(true);
            menu.setPermission("sys:unit");
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m2 = dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("00010002");
            menu.setName("用户管理");
            menu.setAliasName("User");
            menu.setLocation(0);
            menu.setHref("/private/sys/user");
            menu.setTarget("data-pjax");
            menu.setIsShow(true);
            menu.setPermission("sys:user");
            menu.setHasChildren(false);
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m3 = dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("000100020001");
            menu.setName("添加用户");
            menu.setAliasName("Add");
            menu.setLocation(0);
            menu.setIsShow(false);
            menu.setPermission("sys:user:add");
            menu.setParentId(m3.getId());
            menu.setType("button");
            Sys_menu m31=dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("000100020002");
            menu.setName("修改用户");
            menu.setAliasName("Update");
            menu.setLocation(1);
            menu.setIsShow(false);
            menu.setPermission("sys:user:update");
            menu.setParentId(m3.getId());
            menu.setType("button");
            Sys_menu m32=dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("000100020003");
            menu.setName("删除用户");
            menu.setAliasName("Delete");
            menu.setLocation(2);
            menu.setIsShow(false);
            menu.setPermission("sys:user:delete");
            menu.setParentId(m3.getId());
            menu.setType("button");
            Sys_menu m33=dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("00010003");
            menu.setName("角色管理");
            menu.setAliasName("Role");
            menu.setLocation(0);
            menu.setHref("/private/sys/role");
            menu.setIsShow(true);
            menu.setPermission("sys:role");
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m4=dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("00010004");
            menu.setName("菜单管理");
            menu.setAliasName("Menu");
            menu.setLocation(0);
            menu.setHref("/private/sys/menu");
            menu.setTarget("data-pjax");
            menu.setIsShow(true);
            menu.setPermission("sys:menu");
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m5 = dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("00010005");
            menu.setName("系统参数");
            menu.setAliasName("Param");
            menu.setLocation(0);
            menu.setHref("/private/sys/config");
            menu.setTarget("data-pjax");
            menu.setIsShow(true);
            menu.setPermission("sys:config");
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m6=dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(false);
            menu.setPath("00010006");
            menu.setName("日志管理");
            menu.setAliasName("Log");
            menu.setLocation(0);
            menu.setHref("/private/sys/log");
            menu.setIsShow(true);
            menu.setPermission("sys:log");
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m7=dao.insert(menu);
            menu = new Sys_menu();
            menu.setDisabled(true);
            menu.setPath("00010007");
            menu.setName("定时任务");
            menu.setAliasName("Plugins");
            menu.setLocation(0);
            menu.setHref("/private/sys/job");
            menu.setIsShow(true);
            menu.setPermission("sys:job");
            menu.setParentId(m1.getId());
            menu.setType("menu");
            Sys_menu m8=dao.insert(menu);
            //初始化角色
            Sys_role role = new Sys_role();
            role.setName("公共角色");
            role.setCode("public");
            role.setAliasName("Public");
            role.setNote("All user has role");
            role.setUnitid("");
            role.setDisabled(false);
            dao.insert(role);
            role = new Sys_role();
            role.setName("系统管理员");
            role.setCode("sysadmin");
            role.setAliasName("Sysadmin");
            role.setNote("System Admin");
            role.setUnitid("");
            role.setMenus(menuList);
            role.setDisabled(false);
            Sys_role dbrole = dao.insert(role);
            //初始化用户
            Sys_user user = new Sys_user();
            user.setLoginname("superadmin");
            user.setNickname("超级管理员");
            user.setCreateAt(1466571305);
            user.setPassword(StringUtil.getPassword(user.getLoginname(), "1", user.getCreateAt()));
            user.setLoginIp("127.0.0.1");
            user.setEmail("wizzer@qq.com");
            user.setLoginTheme("palette.css");
            user.setLoginBoxed(false);
            user.setLoginScroll(false);
            user.setLoginSidebar(false);
            user.setUnitid(dbunit.getId());
            Sys_user dbuser = dao.insert(user);
            //不同的插入数据方式(安全)
            dao.insert("sys_user_unit", Chain.make("userId", dbuser.getId()).add("unitId", dbunit.getId()));
            dao.insert("sys_user_role", Chain.make("userId", dbuser.getId()).add("roleId", dbrole.getId()));
            //执行自定义SQL插入
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m1.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m2.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m3.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m31.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m32.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m33.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m4.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m5.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m6.getId() + "')"));
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values('" + dbrole.getId() + "','" + m7.getId() + "')"));
            //另外一种写法(安全)
            dao.execute(Sqls.create("insert into `sys_role_menu` (`roleId`, `menuId`) values(@a,@b)").setParam("a", dbrole.getId()).setParam("b", m8.getId()));
        }
    }

    /**
     * 初始化系统变量
     *
     * @param config
     * @param dao
     */
    private void initSysSetting(NutConfig config, Dao dao) {
        List<Sys_config> configList = dao.query(Sys_config.class,Cnd.NEW());
        for (Sys_config sysConfig : configList) {
            switch (sysConfig.getConfigKey()){
                case "AppName":
                    Globals.AppName=sysConfig.getConfigValue();
                    break;
                case "AppShrotName":
                    Globals.AppShrotName=sysConfig.getConfigValue();
                    break;
                case "AppDomain":
                    Globals.AppDomain=sysConfig.getConfigValue();
                    break;
                case "AppUploadPath":
                    Globals.AppUploadPath=sysConfig.getConfigValue();
                    break;
                default:
                    Globals.MyConfig.put(sysConfig.getConfigKey(), sysConfig.getConfigValue());
                    break;
            }
        }
        Globals.AppRoot = Strings.sNull(config.getAppRoot());//项目路径
        Globals.AppBase = Strings.sNull(config.getServletContext().getContextPath());//部署名
    }

    /**
     * 初始化Velocity
     *
     * @param config
     * @throws IOException
     */
    private void velocityInit(NutConfig config) throws IOException {
        log.info("Veloctiy Init Start...");
        Properties p = new Properties();
        p.setProperty("resource.loader", "file,classloader");
        p.setProperty("file.resource.loader.path", config.getAppRoot());
        p.setProperty("file", "org.apache.velocity.tools.view.WebappResourceLoader");
        p.setProperty("classloader.resource.loader.class", "cn.wizzer.common.view.VelocityResourceLoader");
        p.setProperty("classloader.resource.loader.path", config.getAppRoot());
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        p.setProperty("velocimacro.library.autoreload", "false");
        p.setProperty("classloader.resource.loader.root", config.getAppRoot());
        p.setProperty("velocimarco.library.autoreload", "true");
        p.setProperty("runtime.log.error.stacktrace", "false");
        p.setProperty("runtime.log.warn.stacktrace", "false");
        p.setProperty("runtime.log.info.stacktrace", "false");
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        p.setProperty("runtime.log.logsystem.log4j.category", "velocity_log");
        p.setProperty("velocimacro.library", "/WEB-INF/views/common/globals.html");
        Velocity.init(p);
        log.info("Veloctiy Init End.");
    }

    public void destroy(NutConfig config) {
    }
}
