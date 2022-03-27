package cn.wizzer.common.shiro.realm;

import cn.wizzer.common.shiro.exception.EmptyCaptchaException;
import cn.wizzer.common.util.StringUtil;
import cn.wizzer.common.shiro.exception.IncorrectCaptchaException;
import cn.wizzer.modules.models.sys.Sys_user;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.util.ByteSource;
import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NutDaoRealm extends AbstractNutRealm {
	private static final Log log = Logs.get();
	public NutDaoRealm() {
		setAuthenticationTokenClass(CaptchaToken.class);
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws DisabledAccountException {
		CaptchaToken authcToken = (CaptchaToken) token;
		String loginname = authcToken.getUsername();
		String password = Strings.sBlank(authcToken.getPassword());
		String captcha=authcToken.getCaptcha();
		if (Strings.isBlank(loginname)) {
			throw Lang.makeThrow(AuthenticationException.class, "Account name is empty");
		}
		Sys_user user = getUserService().fetch(Cnd.where("loginname","=",loginname));
		if (Lang.isEmpty(user)) {
			throw Lang.makeThrow(UnknownAccountException.class, "Account [ %s ] not found", loginname);
		}
		int errCount=NumberUtils.toInt(Strings.sNull(SecurityUtils.getSubject().getSession(true).getAttribute("errCount")));
		if(errCount>2){
			if(Strings.isBlank(captcha)){
				throw Lang.makeThrow(EmptyCaptchaException.class, "Captcha is empty");
			}
			String _captcha = Strings.sBlank(SecurityUtils.getSubject().getSession(true).getAttribute("captcha"));
			if (!authcToken.getCaptcha().equalsIgnoreCase(_captcha)) {
				throw Lang.makeThrow(IncorrectCaptchaException.class, "Captcha is error");
			}
		}
		if (user.isDisbaled()) {
			throw Lang.makeThrow(LockedAccountException.class, "Account [ %s ] is locked.",loginname);
		}
		if (!StringUtil.getPassword(loginname,password,user.getCreateAt()).equals(user.getPassword())) {
			errCount++;
			SecurityUtils.getSubject().getSession(true).setAttribute("errCount",errCount);
			throw Lang.makeThrow(IncorrectCredentialsException.class, "Account [ %s ]'s password is error", authcToken.getUsername());
		}
		user.setMenus(getUserService().getMenus(user.getId()));
		ByteSource salt = ByteSource.Util.bytes(user.getCreateAt());
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
		info.setCredentialsSalt(salt);
		return info;
	}
}
